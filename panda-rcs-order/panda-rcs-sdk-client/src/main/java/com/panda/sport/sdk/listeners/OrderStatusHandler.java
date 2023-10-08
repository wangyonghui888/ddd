package com.panda.sport.sdk.listeners;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author lithan
 * @description
 * @date 2020/1/18 21:33
 */
@Singleton
public class OrderStatusHandler implements  OrderStatusServer{

    private static final Logger log = LoggerFactory.getLogger(OrderStatusHandler.class);

    @Override
    public void responseReceived(String body) {
        Map map = JSONObject.parseObject(body, Map.class);
        String orderNo = map.get("orderNo").toString();
        String status = map.get("status").toString();
        log.info("订单号:{}状态:{}",orderNo,status);
        /*
           业务操作
         */
    }
}
