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

import com.panda.sport.rcs.virtual.third.client.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * API tests for EntityApi
 */
//@Ignore
public class EntityApiTest {

    private final com.panda.sport.rcs.virtual.third.client.api.EntityApi api = new com.panda.sport.rcs.virtual.third.client.api.EntityApi();

    
    /**
     * 
     *
     * Creation of a new entity, whose name (entityName), its external id (extId),  the external information (extData), the id of the reference entity on which we want to create the new entity (parentId),  boolean for is client (client) and the status of the entity. - the entity parent id (*entityParentId*)  - the entity name (*entityName*),  - the external identifier (*extId*) - a JSON object that is used as Information repository on body request (*extData*) - If it&#39;s client (client). When true, entity is entitled as an invoicing entity. This affects to accountability in statistics and reports for every single ticket created on every child entity. - the status of the entity (enabled, test or disabled). 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void entityAddTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();

        Integer entityParentId = 4018;
        String entityName = "lithan666";
        String status = "ENABLED";
        String extId = "lithan666";
        String extData = "";
        Boolean client = true;
        List<String> profiles = Arrays.asList(new String[] {"External"});
        try {
            com.panda.sport.rcs.virtual.third.client.model.Entity response = api.entityAdd(entityParentId, entityName, status, extId, extData, client, profiles);
            System.err.println(response);
        }catch (com.panda.sport.rcs.virtual.third.client.ApiException e){
            System.err.println(e.getResponseBody());
            System.out.println(1);
        }
        // TODO: test validations
    }
    
    /**
     * 
     *
     * Creation of a new entity message. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void entityAddMessageTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        com.panda.sport.rcs.virtual.third.client.model.EntityBodyMessage body = null;
        api.entityAddMessage(entityId, body);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void entityEditTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        String status = null;
        String entityName = null;
        Boolean client = null;
        String extData = null;
        api.entityEdit(entityId, status, entityName, client, extData);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Finds an entity by unique global Id, under restriction of being under root entity Id. The combined use of the entityId and extId parameters will not be allowed. The search for the extId parameter will be performed in combination with the parentId parameter. In case of this parameter does not have a defined value, the identifier value of the request entity will be used
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void entityFindByIdTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        String extId = null;
        Integer parentId = null;
        com.panda.sport.rcs.virtual.third.client.model.Entity response = api.entityFindById(entityId, extId, parentId);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void entityFindPathTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityParentId = null;
        String regExp = null;
        String extId = null;
        Integer entityId = null;
        List<com.panda.sport.rcs.virtual.third.client.model.TreeItem> response = api.entityFindPath(entityParentId, regExp, extId, entityId);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void entityGetChildrenTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        Integer n = null;
        Integer first = null;
        com.panda.sport.rcs.virtual.third.client.model.TreeInfo response = api.entityGetChildren(entityId, n, first);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void entityRemoveTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        api.entityRemove(entityId);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Return the related CalculationContext of a calculationId. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void getCalculationContextByIdTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();
        Integer calculationId = 2202;
        com.panda.sport.rcs.virtual.third.client.model.CalculationContext response = api.getCalculationContextById(calculationId);
        System.err.println(response);
        // TODO: test validations
    }
    
    /**
     * 
     *
     * Return an actual calculationId from an entity as the entity is logging at the moment of the request. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void getCalculationIdByEntityIdTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();
        Integer entityId = 4835;
        String extId = "lithan666";//"test01";
        try {
            Integer response = api.getCalculationIdByEntityId(entityId, extId);
            System.err.println(response);
        }catch (com.panda.sport.rcs.virtual.third.client.ApiException api){
            System.err.println(api);
            System.err.println(api.getResponseBody());
        }
        // TODO: test validations
    }
    
    /**
     * 
     *
     * Get Context about Entity. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void getContextTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<Integer> entitiesId = null;
        String context = null;
        List<com.panda.sport.rcs.virtual.third.client.model.SettingValue> response = api.getContext(entitiesId, context);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Get all existing Right Profiles in the system. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void getProfilesTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();

        List<String> response = api.getProfiles();
        System.err.println(response);
        // TODO: test validations
    }
    
    /**
     * 
     *
     * Return an actual sessionSettingId. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void getSessionSettingsIdTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Object sessionSettings = null;
        Integer response = api.getSessionSettingsId(sessionSettings);

        // TODO: test validations
    }
    
    /**
     * 
     *
     * Set Context Setting about Entity. 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void setContextTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();

        List<Integer> entitiesId = Arrays.asList(new Integer[]{3936});
        com.panda.sport.rcs.virtual.third.client.model.LocalizationContext contexts = new com.panda.sport.rcs.virtual.third.client.model.LocalizationContext().defaultCurrency("RMB");
        CONtext coNtext = new CONtext(contexts);
        api.setContext(entitiesId, coNtext);

        // TODO: test validations
    }
    
}
