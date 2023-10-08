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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RightApi {
    private ApiClient apiClient;

    public RightApi() {
        this(Configuration.getDefaultApiClient());
    }

    public RightApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for addProfile
     * @param entityId The entity identifier of the right  (required)
     * @param profiles List of name of profiles entities  (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call addProfileCall(Integer entityId, List<String> profiles, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/right/profile/add";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (entityId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("entityId", entityId));
        if (profiles != null)
        localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("multi", "profiles", profiles));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
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
    private com.squareup.okhttp.Call addProfileValidateBeforeCall(Integer entityId, List<String> profiles, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'entityId' is set
        if (entityId == null) {
            throw new ApiException("Missing the required parameter 'entityId' when calling addProfile(Async)");
        }
        
        // verify the required parameter 'profiles' is set
        if (profiles == null) {
            throw new ApiException("Missing the required parameter 'profiles' when calling addProfile(Async)");
        }
        

        com.squareup.okhttp.Call call = addProfileCall(entityId, profiles, progressListener, progressRequestListener);
        return call;

    }

    /**
     * 
     * Add a new relationship of rights between an entity and a profile 
     * @param entityId The entity identifier of the right  (required)
     * @param profiles List of name of profiles entities  (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void addProfile(Integer entityId, List<String> profiles) throws ApiException {
        addProfileWithHttpInfo(entityId, profiles);
    }

    /**
     * 
     * Add a new relationship of rights between an entity and a profile 
     * @param entityId The entity identifier of the right  (required)
     * @param profiles List of name of profiles entities  (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> addProfileWithHttpInfo(Integer entityId, List<String> profiles) throws ApiException {
        com.squareup.okhttp.Call call = addProfileValidateBeforeCall(entityId, profiles, null, null);
        return apiClient.execute(call);
    }

    /**
     *  (asynchronously)
     * Add a new relationship of rights between an entity and a profile 
     * @param entityId The entity identifier of the right  (required)
     * @param profiles List of name of profiles entities  (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call addProfileAsync(Integer entityId, List<String> profiles, final ApiCallback<Void> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = addProfileValidateBeforeCall(entityId, profiles, progressListener, progressRequestListener);
        apiClient.executeAsync(call, callback);
        return call;
    }
    /**
     * Build call for removeProfile
     * @param entityId The entity identifier of the right  (required)
     * @param profiles List of names of the profile entities  (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call removeProfileCall(Integer entityId, List<String> profiles, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/right/profile/remove";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (entityId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("entityId", entityId));
        if (profiles != null)
        localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("multi", "profiles", profiles));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
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
    private com.squareup.okhttp.Call removeProfileValidateBeforeCall(Integer entityId, List<String> profiles, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'entityId' is set
        if (entityId == null) {
            throw new ApiException("Missing the required parameter 'entityId' when calling removeProfile(Async)");
        }
        
        // verify the required parameter 'profiles' is set
        if (profiles == null) {
            throw new ApiException("Missing the required parameter 'profiles' when calling removeProfile(Async)");
        }
        

        com.squareup.okhttp.Call call = removeProfileCall(entityId, profiles, progressListener, progressRequestListener);
        return call;

    }

    /**
     * 
     * Remove a relationship of rights between an entity and a profile 
     * @param entityId The entity identifier of the right  (required)
     * @param profiles List of names of the profile entities  (required)
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public void removeProfile(Integer entityId, List<String> profiles) throws ApiException {
        removeProfileWithHttpInfo(entityId, profiles);
    }

    /**
     * 
     * Remove a relationship of rights between an entity and a profile 
     * @param entityId The entity identifier of the right  (required)
     * @param profiles List of names of the profile entities  (required)
     * @return ApiResponse&lt;Void&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Void> removeProfileWithHttpInfo(Integer entityId, List<String> profiles) throws ApiException {
        com.squareup.okhttp.Call call = removeProfileValidateBeforeCall(entityId, profiles, null, null);
        return apiClient.execute(call);
    }

    /**
     *  (asynchronously)
     * Remove a relationship of rights between an entity and a profile 
     * @param entityId The entity identifier of the right  (required)
     * @param profiles List of names of the profile entities  (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call removeProfileAsync(Integer entityId, List<String> profiles, final ApiCallback<Void> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = removeProfileValidateBeforeCall(entityId, profiles, progressListener, progressRequestListener);
        apiClient.executeAsync(call, callback);
        return call;
    }
}
