package com.panda.sport.rcs.enums;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  风控
 * @Date: 2020-06-14 14:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum RiskManagerCodeEnum {
    MTS(0, "MTS"),
    CLOSE(1, "PA");

    private String name;

    private Integer code;

    RiskManagerCodeEnum(Integer code,String name){
        this.code=code;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
