package com.panda.sport.rcs.pojo.enums;

/**
 * @author :  skykong
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.constants
 * @Description :
 * @Date: 2022-6-4 15:02
 */
public enum SpecialEnum {
    Special(100,"特殊用户"),
    ordinary(200,"一般用户"),
    ;
    private Integer id;
    private String remark;
    private SpecialEnum(Integer id,String remark) {
        this.id = id;
        this.remark=remark;
    }
    public Integer getId() {
        return id;
    }
    public String getRemark() {
        return remark;
    }

    public static SpecialEnum getAbnormalUserType(Integer id){
        for(SpecialEnum abnormalUserTypeEnum:SpecialEnum.values()){
            if(abnormalUserTypeEnum.getId().intValue()==id.intValue()){
                return abnormalUserTypeEnum;
            }
        }
        return null;
    }

}
