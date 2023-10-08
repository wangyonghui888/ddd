package com.panda.sport.sdk.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * @author Regan
 * @create 2023/9/11 15:31
 */

@Getter
@AllArgsConstructor
public enum LNBasktballEnum {

    ShangBanChangRangFen(52,19),
    ShangBanChang1(145, 87),
    ShangBanChang2(146,97),
    ShangBanChangOddEven(53,42),
    ShangBanChangMaxMin(51,18),
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
        return bkPlayId;
    }

}

