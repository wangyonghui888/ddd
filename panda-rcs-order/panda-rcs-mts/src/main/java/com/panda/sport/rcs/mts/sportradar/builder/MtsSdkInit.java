package com.panda.sport.rcs.mts.sportradar.builder;

import java.lang.reflect.Field;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mts.sportradar.config.SdkInitConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.mts.sportradar.constants.Constants;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketAckHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketCancelAckHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketCancelResponseHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketResponseHandler;
import com.sportradar.mts.sdk.api.interfaces.MtsSdkApi;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.interfaces.TicketAckSender;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelAckSender;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
import com.sportradar.mts.sdk.api.interfaces.TicketSender;
import com.sportradar.mts.sdk.api.settings.SdkConfigurationImpl;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MtsSdkInit {

    static TicketBuilderHelper ticketBuilderHelper;
    static TicketSender ticketSender;
    static MtsSdkApi mtsSdk;

    @Autowired
    RedisClient redisClient;
    @Autowired
    SdkInitConfig sdkInitConfig;

    public static TicketBuilderHelper getTicketBuilderHelper() {
        return ticketBuilderHelper;
    }

    public static TicketSender getTicketSender() {
        return ticketSender;
    }

    public static MtsSdkApi getMtsSdk() {
        return mtsSdk;
    }

    @PostConstruct
    public void init() {
        SdkConfiguration config = RcsMtsSdkApi.getConfiguration();
        Constants.setConfig(config);
        try {
            SdkConfigurationImpl temp = (SdkConfigurationImpl) config;
            Field field = SdkConfigurationImpl.class.getDeclaredField("node");
            field.setAccessible(true);
            field.set(temp, getNode());
            Field field1 = SdkConfigurationImpl.class.getDeclaredField("username");
            field1.setAccessible(true);
            field1.set(temp, sdkInitConfig.getUsername());
            Field field2 = SdkConfigurationImpl.class.getDeclaredField("password");
            field2.setAccessible(true);
            field2.set(temp, sdkInitConfig.getPassword());
            Field field3 = SdkConfigurationImpl.class.getDeclaredField("host");
            field3.setAccessible(true);
            field3.set(temp, sdkInitConfig.getHostname());
            Field field4 = SdkConfigurationImpl.class.getDeclaredField("vhost");
            field4.setAccessible(true);
            field4.set(temp, sdkInitConfig.getVhost());
            Field field5 = SdkConfigurationImpl.class.getDeclaredField("bookmakerId");
            field5.setAccessible(true);
            field5.set(temp, Integer.valueOf(sdkInitConfig.getBookmakerId()));
            Field field6 = SdkConfigurationImpl.class.getDeclaredField("limitId");
            field6.setAccessible(true);
            field6.set(temp, Integer.valueOf(sdkInitConfig.getLimitId()));
            Field field7 = SdkConfigurationImpl.class.getDeclaredField("keycloakHost");
            field7.setAccessible(true);
            field7.set(temp, sdkInitConfig.getKeycloakHost());
            Field field8 = SdkConfigurationImpl.class.getDeclaredField("keycloakUsername");
            field8.setAccessible(true);
            field8.set(temp, sdkInitConfig.getKeycloakUsername());
            Field field9 = SdkConfigurationImpl.class.getDeclaredField("keycloakPassword");
            field9.setAccessible(true);
            field9.set(temp, sdkInitConfig.getKeycloakPassword());
            Field field10 = SdkConfigurationImpl.class.getDeclaredField("keycloakSecret");
            field10.setAccessible(true);
            field10.set(temp, sdkInitConfig.getKeycloakSecret());
            Field field11 = SdkConfigurationImpl.class.getDeclaredField("mtsClientApiHost");
            field11.setAccessible(true);
            field11.set(temp, sdkInitConfig.getMtsClientApiHost());

            log.info("初始化配置：{}", JSONObject.toJSONString(config));
            log.info("初始化分配节点：{}", config.getNode());
            mtsSdk = new RcsMtsSdkApi(config);
            mtsSdk.open();
            TicketAckSender ticketAckSender = mtsSdk.getTicketAckSender(new TicketAckHandler());
            TicketCancelAckSender ticketCancelAckSender = mtsSdk.getTicketCancelAckSender(new TicketCancelAckHandler());
            TicketCancelSender ticketCancelSender = mtsSdk.getTicketCancelSender(new TicketCancelResponseHandler(ticketCancelAckSender, mtsSdk.getBuilderFactory()));
            TicketResponseHandler ticketResponseHandler = new TicketResponseHandler(ticketCancelSender, ticketAckSender, mtsSdk.getBuilderFactory());
            ticketSender = mtsSdk.getTicketSender(ticketResponseHandler);
            ticketBuilderHelper = new TicketBuilderHelper(mtsSdk.getBuilderFactory());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setValueInfo(Object sdkConfi, Object initConfig) throws Exception {
        Field[] field = sdkConfi.getClass().getDeclaredFields();
        Field[] field2 = initConfig.getClass().getDeclaredFields();
        //属性对象
        for (Field initField : field2) {
            //要设置的对象
            for (Field sdkField : field) {
                if (sdkField.getName().equals(initField.getName())) {
                    initField.setAccessible(true);
                    sdkField.setAccessible(true);
                    sdkField.set(sdkConfi, initField.getType().getConstructor(initField.getType()).newInstance(initField.getName()));
                }
            }
        }

    }

    @PreDestroy
    public void destroy() {
        mtsSdk.close();
        log.warn("MTS SDK close !");
    }

    private Integer getNode() {
        //最大允许多少个节点
        String nodeMaxkey = "rcs.mts.node.max";
        String nodeMaxStr = redisClient.get(nodeMaxkey);
        if (StringUtils.isBlank(nodeMaxStr)) {
            nodeMaxStr = "4";
        }
        Integer nodeMax = Integer.valueOf(nodeMaxStr);
        log.info("mts节点最大数:{}", nodeMax);

        for (int i = 1; i <= nodeMax; i++) {
            String key = "rcs.mts.node.index.";
            if (StringUtils.isBlank(redisClient.get(key + i))) {
                start(i);
                return i;
            }
        }
        log.info("mts节点获取异常返回随机节点:{}", nodeMax);
        return new Random().nextInt(10);
    }

    private void start(int i) {
        Runnable runable = () -> {
            while (true) {
                try {
                    String key = "rcs.mts.node.index.";
                    redisClient.setExpiry(key + i, "1", 6L);
                    log.info("mts节点自动记录一次:{}", i);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    try {
                        Thread.currentThread().sleep(3000L);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        };
        new Thread(runable).start();
        log.info("mts节点记录开始!");
    }

}
