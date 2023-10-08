package com.panda.sport.rcs.mts.sportradar.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.mts.sportradar.wrapper.MtsCommonService;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.RcsMtsOrderExt;
import com.panda.sport.rcs.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 订单发送到MTS
 */
@Component
@Slf4j
public class OrderSendMtsConsumer extends ConsumerAdapter<Map<String, Object>> {

    @Autowired
    MtsCommonService mtsCommonService;

    @Autowired
    RedisClient redisClient;

    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    public OrderSendMtsConsumer() {
        super("queue_validate_pinnacle_mts_order", "");
    }

    @Override
    public Boolean handleMs(Map<String, Object> dataMap, Map<String, String> paramsMap) {
        String orderId = "";
        try {
            //串几关
            String seriesType = String.valueOf(dataMap.get("seriesNum"));
            String totalMoney = dataMap.get("totalMoney") == null ? "0" : String.valueOf(dataMap.get("totalMoney"));
            String ip = dataMap.get("ip") == null ? null : String.valueOf(dataMap.get("ip"));
            String deviceType = dataMap.get("deviceType") == null ? null : String.valueOf(dataMap.get("deviceType"));
            //是否自动接受赔率变化 1：自动接收更好的赔率 2：自动接受任何赔率变动 3：不自动接受赔率变动
            String acceptOdds = dataMap.get("acceptOdds") == null ? null : String.valueOf(dataMap.get("acceptOdds"));

            //注单列表
            List<ExtendBean> list = JSONObject.parseObject(JSONObject.toJSONString(dataMap.get("list")), new TypeReference<List<ExtendBean>>() {
            });

            //订单号 方便日志跟踪
            orderId = list.get(0).getItemBean().getOrderNo();
            int matchTYpe = list.get(0).getItemBean().getMatchType();
            log.info("{} OrderSendMtsConsumer 收到bean info ：{}", orderId, JSONObject.toJSON(list));
            if (!CollectionUtils.isEmpty(list)) {
                //填充基础信息
                mtsCommonService.convertAllParam(list);
                //saveMtsOrder(orderId, totalMoney, String.valueOf(list.get(0).getMtsAmount()), "");
                //向pinacle下单
                /**
                 * dododododododododo
                 */
                log.info("{}向pinacle发送完成:" + orderId);
            }
        } catch (Exception ex) {
            log.info(orderId + "MTS订单发送异常：{},{}", ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    private void saveMtsOrder(String orderNo, String paMount, String mtsAmount, String requestJson) {
        RcsMtsOrderExtService rcsMtsOrderExtService = SpringContextUtils.getBeanByClass(RcsMtsOrderExtService.class);
        RcsMtsOrderExt rcsMtsOrderExt = new RcsMtsOrderExt();
        rcsMtsOrderExt.setOrderNo(orderNo);
        rcsMtsOrderExt.setRequestJson(requestJson);
        rcsMtsOrderExt.setStatus("INIT");
        rcsMtsOrderExt.setPaAmount(paMount);
        rcsMtsOrderExt.setMtsAmount(mtsAmount);
        rcsMtsOrderExtService.addMtsOrder(rcsMtsOrderExt);
    }
}
