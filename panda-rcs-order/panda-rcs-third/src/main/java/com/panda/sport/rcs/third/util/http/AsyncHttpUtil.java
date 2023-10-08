package com.panda.sport.rcs.third.util.http;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.exeception.RcsServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author beulah
 * http 异步请求工具
 */
@Slf4j
public class AsyncHttpUtil {

    private static CloseableHttpAsyncClient httpclient = AsyncHttpClient.httpclient;
    private static CloseableHttpAsyncClient httpSSLClient = AsyncHttpClient.httpSSLClient;

    public static String get(String url, boolean ssl, FutureCallback<HttpResponse> callback) {
        HttpGet method = new HttpGet(url);
        URIBuilder uri = new URIBuilder();
        CloseableHttpAsyncClient client = null;
        if (ssl) {
            client = httpSSLClient;
        } else {
            client = httpclient;
        }
        try {
            client.execute(method, callback);
        } catch (Exception e) {
            log.error("[发送get请求失败]URL:{},异常:", uri.getUserInfo(), e);
        }
        return null;
    }

    /**
     * post json提交
     *
     * @param url        请求地址
     * @param jsonParams 参数
     * @param ssl        是否https
     * @param headMap    请求头
     * @param callback   回调实现
     */
    public static void postJson(String url, String jsonParams, boolean ssl, Map<String, String> headMap, FutureCallback<HttpResponse> callback) {
        HttpPost method = new HttpPost(url);
        method.addHeader("Accept", "*/*");
        method.addHeader("Content-Type", "application/json; charset=UTF-8");
        headMap.forEach(method::addHeader);
        method.setEntity(new StringEntity(jsonParams, "UTF-8"));
        CloseableHttpAsyncClient client = null;
        if (ssl) {
            client = httpSSLClient;
        } else {
            client = httpclient;
        }
        try {
            client.execute(method, callback);
        } catch (Exception e) {
            log.error("HTTP请求异常, url={}, request={}", url, JSONObject.toJSONString(jsonParams), e);
            throw new RcsServiceException(5031, "http Post异常");
        }
    }

    /**
     * post 表单提交
     *
     * @param url      请求地址
     * @param param    参数
     * @param ssl      是否https
     * @param callback 回调实现
     */
    public static void postMap(String url, Map<String, String> param, boolean ssl, FutureCallback<HttpResponse> callback) {
        CloseableHttpAsyncClient httpclient = null;
        if (ssl) {
            httpclient = AsyncHttpClient.httpSSLClient;
        } else {
            httpclient = AsyncHttpClient.httpclient;
        }
        HttpPost method = new HttpPost(url);
        method.addHeader("Accept", "*/*");
        method.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        try {
            // 遍历param
            if (param != null) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for (Entry<String, String> entry : param.entrySet()) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
                method.setEntity(uefEntity);
            }
            httpclient.execute(method, callback);
        } catch (Exception e) {
            log.error("HTTP请求异常, url={}, request={}", url, JSONObject.toJSONString(param), e);
            throw new RcsServiceException(5031, "HTTP POST异常");
        }
    }


    /**
     * post json提交
     *
     * @param url      请求地址
     * @param param    map参数
     * @param ssl      是否https
     * @param callback 回调实现
     */
    public static void postJsonByMap(String url, Map<String, Object> param, boolean ssl, Map<String, String> headMap, FutureCallback<HttpResponse> callback) {
        CloseableHttpAsyncClient httpclient = null;
        if (ssl) {
            httpclient = AsyncHttpClient.httpSSLClient;
        } else {
            httpclient = AsyncHttpClient.httpclient;
        }
        HttpPost method = new HttpPost(url);
        method.addHeader("Accept", "*/*");
        method.addHeader("Content-Type", "application/json; charset=UTF-8");
        headMap.forEach(method::addHeader);
        try {
            if (param != null) {
                StringEntity stringEntity = new StringEntity(JSONObject.toJSONString(param), "UTF-8");
                method.setEntity(stringEntity);
            }
            httpclient.execute(method, callback);
        } catch (Exception e) {
            log.error("HTTP请求异常, url={}, request={}", url, JSONObject.toJSONString(param), e);
            throw new RcsServiceException(5031, "http Post异常");
        }
    }

}
