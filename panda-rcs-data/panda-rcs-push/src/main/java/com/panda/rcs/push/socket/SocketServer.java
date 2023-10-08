package com.panda.rcs.push.socket;

import com.panda.rcs.push.socket.PushSocketHandler;
import com.panda.rcs.push.utils.NettySslUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SocketServer implements CommandLineRunner {

    //socket服务端口
    @Value("${websocket.port:10602}")
    private Integer socketServerPort;

    @Value("${push.ssl.type:JKS}")
    private String sslType;

    @Value("${push.ssl.path:D://opt/wss.jks}")
    private String sslPath;

    @Value("${push.ssl.password:netty123}")
    private String sslPassword;

    @Value("${push.ssl.enable:false}")
    private Boolean sslEnable;

    @Value("${push.main.thead.num:4}")
    private int pushMainTheadNum;

    @Value("${push.work.thead.num:64}")
    private int pushWorkTheadNum;

    @Value("${push.handler.thead.num:64}")
    private int pushHandlerTheadNum;


    public void start() throws Exception {
        //处理客户端连接组
        EventLoopGroup mainGroup = new NioEventLoopGroup(pushMainTheadNum, new DefaultThreadFactory("BOSS_NIO", true));
        //处理具体业务组
        EventLoopGroup wordGroup = new NioEventLoopGroup(pushWorkTheadNum, new DefaultThreadFactory("WORK_NIO", true));

        EventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(pushHandlerTheadNum);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            //绑定线程池
            serverBootstrap.group(mainGroup, wordGroup);
            //指定使用的通道
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(socketServerPort);
            //绑定监听端口
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                //连接创建
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //SSLContext sslContext = NettySslUtils.createSSLContext("JKS", "D://opt/wss.jks", "netty123");
                    if(sslEnable){
                        SSLContext sslContext = NettySslUtils.createSSLContext(sslType, sslPath, sslPassword);
                        SSLEngine engine = sslContext.createSSLEngine();
                        engine.setUseClientMode(false);

                        socketChannel.pipeline().addLast("ssl", new SslHandler(engine));
                    }
                    //websocket协议本身就是基于http协议的，所以这边也要使用http编解码器
                    socketChannel.pipeline().addLast(new HttpServerCodec());
                    //心跳处理
                    socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, 1800, TimeUnit.SECONDS));
                    //以块的方式来写处理器
                    socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                    socketChannel.pipeline().addLast(new HttpObjectAggregator(8192));
                    socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/rcsWebSockets/",null,true,65536 * 10));
                    //绑定具体处理类
                    socketChannel.pipeline().addLast(eventExecutors, new PushSocketHandler());
                }
            });
            //维持活跃连接，清除死链接
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //关闭延迟发送
            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            //服务器异步创建绑定
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            //关闭服务器通道
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e){
            log.error("->初始化连接异常：", e);
        } finally {
            mainGroup.shutdownGracefully().sync();
            wordGroup.shutdownGracefully().sync();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        this.start();
    }

}
