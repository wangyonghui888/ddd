package com.panda.sport.rcs.third.entity.common;

import com.panda.sport.data.rcs.dto.ExtendBean;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/4/3 17:09
 * @description 订单拓展封装
 */
@Data
public class ThirdOrderExt implements Serializable {

    private static final long serialVersionUID = 2755273554759277427L;

    /**
     * linkId
     */
    String linkId;
    /**
     * pa订单号
     */
    String orderNo;
    /**
     * 第三方订单号
     */
    String thirdOrderNo;
    /**
     * 当前订单状态
     */
    Integer orderStatus;

    /**
     * 第三方返回的订单状态
     */
    Integer thirdOrderStatus;
    /**
     * 订单详情
     */
    List<ExtendBean> list;
    /**
     * pa订单金额
     */
    BigDecimal paTotalAmount;
    /**
     * 传给第三方的金额
     */
    BigDecimal thirdAmount;
    /**
     * 串关类型
     */
    Integer seriesType;
    /**
     * 赔率喜好 1：自动接收更好的赔率 2：自动接受任何赔率变动 3：不自动接受赔率变动
     */
    int acceptOdds;
    /**
     * 第三方标志
     */
    String third;

    /**
     * 第三方延迟时间
     */
    Integer thirdDelay;
    /**
     * 第三方返回的结果
     */
    String thirdResJson;

    /**
     * 商户code
     */
    String merchantCode;

    /**
     * 商户id
     */
    String busId;

    /**
     * 订单币种
     */
    String currency;

    /**
     * 设备类型
     */
    String deviceType;

    /**
     * 用户ip
     */
    String ip;

    /**
     * 用户组
     */
    String orderGroup;

    /**
     * 用户二级标签
     */
    List<String> secondaryLabelIdsList;

    //重试类型 1投注 2取消 3确认
    int retryType;
    /**
     * 是否可以接单
     */
    private Boolean isBetAllowed;

}
