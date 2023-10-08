package com.panda.sport.rcs.trade.vo.tourTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 大小球头默认配置
 */
public enum BallHeadDefaultConfig {


    SPORT_8_172_3(8, 172, 3, false, false, 21.5F, 0.5F),
    SPORT_8_172_5(8, 172, 5, false, false, 32.5F, 0.5F),
    SPORT_8_172_7(8, 172, 7, false, false, 43.5F, 0.5F),


    SPORT_8_173_3(8, 173, 3, true, false, 0F, 22.5F),
    SPORT_8_173_5(8, 173, 5, true, false, 0F, 33.5F),
    SPORT_8_173_7(8, 173, 7, true, false, 0F, 44.5F),

    SPORT_8_176_3(8, 176, 3, false, false, 10.5F, 2.5F),
    SPORT_8_176_5(8, 176, 5, false, false, 10.5F, 2.5F),
    SPORT_8_176_7(8, 176, 7, false, false, 10.5F, 2.5F),

    SPORT_8_177_3(8, 177, 3, true, false, 0F, 11.5F),
    SPORT_8_177_5(8, 177, 5, true, false, 0F, 11.5F),
    SPORT_8_177_7(8, 177, 7, true, false, 0F, 11.5F),

    //排球
    SPORT_9_172_3(9, 172, 3, false, false, 49.5F, 0.5F),
    SPORT_9_172_5(9, 172, 5, false, false, 74.5F, 0.5F),
    SPORT_9_172_7(9, 172, 7, false, false, 99.5F, 0.5F),

    SPORT_9_173_3(9, 173, 3, true, false, 0F, 50.5F),
    SPORT_9_173_5(9, 173, 5, true, false, 0F, 75.5F),
    SPORT_9_173_7(9, 173, 7, true, false, 0F, 100.5F),

    SPORT_9_253_3(9, 253, 3, false, false, 24.5F, 2.5F),
    SPORT_9_253_5(9, 253, 5, false, false, 24.5F, 2.5F),
    SPORT_9_253_7(9, 253, 7, false, false, 24.5F, 2.5F),
    //253 决胜局配置
    SPORT_9_253_3_LAST(9, 253, 3, false, false, 14.5F, 2.5F, BallHeadConfigFeature.LAST),
    SPORT_9_253_5_LAST(9, 253, 5, false, false, 14.5F, 2.5F, BallHeadConfigFeature.LAST),
    SPORT_9_253_7_LAST(9, 253, 7, false, false, 14.5F, 2.5F, BallHeadConfigFeature.LAST),

    SPORT_9_254_3(9, 254, 3, true, false, 0F, 25.5F),
    SPORT_9_254_5(9, 254, 5, true, false, 0F, 25.5F),
    SPORT_9_254_6(9, 254, 7, true, false, 0F, 25.5F),
    //254 决胜局配置
    SPORT_9_254_3_LAST(9, 254, 3, true, false, 0F, 15.5F, BallHeadConfigFeature.LAST),
    SPORT_9_254_5_LAST(9, 254, 5, true, false, 0F, 15.5F, BallHeadConfigFeature.LAST),
    SPORT_9_254_6_LAST(9, 254, 7, true, false, 0F, 15.5F, BallHeadConfigFeature.LAST),

    //斯诺克
    SPORT_7_181_7(7, 181, 7, false, false, 3.5F, 1.5F),
    SPORT_7_181_9(7, 181, 9, false, false, 4.5F, 1.5F),
    SPORT_7_181_11(7, 181, 11, false, false, 5.5F, 1.5F),
    SPORT_7_181_17(7, 181, 17, false, false, 8.5F, 1.5F),
    SPORT_7_181_19(7, 181, 19, false, false, 9.5F, 1.5F),
    SPORT_7_181_21(7, 181, 21, false, false, 10.5F, 1.5F),
    SPORT_7_181_25(7, 181, 25, false, false, 12.5F, 1.5F),
    SPORT_7_181_35(7, 181, 35, false, false, 17.5F, 1.5F),

    SPORT_7_182_7(7, 182, 7, false, false, 6.5F, 4.5F),
    SPORT_7_182_9(7, 182, 9, false, false, 8.5F, 5.5F),
    SPORT_7_182_11(7, 182, 11, false, false, 10.5F, 6.5F),
    SPORT_7_182_17(7, 182, 17, false, false, 16.5F, 9.5F),
    SPORT_7_182_19(7, 182, 19, false, false, 18.5F, 10.5F),
    SPORT_7_182_21(7, 182, 21, false, false, 20.5F, 11.5F),
    SPORT_7_182_25(7, 182, 25, false, false, 24.5F, 13.5F),
    SPORT_7_182_35(7, 182, 35, false, false, 34.5F, 18.5F),

    SPORT_7_185_7(7, 185, 7, false, false, 146.5F, 0.5F),
    SPORT_7_185_9(7, 185, 9, false, false, 146.5F, 0.5F),
    SPORT_7_185_11(7, 185, 11, false, false, 146.5F, 0.5F),
    SPORT_7_185_17(7, 185, 17, false, false, 146.5F, 0.5F),
    SPORT_7_185_19(7, 185, 19, false, false, 146.5F, 0.5F),
    SPORT_7_185_21(7, 185, 21, false, false, 146.5F, 0.5F),
    SPORT_7_185_25(7, 185, 25, false, false, 146.5F, 0.5F),
    SPORT_7_185_35(7, 185, 35, false, false, 146.5F, 0.5F),

    SPORT_7_186_7(7, 186, 7, true, true, 0F, 0F),
    SPORT_7_186_9(7, 186, 9, true, true, 0F, 0F),
    SPORT_7_186_11(7, 186, 11, true, true, 0F, 0F),
    SPORT_7_186_17(7, 186, 17, true, true, 0F, 0F),
    SPORT_7_186_19(7, 186, 19, true, true, 0F, 0F),
    SPORT_7_186_21(7, 186, 21, true, true, 0F, 0F),
    SPORT_7_186_25(7, 186, 25, true, true, 0F, 0F),
    SPORT_7_186_35(7, 186, 35, true, true, 0F, 0F),

    //棒球
    SPORT_3_243_9(3, 243, 9, true, false, 0F, 1.5F),
    SPORT_3_244_9(3, 244, 9, true, false, 0F, 0.5F),
    SPORT_3_245_9(3, 245, 9, true, false, 0F, 0.5F),
    SPORT_3_246_9(3, 246, 9, true, false, 0F, 0.5F),
    SPORT_3_249_9(3, 249, 9, true, false, 0F, 0.5F),
    SPORT_3_250_9(3, 250, 9, true, false, 0F, 0.5F),
    SPORT_3_251_9(3, 251, 9, true, false, 0F, 0.5F),
    SPORT_3_252_9(3, 252, 9, true, false, 0F, 0.5F),
    SPORT_3_290_9(3, 290, 9, true, false, 0F, 0.5F),

    //网球
    SPORT_5_154_3(5, 154, 3, false, false, 1.5F, 1.5F),
    SPORT_5_154_5(5, 154, 5, false, false, 2.5F, 1.5F),
    SPORT_5_155_3(5, 155, 3, false, false, 11.5F, 0.5F),
    SPORT_5_155_5(5, 155, 5, false, false, 17.5F, 0.5F),
    SPORT_5_202_3(5, 202, 3, false, false, 38.5F, 12.5F),
    SPORT_5_202_5(5, 202, 5, false, false, 64.5F, 18.5F),
    SPORT_5_163_3(5, 163, 3, false, false, 5.5F, 0.5F),
    SPORT_5_163_5(5, 163, 5, false, false, 5.5F, 0.5F),
    SPORT_5_164_3(5, 164, 3, false, false, 12.5F, 6.5F),
    SPORT_5_164_5(5, 164, 5, false, false, 12.5F, 6.5F),

    //羽毛球
    SPORT_10_172_3(10, 172, 3, false, false, 41.5F, 0.5F),
    SPORT_10_173_3(10, 173, 3, true, false, 0F, 42.5F),
    SPORT_10_176_3(10, 176, 3, false, false, 20.5F, 2.5F),
    SPORT_10_177_3(10, 177, 3, true, false, 0F, 21.5F),

    //冰球 冰球只有三节，所以roundType=0
    SPORT_4_4_0(4, 4, 0, true, false, 0F, 0.5F),
    SPORT_4_2_0(4, 2, 0, true, false, 0F, 0.5F),
    //加时赛配置
    SPORT_4_4_0_PLUS(4, 4, 0, true, false, 0F, 1.5F, BallHeadConfigFeature.PLUS_TIME),
    //加时赛配置
    SPORT_4_2_0_PLUS(4, 2, 0, true, false, 0F, 1.5F, BallHeadConfigFeature.PLUS_TIME),
    SPORT_4_262_0(4, 262, 0, true, false, 0F, 0.5F),
    SPORT_4_268_0(4, 268, 0, true, false, 0F, 0.5F),

    ;

    /**
     * 赛种
     */
    private Integer sportId;
    /**
     * 玩法ID
     */
    private Integer payId;
    /**
     * 局数 比如7局表示7局4胜
     */
    private Integer roundType;
    /**
     * 最大头球不限
     */
    private Boolean maxBallHeadAuto;
    /**
     * 最小头球不限
     */
    private Boolean minBallHeadAuto;

    /**
     * 最大头球数
     */
    private Float maxBallHead;
    /**
     * 最小头球数
     */
    private Float minBallHead;

    private BallHeadConfigFeature feature;

    BallHeadDefaultConfig(Integer sportId, Integer payId, Integer roundType, Boolean maxBallHeadAuto, Boolean minBallHeadAuto, Float maxBallHead, Float minBallHead, BallHeadConfigFeature feature) {
        this.sportId = sportId;
        this.payId = payId;
        this.roundType = roundType;
        this.maxBallHeadAuto = maxBallHeadAuto;
        this.minBallHeadAuto = minBallHeadAuto;
        this.maxBallHead = maxBallHead;
        this.minBallHead = minBallHead;
        this.feature = feature;
    }

    BallHeadDefaultConfig(Integer sportId, Integer payId, Integer roundType, Boolean maxBallHeadAuto, Boolean minBallHeadAuto, Float maxBallHead, Float minBallHead) {
        this.sportId = sportId;
        this.payId = payId;
        this.roundType = roundType;
        this.maxBallHeadAuto = maxBallHeadAuto;
        this.minBallHeadAuto = minBallHeadAuto;
        this.maxBallHead = maxBallHead;
        this.minBallHead = minBallHead;
        this.feature = null;
    }

    public static List<BallHeadConfig> genDefaultConfig(Integer sportId, Integer payId) {
        List<BallHeadConfig> list = new ArrayList<>();
        for (BallHeadDefaultConfig item : BallHeadDefaultConfig.values()) {
            if (item.sportId.equals(sportId) && item.payId.equals(payId)) {
                list.add(build(item));
            }
        }
        return list;
    }

    private static BallHeadConfig build(BallHeadDefaultConfig entity) {
        return new BallHeadConfig(entity.roundType, entity.maxBallHeadAuto, entity.minBallHeadAuto, entity.maxBallHead, entity.minBallHead, entity.feature);
    }

    public Integer getSportId() {
        return sportId;
    }

    public Integer getPayId() {
        return payId;
    }

    public Integer getRoundType() {
        return roundType;
    }

    public Boolean getMaxBallHeadAuto() {
        return maxBallHeadAuto;
    }

    public Boolean getMinBallHeadAuto() {
        return minBallHeadAuto;
    }

    public Float getMaxBallHead() {
        return maxBallHead;
    }

    public Float getMinBallHead() {
        return minBallHead;
    }
}
