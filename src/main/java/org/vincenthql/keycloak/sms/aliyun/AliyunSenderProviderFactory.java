package org.vincenthql.keycloak.sms.aliyun;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.vincenthql.keycloak.sms.SmsSenderProvider;
import org.vincenthql.keycloak.sms.SmsSenderProviderFactory;

/**
 * 阿里云短信发送
 */
public class AliyunSenderProviderFactory implements SmsSenderProviderFactory {

    public static final String PROVIDER_ID = "aliyun";

    @Override
    public SmsSenderProvider create(KeycloakSession session) {
        return new AliyunSmsSenderProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
