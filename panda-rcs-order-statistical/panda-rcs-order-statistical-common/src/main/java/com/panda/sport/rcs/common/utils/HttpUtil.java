package com.panda.sport.rcs.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
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
import java.util.Map.Entry;

/**
 * @author lithan
 */
public class HttpUtil {

	private static Log log = LogFactory.getLog(HttpUtil.class);
	static int successCode=200;

	public static String doGet(String url) {

		log.info("HttpUtil doGet start url:" + url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(30000)
				.setConnectTimeout(30000).setSocketTimeout(30000).build();

		CloseableHttpClient httpClient = HttpClients.custom().useSystemProperties()
				.setDefaultRequestConfig(requestConfig)
				.build();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet(url);
			response = httpClient.execute(httpget);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == successCode) {
				String str = EntityUtils.toString(response.getEntity(), "UTF-8");
				return str;
			} else {
				httpExceptionHandler(statusCode, url);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		} finally {
			try {
				httpClient.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return null;
	}
	/**
	 * 普通的http get 请求
	 */
	public static String get(String url, Map<String, String> headerMap) throws Exception {

		log.info("HttpUtil get start url:" + url + ", headerMap:" + JSONObject.toJSONString(headerMap));
		CloseableHttpClient httpclient = HttpLoad.getInstance().getHttpClient();
		HttpGet method = new HttpGet(url);
		method.addHeader("Accept", "text/html,*/*");
		method.addHeader("Content-Type", "text/html;charset=utf-8");
		//method.addHeader("Accept", "text/html,*/*");
		method.addHeader("x-forwarded-for",headerMap.get("ip"));
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
				EntityUtils.consume(entity);
				return src;
			} else {
				httpExceptionHandler(statusCode, url);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw e;
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			method.abort();
		}
		return null;

	}

	public static String post(String url, byte[] bytes) throws Exception {
		CloseableHttpClient httpclient = HttpLoad.getInstance().getHttpClient();
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
				EntityUtils.consume(entity);
				return src;
			} else {
				httpExceptionHandler(statusCode, url);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
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
	 * @author  2018年5月21日
	 */
	public static String post(String url, String jsonParams, String appId) throws ClientProtocolException, IOException {

		log.info("HttpUtil post start url:" + url + ", jsonParams:" + jsonParams + ", appId=" + appId);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setEntity(new StringEntity(jsonParams, "UTF-8"));
		if (StringUtils.isNotEmpty(appId)) {
			httpPost.addHeader("appId", appId);
		}
		String result = null;
		try {
			HttpResponse res = httpClient.execute(httpPost);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				result = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);

		} finally {
			// 关闭连接，释放资源
			httpClient.close();
		}
		return result;
	}

	public static String post(String url, Map<String, String> param, boolean ssl, String appId) throws Exception {

		log.info("HttpUtil post start url:" + url + ", param:" + JSONObject.toJSONString(param) + ", ssl=" + ssl + ", appId=" + appId);
		CloseableHttpClient httpclient = null;
		if (ssl) {
			httpclient = HttpLoad.createSSLClientDefault();
		} else {
			httpclient = HttpLoad.getInstance().getHttpClient();
		}
		HttpLoad.getInstance().getHttpClient();
		HttpPost method = new HttpPost(url);
		method.addHeader("Accept", "*/*");
		method.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		if (StringUtils.isNotEmpty(appId)) {
			method.addHeader("appId", appId);
		}
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
				EntityUtils.consume(entity);
				return src;
			} else {
				httpExceptionHandler(statusCode, url);
			}
		} catch (Exception e) {
			log.error("http请求异常:"+url+ JSONObject.toJSONString(param)+e.getMessage());
			throw e;
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			method.abort();
		}
		return null;
	}

	private static void httpExceptionHandler(int code, String urlString) throws Exception {
		switch (code) {
		case 400:
			throw new Exception("下载400错误代码，请求出现语法错误" + urlString);
		case 403:
			throw new Exception("下载403错误代码，资源不可用" + urlString);
		case 404:
			throw new Exception("下载404错误代码，无法找到指定资源地址" + urlString);
		case 503:
			throw new Exception("下载503错误代码，服务不可用" + urlString);
		case 504:
			throw new Exception("下载504错误代码，网关超时" + urlString);
		default:
			throw new Exception("请求超时错误代码："+code + urlString);
		}
	}

}
