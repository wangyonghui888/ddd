package com.panda.sport.rcs.virtual.service;

import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.virtual.third.client.ApiClient;
import com.panda.sport.rcs.virtual.third.client.Configuration;
import com.panda.sport.rcs.virtual.third.client.auth.ApiKeyAuth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 配置初始化
 * @author lithan
 * @date 2020-12-22 20:29:16
 */
@Component
@Slf4j
public class InitServiceImpl {
    @Value("${rcs.virtual.authBasePath}")
    private String authBasePath;
    @Value("${rcs.virtual.authDomain}")
    private String authDomain;
    @Value("${rcs.virtual.authHash}")
    private String authHash;
    @Value("${rcs.virtual.authId}")
    private String authId;
    @Value("${rcs.virtual.environment:0}")
    private String environment;


    @PostConstruct
    public void init() {
        /*
        com.panda.sport.rcs.virtual.third.client.ApiClient defaultClient = com.panda.sport.rcs.virtual.third.client.Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api-int.virtustec.com:8383/api/external/v2");

        ApiKeyAuth apiDomain = (ApiKeyAuth) defaultClient.getAuthentication("apiDomain");
        apiDomain.setApiKey("pc.jsspanda.com");
        ApiKeyAuth apiHash = (ApiKeyAuth) defaultClient.getAuthentication("apiHash");
        apiHash.setApiKey("44889c4bdb7750ad116b8b4b85f9a5d6");
        ApiKeyAuth apiId = (ApiKeyAuth) defaultClient.getAuthentication("apiId");
        apiId.setApiKey("1548");

        */

        try {
            //由于隔离环境用的是生产下发的数据 测试需要用生产的环境测试验证  这里做个兼容配置 可由nacos控制
            if (StringUtils.isNotBlank(environment) && environment.equals("1")) {
                 authBasePath = "https://virtual-api.mixmoon.net:8383/api/external/v2";
                 authDomain = "shenzentechpanda";
                 authHash = "dbb379a00e5d41fcc0b04aa9f9c278db";
                 authId = "2859";
            }

            ApiClient defaultClient = Configuration.getDefaultApiClient();
            defaultClient.setBasePath(authBasePath);
            ApiKeyAuth apiDomain = (ApiKeyAuth) defaultClient.getAuthentication("apiDomain");
            apiDomain.setApiKey(authDomain);
            ApiKeyAuth apiHash = (ApiKeyAuth) defaultClient.getAuthentication("apiHash");
            apiHash.setApiKey(authHash);
            ApiKeyAuth apiId = (ApiKeyAuth) defaultClient.getAuthentication("apiId");
            apiId.setApiKey(authId);
            log.info("初始化完成:" + environment);
        } catch (Exception e) {
            log.info("初始化异常:{},{}", e.getMessage(), e);
        }
    }
}
