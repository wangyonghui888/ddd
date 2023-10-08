package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 赛事维度 各配置数据
 *
 * @description:
 * @author: lithan
 * @date: 2020-09-25 09:50
 **/
@Data
public class MatchLimitDataVo implements Serializable {

    private static final long serialVersionUID = 3312124506068991612L;

    /**
     * 商户单场限额
     *
     * @see com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum#MERCHANT_SINGLE_LIMIT
     */
    RcsQuotaMerchantSingleFieldLimitVo rcsQuotaMerchantSingleFieldLimitVo;

    /**
     * 用户单日限额
     *
     * @see com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum#USER_DAILY_LIMIT
     */
    List<RcsQuotaUserDailyQuotaVo> userDailyQuotaList;

    /**
     * 用户单场限额
     *
     * @see com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum#USER_SINGLE_LIMIT
     */
    RcsQuotaUserSingleSiteQuotaVo rcsQuotaUserSingleSiteQuotaVo;

    /**
     * 用户单注单关限额
     *
     * @see com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum#USER_SINGLE_BET_LIMIT
     */
    List<RcsQuotaUserSingleNoteVo> rcsQuotaUserSingleNoteVoList;

    /**
     * 串关单注赔付限额 <br/>
     * Map<串关类型，单注赔付限额>
     * 串关类型：
     * <li>2-2串1</li>
     * <li>3-3串N</li>
     * <li>4-4串N</li>
     * <li>5-5串N</li>
     * <li>6-6串N</li>
     * <li>7-7串N</li>
     * <li>8-8串N及以上</li>
     *
     * @see com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum#SERIES_PAYMENT_LIMIT
     */
    Map<Integer, BigDecimal> seriesPaymentLimitMap;

    /**
     * 各投注项计入单关限额的投注比例 <br/>
     * Map<串关类型，比例>
     * 串关类型：
     * <li>2-2串1</li>
     * <li>3-3串1</li>
     * <li>4-4串1</li>
     * <li>5-5串1</li>
     * <li>6-6串1</li>
     * <li>7-7串1</li>
     * <li>8-8串1</li>
     * <li>9-9串1</li>
     * <li>10-10串1</li>
     *
     * @see com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum#SERIES_RATIO
     */
    Map<Integer, BigDecimal> seriesRatioMap;

    /**
     * 最低/最高投注额限制
     *
     * @see com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum#BET_AMOUNT_LIMIT
     */
    BetAmountLimitVo betAmountLimitVo;

    /**
     * 计入串关已用额度的比例 <br/>
     * Map<串关类型，比例>
     * 串关类型：
     * <li>2-2串1</li>
     * <li>3-3串N</li>
     * <li>4-4串N</li>
     * <li>5-5串N</li>
     * <li>6-6串N</li>
     * <li>7-7串N</li>
     * <li>8-8串N</li>
     * <li>9-9串N</li>
     * <li>10-10串N</li>
     *
     * @see com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum#SERIES_USED_RATIO
     */
    Map<Integer, BigDecimal> seriesUsedRatioMap;
}