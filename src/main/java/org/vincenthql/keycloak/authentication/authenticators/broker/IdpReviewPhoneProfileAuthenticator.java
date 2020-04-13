package org.vincenthql.keycloak.authentication.authenticators.broker;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.broker.IdpReviewProfileAuthenticator;
import org.keycloak.authentication.authenticators.broker.util.SerializedBrokeredIdentityContext;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.resources.AttributeFormDataProcessor;
import org.keycloak.services.validation.Validation;
import org.vincenthql.keycloak.sms.SmsSenderProvider;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * 补充电话和其他信息
 */
public class IdpReviewPhoneProfileAuthenticator extends IdpReviewProfileAuthenticator {
    private static final Logger logger = Logger.getLogger(IdpReviewPhoneProfileAuthenticator.class);

    private static final String FIELD_PHONE_NUMBER = "user.attributes.phoneNumber";
    private static final String FIELD_VERIFY_CODE = "verifyCode";

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
        List<FormMessage> errors = validateUpdateProfileForm(realm, formData);
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
        // 发送验证码
        if (action != null && action.equals("doSendSmsCode")) {
            boolean isOk = sendSmsCode(formData.getFirst(FIELD_PHONE_NUMBER), context);
            if (isOk) {
                Response challenge = context.form()
                        .addError(new FormMessage("发送验证码成功"))
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
        userCtx.setUsername(formData.getFirst(UserModel.USERNAME));


        AttributeFormDataProcessor.process(formData, realm, userCtx);

        userCtx.saveToAuthenticationSession(context.getAuthenticationSession(), BROKERED_CONTEXT_NOTE);

        logger.debugf("Profile updated successfully after first authentication with identity provider '%s' for broker user '%s'.", brokerContext.getIdpConfig().getAlias(), userCtx.getUsername());

        // Ensure page is always shown when user later returns to it - for example with form "back" button
        context.getAuthenticationSession().setAuthNote(ENFORCE_UPDATE_PROFILE, "true");

        context.success();
    }


    /**
     * 发送短信验证码
     *
     * @param phoneNumber
     * @param context
     * @return
     */
    private boolean sendSmsCode(String phoneNumber, AuthenticationFlowContext context) {
        AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, 6 - 1)));
        SmsSenderProvider smsSenderProvider = context.getSession().getProvider(SmsSenderProvider.class);
        smsSenderProvider.send(phoneNumber, "verify code: " + code);

        return true;
    }

    private void addError(List<FormMessage> errors, String field, String message) {
        errors.add(new FormMessage(field, message));
    }

    private List<FormMessage> validateUpdateProfileForm(RealmModel realm, MultivaluedMap<String, String> formData) {
        List<FormMessage> errors = new ArrayList<>();

        if (!realm.isRegistrationEmailAsUsername() && realm.isEditUsernameAllowed() && Validation.isBlank(formData.getFirst(Validation.FIELD_USERNAME))) {
            addError(errors, Validation.FIELD_USERNAME, Messages.MISSING_USERNAME);
        }

        if (Validation.isBlank(formData.getFirst(FIELD_PHONE_NUMBER))) {
            addError(errors, FIELD_PHONE_NUMBER, "missingPhoneNumberMessage");
        } else if (!isPhoneNumberValid(formData.getFirst(FIELD_PHONE_NUMBER))) {
            addError(errors, FIELD_PHONE_NUMBER, "invalidPhoneNumberMessage");
        }

        return errors;
    }


    private boolean isPhoneNumberValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return false;
        }
        return true;
    }



}
