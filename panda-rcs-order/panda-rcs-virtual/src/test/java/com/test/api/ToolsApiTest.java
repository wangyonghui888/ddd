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
import org.junit.Ignore;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;

import java.util.List;

/**
 * API tests for ToolsApi
 */
@Ignore
public class ToolsApiTest {

    private final com.panda.sport.rcs.virtual.third.client.api.ToolsApi api = new com.panda.sport.rcs.virtual.third.client.api.ToolsApi();

    
    /**
     * 
     *
     * 
     *
     * @throws com.panda.sport.rcs.virtual.third.client.ApiException
     *          if the Api call fails
     */
    @Test
    public void toolCountryFindAllTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<Country> response = api.toolCountryFindAll();

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
    public void toolCurrencyFindTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        List<Currency> response = api.toolCurrencyFind(entityId);

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
    public void toolCurrencyRatesFindTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        OffsetDateTime dateRate = null;
        List<CurrencyInfo> response = api.toolCurrencyRatesFind(dateRate);

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
    public void toolFindTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<String> tools = null;
        Object response = api.toolFind(tools);

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
    public void toolGameTypeFindTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<String> response = api.toolGameTypeFind();

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
    public void toolLanguageFindAllTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<Language> response = api.toolLanguageFindAll();

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
    public void toolReportDomainFindAllTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<String> response = api.toolReportDomainFindAll();

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
    public void toolReportFindAllTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<String> response = api.toolReportFindAll();

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
    public void toolSkinFindAllTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        List<Skin> response = api.toolSkinFindAll();

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
    public void toolTagFindTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        Integer entityId = null;
        List<String> response = api.toolTagFind(entityId);

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
    public void toolTimezoneFindAllTest() throws com.panda.sport.rcs.virtual.third.client.ApiException {
        ApiTest.setApiDomain();
        List<Timezone> response = api.toolTimezoneFindAll();
        System.err.println(response);
        // TODO: test validations
    }
    
}
