package com.panda.sport.rcs.third.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * token类型枚举
 * @author vere
 * @date 2023-05-27
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum TokenTypeEnum {

    //两种token  1 Bet Assessment  API           2 Bet Feed Receiver API
    /**
     * redCat缓存
     */
    BetAssessmentAPI(1, "投注"),
    BetFeedReceiverAPI(2, "收到订单确认消息"),
    ;
    private int code;
    private String text;

    /**
     * 通过code获取对应枚举类型
     * @param code
     * @return
     */
    public static TokenTypeEnum getByCode(Integer code){
        TokenTypeEnum[] enums= TokenTypeEnum.values();
        for (TokenTypeEnum anEnum : enums) {
            if (anEnum.getCode()==code) {
                return anEnum;
            }
        }
        return null;
    }
}
