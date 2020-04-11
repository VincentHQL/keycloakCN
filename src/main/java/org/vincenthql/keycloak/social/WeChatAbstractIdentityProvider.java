package org.vincenthql.keycloak.social;

import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;
import org.keycloak.vault.VaultStringSecret;

import javax.ws.rs.core.UriBuilder;

public abstract class WeChatAbstractIdentityProvider<C extends OAuth2IdentityProviderConfig> extends AbstractOAuth2IdentityProvider<C> {


    public static final String PROFILE_URL = "https://api.weixin.qq.com/sns/userinfo";

    public WeChatAbstractIdentityProvider(KeycloakSession session, C config) {
        super(session, config);
    }

    @Override
    protected UriBuilder createAuthorizationUrl(AuthenticationRequest request) {
        UriBuilder uriBuilder = super.createAuthorizationUrl(request);
        uriBuilder.queryParam("appid", getConfig().getClientId());

        return uriBuilder;
    }

    @Override
    public SimpleHttp authenticateTokenRequest(final SimpleHttp tokenRequest) {
        SimpleHttp superTokenRequest = super.authenticateTokenRequest(tokenRequest);
        try (VaultStringSecret vaultStringSecret = session.vault().getStringSecret(getConfig().getClientSecret())) {
            if (getConfig().isBasicAuthentication()) {
                return tokenRequest.authBasic(getConfig().getClientId(), vaultStringSecret.get().orElse(getConfig().getClientSecret()));
            }
            return superTokenRequest
                    .param("appid", getConfig().getClientId())
                    .param("secret", vaultStringSecret.get().orElse(getConfig().getClientSecret()));
        }
    }

    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
        BrokeredIdentityContext user = new BrokeredIdentityContext(getJsonProperty(profile, "openid"));

        user.setUsername(getJsonProperty(profile, "openid"));
        user.setBrokerUserId(getJsonProperty(profile, "openid"));
        user.setModelUsername(getJsonProperty(profile, "openid"));
        user.setName(getJsonProperty(profile, "nickname"));
        user.setIdpConfig(getConfig());
        user.setUserAttribute("country", getJsonProperty(profile, "country"));
        user.setUserAttribute("sex", getJsonProperty(profile, "sex"));
        user.setUserAttribute("language", getJsonProperty(profile, "language"));
        user.setUserAttribute("city", getJsonProperty(profile, "city"));
        user.setUserAttribute("province", getJsonProperty(profile, "province"));
        user.setUserAttribute("picture", getJsonProperty(profile, "headimgurl"));
        user.setUserAttribute("privilege", getJsonProperty(profile, "privilege"));
        user.setUserAttribute("nickname", getJsonProperty(profile, "nickname"));
        user.setUserAttribute("openid", getJsonProperty(profile, "openid"));
        user.setUserAttribute("unionid", getJsonProperty(profile, "unionid"));
        user.setIdp(this);

        AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());
        return user;

    }

    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return PROFILE_URL;
    }

    @Override
    public BrokeredIdentityContext getFederatedIdentity(String response) {
        String accessToken = extractTokenFromResponse(response, getAccessTokenResponseParameter());
        String openid = extractTokenFromResponse(response, "openid");

        if (accessToken == null || openid == null) {
            throw new IdentityBrokerException("No access token available in OAuth server response: " + response);
        }

        BrokeredIdentityContext context = doGetFederatedIdentityWeiXin(accessToken, openid);
        context.getContextData().put(FEDERATED_ACCESS_TOKEN, accessToken);
        return context;
    }


    private BrokeredIdentityContext doGetFederatedIdentityWeiXin(String accessToken, String openid) {
        try {
            JsonNode profile = SimpleHttp.doGet(PROFILE_URL, session)
                    .param("access_token", accessToken)
                    .param("openid", openid)
                    .param("lang", "zh_CN")
                    .asJson();

            BrokeredIdentityContext user = extractIdentityFromProfile(null, profile);

            return user;
        } catch (Exception e) {
            throw new IdentityBrokerException("Could not obtain user profile from weixin mp.", e);
        }
    }


}
