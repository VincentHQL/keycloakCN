package org.vincenthql.keycloak.authentication.requiredactions;


import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.resources.AttributeFormDataProcessor;
import org.vincenthql.keycloak.utils.KeycloakCNUtils;
import org.vincenthql.keycloak.utils.ValidationCN;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * 重写默认修改用户属性类
 */
public class UpdateProfile extends org.keycloak.authentication.requiredactions.UpdateProfile {

    @Override
    public void processAction(RequiredActionContext context) {
        EventBuilder event = context.getEvent();
        event.event(EventType.UPDATE_PROFILE);
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        UserModel user = context.getUser();
        KeycloakSession session = context.getSession();
        RealmModel realm = context.getRealm();

        List<FormMessage> errors = ValidationCN.validateUpdateProfileCNForm(realm, formData);
        if (errors != null && !errors.isEmpty()) {
            Response challenge = context.form()
                    .setErrors(errors)
                    .setFormData(formData)
                    .createResponse(UserModel.RequiredAction.UPDATE_PROFILE);
            context.challenge(challenge);
            return;
        }

        String action = formData.getFirst("submitAction");
        // 发送验证码
        if (action != null && action.equals("doSendSmsCode")) {
            String code = String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, 6 - 1)));
            if (KeycloakCNUtils.sendSmsCode(context.getSession(), formData.getFirst(ValidationCN.FIELD_PHONE_NUMBER), "您的验证码：" + code)) {
                context.getAuthenticationSession().setAuthNote(KeycloakCNUtils.USR_CRED_MDL_SMS_CODE, code);
                context.getAuthenticationSession().setAuthNote(KeycloakCNUtils.USR_CRED_MDL_SMS_EXP_TIME, String.valueOf(System.currentTimeMillis() + 60000));
                Response challenge = context.form()
                        .addSuccess(new FormMessage("发送验证码成功"))
                        .setAttribute("sendCode", true)
                        .setFormData(formData)
                        .createResponse(UserModel.RequiredAction.UPDATE_PROFILE);
                context.challenge(challenge);
            } else {
                Response challenge = context.form()
                        .addError(new FormMessage("发送验证码失败"))
                        .setFormData(formData)
                        .createResponse(UserModel.RequiredAction.UPDATE_PROFILE);
                context.challenge(challenge);
            }
            return;
        }

        String verifyCode = formData.getFirst(ValidationCN.FIELD_VERIFY_CODE);
        //判断短信验证码是否正确
        if (ValidationCN.isBlank(verifyCode)) {
            Response challenge = context.form()
                    .addError(new FormMessage("验证码不能为空"))
                    .setFormData(formData)
                    .createResponse(UserModel.RequiredAction.UPDATE_PROFILE);
            context.challenge(challenge);
            return;
        }

        try {
            KeycloakCNUtils.isVerifyCodeValid(context.getAuthenticationSession(), verifyCode);
        } catch (Exception e){
            Response challenge = context.form()
                    .addError(new FormMessage(e.getMessage()))
                    .setFormData(formData)
                    .createResponse(UserModel.RequiredAction.UPDATE_PROFILE);
            context.challenge(challenge);
            return;
        }


        if (realm.isEditUsernameAllowed()) {
            String username = formData.getFirst("username");
            String oldUsername = user.getUsername();

            boolean usernameChanged = oldUsername != null ? !oldUsername.equals(username) : username != null;

            if (usernameChanged) {

                if (session.users().getUserByUsername(username, realm) != null) {
                    Response challenge = context.form()
                            .setError(Messages.USERNAME_EXISTS)
                            .setFormData(formData)
                            .createResponse(UserModel.RequiredAction.UPDATE_PROFILE);
                    context.challenge(challenge);
                    return;
                }

                user.setUsername(username);
            }

        }

        String phoneNumber = formData.getFirst(ValidationCN.FIELD_PHONE_NUMBER);

        String oldPhoneNumber = user.getFirstAttribute(ValidationCN.FIELD_PHONE_NUMBER);
        boolean phoneNumberChanged = oldPhoneNumber != null ? !oldPhoneNumber.equals(phoneNumber) : phoneNumber != null;
        if (phoneNumberChanged) {
            List<UserModel> userByPhoneNumberList = session.users().searchForUserByUserAttribute(ValidationCN.FIELD_PHONE_NUMBER, phoneNumber, realm);
            if (userByPhoneNumberList!=null && userByPhoneNumberList.size()>0) {
                Response challenge = context.form()
                        .setError("phoneNumberExistsMessage")
                        .setFormData(formData)
                        .createResponse(UserModel.RequiredAction.UPDATE_PROFILE);
                context.challenge(challenge);
                return;
            }
            user.setAttribute(ValidationCN.FIELD_PHONE_NUMBER, Arrays.asList(phoneNumber));
        }

        AttributeFormDataProcessor.process(formData, realm, user);

        context.success();
    }

    @Override
    public int order() {
        return 1;
    }
}
