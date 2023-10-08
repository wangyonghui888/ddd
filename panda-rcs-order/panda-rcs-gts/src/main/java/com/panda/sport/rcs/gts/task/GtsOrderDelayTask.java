package com.panda.sport.rcs.gts.task;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.gts.service.GtsCommonService;
import com.panda.sport.rcs.gts.service.GtsThirdApiService;
import com.panda.sport.rcs.gts.util.SystemThreadLocal;
import com.panda.sport.rcs.gts.vo.ErrorMessagePrompt;
import com.panda.sport.rcs.gts.vo.GtsMerchantOrder;
import com.panda.sport.rcs.gts.vo.StandardMatchMessage;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.panda.sport.rcs.gts.common.Constants.*;

/**
 * gts延迟接单扫描
 */
@Slf4j
@Component
public class GtsOrderDelayTask {

    @Resource
    RedisClient redisClient;

    @Resource
    GtsCommonService gtsCommonService;

    @Resource
    GtsThirdApiService gtsThirdApiService;
    /**
     * 延迟接单订单map
     */
    public static Map delayMap = new ConcurrentHashMap();

    @PostConstruct
    public void init() {
        //每秒检查订单是否可以接单
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            try {
                checkOrder(delayMap);
                //log.info("::本次检查gts延迟订单数量::{}", delayMap.size());
            } catch (Exception e) {
                log.info("延迟接单异常{}:{}", e.getMessage(), e);
            }
        }, 10, 1, TimeUnit.SECONDS);
    }

    /**
     * 检查待接单的订单
     *
     * @param map
     */
    private void checkOrder(Map<String, String> map) {
        for (String key : map.keySet()) {
            String value = map.get(key);
            try {
                boolean checkSatus = true;
                GtsMerchantOrder merchantOrder = JSONObject.parseObject(value, GtsMerchantOrder.class);
                log.info("::{}::自动检测:{}",merchantOrder.getOrderNo(),JSONObject.toJSONString(merchantOrder));
                SystemThreadLocal.set("orderNo",merchantOrder.getOrderNo());
                //检测是否到了时间
                Long intervals = System.currentTimeMillis() - Long.valueOf(merchantOrder.getOrderTime());
                if (intervals > Long.valueOf(merchantOrder.getDelayTime()) * 1000) {
                    log.info("::{}::自动检测:,订单到达时间接单处理开始", merchantOrder.getOrderNo());
                } else {
                    ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
                    checkSatus = gtsCommonService.dealWithData(merchantOrder.getTOrderDetailList(), errorMessagePrompt);
                    //更新为拒单
                    if (checkSatus) {
                        updateGtsPa(merchantOrder, "REJECTED", errorMessagePrompt.getHintMsg());
                        //跟新第三方订单状态
                        if (merchantOrder.getIsGts() == 1) {
                            log.info("::{}::自动检测通知gts", merchantOrder.getOrderNo());
                            gtsThirdApiService.gtsReceiveBet(merchantOrder.getGtsExtendBeanList(), merchantOrder.getTotalMoney(), merchantOrder.getSeriesType(), REJECTED);
                        }
                    }
                    return;
                }
                //更新为接单
                 updateGtsPa(merchantOrder, "ACCEPTED", "自动检测:商户自己接单成功");
                //跟新第三方订单状态
                if (merchantOrder.getIsGts() == 1) {
                    log.info("::{}::自动检测通知gts", merchantOrder.getOrderNo());
                    gtsThirdApiService.gtsReceiveBet(merchantOrder.getGtsExtendBeanList(), merchantOrder.getTotalMoney(), merchantOrder.getSeriesType(), ACCEPTED);
                }
            } catch (Exception e) {
                log.info("::{}::自动检测:,处理订单异常:{}:{}", key, e.getMessage(), e);
            }
        }
    }

    /**
     * @param gtsMerchantOrder 接拒单对象
     * @param gtsStatus        gts状态  ACCEPTED接单  REJECTED拒单
     * @param reasonMsg        接拒原因描述
     */
    private void updateGtsPa(GtsMerchantOrder gtsMerchantOrder, String gtsStatus, String reasonMsg) {
        Long ticketId = redisClient.incrBy(GTS_AUTO_TICKETID, 1) * (-1);
        String jsonValue = "{}";
        //表示商户不走gts的
        Integer reasonCode = -101;
        //如果是gts返回走延迟的
        if (ObjectUtils.isNotEmpty(gtsMerchantOrder.getGtsExtendBeanList())) {
            reasonCode = 200;
        }
        Integer isCache = 0;
        gtsCommonService.updateGtsOrder(ticketId.toString(), gtsStatus, gtsMerchantOrder.getOrderNo(), jsonValue, reasonCode, reasonMsg, isCache);
        delayMap.remove(gtsMerchantOrder.getOrderNo());
        log.info("::{}::自动检测:处理完成:{}:{}", gtsMerchantOrder.getOrderNo(), gtsStatus, reasonMsg);
    }
}
