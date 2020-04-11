package org.vincenthql.keycloak.sms.twilio;

import org.keycloak.models.KeycloakSession;
import org.vincenthql.keycloak.sms.SmsSenderProvider;

/**
 * 短信发送provide
 */
public class TwilioSmsSenderProvider implements SmsSenderProvider {

    private String accountSid;
    private String authToken;
    private String fromPhoneNumber;
    private KeycloakSession session;

    public TwilioSmsSenderProvider(String accountSid, String authToken, String fromPhoneNumber, KeycloakSession session) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromPhoneNumber = fromPhoneNumber;
        this.session = session;
    }

    @Override
    public void close() {

    }
}
