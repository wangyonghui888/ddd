package com.panda.sport.rcs.dj.enums;

/**
 *
 * 串关枚举
 */
public enum SeriesEnum {
    TWO(2, 1, 2001),
    Three(3, 4, 3004),
    Four(4, 11, 40011),
    Five(5, 26, 50026),
    Six(6, 57, 60057),
    Seven(7, 120, 700120),
    Eight(8, 247, 800247),
    Nine(9, 502, 900502),
    Ten(10, 1013, 10001013);
    //关数
    private Integer seriesNum;

    //可组合的最大串关数
    private Integer seriesMax;

    //拼接标识
    private Integer seriesJoin;

    public Integer getSeriesNum() {
        return seriesNum;
    }

    public Integer getSeriesMax() {
        return seriesMax;
    }

    public void setSeriesNum(Integer seriesNum) {
        this.seriesNum = seriesNum;
    }

    public void setSeriesMax(Integer seriesMax) {
        this.seriesMax = seriesMax;
    }

    public void setSeriesJoin(Integer seriesJoin) {
        this.seriesJoin = seriesJoin;
    }

    public Integer getSeriesJoin() {
        return seriesJoin;
    }

    private SeriesEnum(Integer seriesNum, Integer seriesMax, Integer seriesJoin) {
        this.seriesNum = seriesNum;
        this.seriesMax = seriesMax;
        this.seriesJoin = seriesJoin;
    }

    /**
     * 根据关数获取枚举
     *
     * @param seriesNum
     * @return
     */
    public static SeriesEnum getSeriesEnumBySeriesNum(Integer seriesNum) {
        for (SeriesEnum seriesEnum : SeriesEnum.values()) {
            if (seriesEnum.getSeriesNum().equals(seriesNum)) {
                return seriesEnum;
            }
        }
        return null;
    }

    /**
     * 根据最大组合串关数量获取枚举
     *
     * @param seriesMax
     * @return
     */
    public static SeriesEnum getSeriesEnumBySeriesMax(Integer seriesMax) {
        for (SeriesEnum seriesEnum : SeriesEnum.values()) {
            if (seriesEnum.getSeriesMax().equals(seriesMax)) {
                return seriesEnum;
            }
        }
        return null;
    }

    /**
     * 根据字符串标识获取枚举
     *
     * @param seriesJoin
     * @return
     */
    public static SeriesEnum getSeriesEnumBySeriesJoin(Integer seriesJoin) {
        for (SeriesEnum seriesEnum : SeriesEnum.values()) {
            if (seriesEnum.getSeriesJoin().equals(seriesJoin)) {
                return seriesEnum;
            }
        }
        return null;
    }

}
