package org.vincenthql.keycloak.sms.aliyun;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.vincenthql.keycloak.sms.SmsSenderProvider;

/**
 * 短信发送provide
 */
public class AliyunSmsSenderProvider implements SmsSenderProvider {

    private static final Logger logger = Logger.getLogger(AliyunSmsSenderProvider.class);



    public AliyunSmsSenderProvider(KeycloakSession session) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean send(String phoneNumber, String body) {
        logger.info("aliiyun: " + phoneNumber + " " + body);
       return false;
    }
}
