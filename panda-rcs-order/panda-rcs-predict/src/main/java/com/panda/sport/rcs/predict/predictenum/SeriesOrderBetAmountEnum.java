package com.panda.sport.rcs.predict.predictenum;

/**
 * 串关货量枚举
 */
public enum SeriesOrderBetAmountEnum {


    Three(3, 4, 3, 3004),
    Four(4, 11, 7, 40011),
    Five(5, 26, 15, 50026),
    Six(6, 57, 31, 60057),
    Seven(7, 120, 63, 700120),
    Eight(8, 247, 127, 800247),
    Nine(9, 502, 255, 900502),
    Ten(10, 1013, 511, 10001013);
    //关数
    private Integer seriesNum;


    //每个投注项参与的注数
    private Integer itemNums;

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

    public Integer getItemNums() {
        return itemNums;
    }

    public void setItemNums(Integer itemNums) {
        this.itemNums = itemNums;
    }

    private SeriesOrderBetAmountEnum(Integer seriesNum, Integer seriesMax, Integer itemNums, Integer seriesJoin) {
        this.seriesNum = seriesNum;
        this.seriesMax = seriesMax;
        this.itemNums = itemNums;
        this.seriesJoin = seriesJoin;
    }

    /**
     * 根据关数获取枚举
     *
     * @param seriesNum
     * @return
     */
    public static SeriesOrderBetAmountEnum getSeriesEnumBySeriesNum(Integer seriesNum) {
        for (SeriesOrderBetAmountEnum seriesEnum : SeriesOrderBetAmountEnum.values()) {
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
    public static SeriesOrderBetAmountEnum getSeriesEnumBySeriesMax(Integer seriesMax) {
        for (SeriesOrderBetAmountEnum seriesEnum : SeriesOrderBetAmountEnum.values()) {
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
    public static SeriesOrderBetAmountEnum getSeriesEnumBySeriesJoin(Integer seriesJoin) {
        for (SeriesOrderBetAmountEnum seriesEnum : SeriesOrderBetAmountEnum.values()) {
            if (seriesEnum.getSeriesJoin().equals(seriesJoin)) {
                return seriesEnum;
            }
        }
        return null;
    }

}
