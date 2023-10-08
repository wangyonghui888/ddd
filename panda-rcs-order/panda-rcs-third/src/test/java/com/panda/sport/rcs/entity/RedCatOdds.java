package com.panda.sport.rcs.entity;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.third.entity.gts.GtsAuthorizationVo;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 红猫赔率
 */
@Data
public class RedCatOdds {
    /**
     * "oddsId": 3278117,
     *                   "name": "埃尔切",
     *                   "nameEn": "ELCHE CF",
     *                   "oddsValue": 2.979,
     *                   "oddsOrder": 1,
     *                   "selectionStatus": "P",
     *                   "isSuspended": false,
     *                   "isClosed": false,
     *                   "betSettlementCertainty": "Unknown",
     *                   "settlementResult": 0,
     *                   "addition1": "TEAMID=27",
     *                   "selectionKey": "MATCHWINNER_FT_1|0.00000",
     *                   "tsUpdated": "2023-06-06T04:23:57.613"
     */
    /**
     * 赔率类型
     */
    /**
     * 投注项
     */
    public Integer oddsId;
    /**
     * 赔率
     */
    public BigDecimal oddsValue;
    /**
     * 是否暂停
     */
    public String isSuspended;
    /**
     * 关盘
     */
    public String isClosed;

  }
