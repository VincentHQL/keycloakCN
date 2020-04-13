package org.vincenthql.keycloak.authentication.authenticators.broker;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.broker.IdpReviewProfileAuthenticatorFactory;
import org.keycloak.models.KeycloakSession;

public class IdpReviewPhoneProfileAuthenticatorFactory extends IdpReviewProfileAuthenticatorFactory {


    public static final String PROVIDER_ID = "idp-review-phone-profile";
    static IdpReviewPhoneProfileAuthenticator SINGLETON = new IdpReviewPhoneProfileAuthenticator();


    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }


    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getReferenceCategory() {
        return "reviewPhoneProfile";
    }

    @Override
    public String getDisplayType() {
        return "Review Phone Profile";
    }

    @Override
    public String getHelpText() {
        return "User reviews and updates profile data retrieved from Identity Provider in the displayed form";
    }

}
