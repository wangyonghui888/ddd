package com.panda.sport.rcs.enums;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2020-03-07 17:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum DataSourceTypeEnum {
    AUTOMATIC(0, "自动"),
    MANUAL(1, "手动");
    private Integer value;
    private String code;

    DataSourceTypeEnum(Integer value, String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
