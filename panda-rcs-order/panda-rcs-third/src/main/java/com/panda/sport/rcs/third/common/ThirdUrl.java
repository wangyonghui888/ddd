package com.panda.sport.rcs.third.common;

/**
 * @author Beulah
 * @date 2023/4/1 11:26
 * @description todo
 */
public class ThirdUrl {


    //=======================================BE 数据源======================================
    //获取profile url
    public final static String BE_PROFILE_URL = "/public/api/profile";
    //获取默认限额url
    public final static String BE_PRESET_URL = "/api/sportsbook/placebet/maxbet/preset";
    //获取最大限额url
    public final static String BE_MAXBET_URL = "/api/sportsbook/placebet/maxbet";
    //单关投注 url
    public final static String BE_ORDINARY_URL = "/api/sportsbook/placebet/ordinary";
    //串关投注url
    public final static String BE_EXPRESS_URL = "/api/sportsbook/placebet/express";
    //投注确认
    public final static String BE_RESULTS_URL = "/api/sportsbook/placebet/results/";
    //取消投注
    public final static String BE_CANCEL_URL = "/api/sportsbook/placebet/cancel";


    //=======================================BC 数据源======================================
    //获取最大限额url
    public final static String BC_MAXBET_URL = "/Bet/maxbet";

    //单关投注 URL：http://hostname/api/LangId/PartnerId/Bet/CreateBet
    public final static String BC_CREATE_BET_URL = "/Bet/CreateBet";

   /*在交易状态为“无应答”和“错误”状态下时调用
   无法得到“BetResulted”的返回
   使用此方法重新提交/重新发送/触发重新发送
   URL：http://hostname/api/LangId/PartnerId/PartnerAPI/ResendFailedTransfers
    */
   public final static String BET_RESEND_FAILED_TRANSFERS_URL = "/Bet/ResendFailedTransfers";


    //标记投注为现金支付
    public final static String BC_MARK_BET_AS_CASHOUT_URL = "/Bet/express";
    //串关投注url
    public final static String BC_EXPRESS_URL = "/Bet/express";

    //投注确认
    public final static String BC_RESULTS_URL = "/Bet/results";

    //取消投注 URL：http://hostname/api/LangId/PartnerId/PartnerAPI/ReturnBet
    public final static String BC_CANCEL_URL = "/Bet/ReturnBet";


    /**
     * 红猫token接口地址
     */
    public final static String RED_CAT_TOKEN_URL = "/api/v1/authorize";
    /**
     * 红猫投注接口地址
     */
    public final static String RED_CAT_BET_PLACED_URL = "/api/v2/placebet";
    /**
     * 红猫取消投注接口
     */
    public final static String RED_CAT_BET_CANCEL_URL="/api/v2/cancelBet";
    /**
     * 请求头组装token是使用
     */
    public final static String BEARER="Bearer";




}
