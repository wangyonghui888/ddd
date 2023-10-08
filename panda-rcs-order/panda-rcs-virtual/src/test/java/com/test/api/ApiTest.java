package com.test.api;

/**
 * @author :  Jesson
 * @Project Name :  swagger-java-client
 * @Package Name :  com.panda.sport.rcs.virtual.third.client.api
 * @Description :  TODO
 * @Date: 2020-09-06 20:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class ApiTest {
    public static void setApiDomain() {
        com.panda.sport.rcs.virtual.third.client.ApiClient defaultClient = com.panda.sport.rcs.virtual.third.client.Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api-int.virtustec.com:8383/api/external/v2");

        com.panda.sport.rcs.virtual.third.client.auth.ApiKeyAuth apiDomain = (com.panda.sport.rcs.virtual.third.client.auth.ApiKeyAuth) defaultClient.getAuthentication("apiDomain");
        apiDomain.setApiKey("pc.jsspanda.com");
        com.panda.sport.rcs.virtual.third.client.auth.ApiKeyAuth apiHash = (com.panda.sport.rcs.virtual.third.client.auth.ApiKeyAuth) defaultClient.getAuthentication("apiHash");
        apiHash.setApiKey("44889c4bdb7750ad116b8b4b85f9a5d6");
        com.panda.sport.rcs.virtual.third.client.auth.ApiKeyAuth apiId = (com.panda.sport.rcs.virtual.third.client.auth.ApiKeyAuth) defaultClient.getAuthentication("apiId");
        apiId.setApiKey("1548");
    }
}
