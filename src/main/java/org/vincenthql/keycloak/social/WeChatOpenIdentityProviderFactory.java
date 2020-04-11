package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * 微信开放平台登录
 */
public class WeChatOpenIdentityProviderFactory extends AbstractIdentityProviderFactory<WeChatOpenIdentityProvider>
        implements SocialIdentityProviderFactory<WeChatOpenIdentityProvider> {

    public static final String PROVIDER_ID = "wechat-open";

    @Override
    public String getName() {
        return "WeChat Open";
    }

    @Override
    public WeChatOpenIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new WeChatOpenIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
