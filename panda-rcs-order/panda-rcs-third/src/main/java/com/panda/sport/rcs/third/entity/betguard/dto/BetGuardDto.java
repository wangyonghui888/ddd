package com.panda.sport.rcs.third.entity.betguard.dto;

import cn.hutool.core.date.DateTime;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/29 18:42
 * @description BetGuard入参
 */
@Data
public class BetGuardDto implements Serializable {


    private static final long serialVersionUID = 3644319732670494609L;

    //令牌
    @ApiModelProperty(value = "令牌")
    private String AuthToken;
    //时间戳
    @ApiModelProperty(value = "时间戳")
    private String TS;
    //签名
    @ApiModelProperty(value = "签名")
    private String Hash;
    //BC后端中的唯一交易 ID，用于识别与投注有关的对PM后端的每次调用。这不是投注 ID。PM后端应保留此 ID 以消除重复调用。 它还用于回滚调用以取消以前的事务。 （必填）
    @ApiModelProperty(value = "交易唯一ID")
    private Long TransactionId;
    //BC后端中用于识别投注的唯一 ID。 在与BC的 LiveChat 和其他支持服务交谈时，应参考此值。（必填）
    @ApiModelProperty(value = "投注的唯一ID")
    private Long BetId;
    //投注金額。（必填）
    @ApiModelProperty(value = "投注金額")
    private BigDecimal Amount;
    //下注的 UTC 時間。（必填）
    @ApiModelProperty(value = "下注的UTC時間")
    private DateTime Created;
    //注单类型
    @ApiModelProperty(value = "注单类型")
    private Integer BetType;
    //下注系统投注时，子投注计数。 例如，仅针对“系统”，在系统 2（共 3 个）中放置了多少事件。
    @ApiModelProperty(value = "下注系统投注时,子投注计数")
    private Integer SystemMinCount;
    //下注的总价格/赔率/系数。
    @ApiModelProperty(value = "下注的总价格")
    private Integer TotalPrice;
    //已下注的投注项/事件列表。 单注始终为 1，多重投注和系统投注始终大于 1。 该字段不应包含在哈希生成中。（必填）- 至少一个
    @ApiModelProperty(value = "已下注的投注项/事件列表")
    private List<BetGuardSelectionDto> Selections;
    //派彩金额
    @ApiModelProperty(value = "派彩金额")
    private BigDecimal BonusBetAmount;
    //用于此投注的用户奖金 ID。 如果用户的奖金是由 GetBonusDetails API 调用创建的，则该值为从 GetClientDetails 调用收到的 ExternalBonusId。
    @ApiModelProperty(value = "用户奖金ID")
    private Integer BonusId;
    //终端类型 該字段不包含在哈希中。 42 - Web,4 - Mobile,16 - Android,17 - iOS,98 - Betshop,99 - Terminal.
    @ApiModelProperty(value = "终端类型")
    private Integer Source;
    //玩家用来下注的赔率类型。0 - Decimal，1 - Fractional，2 - American，3 - HongKong，4 - Malay，5 – Indo
    @ApiModelProperty(value = "玩家下注赔率类型")
    private Integer OddType;
    //投注的结果状态。（必填）1 – Accepted (not resulted) 2 – Returned 3 – Lost 4 – Won 5 – Cashed-out
    @ApiModelProperty(value = "投注的结果状态")
    private Integer BetState;


}
