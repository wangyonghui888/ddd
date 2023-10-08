package com.panda.sport.rcs.mgr.aspect;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.aspect
 * @Description :  类型
 * @Date: 2020-04-02 8:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum  RcsLockSeriesTypeEnum {
    All(0,"所有"),
    Single(1,"单关"),
    Duplex(2,"串关");

    private Integer code;
    private String name;


    RcsLockSeriesTypeEnum(Integer code,String name){
        this.code=code;
        this.name=name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
