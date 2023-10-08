package com.panda.sport.rcs.oddin.config;

import com.panda.sport.rcs.oddin.interceptor.AuthenticationInterceptor;
import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;

/**
 * @Author :wiker
 * @Date: 2023-07 15:49
 * oddIn第三方请求连接工具类 -- 创建通道连接
 **/
@Slf4j
@Configuration
public class OddInGrpcConfig {
    @Resource
    private NacosParameter nacosParameter;

    @Bean
    public ManagedChannel grpcChannel() {
        log.info("创建连接通道开始... url={}, accessToken={}", nacosParameter.getOddinGrpcUrl(), nacosParameter.getOddinGrpcToken());
        //建立连接
        ChannelCredentials credentials = TlsChannelCredentials.newBuilder().build();
        ManagedChannel channel = Grpc.newChannelBuilder(nacosParameter.getOddinGrpcUrl(), credentials)
                .intercept(new AuthenticationInterceptor(nacosParameter.getOddinGrpcToken()))
                .build();
        log.info("创建连接通道结束... channel={}", channel);
        return channel;
    }

}
