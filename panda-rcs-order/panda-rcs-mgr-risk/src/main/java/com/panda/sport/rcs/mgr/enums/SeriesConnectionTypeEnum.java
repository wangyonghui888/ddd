package com.panda.sport.rcs.mgr.enums;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.enums
 * @Description :  TODO
 * @Date: 2020-09-12 15:12
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum SeriesConnectionTypeEnum {
    TWO(1,"2串1"),
    THREE(2,"3串N"),
    FOUR(3,"4串N"),
    FIVE(4,"5串N"),
    SIX(5,"6串N"),
    SEVEN(6,"7串N"),
    EIGHT(7,"8串N以及以上");
    /**
     * 串关类型
     */
    private Integer seriesConnectionType;
    /**
     * 描述
     */
    private String vales;

    public Integer getSeriesConnectionType() {
        return seriesConnectionType;
    }

    public String getVales() {
        return vales;
    }

    SeriesConnectionTypeEnum(Integer seriesConnectionType, String vales) {
        this.seriesConnectionType = seriesConnectionType;
        this.vales = vales;
    }
}
