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
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.MDC;

/**
 * http 链接
 *
 * @author ethan
 */
@Slf4j
public class HttpLoad {

    private HttpLoad() {
        scanCloseHttpStart();
    }

    /**
     * 用内部类构造器保证线程安全
     *
     * @author gavin
     */
    private static class HttpLoadHolder {
        private static HttpLoad instance = new HttpLoad();
    }

    public static HttpLoad getInstance() {
        return HttpLoadHolder.instance;
    }

    /**
     * 创建httpclient连接池
     */
    private static PoolingHttpClientConnectionManager httpClientConnectionManager = null;
    private static final CloseableHttpClient http;
    private static final CloseableHttpClient sslHttp;


    public static PoolingHttpClientConnectionManager getHttpClientConnectionManager() {
        return httpClientConnectionManager;
    }

    public static CloseableHttpClient getHttp() {
        return http;
    }

    public static CloseableHttpClient getSslHttp() {
        return sslHttp;
    }

    /**
     * 长连接机制
     */
    private static final ConnectionKeepAliveStrategy keepAliveStrategy  = new ConnectionKeepAliveStrategy() {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 60 * 1000;//如果没有约定，则默认定义时长为60s
        }
    };
    /**
     * 请求重试机制
     */
    private static final HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            final HttpClientContext clientContext = HttpClientContext.adapt(context);
            final HttpRequest request = clientContext.getRequest();
            String orderNo = MDC.get("orderNo");
            //鉴于AWS的机制 设置为重试2次
            if (executionCount > 2) {
                log.warn("::{}::=======》重试超过2次不在重试《=======", orderNo);
                //转换标识 为未成功
                MDC.put("retryFailed", orderNo);
                return false;
            }
            if (exception instanceof InterruptedIOException
                    || exception instanceof NoHttpResponseException) {
                // Timeout or 服务端断开连接
                log.error("::{}::=======》响应超时或者服务端断开,触发重试:{},{}《=======", orderNo, executionCount, JSONObject.toJSONString(request));
                return false;
            }
            // Unknown host
            if (exception instanceof UnknownHostException) {
                log.error("::{}::=======》host解析失败《=======", orderNo);
                return false;
            }
            // SSL handshake exception
            if (exception instanceof SSLException) {
                log.error("::{}::=======》SSL handshake exception《=======", orderNo);
                return false;
            }
            if (exception instanceof HttpHostConnectException) {
                log.error("::{}::=======》Connection refused: connect 触发重试:{},{}《=======", orderNo, executionCount, JSONObject.toJSONString(request));
                return true;
            }

            boolean endpoint = (request instanceof HttpEntityEnclosingRequest);
            if (endpoint) {
                log.warn("::{}::=======》幂等请求,不需重试《======={}", orderNo, JSONObject.toJSONString(request));
            }
            // 网络波动，引起重复请求 无需重试
            return !endpoint;
        }

    };

    /**
     * 创建全局的requestConfig
     */
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000) //创建连接超时
            .setSocketTimeout(20000)  //接收数据超时
            .setConnectionRequestTimeout(5000) //从连接池获取连接超时
            .setCookieSpec(CookieSpecs.DEFAULT).build();

    //连接约束
    private static final ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE) //编码错误处理- 丢弃
            .setCharset(Consts.UTF_8)
            .setMessageConstraints(MessageConstraints.custom().setMaxHeaderCount(200)
                    .setMaxLineLength(2000).build()).build();


    static {
        // 创建httpclient连接池 (参数设置长连接有效期)
        httpClientConnectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        // 设置连接池最大数量
        httpClientConnectionManager.setMaxTotal(3600);
        // 设置单个路由最大连接数量(500/s次请求，接口响应平均在3s的情况下，会开到1200个请求)
        httpClientConnectionManager.setDefaultMaxPerRoute(1200);
        httpClientConnectionManager.setDefaultConnectionConfig(connectionConfig);
        //创建http协议client
        http = createHttpClient();
        //创建https协议client
        sslHttp = createSSLClientDefault();
    }


    /**
     * 创建一个http连接
     */
    private static CloseableHttpClient createHttpClient() {
        return HttpClients.custom().setConnectionManager(httpClientConnectionManager)
                //.setKeepAliveStrategy(keepAliveStrategy)
                .setDefaultRequestConfig(requestConfig).setRedirectStrategy(new LaxRedirectStrategy())
                .setRetryHandler(retryHandler).build();
    }

    /**
     * 创建一个默认的ssl连接
     */
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            return HttpClients.custom()
                    .setConnectionManager(httpClientConnectionManager)
                    .setDefaultRequestConfig(requestConfig)
                    //.setKeepAliveStrategy(keepAliveStrategy)
                    .setRetryHandler(retryHandler)
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext)).build();

        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("create ssl failed");
        }

        return HttpClients.createDefault();
    }


    /**
     * 开启线程扫描无效连接
     */
    public void scanCloseHttpStart() {
        new IdleConnectionMonitorThread(httpClientConnectionManager).start();
    }

}

class IdleConnectionMonitorThread extends Thread {
    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    /**
     * 每3s扫描一次连接池 释放无效连接
     */
    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(1000);
                    // 关闭无效连接
                    connMgr.closeExpiredConnections();
                    // 可选, 关闭空闲超过30秒的
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

}
