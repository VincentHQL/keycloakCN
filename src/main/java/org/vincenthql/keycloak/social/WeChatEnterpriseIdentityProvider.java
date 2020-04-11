package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.models.KeycloakSession;

/**
 * 企业微信登录
 */
public class WeChatEnterpriseIdentityProvider extends AbstractOAuth2IdentityProvider<OAuth2IdentityProviderConfig>
        implements SocialIdentityProvider<OAuth2IdentityProviderConfig> {


    public static final String AUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";

    public static final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    public static final String PROFILE_URL = "https://api.weixin.qq.com/sns/userinfo";

    public static final String DEFAULT_SCOPE = "snsapi_userinfo";



    public WeChatEnterpriseIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
        super(session, config);
    }

    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }
}
