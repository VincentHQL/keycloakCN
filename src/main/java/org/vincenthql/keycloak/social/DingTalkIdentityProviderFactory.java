package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * 钉钉登录
 */
public class DingTalkIdentityProviderFactory extends AbstractIdentityProviderFactory<DingTalkIdentityProvider>
        implements SocialIdentityProviderFactory<DingTalkIdentityProvider> {

    public static final String PROVIDER_ID = "ding-talk";

    @Override
    public String getName() {
        return "Ding Talk";
    }

    @Override
    public DingTalkIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new DingTalkIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
