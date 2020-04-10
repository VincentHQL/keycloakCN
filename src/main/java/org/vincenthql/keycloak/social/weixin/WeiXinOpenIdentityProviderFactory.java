package org.vincenthql.keycloak.social.weixin;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * 微信开放平台
 */
public class WeiXinOpenIdentityProviderFactory extends AbstractIdentityProviderFactory<WeiXinOpenIdentityProvider>
        implements SocialIdentityProviderFactory<WeiXinOpenIdentityProvider> {

    public static final String PROVIDER_ID = "weixin-open";

    @Override
    public String getName() {
        return "WeiXin Open";
    }

    @Override
    public WeiXinOpenIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return null;
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
