package org.vincenthql.keycloak.social;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;

public class DingTalkUserAttributeMapper extends AbstractJsonUserAttributeMapper {

    private static final String[] cp = new String[] { DingTalkIdentityProviderFactory.PROVIDER_ID };

    @Override
    public String[] getCompatibleProviders() {
        return cp;
    }

    @Override
    public String getId() {
        return "ding-talk-user-attribute-mapper";
    }

}
