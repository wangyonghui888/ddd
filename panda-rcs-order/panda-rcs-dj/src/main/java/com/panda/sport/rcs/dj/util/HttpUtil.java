package com.panda.sport.rcs.dj.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName HttpUtil
 * @Description TODO
 * @Author zerone
 * @Date 2021/9/18 17:12
 * @Version 1.0
 **/
@Slf4j
public class HttpUtil {


      public static String doPost(String url, String path, List<NameValuePair> nvps, Map<String,String> header){
          //创建httpClient对象 =HttpClients.custom().useSystemProperties().build()();
          CloseableHttpClient httpClient =HttpClients.custom().useSystemProperties().build();

          CloseableHttpResponse response = null;
          String resultString = "";
          try {
              URI uri = new URIBuilder().setScheme("https").setHost(url).setPath(path).build();
              //创建http post 请求
              HttpPost httpPost = new HttpPost(uri);
              if (header!=null){
                  for (String key : header.keySet()){
                      httpPost.setHeader(key,header.get(key));
                  }
              }
              httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
              httpPost.setHeader("Accept","application/json");

              UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps,"utf-8");
              httpPost.setEntity(entity);
              log.info("POST请求体:{}", JSON.toJSONString(httpPost));
              response = httpClient.execute(httpPost);
              resultString = EntityUtils.toString(response.getEntity(),"utf-8");
          }catch (Exception e){
              log.error("HTTP请求异常:{}",e.getMessage());
          } finally {
              try {
                  response.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
          return resultString;
      }

    public static String doPostJson(String url, String json,String merchant) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type","application/json");
            httpPost.setHeader("merchant",merchant);
//            httpPost.setHeader(key,value);                            //设置post请求的请求头
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);     //指定传输参数为json
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }


    public static String doPostJsonHeader(String url,Map<String, String> headerMap, String json) {
        log.info("v2电竞查询余额Params:{}", headerMap.toString());
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            if (headerMap != null) {
                Iterator headerIterator = headerMap.entrySet().iterator();          //循环增加header
                while(headerIterator.hasNext()){
                    Map.Entry<String,String> elem = (Map.Entry<String, String>) headerIterator.next();
                    httpPost.addHeader(elem.getKey(),elem.getValue());
                }
            }
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGet(String url, String path, List<NameValuePair> nvps){
        //创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().useSystemProperties().build();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            URI uri = new URIBuilder().setScheme("https").setHost(url).setPath(path).setParameters(nvps).build();
            log.info("GET请求路径:{}",uri.toString());
            //创建http post 请求
            HttpGet httpGet = new HttpGet(uri);

            response = httpClient.execute(httpGet);
            resultString = EntityUtils.toString(response.getEntity(),"utf-8");
        }catch (Exception e){
            log.error("HTTP请求异常",e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static JSONObject toJsonObj(Map<String, String> map) {
        JSONObject resultJson = new JSONObject();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            resultJson.put(key, map.get(key));
        }
        return resultJson;
    }
}
