package com.panda.sport.rcs.third.util.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.panda.sport.rcs.exeception.RcsServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author ethan
 */
@Slf4j
public class HttpUtil {

    static int successCode = 200;

    /**
     * 普通的http get 请求
     */
    public static String get(String url, Map<String, String> headerMap) throws Exception {

        CloseableHttpClient httpclient = HttpLoad.getHttp();
        HttpGet method = new HttpGet(url);
        method.addHeader("Accept", "text/html,*/*");
        method.addHeader("Content-Type", "text/html;charset=utf-8");
        if (headerMap != null) {
            Iterator<String> kiter = headerMap.keySet().iterator();
            while (kiter.hasNext()) {
                String ki = kiter.next();
                method.addHeader(ki, headerMap.get(ki));
            }
        }
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = httpclient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == successCode) {
                entity = response.getEntity();
                String src = EntityUtils.toString(entity, "UTF-8");
                //EntityUtils.consume(entity);
                return src;
            } else {
                log.warn("::HTTP请求失败::url={}, response code={}, response date={}", url, statusCode, JSONObject.toJSONString(response));
                httpExceptionHandler(statusCode, url);
            }
        } catch (Exception e) {
            log.error("::HTTP请求异常::url={}", url, e);
            throw new RcsServiceException(5031, "http Get异常");
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("response.close异常=" + e.getMessage());
                    // TODO Auto-generated catch block
                }
            }
            method.abort();
        }
        return null;

    }

    public static String post(String url, byte[] bytes) throws Exception {
        CloseableHttpClient httpclient = HttpLoad.getHttp();
        HttpPost method = new HttpPost(url);
        method.addHeader("Accept", "*/*");
        method.addHeader("Content-Type", "charset=UTF-8");
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            // 遍历param
            if (bytes != null) {
                ByteArrayEntity byteentity = new ByteArrayEntity(bytes);
                method.setEntity(byteentity);
            }
            response = httpclient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == successCode) {
                entity = response.getEntity();
                String src = EntityUtils.toString(entity, "UTF-8");
                //EntityUtils.consume(entity);
                return src;
            } else {
                log.warn("::HTTP请求失败::url={}, response code={}, response date={}", url, statusCode, JSONObject.toJSONString(response));
                httpExceptionHandler(statusCode, url);
            }
        } catch (Exception e) {
            log.error("::HTTP请求异常::url={}", url, e);
            throw new RcsServiceException(5031, "http Post异常");
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            method.abort();
        }
        return null;
    }

    /**
     * v4.3 模拟POST方式提交(json参数)
     *
     * @param url
     * @param jsonParams
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @author 2018年5月21日
     */
    public static String post(String url, String jsonParams, Map<String, String> headMap) throws ClientProtocolException, IOException {
        // CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = HttpLoad.getHttp();
        HttpPost method = new HttpPost(url);
        method.setHeader("Content-Type", "application/json");
        headMap.forEach(method::setHeader);
        method.setEntity(new StringEntity(jsonParams, "UTF-8"));
        String result = null;
        CloseableHttpResponse res = null;
        try {
            res = httpClient.execute(method);
            int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
            } else {
                HttpEntity entity = res.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
                log.warn("::HTTP请求失败::url={},request={}, response code={}, response date={}", url, JSONObject.toJSONString(jsonParams), res.getStatusLine().getStatusCode(), JSONObject.toJSONString(result));
                httpExceptionHandler(statusCode, url);
            }
        } catch (Exception e) {
            log.error("HTTP请求异常, url={}, request={}", url, JSONObject.toJSONString(jsonParams), e);
            if (e instanceof HttpHostConnectException) {
                //请求异常 记录
                throw new RcsServiceException(5031, "http Post异常");
            }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            method.abort();
        }
        return result;
    }

    public static String post(String url, Map<String, String> param, boolean ssl) throws Exception {
        CloseableHttpClient httpclient = null;
        if (ssl) {
            httpclient = HttpLoad.getSslHttp();
        } else {
            httpclient = HttpLoad.getHttp();
        }
        HttpPost method = new HttpPost(url);
        method.addHeader("Accept", "*/*");
        method.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            // 遍历param
            if (param != null) {
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                Iterator<Entry<String, String>> iter = param.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, String> entry = iter.next();
                    formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
                method.setEntity(uefEntity);
            }
            response = httpclient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == successCode) {
                entity = response.getEntity();
                String src = EntityUtils.toString(entity, "UTF-8");
                //EntityUtils.consume(entity);
                return src;
            } else {
                log.warn("::HTTP请求失败::url={}, request={}, response code={}, response date={}", url, JSONObject.toJSONString(param), statusCode, JSONObject.toJSONString(response));
                httpExceptionHandler(statusCode, url);
            }
        } catch (Exception e) {
            log.error("HTTP请求异常, url={}, request={}", url, JSONObject.toJSONString(param), e);
            throw new RcsServiceException(5031, "HTTP POST异常");
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    log.error(" response.close异常,url=" + url + "参数=" + JSONObject.toJSONString(param));
                }
            }
            method.abort();
        }
        return null;
    }

    public static String post(String url, Map<String, Object> param, boolean ssl, Map<String, String> headMap) throws Exception {
        CloseableHttpClient httpclient = null;
        if (ssl) {
            httpclient = HttpLoad.getSslHttp();
        } else {
            httpclient = HttpLoad.getHttp();
        }
        HttpPost method = new HttpPost(url);
        method.addHeader("Accept", "*/*");
        method.addHeader("Content-Type", "application/json; charset=UTF-8");
        headMap.forEach(method::addHeader);
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            // 遍历param
            if (param != null) {
               /* List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                Iterator<Entry<String, String>> iter = param.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, String> entry = iter.next();
                    formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");*/
                StringEntity stringEntity = new StringEntity(JSONObject.toJSONString(param), "UTF-8");
                method.setEntity(stringEntity);
            }
            response = httpclient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == successCode) {
                entity = response.getEntity();
                String src = EntityUtils.toString(entity, "UTF-8");
                //EntityUtils.consume(entity);
                return src;
            } else {
                log.warn("::HTTP请求失败::url={}, request={}, response code={}, response date={}", url, JSONObject.toJSONString(param), statusCode, JSONObject.toJSONString(response));
                httpExceptionHandler(statusCode, url);
            }
        } catch (Exception e) {
            log.error("HTTP请求异常, url={}, request={}", url, JSONObject.toJSONString(param), e);
            throw new RcsServiceException(5031, "http Post异常");
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(" response.close异常,url=" + url + "参数=" + JSONObject.toJSONString(param));
                }
            }
            method.abort();
        }
        return null;
    }


    private static void httpExceptionHandler(int code, String urlString) throws Exception {
        switch (code) {
            case 400:
                throw new Exception("下载400错误代码,请求出现语法错误,url=" + urlString);
            case 403:
                throw new Exception("下载403错误代码,资源不可用,url=" + urlString);
            case 404:
                throw new Exception("下载404错误代码,无法找到指定资源地址,url=" + urlString);
            case 500:
                throw new Exception("下载500错误代码,服务器内部错误,url=" + urlString);
            case 503:
                throw new Exception("下载503错误代码,服务不可用,url=" + urlString);
            case 504:
                throw new Exception("下载504错误代码,网关超时,url=" + urlString);
            default:
                throw new Exception("请求超时错误代码：" + code + ",url=" + urlString);
        }
    }

}
