package org.vincenthql.keycloak.sms.twilio;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.vincenthql.keycloak.sms.SmsSenderProvider;

/**
 * 短信发送provide
 */
public class TwilioSmsSenderProvider implements SmsSenderProvider {

    private static final Logger logger = Logger.getLogger(TwilioSmsSenderProvider.class);

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

    @Override
    public boolean send(String phoneNumber, String body) {
        logger.info("twilio: " + phoneNumber + " " + body);
        TwilioRestClient client = new TwilioRestClient.Builder(this.accountSid, this.authToken).build();

        try {
            MessageCreator messageCreator = new MessageCreator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(this.fromPhoneNumber),
                    body);
//        messageCreator.setMediaUrl(mediaUrl);
            messageCreator.create(client);
            return true;
        } catch (Exception e) {
            logger.error("send sms error", e);
            return false;
        }
    }
}
