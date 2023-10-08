package com.panda.sport.rcs.oddin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author :wiker
 * @Date: 2023-13 14:24
 **/
@Getter
@AllArgsConstructor
public enum ResultingStatus {


    RESULTING_STATUS_UNSPECIFIED(0),
    RESULTING_STATUS_WON(1),
    RESULTING_STATUS_VOIDED(2),
    RESULTING_STATUS_LOST(3),
    RESULTING_STATUS_NOT_RESULTED(4),
    RESULTING_STATUS_PENDING_LOST(5),
    RESULTING_STATUS_REJECTED(6);

    private Integer code;


    public static ResultingStatus forNumber(int value) {
        switch (value) {
            case 0:
                return RESULTING_STATUS_UNSPECIFIED;
            case 1:
                return RESULTING_STATUS_WON;
            case 2:
                return RESULTING_STATUS_VOIDED;
            case 3:
                return RESULTING_STATUS_LOST;
            case 4:
                return RESULTING_STATUS_NOT_RESULTED;
            case 5:
                return RESULTING_STATUS_PENDING_LOST;
            case 6:
                return RESULTING_STATUS_REJECTED;
            default:
                return null;
        }
        /*private String message;*/

    }
}
