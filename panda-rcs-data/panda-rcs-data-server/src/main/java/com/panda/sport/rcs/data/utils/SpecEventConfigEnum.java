package com.panda.sport.rcs.data.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务枚举
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/10 18:19
 */
@Getter
@AllArgsConstructor
public enum SpecEventConfigEnum {
    
    PENALTY_AWARDED("PK", "penalty_awarded",1),
    BREAKAWAY("单刀", "breakaway",2),
    DFK("危险任意球", "dfk",3),
    DANGER_BALL("最后几分钟危险球", "danger_ball",4),
    ;
    
    private String eventName;
    private String eventCode;
    private Integer eventType;


    /**
     * 是否是特殊事件
     * @param eventCode
     * @return
     */
    public static boolean hasSpecEvent(String eventCode){
        for(SpecEventConfigEnum item : SpecEventConfigEnum.values()){
            if(item.eventCode.equals(eventCode)){
                return true;
            }
        }
        return false;
    }
}
