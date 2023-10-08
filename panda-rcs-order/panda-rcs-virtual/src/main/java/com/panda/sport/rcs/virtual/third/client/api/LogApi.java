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
import com.panda.sport.rcs.virtual.third.client.model.Log;
import org.threeten.bp.OffsetDateTime;
import com.panda.sport.rcs.virtual.third.client.model.SessionLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogApi {
    private ApiClient apiClient;

    public LogApi() {
        this(Configuration.getDefaultApiClient());
    }

    public LogApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for logFind
     * @param entityId The id of the entity.  (required)
     * @param startTime Interval start time from which the search will be performed.  (required)
     * @param n Number of elements to return from query. If 0, get all elements.  (required)
     * @param first First element of query to be returned, for paging purposes. If 0, start from first element.  (required)
     * @param orderBy Define order ASC or DESC  (required)
     * @param endTime Interval end time to which the search will be performed. Use n or endTime  (optional)
     * @param type The type of operation. Could be EVENT, MANAGER, CLIENT, EXTERNAL or SYSTEM  (optional)
     * @param method The method operation of Log.  (optional)
     * @param responseStatusCodes The response code of operation.  (optional)
     * @param ips An array of ips of operations.  (optional)
     * @param description Description on the log operation.  (optional)
     * @param withChildren If true, find wallets for the entity and his children. If false, find wallets only for the entity. (optional, default to true)
     * @param advancedInfo Advanced log info (resultData). (optional, default to false)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call logFindCall(Integer entityId, OffsetDateTime startTime, Integer n, Integer first, String orderBy, OffsetDateTime endTime, String type, String method, List<Integer> responseStatusCodes, List<String> ips, String description, Boolean withChildren, Boolean advancedInfo, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/log/find";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (entityId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("entityId", entityId));
        if (startTime != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("startTime", startTime));
        if (endTime != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("endTime", endTime));
        if (type != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("type", type));
        if (method != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("method", method));
        if (responseStatusCodes != null)
        localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("multi", "responseStatusCodes", responseStatusCodes));
        if (ips != null)
        localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("multi", "ips", ips));
        if (description != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("description", description));
        if (n != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("n", n));
        if (first != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("first", first));
        if (orderBy != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("orderBy", orderBy));
        if (withChildren != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("withChildren", withChildren));
        if (advancedInfo != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("advancedInfo", advancedInfo));

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
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call logFindValidateBeforeCall(Integer entityId, OffsetDateTime startTime, Integer n, Integer first, String orderBy, OffsetDateTime endTime, String type, String method, List<Integer> responseStatusCodes, List<String> ips, String description, Boolean withChildren, Boolean advancedInfo, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'entityId' is set
        if (entityId == null) {
            throw new ApiException("Missing the required parameter 'entityId' when calling logFind(Async)");
        }
        
        // verify the required parameter 'startTime' is set
        if (startTime == null) {
            throw new ApiException("Missing the required parameter 'startTime' when calling logFind(Async)");
        }
        
        // verify the required parameter 'n' is set
        if (n == null) {
            throw new ApiException("Missing the required parameter 'n' when calling logFind(Async)");
        }
        
        // verify the required parameter 'first' is set
        if (first == null) {
            throw new ApiException("Missing the required parameter 'first' when calling logFind(Async)");
        }
        
        // verify the required parameter 'orderBy' is set
        if (orderBy == null) {
            throw new ApiException("Missing the required parameter 'orderBy' when calling logFind(Async)");
        }
        

        com.squareup.okhttp.Call call = logFindCall(entityId, startTime, n, first, orderBy, endTime, type, method, responseStatusCodes, ips, description, withChildren, advancedInfo, progressListener, progressRequestListener);
        return call;

    }

    /**
     * 
     * Produces a history of logs 
     * @param entityId The id of the entity.  (required)
     * @param startTime Interval start time from which the search will be performed.  (required)
     * @param n Number of elements to return from query. If 0, get all elements.  (required)
     * @param first First element of query to be returned, for paging purposes. If 0, start from first element.  (required)
     * @param orderBy Define order ASC or DESC  (required)
     * @param endTime Interval end time to which the search will be performed. Use n or endTime  (optional)
     * @param type The type of operation. Could be EVENT, MANAGER, CLIENT, EXTERNAL or SYSTEM  (optional)
     * @param method The method operation of Log.  (optional)
     * @param responseStatusCodes The response code of operation.  (optional)
     * @param ips An array of ips of operations.  (optional)
     * @param description Description on the log operation.  (optional)
     * @param withChildren If true, find wallets for the entity and his children. If false, find wallets only for the entity. (optional, default to true)
     * @param advancedInfo Advanced log info (resultData). (optional, default to false)
     * @return List&lt;Log&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Log> logFind(Integer entityId, OffsetDateTime startTime, Integer n, Integer first, String orderBy, OffsetDateTime endTime, String type, String method, List<Integer> responseStatusCodes, List<String> ips, String description, Boolean withChildren, Boolean advancedInfo) throws ApiException {
        ApiResponse<List<Log>> resp = logFindWithHttpInfo(entityId, startTime, n, first, orderBy, endTime, type, method, responseStatusCodes, ips, description, withChildren, advancedInfo);
        return resp.getData();
    }

    /**
     * 
     * Produces a history of logs 
     * @param entityId The id of the entity.  (required)
     * @param startTime Interval start time from which the search will be performed.  (required)
     * @param n Number of elements to return from query. If 0, get all elements.  (required)
     * @param first First element of query to be returned, for paging purposes. If 0, start from first element.  (required)
     * @param orderBy Define order ASC or DESC  (required)
     * @param endTime Interval end time to which the search will be performed. Use n or endTime  (optional)
     * @param type The type of operation. Could be EVENT, MANAGER, CLIENT, EXTERNAL or SYSTEM  (optional)
     * @param method The method operation of Log.  (optional)
     * @param responseStatusCodes The response code of operation.  (optional)
     * @param ips An array of ips of operations.  (optional)
     * @param description Description on the log operation.  (optional)
     * @param withChildren If true, find wallets for the entity and his children. If false, find wallets only for the entity. (optional, default to true)
     * @param advancedInfo Advanced log info (resultData). (optional, default to false)
     * @return ApiResponse&lt;List&lt;Log&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Log>> logFindWithHttpInfo(Integer entityId, OffsetDateTime startTime, Integer n, Integer first, String orderBy, OffsetDateTime endTime, String type, String method, List<Integer> responseStatusCodes, List<String> ips, String description, Boolean withChildren, Boolean advancedInfo) throws ApiException {
        com.squareup.okhttp.Call call = logFindValidateBeforeCall(entityId, startTime, n, first, orderBy, endTime, type, method, responseStatusCodes, ips, description, withChildren, advancedInfo, null, null);
        Type localVarReturnType = new TypeToken<List<Log>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Produces a history of logs 
     * @param entityId The id of the entity.  (required)
     * @param startTime Interval start time from which the search will be performed.  (required)
     * @param n Number of elements to return from query. If 0, get all elements.  (required)
     * @param first First element of query to be returned, for paging purposes. If 0, start from first element.  (required)
     * @param orderBy Define order ASC or DESC  (required)
     * @param endTime Interval end time to which the search will be performed. Use n or endTime  (optional)
     * @param type The type of operation. Could be EVENT, MANAGER, CLIENT, EXTERNAL or SYSTEM  (optional)
     * @param method The method operation of Log.  (optional)
     * @param responseStatusCodes The response code of operation.  (optional)
     * @param ips An array of ips of operations.  (optional)
     * @param description Description on the log operation.  (optional)
     * @param withChildren If true, find wallets for the entity and his children. If false, find wallets only for the entity. (optional, default to true)
     * @param advancedInfo Advanced log info (resultData). (optional, default to false)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call logFindAsync(Integer entityId, OffsetDateTime startTime, Integer n, Integer first, String orderBy, OffsetDateTime endTime, String type, String method, List<Integer> responseStatusCodes, List<String> ips, String description, Boolean withChildren, Boolean advancedInfo, final ApiCallback<List<Log>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = logFindValidateBeforeCall(entityId, startTime, n, first, orderBy, endTime, type, method, responseStatusCodes, ips, description, withChildren, advancedInfo, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Log>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for logFindById
     * @param entityId The id of the entity.  (required)
     * @param transactionId The id of the transaction related to log.  (required)
     * @param withChildren If true, find wallets for the entity and his children. If false, find wallets only for the entity. (optional, default to true)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call logFindByIdCall(Integer entityId, String transactionId, Boolean withChildren, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/log/findById";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (entityId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("entityId", entityId));
        if (transactionId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("transactionId", transactionId));
        if (withChildren != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("withChildren", withChildren));

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
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call logFindByIdValidateBeforeCall(Integer entityId, String transactionId, Boolean withChildren, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'entityId' is set
        if (entityId == null) {
            throw new ApiException("Missing the required parameter 'entityId' when calling logFindById(Async)");
        }
        
        // verify the required parameter 'transactionId' is set
        if (transactionId == null) {
            throw new ApiException("Missing the required parameter 'transactionId' when calling logFindById(Async)");
        }
        

        com.squareup.okhttp.Call call = logFindByIdCall(entityId, transactionId, withChildren, progressListener, progressRequestListener);
        return call;

    }

    /**
     * 
     * Return a list of logs by transactionId 
     * @param entityId The id of the entity.  (required)
     * @param transactionId The id of the transaction related to log.  (required)
     * @param withChildren If true, find wallets for the entity and his children. If false, find wallets only for the entity. (optional, default to true)
     * @return List&lt;Log&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<Log> logFindById(Integer entityId, String transactionId, Boolean withChildren) throws ApiException {
        ApiResponse<List<Log>> resp = logFindByIdWithHttpInfo(entityId, transactionId, withChildren);
        return resp.getData();
    }

    /**
     * 
     * Return a list of logs by transactionId 
     * @param entityId The id of the entity.  (required)
     * @param transactionId The id of the transaction related to log.  (required)
     * @param withChildren If true, find wallets for the entity and his children. If false, find wallets only for the entity. (optional, default to true)
     * @return ApiResponse&lt;List&lt;Log&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<Log>> logFindByIdWithHttpInfo(Integer entityId, String transactionId, Boolean withChildren) throws ApiException {
        com.squareup.okhttp.Call call = logFindByIdValidateBeforeCall(entityId, transactionId, withChildren, null, null);
        Type localVarReturnType = new TypeToken<List<Log>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Return a list of logs by transactionId 
     * @param entityId The id of the entity.  (required)
     * @param transactionId The id of the transaction related to log.  (required)
     * @param withChildren If true, find wallets for the entity and his children. If false, find wallets only for the entity. (optional, default to true)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call logFindByIdAsync(Integer entityId, String transactionId, Boolean withChildren, final ApiCallback<List<Log>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = logFindByIdValidateBeforeCall(entityId, transactionId, withChildren, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<Log>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for logSessionFind
     * @param entityId The id of the entity.  (required)
     * @param startTime Interval start time from which the search will be performed.  (required)
     * @param n Number of elements to return from query. If 0, get all elements.  (required)
     * @param orderBy Define order ASC or DESC  (required)
     * @param endTime Interval end time to which the search will be performed. Use n or endTime  (optional)
     * @param withChildren If true, find session logs for the entity and his children. If false, find session logs only for the entity. (optional, default to true)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call logSessionFindCall(Integer entityId, OffsetDateTime startTime, Integer n, String orderBy, OffsetDateTime endTime, Boolean withChildren, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/log/session/find";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (entityId != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("entityId", entityId));
        if (startTime != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("startTime", startTime));
        if (endTime != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("endTime", endTime));
        if (n != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("n", n));
        if (orderBy != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("orderBy", orderBy));
        if (withChildren != null)
        localVarQueryParams.addAll(apiClient.parameterToPair("withChildren", withChildren));

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
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call logSessionFindValidateBeforeCall(Integer entityId, OffsetDateTime startTime, Integer n, String orderBy, OffsetDateTime endTime, Boolean withChildren, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'entityId' is set
        if (entityId == null) {
            throw new ApiException("Missing the required parameter 'entityId' when calling logSessionFind(Async)");
        }
        
        // verify the required parameter 'startTime' is set
        if (startTime == null) {
            throw new ApiException("Missing the required parameter 'startTime' when calling logSessionFind(Async)");
        }
        
        // verify the required parameter 'n' is set
        if (n == null) {
            throw new ApiException("Missing the required parameter 'n' when calling logSessionFind(Async)");
        }
        
        // verify the required parameter 'orderBy' is set
        if (orderBy == null) {
            throw new ApiException("Missing the required parameter 'orderBy' when calling logSessionFind(Async)");
        }
        

        com.squareup.okhttp.Call call = logSessionFindCall(entityId, startTime, n, orderBy, endTime, withChildren, progressListener, progressRequestListener);
        return call;

    }

    /**
     * 
     * Retrieve the session logs of an entity and its children 
     * @param entityId The id of the entity.  (required)
     * @param startTime Interval start time from which the search will be performed.  (required)
     * @param n Number of elements to return from query. If 0, get all elements.  (required)
     * @param orderBy Define order ASC or DESC  (required)
     * @param endTime Interval end time to which the search will be performed. Use n or endTime  (optional)
     * @param withChildren If true, find session logs for the entity and his children. If false, find session logs only for the entity. (optional, default to true)
     * @return List&lt;SessionLog&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<SessionLog> logSessionFind(Integer entityId, OffsetDateTime startTime, Integer n, String orderBy, OffsetDateTime endTime, Boolean withChildren) throws ApiException {
        ApiResponse<List<SessionLog>> resp = logSessionFindWithHttpInfo(entityId, startTime, n, orderBy, endTime, withChildren);
        return resp.getData();
    }

    /**
     * 
     * Retrieve the session logs of an entity and its children 
     * @param entityId The id of the entity.  (required)
     * @param startTime Interval start time from which the search will be performed.  (required)
     * @param n Number of elements to return from query. If 0, get all elements.  (required)
     * @param orderBy Define order ASC or DESC  (required)
     * @param endTime Interval end time to which the search will be performed. Use n or endTime  (optional)
     * @param withChildren If true, find session logs for the entity and his children. If false, find session logs only for the entity. (optional, default to true)
     * @return ApiResponse&lt;List&lt;SessionLog&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<SessionLog>> logSessionFindWithHttpInfo(Integer entityId, OffsetDateTime startTime, Integer n, String orderBy, OffsetDateTime endTime, Boolean withChildren) throws ApiException {
        com.squareup.okhttp.Call call = logSessionFindValidateBeforeCall(entityId, startTime, n, orderBy, endTime, withChildren, null, null);
        Type localVarReturnType = new TypeToken<List<SessionLog>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Retrieve the session logs of an entity and its children 
     * @param entityId The id of the entity.  (required)
     * @param startTime Interval start time from which the search will be performed.  (required)
     * @param n Number of elements to return from query. If 0, get all elements.  (required)
     * @param orderBy Define order ASC or DESC  (required)
     * @param endTime Interval end time to which the search will be performed. Use n or endTime  (optional)
     * @param withChildren If true, find session logs for the entity and his children. If false, find session logs only for the entity. (optional, default to true)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call logSessionFindAsync(Integer entityId, OffsetDateTime startTime, Integer n, String orderBy, OffsetDateTime endTime, Boolean withChildren, final ApiCallback<List<SessionLog>> callback) throws ApiException {

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

        com.squareup.okhttp.Call call = logSessionFindValidateBeforeCall(entityId, startTime, n, orderBy, endTime, withChildren, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<SessionLog>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}