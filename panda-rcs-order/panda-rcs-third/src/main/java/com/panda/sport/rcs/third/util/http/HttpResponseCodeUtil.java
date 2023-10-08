package com.panda.sport.rcs.third.util.http;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.exeception.RcsServiceException;
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
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.panda.sport.rcs.third.enums.RcsThirdExceptionEnum.*;

/**
 * http请求以后返回对应错误码
 *
 * @author vere
 * @version 1.0.0
 * @date 2023-05-28
 */
public class HttpResponseCodeUtil {
    private static Log log = LogFactory.getLog(HttpResponseCodeUtil.class);
    static final int SUCCESS_CODE = 200;



    /**
     * 普通的http get 请求
     */
    public static String get(String url, Map<String, String> headerMap) {

        CloseableHttpClient httpclient =HttpConfig.getInstance().getHttpClient();
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
            response.getAllHeaders();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == SUCCESS_CODE) {
                entity = response.getEntity();
                String src = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                return src;
            } else {
                //正常响应
                httpExceptionHandler(statusCode, url);
            }
        } catch (RcsServiceException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            handlerExceptionMessage(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("网络IO异常", e);
                }
            }
            method.abort();
        }
        return null;

    }

    public static String post(String url, byte[] bytes) throws Exception {
        CloseableHttpClient httpclient = HttpConfig.getInstance().getHttpClient();
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
            if (statusCode == SUCCESS_CODE) {
                entity = response.getEntity();
                String src = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                return src;
            } else {
                httpExceptionHandler(statusCode, url);
            }
        } catch (Exception e) {
            handlerExceptionMessage(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("网络IO异常", e);
                }
            }
            method.abort();
        }
        return null;
    }

    /**
     * v4.3 模拟POST方式提交(json参数)
     *
     * @param url 请求地址
     * @param jsonParams 请求参数
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @author 2018年5月21日
     */
    public static String post(String url, String jsonParams, Map<String, String> headMap) throws ClientProtocolException, IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        headMap.forEach((k, v) -> {httpPost.setHeader(k, v);});
        httpPost.setEntity(new StringEntity(jsonParams, "UTF-8"));
        String result = null;
        try {
            HttpResponse res = httpClient.execute(httpPost);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
            } else {
                HttpEntity entity = res.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
                throw new RcsServiceException("http Post异常:code:" + res.getStatusLine().getStatusCode() + "data:" + result);
            }
        } catch (Exception e) {
            handlerExceptionMessage(e);
        } finally {
            httpClient.close();
        }
        return result;
    }

    public static String post(String url, Map<String, String> param, boolean ssl) throws Exception {
        CloseableHttpClient httpclient = null;
        if (ssl) {
            httpclient = HttpConfig.getInstance().createSSLClientDefault();
        } else {
            httpclient = HttpConfig.getInstance().getHttpClient();
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
                Iterator<Map.Entry<String, String>> iter = param.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
                method.setEntity(uefEntity);
            }
            response = httpclient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == SUCCESS_CODE) {
                entity = response.getEntity();
                String src = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                return src;
            } else {
                httpExceptionHandler(statusCode, url);
            }
        } catch (Exception e) {
            handlerExceptionMessage(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("网络IO异常", e);
                }
            }
            method.abort();
        }
        return null;
    }

    public static String post(String url, Map<String, Object> param, boolean ssl, Map<String, String> headMap) {
        CloseableHttpClient httpclient = ssl?HttpConfig.getInstance().createSSLClientDefault():HttpConfig.getInstance().getHttpClient();
        HttpPost method = new HttpPost(url);
        method.addHeader("Accept", "*/*");
        method.addHeader("Content-Type", "application/json; charset=UTF-8");
        headMap.forEach((k, v) -> {method.addHeader(k, v);});
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            // 遍历param
            if (param != null) {
                StringEntity stringEntity = new StringEntity(JSONObject.toJSONString(param), "UTF-8");
                method.setEntity(stringEntity);
            }
            response = httpclient.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == SUCCESS_CODE) {
                entity = response.getEntity();
                String src = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                return src;
            } else {
                httpExceptionHandler(statusCode, url);
            }
        } catch (RcsServiceException e) {
            //自定义异常处理
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            handlerExceptionMessage(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("网络IO异常", e);
                }
            }
            method.abort();
        }
        return null;
    }

    /**
     * 异常信息处理
     * @param e
     */
    private static void handlerExceptionMessage(Exception e){
        log.error("请求http异常",e);
        if (e.getMessage().contains("connect timed out")) {
            log.error("进入连接超时逻辑");
            //网络连接超时，无法连接
            throw new RcsServiceException(HTTP_GATEWAY_TIME_OUT.getCode(), e.getMessage());
        }else if(e.getMessage().contains("Read timed out")){
            log.error("进入读取超时逻辑");
            //网络连接上，但是读取超时
            throw new RcsServiceException(HTTP_READ_TIME_OUT.getCode(), HTTP_READ_TIME_OUT.getMessage());
        }
        throw new RcsServiceException(HTTP_OTHER_ERROR.getCode(), e.getMessage());

    }

    /**
     * 异常处理类
     *
     * @param code 错误码
     * @param urlString 请求地址
     * @throws Exception
     */
    private static void httpExceptionHandler(int code, String urlString) throws Exception {
        switch (code) {
            case 400:
                throw new RcsServiceException(HTTP_SYNTAX_ERROR.getCode(), HTTP_SYNTAX_ERROR.getMessage() + urlString);
            case 401:
                throw new RcsServiceException(HTTP_UN_AUTHORIZATION.getCode(), HTTP_UN_AUTHORIZATION.getMessage() + urlString);
            case 403:
                throw new RcsServiceException(HTTP_ACCESS_DENIED.getCode(), HTTP_ACCESS_DENIED.getMessage() + urlString);
            case 404:
                throw new RcsServiceException(HTTP_NOT_FOUND.getCode(), HTTP_NOT_FOUND.getMessage() + urlString);
            case 503:
                throw new RcsServiceException(HTTP_SERVER_NOT_FOUND.getCode(), HTTP_SERVER_NOT_FOUND.getMessage() + urlString);
            case 504:
                throw new RcsServiceException(HTTP_GATEWAY_TIME_OUT.getCode(), HTTP_GATEWAY_TIME_OUT.getMessage() + urlString);
            default:
                throw new RcsServiceException(code, "其他异常" + urlString);
        }
    }
}
