package org.vincenthql.keycloak.utils;


import org.keycloak.models.KeycloakSession;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.vincenthql.keycloak.sms.SmsSenderProvider;

public class KeycloakCNUtils {

    public static final String USR_CRED_MDL_SMS_CODE = "USR_CRED_MDL_SMS_CODE";
    public static final String USR_CRED_MDL_SMS_EXP_TIME = "USR_CRED_MDL_SMS_EXP_TIME";

    /**
     * 发送短信验证码
     * @param keycloakSession
     * @param phoneNumber
     * @param body
     * @return
     */
    public static boolean sendSmsCode(KeycloakSession keycloakSession, String phoneNumber, String body) {
        SmsSenderProvider smsSenderProvider = keycloakSession.getProvider(SmsSenderProvider.class);
        return smsSenderProvider.send(phoneNumber, body);
    }

    /**
     * 判断验证码是否有效
     *
     * @param verifyCode
     * @return
     */
    public static boolean isVerifyCodeValid(AuthenticationSessionModel authenticationSessionModel, String verifyCode) {
        String expectedCode = authenticationSessionModel.getAuthNote(USR_CRED_MDL_SMS_CODE);
        String expTimeString = authenticationSessionModel.getAuthNote(USR_CRED_MDL_SMS_EXP_TIME);
        if (expectedCode == null || !expectedCode.equals(verifyCode)) {
            throw new IllegalArgumentException("verifyCodeError");
        }

        long now = System.currentTimeMillis();
        if (Long.parseLong(expTimeString) < now) {
            throw new IllegalArgumentException("verifyCodeExpired");
        }

        return true;
    }
}
