package com.panda.sport.rcs.mgr.predict;

import com.panda.sport.data.rcs.dto.OrderItem;

import java.math.BigDecimal;

/**
 * 赛事预测  获取rediskeys
 *
 * @description:
 * @author: lithan
 * @date: 2020-07-23 09:37
 **/
public class PredictRedisKeyUtil {

    //赛事总投注
    public static String getMatchTotalBetAmonutKey() {
        return "rcs.risk.predict.matchTotalBetAmount";
    }

    //赛事总投注
    public static String getMatchTotalBetNumKey() {
        return "rcs.risk.predict.matchTotalBetNum";
    }

    //赛事已结算货量
    public static String getMatchSettledTotalBetAmonutKey() {
        return "rcs.risk.predict.matchSettledTotalBetAmonut";
    }

    //赛事已结算盈亏
    public static String getMatchsettledProfitKey() {
        return "rcs.risk.predict.matchsettledProfitKey";
    }

    //投注项总投注金额
    public static String getOddsTotalBetAmountKey(OrderItem item) {
        String oddsTotalBetAmonutKey = "rcs.risk.predict.oddsTotalBetAmount.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
        oddsTotalBetAmonutKey = String.format(oddsTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getMarketId());
        return oddsTotalBetAmonutKey;
    }

    //盘口位置 投注项总投注
    public static String getPalceNumTotalBetAmountKey(OrderItem item) {
        String placeNumTotalBetAmonutKey = "rcs.risk.predict.palceNumTotalBetAmount.match_id.%s.match_type.%s.play_id.%s.place_num.%s";
        placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getPlaceNum());
        return placeNumTotalBetAmonutKey;
    }

    //投注项总投注笔数
    public static String getOddsTotalBetNumKey(OrderItem item) {
        String oddsTotalBetNumKey = "rcs.risk.predict.oddsTotalBetNum.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
        oddsTotalBetNumKey = String.format(oddsTotalBetNumKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getMarketId());
        return oddsTotalBetNumKey;
    }

    //盘口位置 投注项 总投注笔数
    public static String getPalceNumTotalBetNumKey(OrderItem item) {
        String getPalceNumTotalBetNumKey = "rcs.risk.predict.palceNumTotalBetNum.match_id.%s.match_type.%s.play_id.%s.place_num.%s";
        getPalceNumTotalBetNumKey = String.format(getPalceNumTotalBetNumKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getPlaceNum());
        return getPalceNumTotalBetNumKey;
    }


    //盘口投注总金额
    public static String getMarketTotalBetAmonutKey(OrderItem item) {
        String marketTotalBetAmonutKey = "rcs.risk.predict.marketTotalBetAmonut.match_id.%s.match_type.%s.play_id.%s";
        marketTotalBetAmonutKey = String.format(marketTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return marketTotalBetAmonutKey;
    }



    //投注项最大赔付
    public static String getOddsTotalPaidAmonutKey(OrderItem item) {
        String oddsTotalPaidAmonutKey = "rcs.risk.predict.oddsTotalPaidAmonut.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
        oddsTotalPaidAmonutKey = String.format(oddsTotalPaidAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getMarketId());
        return oddsTotalPaidAmonutKey;
    }

    //投注项级别的期望值
    public static String getOddsProfitAmonutKey(OrderItem item) {
        String oddsProfitAmonutKey = "rcs.risk.predict.oddsProfitAmonut.match_id.%s.match_type.%s.play_id.%s.market_id.%s";
        oddsProfitAmonutKey = String.format(oddsProfitAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getMarketId());
        return oddsProfitAmonutKey;
    }

}