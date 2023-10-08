package com.panda.sport.rcs.pojo.vo;

import com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: 清理缓存 通知参数
 * @author: lithan
 * @date: 2020-10-15 14:49
 **/
@Data
public class LimitCacheClearVo {
    /**
     * 体育种类
     */
    private Integer sportId;
    /**
     *     数据类型
     *     0 商户限额
     *
     *     1.商户单场限额
     *     MERCHANT_SINGLE_LIMIT(1),
     *
     *     2.用户单日限额
     *     USER_DAILY_LIMIT(2),
     *
     *     3.用户单场限额
     *     USER_SINGLE_LIMIT(3),
     *
     *     4.用户单注单关限额
     *     USER_SINGLE_BET_LIMIT(4),
     *
     *     5.串关单注赔付限额
     *     SERIES_PAYMENT_LIMIT(5),
     *
     *     6.各投注项计入单关限额的投注比例
     *     SERIES_RATIO(6),
     *
     *     7.最低/最高投注额限制
     *     BET_AMOUNT_LIMIT(7);
     */
    private Integer dataType;


    private Integer matchId;

    private String businessId;


    //2022-01-09 联赛模板大key速优 新增下面的字段
    private Integer tournamentLevel;
    private String matchType;


    //商户单场变更参数
    private List<RcsQuotaMerchantSingleFieldLimit> merchantSingleLimitList;
    //用户单场
    private List<RcsQuotaMerchantSingleFieldLimit> userSingleLimitList;

    //用户单注单关
    private String val;
    private String val2;
    private String val3;



}