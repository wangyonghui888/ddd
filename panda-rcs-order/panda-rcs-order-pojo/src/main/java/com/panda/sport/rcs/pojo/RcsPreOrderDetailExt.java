package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * rcs_operation_log 日志
 *
 * @author Eamon
 */
@Data
public class RcsPreOrderDetailExt implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 注单号
     */
    private String betNo;

    /**
     * 处理状态 0 待处理 1 接单 2拒单 3：一键秒接 4：手动接单 5：手动拒单 6:中场休息秒接
     */
    private Integer orderStatus;

    /**
     * 订单扫描状态 0 未处理 1 已处理
     */
    private Integer handleStatus = 0;

    /**
     * 下单时间
     */
    private Long betTime;

    /**
     * 预期最迟接单时间
     */
    private Long maxAcceptTime;

    /**
     * 当前事件最长接单等待时长
     */
    private Integer maxWait;

    /**
     * 最小等待时间
     */
    private Integer minWait;

    /**
     * 当前事件
     */
    private String currentEvent;

    /**
     * 事件类型0-安全;1-危险;2-封盘;3拒单
     */
    private Integer currentEventType;

    /**
     * 当前事件时间
     */
    private Long currentEventTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 玩法集id
     */
    private Long categorySetId;

    /**
     * 串关类型(1：单关(默认) 、N00F：串关投注)
     */
    private Integer seriesType;

    /**
     * 接拒单原因
     */
    private String reason;

    /**
     * 0：待处理 1：系统接单通过 2：系统拒单3：下游拒单
     */
    private Integer infoStatus;

    /**
     * 提前结算订单号
     */
    private String preOrderNo;

    /**
     * 事件流程
     */
    private String eventAxis;

    /**
     * 赛事类型：1 ：早盘赛事 ，2： 滚球盘赛事，3： 冠军盘赛事，5：活动赛事
     */
    private Integer matchType;

    /**
     * 提前结算请求发起时间
     */
    private Long reqTime;

    private static final long serialVersionUID = 1L;
}