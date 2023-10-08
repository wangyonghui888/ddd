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


package com.panda.sport.rcs.virtual.third.client.api;

import com.panda.sport.rcs.virtual.third.client.ApiCallback;
import com.panda.sport.rcs.virtual.third.client.ApiClient;
import com.panda.sport.rcs.virtual.third.client.ApiException;
import com.panda.sport.rcs.virtual.third.client.ApiResponse;
import com.panda.sport.rcs.virtual.third.client.Configuration;
import com.panda.sport.rcs.virtual.third.client.Pair;
import com.panda.sport.rcs.virtual.third.client.ProgressRequestBody;
import com.panda.sport.rcs.virtual.third.client.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import com.panda.sport.rcs.virtual.third.client.model.ErrorInfo;
import com.panda.sport.rcs.virtual.third.client.model.ExternalAuthResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionApi {
    private ApiClient apiClient;

    public SessionApi() {
        this(Configuration.getDefaultApiClient());
    }

    public SessionApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for sessionExternalClientLogin
     * @param accountId Id of unit entity, identifes **account**, used to own credit, store wallet transactions and own tickets. (required)
     * @param userId Id of staff entity, identifies **user**, that perform operations during session, to a given unit/acount. (required)
     * @param sessionContext Extensible json object.  This object stores all information from GoldenRace external systems  for this user session.  Example.  External token session/or ids to be included on screen/ticket.  (optional)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call sessionExternalClientLoginCall(Integer accountId, Integer userId, Object sessionContext, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = sessionContext;

        // create path and map variables
        String localVarPath = "/session/login";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (accountId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("accountId", accountId));
        if (userId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("userId", userId));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] { "apiDomain", "apiHash", "apiId" };
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call sessionExternalClientLoginValidateBeforeCall(Integer accountId, Integer userId, Object sessionContext, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'accountId' is set
        if (accountId == null) {
            throw new ApiException("Missing the required parameter 'accountId' when calling sessionExternalClientLogin(Async)");
        }
        
        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException("Missing the required parameter 'userId' when calling sessionExternalClientLogin(Async)");
        }
        

        com.squareup.okhttp.Call call = sessionExternalClientLoginCall(accountId, userId, sessionContext, progressListener, progressRequestListener);
        return call;

    }

    /**
     * 
     * Generates authentication by external API to account/user entity client to be able to operate with tickets. - **Unit entity**, related with accountId.  Account used to store products configuration, wallet operations and ticket ownership. - **Staff entity**, related with userId. Identifies logged user that perform operations over credit and tickets. In the scenario of single user accounts, it is a common scenario to use a single entity as userId and accountId, providing duplicated value on both input parammeters.  Allows binding of an external **sessionContext** by providing a JSON object on body request.  This information, will be stored on every ticket created during this session, and could be used as extensible information for external integrations and product customization extensions as well.  **WARNING** Generated session token can be used only once, and would deprecate 300 secs after login. 
     * @param accountId Id of unit entity, identifes **account**, used to own credit, store wallet transactions and own tickets. (required)
     * @param userId Id of staff entity, identifies **user**, that perform operations during session, to a given unit/acount. (required)
     * @param sessionContext Extensible json object.  This object stores all information from GoldenRace external systems  for this user session.  Example.  External token session/or ids to be included on screen/ticket.  (optional)
     * @return ExternalAuthResult
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ExternalAuthResult sessionExternalClientLogin(Integer accountId, Integer userId, Object sessionContext) throws ApiException {
        ApiResponse<ExternalAuthResult> resp = sessionExternalClientLoginWithHttpInfo(accountId, userId, sessionContext);
        return resp.getData();
    }

    /**
     * 
     * Generates authentication by external API to account/user entity client to be able to operate with tickets. - **Unit entity**, related with accountId.  Account used to store products configuration, wallet operations and ticket ownership. - **Staff entity**, related with userId. Identifies logged user that perform operations over credit and tickets. In the scenario of single user accounts, it is a common scenario to use a single entity as userId and accountId, providing duplicated value on both input parammeters.  Allows binding of an external **sessionContext** by providing a JSON object on body request.  This information, will be stored on every ticket created during this session, and could be used as extensible information for external integrations and product customization extensions as well.  **WARNING** Generated session token can be used only once, and would deprecate 300 secs after login. 
     * @param accountId Id of unit entity, identifes **account**, used to own credit, store wallet transactions and own tickets. (required)
     * @param userId Id of staff entity, identifies **user**, that perform operations during session, to a given unit/acount. (required)
     * @param sessionContext Extensible json object.  This object stores all information from GoldenRace external systems  for this user session.  Example.  External token session/or ids to be included on screen/ticket.  (optional)
     * @return ApiResponse&lt;ExternalAuthResult&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<ExternalAuthResult> sessionExternalClientLoginWithHttpInfo(Integer accountId, Integer userId, Object sessionContext) throws ApiException {
        com.squareup.okhttp.Call call = sessionExternalClientLoginValidateBeforeCall(accountId, userId, sessionContext, null, null);
        Type localVarReturnType = new TypeToken<ExternalAuthResult>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Generates authentication by external API to account/user entity client to be able to operate with tickets. - **Unit entity**, related with accountId.  Account used to store products configuration, wallet operations and ticket ownership. - **Staff entity**, related with userId. Identifies logged user that perform operations over credit and tickets. In the scenario of single user accounts, it is a common scenario to use a single entity as userId and accountId, providing duplicated value on both input parammeters.  Allows binding of an external **sessionContext** by providing a JSON object on body request.  This information, will be stored on every ticket created during this session, and could be used as extensible information for external integrations and product customization extensions as well.  **WARNING** Generated session token can be used only once, and would deprecate 300 secs after login. 
     * @param accountId Id of unit entity, identifes **account**, used to own credit, store wallet transactions and own tickets. (required)
     * @param userId Id of staff entity, identifies **user**, that perform operations during session, to a given unit/acount. (required)
     * @param sessionContext Extensible json object.  This object stores all information from GoldenRace external systems  for this user session.  Example.  External token session/or ids to be included on screen/ticket.  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call sessionExternalClientLoginAsync(Integer accountId, Integer userId, Object sessionContext, final ApiCallback<ExternalAuthResult> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = sessionExternalClientLoginValidateBeforeCall(accountId, userId, sessionContext, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<ExternalAuthResult>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
