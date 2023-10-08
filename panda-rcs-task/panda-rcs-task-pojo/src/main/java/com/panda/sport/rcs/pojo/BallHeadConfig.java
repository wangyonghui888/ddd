package com.panda.sport.rcs.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 头球配置
 */
@Data
@NoArgsConstructor
public class BallHeadConfig {

    public BallHeadConfig(Integer roundType, Boolean maxBallHeadAuto, Boolean minBallHeadAuto, Float maxBallHead, Float minBallHead) {
        this.roundType = roundType;
        this.maxBallHeadAuto = maxBallHeadAuto;
        this.minBallHeadAuto = minBallHeadAuto;
        this.maxBallHead = maxBallHead;
        this.minBallHead = minBallHead;
    }


    public BallHeadConfig(Integer roundType, Boolean maxBallHeadAuto, Boolean minBallHeadAuto, Float maxBallHead, Float minBallHead, BallHeadConfigFeature feature) {
        this.roundType = roundType;
        this.maxBallHeadAuto = maxBallHeadAuto;
        this.minBallHeadAuto = minBallHeadAuto;
        this.maxBallHead = maxBallHead;
        this.minBallHead = minBallHead;
        this.feature = feature;
    }

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

    /**
     * 特殊局
     */
    private BallHeadConfigFeature feature;


}
