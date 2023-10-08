package com.panda.rcs.pending.order.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预约注单入参
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PendingOrderDto", description = "预约注单入参对象")
public class PendingOrderDto {
    /**
     * 预约订单编号(require=true)
     */
    @ApiModelProperty(name = "orderNo",value = "预约订单编号")
    private String orderNo;

    /**
     * 用户id(require=true)
     */
    @ApiModelProperty(name = "userId",value = "用户id")
    private Long userId;

    /**
     * 商户id(require=true)
     */
    @ApiModelProperty(name = "merchantId",value = "商户id")
    private Long merchantId;

    /**
     * 赛事id(require=true)
     */
    @ApiModelProperty(name = "matchId",value = "赛事id")
    private Long matchId;

    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘 (require=true)
     */
    @ApiModelProperty(name = "matchType",value = "类型(1.早盘,2滚球,3冠军)")
    private Integer matchType;

    /**
     * 赛事种类 (require=true)
     */
    @ApiModelProperty(name = "sportId",value = "赛事种类")
    private Long sportId;

    /**
     * 盘口id
     */
    @ApiModelProperty(name = "marketId",value = "盘口id")
    private Long marketId;

    /**
     * 投注项id
     */
    @ApiModelProperty(name = "oddsId",value = "投注项id")
    private Long oddsId;

    /**
     * 标准玩法id,如全场让球为4 (require=true)
     */
    @ApiModelProperty(name = "playId",value = "标准玩法id(如全场让球为4)")
    private Long playId;

    /**
     * 预约盘口值(require=true)
     */
    @ApiModelProperty(name = "marketValue",value = "预约盘口值")
    private String marketValue;

    /**
     * 预约注单下注金额，指的是下注本金2位小数，投注时x100,以分(require=true)
     */
    @ApiModelProperty(name = "betAmount",value = "预约注单下注金额")
    private Long betAmount;

    /**
     * 预约盘口赔率(require=true)
     */
    @ApiModelProperty(name = "orderOdds",value = "预约盘口赔率")
    private String orderOdds;

    /**
     * 预约订单状态(0.待处理  1.成功  2.失败   3.会议取消订单)(require=true)
     */
    @ApiModelProperty(name = "orderStatus",value = "预约订单状态(0.待处理  1.成功  2.失败   3.会议取消订单)")
    private Integer orderStatus;

    /**
     * (投注项类型) Over/Under(require=true)
     */
    @ApiModelProperty(name = "oddType",value = "(投注项类型) ")
    private String oddType;

    /**
     * 注单胜率
     */
    @ApiModelProperty(name = "winPercent",value = "注单胜率")
    private Integer winPercent;

    /**
     * 赛事所属时间期号(require=true)
     */
    private String dateExpect;

}
