package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/**
 * 微信公众平台登录
 */
public class WeChatMpIdentityProviderFactory extends AbstractIdentityProviderFactory<WeChatMpIdentityProvider>
		implements SocialIdentityProviderFactory<WeChatMpIdentityProvider> {

    public static final String PROVIDER_ID = "wechat-mp";

    @Override
    public String getName() {
        return "WeChat Mp";
    }

    @Override
    public WeChatMpIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new WeChatMpIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
