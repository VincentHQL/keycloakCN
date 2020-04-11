package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * 支付宝登录
 */
public class AlipayIdentityProviderFactory extends AbstractIdentityProviderFactory<AlipayIdentityProvider>
        implements SocialIdentityProviderFactory<AlipayIdentityProvider> {


    public static final String PROVIDER_ID = "alipay";

    @Override
    public String getName() {
        return "alipay";
    }

    @Override
    public AlipayIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new AlipayIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
