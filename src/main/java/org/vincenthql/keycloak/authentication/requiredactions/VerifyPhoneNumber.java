package org.vincenthql.keycloak.authentication.requiredactions;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.DisplayTypeRequiredActionFactory;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * 验证手机号
 */
public class VerifyPhoneNumber implements RequiredActionProvider, RequiredActionFactory, DisplayTypeRequiredActionFactory {
    private static final Logger logger = Logger.getLogger(VerifyPhoneNumber.class);

    public static final String PROVIDER_ID = "verify-phone-number";

    @Override
    public RequiredActionProvider createDisplay(KeycloakSession session, String displayType) {
        return null;
    }

    @Override
    public String getDisplayText() {
        return "Verify Phone Number";
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {

    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {

    }

    @Override
    public void processAction(RequiredActionContext context) {

    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return null;
    }

    @Override
    public void init(Config.Scope config) {

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
