/*
 * GoldenRace External API
 * Definitions of External API for GoldenRace Java Server 
 *
 * OpenAPI spec version: 7.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.test.api;

import org.junit.Ignore;
import org.junit.Test;

/**
 * API tests for SessionApi
 */
@Ignore
public class SessionApiTest {

    private final com.panda.sport.rcs.virtual.third.client.api.SessionApi api = new com.panda.sport.rcs.virtual.third.client.api.SessionApi();

    
    /**
     * 
     *
     * Generates authentication by external API to account/user entity client to be able to operate with tickets. - **Unit entity**, related with accountId.  Account used to store products configuration, wallet operations and ticket ownership. - **Staff entity**, related with userId. Identifies logged user that perform operations over credit and tickets. In the scenario of single user accounts, it is a common scenario to use a single entity as userId and accountId, providing duplicated value on both input parammeters.  Allows binding of an external **sessionContext** by providing a JSON object on body request.  This information, will be stored on every ticket created during this session, and could be used as extensible information for external integrations and product customization extensions as well.  **WARNING** Generated session token can be used only once, and would deprecate 300 secs after login. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void sessionExternalClientLoginTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();

        Integer accountId = 1548;
        Integer userId = 1548;
        Object sessionContext = null;
        //new SessionApi().sessionExternalClientLoginCall();
        try {
            com.panda.sport.rcs.virtual.third.client.model.ExternalAuthResult response = api.sessionExternalClientLogin(accountId, userId, sessionContext);
            System.err.println(response);
        } catch (com.panda.sport.rcs.virtual.third.client.ApiException api){
            System.err.println(api);
        }
        // TODO: test validations
    }
    
}