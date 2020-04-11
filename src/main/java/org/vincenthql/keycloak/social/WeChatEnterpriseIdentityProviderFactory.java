package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * 企业微信
 */
public class WeChatEnterpriseIdentityProviderFactory extends AbstractIdentityProviderFactory<WeChatEnterpriseIdentityProvider>
        implements SocialIdentityProviderFactory<WeChatEnterpriseIdentityProvider> {

    public static final String PROVIDER_ID = "wechat-enterprise";

    @Override
    public String getName() {
        return "WeChat Enterprise";
    }

    @Override
    public WeChatEnterpriseIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new WeChatEnterpriseIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
