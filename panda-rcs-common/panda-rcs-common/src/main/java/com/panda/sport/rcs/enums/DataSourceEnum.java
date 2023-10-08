package com.panda.sport.rcs.enums;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2020-02-01 14:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum DataSourceEnum {
    SR(1, "SR"),
    BC(2, "BC"),
    BG(3, "BG"),
    PI(4, "PI");

    private Integer value;

    private String dataSource;

    DataSourceEnum(Integer value, String dataSource) {
        this.value = value;
        this.dataSource = dataSource;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
