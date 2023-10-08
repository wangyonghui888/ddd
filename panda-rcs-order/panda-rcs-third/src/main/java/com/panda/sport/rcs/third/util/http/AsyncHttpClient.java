package com.panda.sport.rcs.third.util.http;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * httpClient 初始化
 */
@Slf4j
public class AsyncHttpClient {
    //从池中获取链接超时时间(ms)
    private static final int CONNECTION_REQUEST_TIMEOUT = 10000;
    //建立链接超时时间(ms)
    private static final int CONNECT_TIMEOUT = 10000;
    //读取超时时间(ms)
    private static final int SOCKET_TIMEOUT = 5000;
    //连接数
    private static final int MAX_TOTAL = 5000;
    //单个请求最大连接数
    private static final int MAX_PER_ROUTE = 1000;

    public static final CloseableHttpAsyncClient httpclient;

    public static final CloseableHttpAsyncClient httpSSLClient;
    public static PoolingNHttpClientConnectionManager poolManager;

    static {
        poolManager = initPool();
        httpclient = init();
        httpSSLClient = initSSL();
        assert httpSSLClient != null;
        httpSSLClient.start();
        assert httpclient != null;
        httpclient.start();
    }

    /**
     * 初始化http连接池
     *
     * @return pool
     */
    private static PoolingNHttpClientConnectionManager initPool() {
        try {
            //配置io线程
            IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                    //setIoThreadCount(Runtime.getRuntime().availableProcessors())
                    setIoThreadCount(100)
                    .setSoKeepAlive(true)
                    .build();
            //创建一个ioReactor
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);

            //消息约束
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
                    .setMaxLineLength(2000).build();

            //连接约束
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE) //编码错误处理- 丢弃
                    .setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints).build();

            poolManager = new PoolingNHttpClientConnectionManager(ioReactor);
            //设置连接池大小
            poolManager.setMaxTotal(MAX_TOTAL);
            //设置单个路由最大数
            poolManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
            //设置连接约束
            poolManager.setDefaultConnectionConfig(connectionConfig);
            return poolManager;
        } catch (IOReactorException e) {
            log.error("初始化http连接池异常", e);
            throw new RuntimeException(e);
        }
    }

    private static CloseableHttpAsyncClient init() {
        try {
            // 配置请求的超时设置
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .build();

            return HttpAsyncClients.custom()
                    .setConnectionManager(poolManager)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        } catch (Exception e) {
            log.error("创建CloseableHttpAsyncClient异常", e);
        }
        return null;
    }

    private static CloseableHttpAsyncClient initSSL() {
        try {
            // 配置请求的超时设置
            //ConnectTimeout : 连接超时,连接建立时间,三次握手完成时间。
            //SocketTimeout : 请求超时,数据传输过程中数据包之间间隔的最大时间。
            //ConnectionRequestTimeout : 使用连接池来管理连接,从连接池获取连接的超时时间。
            //ConnTotal:连接池中最大连接数;
            //ConnPerRoute(1000):分配给同一个route(路由)最大的并发连接数,route为运行环境机器到目标机器的一条线路
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .build();

            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            return HttpAsyncClients.custom()
                    .setConnectionManager(poolManager)
                    .setDefaultRequestConfig(requestConfig)
                    .setSSLContext(sslContext)
                    .build();
        } catch (Exception e) {
            log.error("创建CloseableHttpAsyncClient异常", e);
        }
        return null;
    }

}