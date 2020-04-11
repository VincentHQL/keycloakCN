package org.vincenthql.keycloak.social;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.codec.binary.Base64;
import org.keycloak.OAuthErrorException;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.messages.Messages;
import org.keycloak.vault.VaultStringSecret;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class DingTalkIdentityProvider extends AbstractOAuth2IdentityProvider<OAuth2IdentityProviderConfig>
        implements SocialIdentityProvider<OAuth2IdentityProviderConfig> {

    public static final String AUTH_URL = "https://oapi.dingtalk.com/connect/qrconnect";
    public static final String TOKEN_URL = "https://oapi.dingtalk.com/gettoken";
    public static final String PROFILE_URL = "https://oapi.dingtalk.com/sns/getuserinfo_bycode";
    public static final String DEFAULT_SCOPE = "snsapi_login";

    public DingTalkIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(PROFILE_URL);
    }

    @Override
    protected UriBuilder createAuthorizationUrl(AuthenticationRequest request) {
        UriBuilder uriBuilder = super.createAuthorizationUrl(request);
        uriBuilder.queryParam("appid", getConfig().getClientId());

        return uriBuilder;
    }

    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
        BrokeredIdentityContext user = new BrokeredIdentityContext(getJsonProperty(profile, "openid"));

        user.setUsername(getJsonProperty(profile, "openid"));
        user.setBrokerUserId(getJsonProperty(profile, "openid"));
        user.setModelUsername(getJsonProperty(profile, "openid"));
        user.setName(getJsonProperty(profile, "nick"));
        user.setIdpConfig(getConfig());
        user.setUserAttribute("nickname", getJsonProperty(profile, "nick"));
        user.setUserAttribute("openid", getJsonProperty(profile, "openid"));
        user.setUserAttribute("unionid", getJsonProperty(profile, "unionid"));
        user.setIdp(this);

        AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());
        return user;

    }

    /**
     * 计算签名
     * @param appSecret
     * @param timestamp
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeyException
     */
    private String getSignature(String appSecret, String timestamp) throws NoSuchAlgorithmException,
            IOException, InvalidKeyException {
        // 根据timestamp, appSecret计算签名值
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signatureBytes = mac.doFinal(timestamp.getBytes("UTF-8"));
        return new String(Base64.encodeBase64(signatureBytes));
    }


    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }


    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return PROFILE_URL;
    }

    @Override
    public Object callback(RealmModel realm, AuthenticationCallback callback, EventBuilder event) {
        return new DingTalkEndpoint(callback, realm, event);
    }

    protected class DingTalkEndpoint extends Endpoint {

        public DingTalkEndpoint(AuthenticationCallback callback, RealmModel realm, EventBuilder event) {
            super(callback, realm, event);
        }


        @Override
        public Response authResponse(String state, String authorizationCode, String error) {
            if (error != null) {
                logger.error(error + " for broker login " + getConfig().getProviderId());
                if (error.equals(ACCESS_DENIED)) {
                    return callback.cancelled(state);
                } else if (error.equals(OAuthErrorException.LOGIN_REQUIRED) || error.equals(OAuthErrorException.INTERACTION_REQUIRED)) {
                    return callback.error(state, error);
                } else {
                    return callback.error(state, Messages.IDENTITY_PROVIDER_UNEXPECTED_ERROR);
                }
            }

            try {

                if (authorizationCode != null) {
                    BrokeredIdentityContext federatedIdentity = getFederatedIdentityByCode(authorizationCode);

                    federatedIdentity.setIdpConfig(getConfig());
                    federatedIdentity.setIdp(DingTalkIdentityProvider.this);
                    federatedIdentity.setCode(state);

                    return callback.authenticated(federatedIdentity);
                }
            } catch (WebApplicationException e) {
                return e.getResponse();
            } catch (Exception e) {
                logger.error("Failed to make identity provider oauth callback", e);
            }
            event.event(EventType.LOGIN);
            event.error(Errors.IDENTITY_PROVIDER_LOGIN_FAILURE);
            return ErrorPage.error(session, null, Response.Status.BAD_GATEWAY, Messages.IDENTITY_PROVIDER_UNEXPECTED_ERROR);
        }

    }



    private BrokeredIdentityContext getFederatedIdentityByCode(String authorizationCode) {
        try (VaultStringSecret vaultStringSecret = session.vault().getStringSecret(getConfig().getClientSecret())) {
            Map params = new HashMap<String, String>(1);
            params.put("tmp_auth_code", authorizationCode);
            String timestamp = String.valueOf(System.currentTimeMillis());

            String url = String.format("%s?accessKey=%s&timestamp=%s&signature=%s", PROFILE_URL, getConfig().getClientId(), timestamp, getSignature(vaultStringSecret.get().orElse(getConfig().getClientSecret()), timestamp));

            JsonNode profile = SimpleHttp.doPost(url, session)
                    .json(params)
                    .asJson();
            if ("0".equals(getJsonProperty(profile, "errcode"))){
                return extractIdentityFromProfile(null, profile.get("user_info"));
            } else {
                throw new RuntimeException(getJsonProperty(profile, "errmsg"));
            }

        } catch (Exception e) {
            throw new IdentityBrokerException("Could not obtain user profile from Ding Talk.", e);
        }
    }

}
