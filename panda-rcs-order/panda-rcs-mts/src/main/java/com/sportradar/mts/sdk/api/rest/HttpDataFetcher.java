package com.sportradar.mts.sdk.api.rest;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mts.sportradar.mtshttp.HttpLoad;
import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;

class HttpDataFetcher {
    private final SdkConfiguration config;
    private final CloseableHttpClient httpClient;
    private int statusCode;
    private static final Logger logger = LoggerFactory.getLogger(HttpDataFetcher.class);

    HttpDataFetcher(SdkConfiguration config, CloseableHttpClient httpClient) {
        this.config = config;
//    this.httpClient = httpClient;
        this.httpClient = HttpLoad.getInstance().getHttpClient();
    }

    public String get(String path) {
        return get(null, path);
    }

    public String get(AccessToken token, String path) {
        return send(token, new HttpGet(path));
    }

    public String post(HttpEntity content, String path) {
        return post(null, content, path);
    }

    public String post(AccessToken token, HttpEntity content, String path) {
        HttpPost httpPost = new HttpPost(path);
        httpPost.setEntity(content);
        return send(token, httpPost);
    }

    protected String send(AccessToken token, HttpUriRequest request) {
        String path = request.getURI().toString();
        try {
            if (this.config != null) {
                String xAccessToken = this.config.getAccessToken();
                if (!StringUtils.isNullOrEmpty(xAccessToken)) {
                    request.addHeader("x-access-token", xAccessToken);
                }
            }
            if (token != null && !StringUtils.isNullOrEmpty(token.getAccessToken())) {
                request.addHeader("Authorization", "Bearer " + token.getAccessToken());
            }
            ResponseHandler<String> handler = resp -> {
                this.statusCode = resp.getStatusLine().getStatusCode();

                boolean isWhoAmI = path.endsWith("whoami.xml");
                if (this.statusCode == 200 || (isWhoAmI && this.statusCode == 403)) {
                    return EntityUtils.toString(resp.getEntity());
                }
                logger.warn("Non OK API response: " + resp.getStatusLine() + " " + this.statusCode + " " + path);

                Map<String, Object> result = new HashMap<>();
                result.put("code", this.statusCode);

                return JSONObject.toJSONString(result);
            };
            StopWatch sw = new StopWatch();
            sw.start();
            String resp = (String) this.httpClient.execute(request, handler);
            sw.stop();
            logger.info("httpClient请求耗时：" + sw.getTotalTimeMillis());
            if (!resp.equals("")) {
                return resp;
            }
        } catch (Exception e) {
            logger.warn("Problems reading: " + path + " " + e.getMessage(), e);
        }
        return "";
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
