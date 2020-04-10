package org.vincenthql.keycloak.social.weixin;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;


public class WeiXinMpIdentityProviderFactory extends AbstractIdentityProviderFactory<WeiXinMpIdentityProvider>
		implements SocialIdentityProviderFactory<WeiXinMpIdentityProvider> {

    public static final String PROVIDER_ID = "weixin-mp";

    @Override
    public String getName() {
        return "WeiXin Mp";
    }

    @Override
    public WeiXinMpIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new WeiXinMpIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
