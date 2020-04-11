package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;

public class WeChatMpUserAttributeMapper extends AbstractJsonUserAttributeMapper {

    private static final String[] cp = new String[] { WeChatMpIdentityProviderFactory.PROVIDER_ID };

    @Override
    public String[] getCompatibleProviders() {
        return cp;
    }

    @Override
    public String getId() {
        return "WeChat-mp-user-attribute-mapper";
    }

}
