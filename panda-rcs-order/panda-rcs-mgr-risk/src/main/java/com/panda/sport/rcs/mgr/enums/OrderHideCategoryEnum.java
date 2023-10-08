package com.panda.sport.rcs.mgr.enums;

/**
 * @author :  skykong
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.constants
 * @Description :
 * @Date: 2022-6-4 15:02
 */
public enum OrderHideCategoryEnum {
    USER(0,"用户"),
    LABEL(1,"标签"),
    EQUIPMENT(2,"设备类型"),
    MERCHANT(3,"商户"),
    OTHER(4,"其他商户"),
    DYNAMIC(5,"动态藏单"),
    AMOUNT(6,"金额区间"),
    ;
    private Integer id;
    private String remark;
    private OrderHideCategoryEnum(Integer id, String remark) {
        this.id = id;
        this.remark=remark;
    }
    public Integer getId() {
        return id;
    }
    public String getRemark() {
        return remark;
    }

    public static OrderHideCategoryEnum getAbnormalUserType(Integer id){
        for(OrderHideCategoryEnum abnormalUserTypeEnum: OrderHideCategoryEnum.values()){
            if(abnormalUserTypeEnum.getId().intValue()==id.intValue()){
                return abnormalUserTypeEnum;
            }
        }
        return null;
    }

}
