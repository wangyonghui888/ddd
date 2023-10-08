package com.panda.sport.rcs.mgr.mq.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  YiMing
 * @Description :  请求提前结算-消息体
 * @Date: 2022-01-26 14:00
 */
@Data
public class PreSettleBean implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 提前结算订单号
     */
    private String preOrderNo;

    /**
     * 订单状态 1:接  2：拒  3：取消  4：取消回滚
     */
    private Integer orderState;

    /**
     * 提前结算使用赔率
     */
    private Double preSettleOdds;

    /**
     * 事件编码
     */
    private String currentEvent;

    /**
     * 原因
     */
    private String reason;

    /**
     * 等待时长 单位毫秒
     */
    private Integer waitTime;

    /**
     * 提前结算请求发起时间
     */
    private Long reqTime;

    /**
     * 用户UID 风控需要带回来给ws使用
     */
    private String userId;

    /**
     * 事件流程
     */
    private String eventAxis;
    /**
     * 提前结算金额
     */
    private String preSettleAmount;
    /**
     * 注单金额
     */
    private String orderAmount;

    /**
     * 提前结算比分
     */
    private String preSettleScore;

    /**
     * 提前结算投注额
     */
    private String preSettleBetAmount;

    /**
     * 二级tag
     */
    private List<String> secondaryLabelIdsList;


}
