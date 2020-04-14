package org.vincenthql.keycloak.authentication.authenticators.broker;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.broker.IdpReviewProfileAuthenticator;
import org.keycloak.authentication.authenticators.broker.util.SerializedBrokeredIdentityContext;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.resources.AttributeFormDataProcessor;
import org.vincenthql.keycloak.utils.KeycloakCNUtils;
import org.vincenthql.keycloak.utils.ValidationCN;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * 补充电话和其他信息
 */
public class IdpReviewPhoneProfileAuthenticator extends IdpReviewProfileAuthenticator {
    private static final Logger logger = Logger.getLogger(IdpReviewPhoneProfileAuthenticator.class);


    @Override
    protected void authenticateImpl(AuthenticationFlowContext context, SerializedBrokeredIdentityContext userCtx, BrokeredIdentityContext brokerContext) {
        IdentityProviderModel idpConfig = brokerContext.getIdpConfig();
        if (requiresUpdateProfilePage(context, userCtx, brokerContext)) {

            logger.debugf("Identity provider '%s' requires update profile action for broker user '%s'.", idpConfig.getAlias(), userCtx.getUsername());

            // No formData for first render. The profile is rendered from userCtx
            Response challengeResponse = context.form()
                    .setAttribute(LoginFormsProvider.UPDATE_PROFILE_CONTEXT_ATTR, userCtx)
                    .setFormData(null)
                    .createUpdateProfilePage();
            context.challenge(challengeResponse);
        } else {
            // Not required to update profile. Marked success
            context.success();
        }
    }


    @Override
    protected void actionImpl(AuthenticationFlowContext context, SerializedBrokeredIdentityContext userCtx, BrokeredIdentityContext brokerContext) {
        EventBuilder event = context.getEvent();
        event.event(EventType.UPDATE_PROFILE);
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        RealmModel realm = context.getRealm();
        List<FormMessage> errors = ValidationCN.validateUpdateProfileCNForm(realm, formData);
        if (errors != null && !errors.isEmpty()) {
            Response challenge = context.form()
                    .setErrors(errors)
                    .setAttribute(LoginFormsProvider.UPDATE_PROFILE_CONTEXT_ATTR, userCtx)
                    .setFormData(formData)
                    .createUpdateProfilePage();
            context.challenge(challenge);
            return;
        }

        String action = formData.getFirst("submitAction");
        String phoneNumber = formData.getFirst(ValidationCN.FIELD_PHONE_NUMBER);
        userCtx.setAttribute(ValidationCN.FIELD_PHONE_NUMBER, Arrays.asList(phoneNumber));

        // 发送验证码
        if (action != null && action.equals("doSendSmsCode")) {
            String code = String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, 6 - 1)));
            if (KeycloakCNUtils.sendSmsCode(context.getSession(), phoneNumber, "您的验证码：" + code)) {
                context.getAuthenticationSession().setAuthNote(KeycloakCNUtils.USR_CRED_MDL_SMS_CODE, code);
                context.getAuthenticationSession().setAuthNote(KeycloakCNUtils.USR_CRED_MDL_SMS_EXP_TIME, String.valueOf(System.currentTimeMillis() + 60000));
                Response challenge = context.form()
                        .addSuccess(new FormMessage("发送验证码成功"))
                        .setAttribute(LoginFormsProvider.UPDATE_PROFILE_CONTEXT_ATTR, userCtx)
                        .setAttribute("sendCode", true)
                        .setFormData(formData)
                        .createUpdateProfilePage();
                context.challenge(challenge);
            } else {
                Response challenge = context.form()
                        .addError(new FormMessage("发送验证码失败"))
                        .setAttribute(LoginFormsProvider.UPDATE_PROFILE_CONTEXT_ATTR, userCtx)
                        .setFormData(formData)
                        .createUpdateProfilePage();
                context.challenge(challenge);
            }

            return;
        }

        String verifyCode = formData.getFirst(ValidationCN.FIELD_VERIFY_CODE);
        //判断短信验证码是否正确
        if (ValidationCN.isBlank(verifyCode)) {
            Response challenge = context.form()
                    .addError(new FormMessage("验证码不能为空"))
                    .setAttribute(LoginFormsProvider.UPDATE_PROFILE_CONTEXT_ATTR, userCtx)
                    .setFormData(formData)
                    .createUpdateProfilePage();
            context.challenge(challenge);
            return;
        }

        try {
            KeycloakCNUtils.isVerifyCodeValid(context.getAuthenticationSession(), verifyCode);
        } catch (Exception e) {
            Response challenge = context.form()
                    .addError(new FormMessage(e.getMessage()))
                    .setAttribute(LoginFormsProvider.UPDATE_PROFILE_CONTEXT_ATTR, userCtx)
                    .setFormData(formData)
                    .createUpdateProfilePage();
            context.challenge(challenge);
            return;
        }

        userCtx.setUsername(formData.getFirst(UserModel.USERNAME));


        AttributeFormDataProcessor.process(formData, realm, userCtx);

        userCtx.saveToAuthenticationSession(context.getAuthenticationSession(), BROKERED_CONTEXT_NOTE);

        logger.debugf("Profile updated successfully after first authentication with identity provider '%s' for broker user '%s'.", brokerContext.getIdpConfig().getAlias(), userCtx.getUsername());

        // Ensure page is always shown when user later returns to it - for example with form "back" button
        context.getAuthenticationSession().setAuthNote(ENFORCE_UPDATE_PROFILE, "true");

        context.success();
    }


}
