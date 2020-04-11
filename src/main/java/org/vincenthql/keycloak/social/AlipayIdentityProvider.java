package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.models.KeycloakSession;

public class AlipayIdentityProvider extends AbstractOAuth2IdentityProvider<OAuth2IdentityProviderConfig>
        implements SocialIdentityProvider<OAuth2IdentityProviderConfig> {

    public static final String AUTH_URL = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";
    public static final String TOKEN_URL = "https://openapi.alipay.com/gateway.do";
    public static final String DEFAULT_SCOPE = "auth_user";


    public AlipayIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
        super(session, config);
    }

    @Override
    protected String getDefaultScopes() {
        return null;
    }
}
