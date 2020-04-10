package org.vincenthql.keycloak.social.weixin;


import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.core.UriBuilder;

/**
 * 微信开放平台
 */
public class WeiXinOpenIdentityProvider extends WeiXinAbstractIdentityProvider<OAuth2IdentityProviderConfig>
        implements SocialIdentityProvider<OAuth2IdentityProviderConfig> {

    public static final String AUTH_URL = "https://open.weixin.qq.com/connect/qrconnect";
    public static final String TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    public static final String PROFILE_URL = "https://api.weixin.qq.com/sns/userinfo";
    public static final String DEFAULT_SCOPE = "snsapi_login";

    public WeiXinOpenIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(PROFILE_URL);
    }

    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }


    @Override
    protected UriBuilder createAuthorizationUrl(AuthenticationRequest request) {
        UriBuilder uriBuilder = super.createAuthorizationUrl(request);
        uriBuilder.replaceQueryParam(OAUTH2_PARAMETER_STATE, String.format("%s%s", request.getState().getEncoded(), "#wechat_redirect"));
        return uriBuilder;
    }



}
