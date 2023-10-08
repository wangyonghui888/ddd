package com.panda.sport.rcs.third.entity.betguard.dto.bts2pa;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.panda.sport.rcs.third.entity.betguard.dto.BetGuardBaseDto;
import com.panda.sport.rcs.third.entity.betguard.dto.SelectionDto;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/29 19:08
 * @description BetPlaced 请求实体
 */

@Data
public class BetPlacedDto extends BetGuardBaseDto implements Serializable {

    private static final long serialVersionUID = -1606664692753764046L;

    //BC后端中的唯一交易 ID，用于识别与投注有关的对PM后端的每次调用。这不是投注 ID。
    //PM后端应保留此 ID 以消除重复调用。 它还用于回滚调用以取消以前的事务。 （必填）
    @JsonProperty("TransactionId")
    private Long TransactionId;

    //BC后端中用于识别投注的唯一 ID。 在与BC的 LiveChat 和其他支持服务交谈时，应参考此值。（必填）
    @JsonProperty("BetId")
    private Long BetId;

    // 投注金額。（必填）
    @JsonProperty("Amount")
    private BigDecimal amount;

    //邮箱
    @JsonProperty("Created")
    private String created;

    //1 – Single/Ordinar,	单关
    //2 – Multiple/Express,	单关多注
    //3 – System,	串关
    //4 - Chain,	链式
    //5 - Trixie,
    //3 x Doubles
    //1 x Treble

    //6 - Yankee,
    //6 x Doubles
    //
    //4 x Trebles
    //
    //1 x 4 Fold ......
    @JsonProperty("BetType")
    private int betType;

    /*//下注系统投注时，子投注计数。 例如，仅针对“系统”，在系统 2（共 3 个）中放置了多少事件。
    private int SystemMinCount;
*/
    //下注的总价格/赔率/系数。
    @JsonProperty("TotalPrice")
    private String totalPrice;

    //已下注的投注项/事件列表。 单注始终为 1，多重投注和系统投注始终大于 1。 该字段不应包含在哈希生成中。
    //（必填）- 至少一个
    @JsonProperty("Selections")
    private List<SelectionDto> selections;

    //用于下注的以用户货币表示的奖金金额。
    //当此投注使用红利时，此字段可能大于 0，并且部分投注金额会从红利金额中扣除。 如果操作员不使用奖金，则可以忽略该字段。 有关详细信息，请参阅奖励 API。
    /*private Decimal BonusBetAmount;

    // 用于此投注的用户奖金 ID。 如果用户的奖金是由 GetBonusDetails API 调用创建的，则该值为从 GetClientDetails 调用收到的 ExternalBonusId。
    private int BonusId;

    //42 - Web,
    //4 - Mobile,
    //16 - Android,
    //17 - iOS,
    //98 - Betshop,
    //99 - Terminal.
    //該字段不包含在哈希中。
    private int Source;

    //玩家用来下注的赔率类型。
    //0 - Decimal
    //1 - Fractional
    //2 - American
    //3 - HongKong
    //4 - Malay
    //5 – Indo
    //(Optional)
    private int OddType;*/


}
