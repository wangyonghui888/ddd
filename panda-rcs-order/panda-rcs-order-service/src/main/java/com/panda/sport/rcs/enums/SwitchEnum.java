package com.panda.sport.rcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SwitchEnum {

    OPEN(0,"开"),
    CLOSE(1,"关"),
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
