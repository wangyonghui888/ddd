package com.panda.rcs.stray.limit.enums;

import com.panda.rcs.stray.limit.utils.BaseUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 串关类型枚举
 * @Author : Paca
 * @Date : 2022-04-06 16:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum SeriesTypeEnum {

    SINGLE(1, 1, 1, "单关"),

    TWO(2001, 2, 1, "2串1"),

    THREE(3001, 3, 1, "3串1"),
    THREE_MAX(3004, 3, 4, "3串4"),

    FOUR(4001, 4, 1, "4串1"),
    FOUR_MAX(40011, 4, 11, "4串11"),

    FIVE(5001, 5, 1, "5串1"),
    FIVE_MAX(50026, 5, 26, "5串26"),

    SIX(6001, 6, 1, "6串1"),
    SIX_MAX(60057, 6, 57, "6串57"),

    SEVEN(7001, 7, 1, "7串1"),
    SEVEN_MAX(700120, 7, 120, "7串120"),

    EIGHT(8001, 8, 1, "8串1"),
    EIGHT_MAX(800247, 8, 247, "8串247"),

    NINE(9001, 9, 1, "9串1"),
    NINE_MAX(900502, 9, 502, "9串502"),

    TEN(10001, 10, 1, "10串1"),
    TEN_MAX(10001013, 10, 1013, "10串1013");

    /**
     * 串关类型
     */
    private Integer seriesType;

    /**
     * M串N 中的 M
     */
    private Integer m;

    /**
     * M串N 中的 N
     */
    private Integer n;

    /**
     * 串关名称
     */
    private String name;

    /**
     * 获取单个投注项参与注单的数量
     *
     * @param matchAmount 赛事数量
     * @return
     */
    public int getNum(int matchAmount) {
        if (this.getN() > 1) {
            return BaseUtils.combinationSum2(matchAmount - 1);
        }
        if (this.getM() < matchAmount) {
            return BaseUtils.combination(matchAmount, this.getM());
        }
        return 1;
    }

    public static SeriesTypeEnum getBySeriesType(Integer seriesType) {
        for (SeriesTypeEnum seriesTypeEnum : values()) {
            if (seriesTypeEnum.getSeriesType().equals(seriesType)) {
                return seriesTypeEnum;
            }
        }
        return SINGLE;
    }

    /**
     * 获取单个投注项参与注单的数量
     *
     * @param seriesType
     * @param matchAmount
     * @return
     */
    public static int getNum(Integer seriesType, int matchAmount) {
        return getBySeriesType(seriesType).getNum(matchAmount);
    }

    public static String getNameByType(Integer seriesType) {
        for (SeriesTypeEnum seriesTypeEnum : values()) {
            if (seriesTypeEnum.getSeriesType().equals(seriesType)) {
                return seriesTypeEnum.name;
            }
        }
        return null;
    }
}
