package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * QQ登录
 */
public class QQIdentityProviderFactory extends AbstractIdentityProviderFactory<QQIdentityProvider>
        implements SocialIdentityProviderFactory<QQIdentityProvider> {

    public static final String PROVIDER_ID = "qq";

    @Override
    public String getName() {
        return "QQ";
    }

    @Override
    public QQIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new QQIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
