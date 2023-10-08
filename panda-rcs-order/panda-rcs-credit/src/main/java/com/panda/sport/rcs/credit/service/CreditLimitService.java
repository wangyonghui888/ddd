package com.panda.sport.rcs.credit.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.TradeModeEnum;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用限额服务
 * @Author : Paca
 * @Date : 2021-05-05 17:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface CreditLimitService {

    /**
     * 查询投注限额
     *
     * @param orderBean
     * @return
     */
    List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean);

    /**
     * 校验订单额度
     *
     * @param orderBean
     * @return
     */
    Map<String, Object> checkOrder(OrderBean orderBean);

    /**
     * 1-风控，2-MTS
     *
     * @return
     */
    int orderType();

    /**
     * 是否冠军玩法
     *
     * @param orderBean
     * @return
     */
    static boolean isChampion(OrderBean orderBean) {
        if (orderBean != null && CollectionUtils.isNotEmpty(orderBean.getItems())) {
            Integer matchType = orderBean.getItems().get(0).getMatchType();
            return matchType != null && matchType == 3;
        }
        return false;
    }

    /**
     * 设置限额类型
     *
     * @param orderBean
     */
    static void setLimitType(OrderBean orderBean) {
        // 限额类型，1-标准模式，2-信用模式
        int limitType;
        if (isChampion(orderBean)) {
            if (NumberUtils.INTEGER_ONE.equals(orderBean.getLimitType())) {
                limitType = 1;
            } else {
                limitType = 2;
            }
        } else {
            limitType = 2;
        }
        if (orderBean != null) {
            orderBean.setLimitType(limitType);
        }
    }

    /**
     * 是否MTS
     *
     * @param orderBean
     * @return
     */
    static boolean isMts(OrderBean orderBean) {
        List<OrderItem> orderItemList = orderBean.getItems();
        // 是否都是SR数据，并且有一个是MTS操盘就交给MTS操盘
        boolean isAllSr = true;
        boolean isAllMts = true;
        boolean isAllAuto = true;
        for (OrderItem orderItem : orderBean.getItems()) {
            if (!"SR".equals(orderItem.getDataSourceCode())) {
                isAllSr = false;
            }
            if (!"MTS".equalsIgnoreCase(orderItem.getPlatform())) {
                isAllMts = false;
            }
            if (TradeModeEnum.isNotAuto(orderItem.getTradeType())) {
                isAllAuto = false;
            }
        }

        boolean isMts;
        if (orderItemList.size() == 1 && !isAllAuto) {
            // 是单关并且不是自动操盘，则不走MTS
            isMts = false;
        } else {
            isMts = isAllSr && isAllMts;
        }
        return isMts;
    }

    /**
     * 是否滚球
     *
     * @param orderBean
     * @return
     */
    static boolean isLiveOrder(OrderBean orderBean) {
        // 出现任何滚球赛事，需走滚球流程
        boolean isLive = false;
        for (OrderItem orderItem : orderBean.getItems()) {
            if (orderItem.getMatchType() == 2) {
                isLive = true;
                break;
            }
        }
        return isLive;
    }

    static String getServiceName(OrderBean orderBean) {
        if (isChampion(orderBean)) {
            return "championLimitService";
        } else {
            boolean isSingle = NumberUtils.INTEGER_ONE.equals(orderBean.getSeriesType());
            if (isMts(orderBean)) {
                return isSingle ? "creditSingleLimitMtsService" : "creditSeriesLimitMtsService";
            } else {
                return isSingle ? "creditSingleLimitService" : "creditSeriesLimitService";
            }
        }
    }

    static BigDecimal getEuOdds(OrderItem orderItem) {
        Double oddsValue = orderItem.getOddsValue();
        if (oddsValue == null) {
            return BigDecimal.ONE;
        }
        return new BigDecimal(oddsValue.toString()).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE), 2, RoundingMode.DOWN);
    }

    static boolean checkNo(Integer number) {
        return number == null || number <= 0;
    }

    static boolean checkNo(Long number) {
        return number == null || number <= 0L;
    }

    static boolean checkNo(Double number) {
        return number == null || number <= 0.0D;
    }
}
