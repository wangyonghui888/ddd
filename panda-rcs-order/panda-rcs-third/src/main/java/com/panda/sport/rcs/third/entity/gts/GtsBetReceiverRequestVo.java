package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * gts Bet Receiver api 入参
 * @author z9-lithan
 * @date 2023-01-07 14:21:20
 */
@Data
public class GtsBetReceiverRequestVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 投注编号
     */
    private String id ;
    /**
     * 下注时间
     */
    private String betPlacedTimestampUTC;
    /**
     * 更新时间
     */
    private String betUpdatedTimestampUTC;
    /**
     * 平台名
     */
    private String bookmakerName;

    /**
     * 用户 玩家 id
     */
    private String playerId;

    /**
     * 投注金额 我们只会有整数
     */
    private BigDecimal totalStake;


    /**
     * 币种标识
     */
    private String currencyCode;

    /**
     * 币种标识
     */
    private String systemBetType;


    /**
     * 是否优先 1 优先高 2优先低
     */
    private Integer priority;

    /**
     * 投注项
     */
    private List<GtsBetReceiveLegsVo> legs;

    /**
     * 投注的当前状态 支持的值：["Open"/"Settled"/“Cancelled“]
     */
    private String status;


    /**
     *    systemBetType 串关方式
     *    "SystemBetTypes": {
     *     "BXMUL-2L": [ 2 ],
     *     "BXMUL-3L": [ 3 ],
     *     "BXMUL-4L": [ 4 ],
     *     "BXMUL-5L": [ 5 ],
     *     "BXMUL-6L": [ 6 ],
     *     "BXMUL-7L": [ 7 ],
     *     "BXMUL-8L": [ 8 ],
     *     "BXMUL-9L": [ 9 ],
     *     "BXMUL-10L": [ 10 ],
     *     "BXMUL-11L": [ 11 ],
     *     "BXMUL-12L": [ 12 ],
     *     "BXMUL-13L": [ 13 ],
     *     "BXMUL-14L": [ 14 ],
     *     "PATENT": [ 1, 2, 3 ],
     *     "LUCKY15": [ 1, 2, 3, 4 ],
     *     "LUCKY31": [ 1, 2, 3, 4, 5 ],
     *     "LUCKY63": [ 1, 2, 3, 4, 5, 6 ],
     *     "TRIXIE": [ 2, 3 ],
     *     "YANKEE": [ 2, 3, 4 ],
     *     "SUPYANKEE": [ 2, 3, 4, 5 ],
     *     "HEINZ": [ 2, 3, 4, 5, 6 ],
     *     "SUPHEINZ": [ 2, 3, 4, 5, 6, 7 ],
     *     "GOLIATH": [ 2, 3, 4, 5, 6, 7, 8 ]
     *   }
     */

    /**
     * 赔付
     */
    private BigDecimal cashedOutTotalStake;
    /**
     * 赔付
     */
    private BigDecimal cashedOutAmount;
    /**
     * 赔付
     */
    private BigDecimal payout;


}
