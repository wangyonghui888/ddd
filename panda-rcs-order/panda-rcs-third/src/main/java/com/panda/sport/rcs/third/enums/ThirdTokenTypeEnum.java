package com.panda.sport.rcs.third.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方token类型枚举
 * @author vere
 * @date 2023-05-27
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ThirdTokenTypeEnum {

    /**
     * redCat缓存
     */
    RC(1, "redCat缓存")
    ;
    private int code;
    private String text;

    /**
     * 通过code获取对应枚举类型
     * @param code
     * @return
     */
    public static ThirdTokenTypeEnum getByCode(Integer code){
        ThirdTokenTypeEnum[] enums= ThirdTokenTypeEnum.values();
        for (ThirdTokenTypeEnum anEnum : enums) {
            if (anEnum.getCode()==code) {
                return anEnum;
            }
        }
        return null;
    }
}
