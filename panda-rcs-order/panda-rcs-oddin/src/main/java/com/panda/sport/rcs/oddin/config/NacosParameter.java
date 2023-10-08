package com.panda.sport.rcs.oddin.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 获取nacos配置的第二个mq配置
 * @author Z9-conway
 */
@Data
@Component
// 刷新配置
@RefreshScope
public class NacosParameter {

    /**
     * 推送给电竞消息的mq地址
     */
    @Value("${dj.rocketmq.address}")
    private String djRocketmqAddress;

    /**
     * 推送给电竞消息的mq组
     */
    @Value("${dj.rocketmq.group}")
    private String djRocketmqGroup;

    /**
     * oddin grpc服务域名
     */
    @Value("${oddin.grpc.url}")
    private String oddinGrpcUrl;

    /**
     * oddin grpc请求认证的token
     */
    @Value("${oddin.grpc.token}")
    private String oddinGrpcToken;

    /**
     * 体育早盘超时撤单时间单位秒
     */
    @Value("${earlay.cancel.time:4}")
    private Long earlayCancelTime;

    /**
     * 商户打折默认值
     */
    @Value("${tenant.default.discount:1}")
    private String tenantDefaultDiscount;

    /**
     * 校验gprc是否异常断链的判断时间值(毫秒)
     */
    @Value("${validate.grpc.connection.timeout: 30000}")
    private Long validateGrpcConnectionTimeout;
}