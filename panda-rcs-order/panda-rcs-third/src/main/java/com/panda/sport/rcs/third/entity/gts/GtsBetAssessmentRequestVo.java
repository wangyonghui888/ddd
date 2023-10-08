package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * gts Bet Assessment api 入参
 *
 * {
 *  "betId": "1234",
 *  "playerId": "10",
 *  "totalStake": 10,
 *  "currencyCode": "EUR",
 *  "legs": [
 *  {
 *  "selectionId": "mytest",
 *  "price": 2,
 *  "gameState": "PreMatch"
 *  }
 *  ]
 *  }
 *
 * @author z9-lithan
 * @date 2023-01-06 15:21:20
 */
@Data
public class GtsBetAssessmentRequestVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 投注编号
     */
    private String betId;
    /**
     * 玩家编号
     */
    private String playerId;
    /**
     * 投注总金额
     */
    private BigDecimal totalStake;
    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 投注项
     */
    List<GtsBetAssessmentLegsRequestVo> legs;

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
    private String systemBetType;

    /**
     * Property to determine what type of price change is allowed
     */
    private String priceChangeRule;
}
