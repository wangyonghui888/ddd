package com.panda.sport.rcs.dto.limit;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.dto.limit
 * @Description : 串关赔付数额
 * @Author : Paca
 * @Date : 2020-10-01 17:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class SeriesPaymentAmount implements Serializable {

    private static final long serialVersionUID = 6073433635085181834L;

    /**
     * 串关订单总赔付数额，单位：分
     */
    private Long seriesPaymentTotal;

    /**
     * 计入单关的赔付数额
     */
    private List<SinglePaymentAmount> singlePaymentList;

    @Data
    public static class SinglePaymentAmount implements Serializable {

        private static final long serialVersionUID = -5217140033486601707L;

        /**
         * 赛种
         */
        private String sportId;

        /**
         * 联赛等级
         */
        private String tournamentLevel;

        /**
         * 赛事ID
         */
        private String matchId;

        /**
         * 投注阶段，0-早盘，1-滚球
         */
        private String matchType;

        /**
         * 玩法ID
         */
        private String playId;

        /**
         * 玩法类型
         */
        private String playType;

        /**
         * 赛事账务日
         */
        private String dateExpect;

        /**
         * 计入单关的赔付数额，单位：分
         */
        private Long singlePayment;

    }

}
