package org.vincenthql.keycloak.sms.twilio;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.vincenthql.keycloak.sms.SmsSenderProvider;
import org.vincenthql.keycloak.sms.SmsSenderProviderFactory;

public class TwilioSenderProviderFactory implements SmsSenderProviderFactory {

    public static final String PROVIDER_ID = "twilio";

    private String accountSid;

    private String authToken;

    private String fromPhoneNumber;

    @Override
    public SmsSenderProvider create(KeycloakSession session) {
        return new TwilioSmsSenderProvider(accountSid, authToken, fromPhoneNumber, session);
    }

    @Override
    public void init(Config.Scope config) {
        this.accountSid = config.get("accountSid", "");
        this.authToken = config.get("authToken", "");
        this.fromPhoneNumber = config.get("fromPhoneNumber", "");
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
