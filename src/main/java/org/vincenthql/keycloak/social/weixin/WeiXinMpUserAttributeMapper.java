package org.vincenthql.keycloak.social.weixin;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;

public class WeiXinMpUserAttributeMapper extends AbstractJsonUserAttributeMapper {

    private static final String[] cp = new String[] { WeiXinMpIdentityProviderFactory.PROVIDER_ID };

    @Override
    public String[] getCompatibleProviders() {
        return cp;
    }

    @Override
    public String getId() {
        return "weixin-mp-user-attribute-mapper";
    }

}
