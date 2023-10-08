package com.panda.sport.rcs.third.entity.betguard.dto.bts2pa;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.panda.sport.rcs.third.entity.betguard.dto.BetGuardBaseDto;
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
public class BetResultedDto extends BetGuardBaseDto implements Serializable {

    private static final long serialVersionUID = -1606664692753764046L;


    //BC后端中的唯一交易 ID，用于识别与投注有关的对PM后端的每次调用。这不是投注 ID。
    //PM后端应保留此 ID 以消除重复调用。 它还用于回滚调用以取消以前的事务。 （必填）
    @JsonProperty("TransactionId")
    private Long TransactionId;

    //BC后端中用于识别投注的唯一 ID。 在与BC的 LiveChat 和其他支持服务交谈时，应参考此值。（必填）
    @JsonProperty("BetId")
    private Long BetId;


    //投注的结果状态。（必填）
    //1 – Accepted (not resulted)
    //2 – Returned
    //3 – Lost
    //4 – Won
    //5 – Cashed-out
    @JsonProperty("BetState")
    private int BetState;

    // 投注金額。（必填）
    @JsonProperty("Amount")
    private BigDecimal Amount;

    /*// 此投注赢得的以用户货币表示的奖金金额。
    //当赌注是下注红利赌注时，赢取的金额将进入此字段。 有关详细信息，请参阅奖励 API
    private Decimal BonusAmount;

    // 用于此投注的用户奖金 ID。 如果用户的奖金是由 GetBonusDetails API 调用创建的，则该值为从 GetClientDetails 调用收到的 ExternalBonusId。
    private int BonusId;

    //投注结算的 UTC 时间。
    private DateTime CalcDate;

    //True – 滚球, False – 赛前.
    private boolean IsLive;

    // State 字段的可能值为
    //  NotResulted = 0,
    //  Returned = 2
    //  Lost = 3
    //  Won = 4
    //  WinReturn = 5
    //  LossReturn = 6
    //SelectionId 将指示当前投注中的投注项 ID。
    private List<SelectionModelDto> BetSelectionState;

    //True – 当投注被取消/作废时
    //False – 其他
    private boolean IsVoid;

    //True – 当赌注被重新结算时
    //False – 其他
    private boolean IsResettled;

    //True – 当出现部分套现时，说明这是初步结果
    //False – 其他
    private boolean IsPartial;*/

}
