package com.panda.sport.rcs.third.entity.betguard.dto;

import cn.hutool.core.date.DateTime;
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
public class BetModelDto implements Serializable {

    private static final long serialVersionUID = -1606664692753764046L;

    /**
     * 详见AuthToken parameter说明。（必填）
    */
    private String AuthToken;

   /**
    * BC后端中的唯一交易 ID，用于识别与投注有关的对PM后端的每次调用。这不是投注 ID。
    * PM后端应保留此 ID 以消除重复调用。 它还用于回滚调用以取消以前的事务。 （必填）
    */
    private Long TransactionId;

    /**
     * 详见上方TS parameter说明
    */
    private Long BetId;


    /**
     * Bet amount
    */
    private Decimal Amount;
    /**
     * Bet placement time
     */
    private DateTime Created;
    /**
     Bet type (Single = 1, Multiple = 2, System = 3,
     Chain = 4, StraightForecast = 40, ReverseForecast
     = 41, CombinationForecast = 42, StraightTricast =
     43, CombinationTricast = 44)
    */
    private int BetType;
    /**
     Accept only if odd has not changed = 0
     Accept only if odd has not changed or odd has
     been increased = 1
     Accept with any odd changes = 2
    */
    private int AcceptTypeId;
    /**
     (optional)Multiple length in system bet
    */
    private int SystemMinCount;
    /**
     Bet’s total price (odd)
    */
    private Decimal TotalPrice;
    /**
     Bet state (Accepted = 1, Returned = 2, Lost = 3,
     Won = 4, CashedOut = 5)
    */
    private int State;
    /**
     Bet is placed on Live selection
    */
    private boolean IsLive;
    /**
     Currency ISO code
    */
    private String Currency;
    /**
     Player’s unique Id
    */
    private int ClientId;
    /**
     Bet possible win amount
    */
    private Decimal PossibleWin;
    /**
     Bet’s selections
    */
    private List<BetSelectionModelDto> Selections;
    /**
     (optional) Odd type used to place the bet (Decimal
     = 0, Fractional = 1, American = 2, HongKong = 3,
     Malay = 4, Indo = 5)
    */
    private int OddType;
    /**
     Bet is an EachWay bet (optional)
    */
    private boolean IsEachWay;
    /**
     * 最大投注金额
    */
    private BigDecimal maxAmout;
    /**
     * 最小投注金额
    */
    private BigDecimal minAmout;









}
