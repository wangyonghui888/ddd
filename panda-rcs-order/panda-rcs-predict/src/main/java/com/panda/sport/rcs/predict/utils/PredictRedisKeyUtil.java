package com.panda.sport.rcs.predict.utils;

import com.panda.sport.data.rcs.dto.OrderItem;

import javax.ws.rs.GET;

/**
 * 赛事预测  获取rediskeys
 *
 * @description:
 * @author: lithan
 * @date: 2020-07-23 09:37
 **/
public class PredictRedisKeyUtil {

    //赛事总投注货量（新）
    public static String MATCH_TOTAL_SETTLED_VOLUME_KEY = "rcs:risk:predict:match:totalSettledVolume:";

    //赛事已结算盈亏（新）
    public static String MATCH_SETTLED_PROFIT_KEY = "rcs:risk:predict:match:totalsettledProfitKey:";

    //赛事总投注（新）
    public static String MATCH_TOTAL_BET_AMONUT_KEY = "rcs:risk:predict:match:totalBetAmount:";

    //赛事投注总数量（新）
    public static String MATCH_TOTAL_BET_NUM_KEY = "rcs:risk:predict:match:totalBetNum:";

    //赛事总投注
    public static String getMatchTotalBetAmonutKey() {
        return "rcs:risk:predict:matchTotalBetAmount";
    }

    //赛事总投注
    public static String getMatchTotalBetNumKey() {
        return "rcs:risk:predict:matchTotalBetNum";
    }

    //赛事已结算货量
    public static String getMatchSettledTotalBetAmonutKey() {
        return "rcs:risk:predict:matchSettledTotalBetAmonut";
    }

    //赛事已结算盈亏
    public static String getMatchsettledProfitKey() {
        return "rcs:risk:predict:matchsettledProfitKey";
    }

    //投注项 纯投注额
    public static String getOddsTotalBetAmountKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalBetAmonutKey = seriesType + "rcs:risk:predict:oddsTotalBetAmount.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s";
        oddsTotalBetAmonutKey = String.format(oddsTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId());
        return oddsTotalBetAmonutKey;
    }


    //投注项 纯投注额 进球后缓存清零
    public static String getOddsTotalBetAmountTempKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalBetAmonutTempKey = seriesType + "rcs:risk:predict:oddsTotalBetAmountTemp.match_id.%s.match_type.%s.play_id.%s";
        oddsTotalBetAmonutTempKey = String.format(oddsTotalBetAmonutTempKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return oddsTotalBetAmonutTempKey;
    }
    public static String getOddsTotalCommonKey(OrderItem item,String playOption){
     return String.format("sub_play_id.%s.market_id.%s.play_options.%s",item.getSubPlayId(),item.getMarketId(),playOption);
    }

    //投注项 纯赔付额
    public static String getOddsTotalBetAmountPayKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalBetAmonutKey = seriesType + "rcs:risk:predict:oddsTotalBetAmountPay.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s";
        oddsTotalBetAmonutKey = String.format(oddsTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId());
        return oddsTotalBetAmonutKey;
    }

    //投注项 纯赔付额 进球后数据清零
    public static String getOddsTotalBetAmountPayTempKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalBetAmonutTempKey = seriesType + "rcs:risk:predict:oddsTotalBetAmountPayTemp.match_id.%s.match_type.%s.play_id.%s";
        oddsTotalBetAmonutTempKey = String.format(oddsTotalBetAmonutTempKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId());
        return oddsTotalBetAmonutTempKey;
    }

    //投注项 混合型
    public static String getOddsTotalBetAmountComplexKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalBetAmonutKey = seriesType + "rcs:risk:predict:oddsTotalBetAmountComplex.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s";
        oddsTotalBetAmonutKey = String.format(oddsTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId());
        return oddsTotalBetAmonutKey;
    }

    //投注项 混合型 进球后数据清零
    public static String getOddsTotalBetAmountComplexTempKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalBetAmonutTempKey = seriesType + "rcs:risk:predict:oddsTotalBetAmountComplexTemp.match_id.%s.match_type.%s.play_id.%s";
        oddsTotalBetAmonutTempKey = String.format(oddsTotalBetAmonutTempKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return oddsTotalBetAmonutTempKey;
    }

    //盘口位置 纯投注额
    public static String getPalceNumTotalBetAmountKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String placeNumTotalBetAmonutKey = seriesType + "rcs:risk:predict:palceNumTotalBetAmount.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.place_num.%s";
        placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getPlaceNum());
        return placeNumTotalBetAmonutKey;
    }

    //盘口位置 纯投注额 进球数据清零
    public static String getPalceNumTotalBetAmountTempKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String placeNumTotalBetAmonutKey = seriesType + "rcs:risk:predict:palceNumTotalBetAmountTemp.match_id.%s.match_type.%s.play_id.%s";
        placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return placeNumTotalBetAmonutKey;
    }

    //盘口位置 纯赔付额
    public static String getPalceNumTotalBetAmountPayKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String placeNumTotalBetAmonutKey = seriesType + "rcs:risk:predict:palceNumTotalBetAmountPay.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.place_num.%s";
        placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getPlaceNum());
        return placeNumTotalBetAmonutKey;
    }


    //盘口位置 纯赔付额 进球后数据清零
    public static String getPalceNumTotalBetAmountPayTempKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String placeNumTotalBetAmonutKey = seriesType + "rcs:risk:predict:palceNumTotalBetAmountPayTemp.match_id.%s.match_type.%s.play_id.%s";
        placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return placeNumTotalBetAmonutKey;
    }

    //盘口位置 混合型
    public static String getPalceNumTotalBetAmountComplexKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String placeNumTotalBetAmonutKey = seriesType + "rcs:risk:predict:palceNumTotalBetAmountComplex.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.place_num.%s";
        placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getPlaceNum());
        return placeNumTotalBetAmonutKey;
    }

    //盘口位置 混合型
    public static String getPalceNumTotalBetAmountComplexTempKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String placeNumTotalBetAmonutKey = seriesType + "rcs:risk:predict:palceNumTotalBetAmountComplexTemp.match_id.%s.match_type.%s.play_id.%s";
        placeNumTotalBetAmonutKey = String.format(placeNumTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return placeNumTotalBetAmonutKey;
    }
    public static String getMarketIdCommonKey(OrderItem item) {
        String key ="rcs:risk:predict:marketId.match_id.%s.match_type.%s.play_id.%s";
        key = String.format(key, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return key;
    }
    public static String getPlaceNumCommonKey(OrderItem item) {
        String key ="rcs:risk:predict:placeNum.match_id.%s.match_type.%s.play_id.%s";
        key = String.format(key, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return key;
    }



    public static String getCommonKey(OrderItem orderItem,String playOptions){
        return String.format("sub_play_id.%s.place_num.%s.play_options.%s",orderItem.getSubPlayId(),orderItem.getPlaceNum(),playOptions);
    }

    //投注项总投注笔数
    public static String getOddsTotalBetNumKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalBetNumKey = seriesType + "rcs:risk:predict:oddsTotalBetNum.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s";
        oddsTotalBetNumKey = String.format(oddsTotalBetNumKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId());
        return oddsTotalBetNumKey;
    }

    //投注项总投注笔数 进球后数据清零
    public static String getOddsTotalBetNumTempKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalBetNumKey = seriesType + "rcs:risk:predict:oddsTotalBetNumTemp.match_id.%s.match_type.%s.play_id.%s";
        oddsTotalBetNumKey = String.format(oddsTotalBetNumKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return oddsTotalBetNumKey;
    }

    //盘口位置 投注项 总投注笔数
    public static String getPalceNumTotalBetNumKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String getPalceNumTotalBetNumKey = seriesType + "rcs:risk:predict:palceNumTotalBetNum.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.place_num.%s";
        getPalceNumTotalBetNumKey = String.format(getPalceNumTotalBetNumKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getPlaceNum());
        return getPalceNumTotalBetNumKey;
    }

    //盘口位置 投注项 总投注笔数
    public static String getPalceNumTotalBetNumTempKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String getPalceNumTotalBetNumKey = seriesType + "rcs:risk:predict:palceNumTotalBetNumTemp.match_id.%s.match_type.%s.play_id.%s";
        getPalceNumTotalBetNumKey = String.format(getPalceNumTotalBetNumKey, item.getMatchId(), item.getMatchType(), item.getPlayId());
        return getPalceNumTotalBetNumKey;
    }

    //盘口投注总金额
    public static String getMarketTotalBetAmonutKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String marketTotalBetAmonutKey = seriesType + "rcs:risk:predict:marketTotalBetAmonut.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s";
        marketTotalBetAmonutKey = String.format(marketTotalBetAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId());
        return marketTotalBetAmonutKey;
    }


    //投注项最大赔付
    public static String getOddsTotalPaidAmonutKey(OrderItem item, String seriesType) {
        seriesType = getSeriesTypeStr(seriesType);
        String oddsTotalPaidAmonutKey = seriesType + "rcs:risk:predict:oddsTotalPaidAmonut.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s";
        oddsTotalPaidAmonutKey = String.format(oddsTotalPaidAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId());
        return oddsTotalPaidAmonutKey;
    }

    //投注项级别的期望值
    public static String getOddsProfitAmonutKey(OrderItem item) {
        String oddsProfitAmonutKey = "rcs:risk:predict:oddsProfitAmonut.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s";
        oddsProfitAmonutKey = String.format(oddsProfitAmonutKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId());
        return oddsProfitAmonutKey;
    }


    public static String getRcsOddsGoalRedisKey(OrderItem item) {
        return  String.format("RCS_SPORT_ID_STANDARD_MATCH_ID_REDIS_KEY:%s:%s", item.getSportId(), item.getMatchId());
    }


    /**
     * 后去单关串关标识  单关为"" 串关为2
     *
     * @param seriesType 1单关 2串关
     * @return
     */
    public static String getSeriesTypeStr(String seriesType) {
        if (seriesType.equals("1")) {
            seriesType = "";
        }
        return seriesType;
    }
}