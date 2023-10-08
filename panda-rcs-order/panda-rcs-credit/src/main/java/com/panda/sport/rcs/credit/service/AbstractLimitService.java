package com.panda.sport.rcs.credit.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 限额服务
 * @Author : Paca
 * @Date : 2021-06-22 15:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public abstract class AbstractLimitService implements CreditLimitService {

    @Autowired
    protected ProducerSendMessageUtils producerSendMessageUtils;

    protected Map<String, Object> checkOrderResult(OrderBean orderBean, int infoCode, String msg) {
        // 返回到业务端状态，0-失败，1-成功，2-待处理
        int status;
        int infoStatus;
        String infoMsg;
        int validateResult;
        int orderStatus;
        if (infoCode == 0) {
            if (CreditLimitService.isLiveOrder(orderBean)) {
                status = 2;
                infoStatus = OrderInfoStatusEnum.RISK_PROCESSING.getCode();
                infoMsg = "风控滚球接拒单处理中";
                validateResult = 1;
                orderStatus = 0;
            } else {
                status = 1;
                infoStatus = OrderInfoStatusEnum.EARLY_PASS.getCode();
                infoMsg = "风控早盘接单";
                validateResult = 1;
                orderStatus = 1;
            }
        } else {
            status = 0;
            infoStatus = OrderInfoStatusEnum.EARLY_REFUSE.getCode();
            infoMsg = "风控拒单";
            validateResult = 2;
            orderStatus = 2;
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(orderBean.getOrderNo(), infoCode == 0);
        resultMap.put(orderBean.getOrderNo() + "_error_msg", msg);
        resultMap.put("status", status);
        resultMap.put("infoStatus", infoStatus);
        resultMap.put("infoCode", infoCode);
        resultMap.put("infoMsg", infoMsg);
        resultMap.put("orderType", orderType());
        resultMap.put("isVip", orderBean.getVipLevel());
        modifyStatus(orderBean, validateResult, orderStatus, infoStatus);
        log.info("额度查询，订单::{}::校验返回结果：result={}", orderBean.getOrderNo(), JSON.toJSONString(resultMap));
        if (CreditLimitService.isChampion(orderBean) || !CreditLimitService.isMts(orderBean)) {
            producerSendMessageUtils.sendMessage(MqConstants.RCS_ORDER_UPDATE, BaseConstants.SAVE_ORDER_TAGS, orderBean.getOrderNo(), orderBean);
        }
        return resultMap;
    }

    private void modifyStatus(OrderBean orderBean, int validateResult, int orderStatus, int infoStatus) {
        orderBean.setValidateResult(validateResult);
        orderBean.setOrderStatus(orderStatus);
        orderBean.setInfoStatus(infoStatus);
//        orderBean.getExtendBean().setValidateResult(validateResult);
        for (OrderItem orderItem : orderBean.getItems()) {
            orderItem.setValidateResult(validateResult);
        }
    }
}
