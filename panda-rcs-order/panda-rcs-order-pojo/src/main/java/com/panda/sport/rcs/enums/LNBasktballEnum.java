package com.panda.sport.rcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LNBasktballEnum {

    ShangBanChang1(145, 87),
    ShangBanChang2(146,97),

    ShangBanChangOddEven(53,42),
    ShangBanChangMaxMin(51,18),
    ShangBanChangRangFen(52,19),
    COUNT1(145,198),
    COUNT2(146,199),
    QuanChangOddEven(65,40),
    QuanChangMaxMin(63,38),
    QuanChangRangFen(64,39);



    /**
     * 被控玩法id
     */
    private Integer bkPlayId;

    /**
     * 主控玩法id
     */
    private Integer zkPlayId;

    public static Integer getNameById(Integer bkPlayId) {
        for (LNBasktballEnum ele : values()) {
            if(ele.getBkPlayId().equals(bkPlayId) ) {
                return ele.zkPlayId;
            }
        }
        return null;
    }

}
