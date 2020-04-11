package org.vincenthql.keycloak.sms;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

/**
 * 短信发送
 */
public class SmsSenderSpi implements Spi {

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public String getName() {
        return "smsSender";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return SmsSenderProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return SmsSenderProviderFactory.class;
    }
}
