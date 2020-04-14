package org.vincenthql.keycloak.utils;

import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

public class ValidationCN extends Validation {


    public static final String FIELD_PHONE_NUMBER = "phoneNumber";
    public static final String FIELD_VERIFY_CODE = "verifyCode";


    public static void addError(List<FormMessage> errors, String field, String message){
        errors.add(new FormMessage(field, message));
    }

    /**
     * 验证 自定义的修改用户信息验证
     * @param realm
     * @param formData
     * @return
     */
    public static List<FormMessage> validateUpdateProfileCNForm(RealmModel realm, MultivaluedMap<String, String> formData) {
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

    /**
     * 判断是否是有效的手机号，这里只判断位数
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return false;
        }
        return true;
    }



}
