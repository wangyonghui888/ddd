package com.panda.sport.rcs.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SwitchEnum {

    OPEN(1,"开"),
    CLOSE(0,"关"),
    ;

    private Integer id;
    private String desc;

    /**
     * 获取描述
     * @param id
     * @return
     */
    public static String getDescById(Integer id){
        if(id == null){
            return "";
        }
        for (SwitchEnum value : SwitchEnum.values()) {
            if(value.getId().compareTo(id) == 0){
                return value.getDesc();
            }
        }
        return "";
    }
}
