package com.panda.rcs.pending.order.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 预约订单表
 *
 * @TableName rcs_pending_order
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("rcs_pending_order")
public class RcsPendingOrder extends RcsBaseEntity<RcsPendingOrder> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 预约订单编号
     */
    @TableId(value = "order_no")
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商户id
     */
    private Long merchantId;

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 赛事种类
     */
    private Long sportId;

    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    private Integer matchType;

    /**
     * 盘口id
     */
    private Long marketId;

    /**
     * 投注项id
     */
    private Long oddsId;

    /**
     * 标准玩法id,如全场让球为4
     */
    private Long playId;

    /**
     * 预约盘口值
     */
    private String marketValue;

    /**
     * 预约注单下注金额，指的是下注本金2位小数，投注时x100
     */
    private Long betAmount;

    /**
     * 预约盘口赔率
     */
    private String orderOdds;

    /**
     * 预约订单状态(0.待处理  1.成功  2.失败   3.会议取消订单)
     */
    private Integer orderStatus;

    /**
     * (投注项类型) Over/Under
     */
    private String oddType;

    /**
     * 注单胜率
     */
    private Integer winPercent;

    /**
     * 赛事所属时间期号
     */
    private String dateExpect;

    /**
     * 赛事阶段  1：全场  2：上半场  3：下半场
     */
    private String playType;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 定时任务触发时间
     */
    private Long triggerTime;

    /**
     * 预约订单取消时间
     */
    private Long cancelTime;

    /**
     * 失败或者取消原因
     */
    private String remark;
}