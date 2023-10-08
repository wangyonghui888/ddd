package com.panda.sport.rcs.third.util.http;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

/**
 * http 链接
 * @author ethan
 *
 */
@Slf4j
public class HttpConfig {
    /**
     * 请求连接
     */
    private static CloseableHttpClient closeableHttpClient;
    /**
     * 请求配置参数
     */
    private static RequestConfig requestConfig;
    private HttpConfig() {
        initHttpClient();
    };
    /**
     * 用内部类构造器保证线程安全
     *
     * @author gavin
     *
     */
    private static class HttpLoadHolder {
        private static HttpConfig instance = new HttpConfig();
    }

    public static HttpConfig getInstance() {
        return HttpLoadHolder.instance;
    }

    /**
     * 创建httpclient连接池
     */
    private PoolingHttpClientConnectionManager httpClientConnectionManager = null;
    private static IdleConnectionMonitorThread scanThread = null;
    /**
     * 获取连接超时配置
     * @return
     */
    private static RequestConfig getConfig() {
        if (requestConfig!=null) {
            return requestConfig;
        }
        // 创建全局的requestConfig
        requestConfig = RequestConfig.custom().setConnectTimeout(1000) //创建连接超时
                .setSocketTimeout(2000)  //接收数据超时
                .setConnectionRequestTimeout(2000) //请求排队时间超时
                .setCookieSpec(CookieSpecs.DEFAULT).build();
        return requestConfig;
    }
    public void initHttpClient() {

        //消息约束
        MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
                .setMaxLineLength(2000).build();

        //连接约束
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE) //编码错误处理- 丢弃
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints).build();

        // 创建httpclient连接池
        httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 设置连接池最大数量
        httpClientConnectionManager.setMaxTotal(10000);
        // 设置单个路由最大连接数量
        httpClientConnectionManager.setDefaultMaxPerRoute(2000);

        httpClientConnectionManager.setDefaultConnectionConfig(connectionConfig);

        // 扫描无效链接
        scanThread = new IdleConnectionMonitorThread(httpClientConnectionManager);

        scanThread.start();
    }

    /**
     * 请求重试机制
     */
    HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            int times = 3;
            if (executionCount >= times) {
                // 超过三次则不再重试请求
                log.warn("HTTP获取链接-重试3次未成功");
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // Timeout
                log.warn("HTTP获取链接-超时");
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                log.warn("HTTP获取链接-未知HOST");
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {
                // Connection refused
                log.warn("HTTP获取链接-拒绝");
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                log.warn("HTTP获取SSL链接-握手失败");
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        }
    };
    public CloseableHttpClient getHttpClient() {
        if (closeableHttpClient!=null) {
            return closeableHttpClient;
        }else{
            RequestConfig requestConfig = getConfig();
            // 声明重定向策略对象
            LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

            closeableHttpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager)
                    .setDefaultRequestConfig(requestConfig).setRedirectStrategy(redirectStrategy)
                    .setRetryHandler(myRetryHandler).setMaxConnPerRoute(20).setMaxConnTotal(60).build();

            return closeableHttpClient;
        }
    }



    /**
     * 创建一个默认的httpsclient
     *
     * @return
     */
    public CloseableHttpClient createSSLClientDefault() {
        try {
            if (closeableHttpClient!=null) {
                return closeableHttpClient;
            }else{
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    // 信任所有
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                }).build();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                closeableHttpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager).setDefaultRequestConfig(getConfig()).setSSLSocketFactory(sslsf).setMaxConnPerRoute(10).setMaxConnTotal(20).build();
            }
            return closeableHttpClient;
        } catch (KeyManagementException e) {
            log.error("创建httpsclient失败", e);

        } catch (NoSuchAlgorithmException e) {
            log.error("创建httpsclient失败", e);
        } catch (KeyStoreException e) {
            log.error("创建httpsclient失败", e);
        }

        return HttpClients.createDefault();
    }

}
