package com.panda.rcs.logService.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Ao参数类型
 * @author Z9-jing
 */

@Getter
@AllArgsConstructor
public enum AoParameterEnum {
    g_goal(0, "Goal:", "g_goal"),
    twoInjTime(1, "2nd Inj Time", "2nd Inj Time"),
    oneInjTime(2, "1st inj Time", "1st inj Time"),
    refresh(3, "refresh", "refresh"),
    zeroOneFive(4, "Goal:00-15", "00-15"),
    oneFiveThree(5, "Goal:15-30", "15-30"),
    htDrawAdj(6, "Corner:HT", "HT"),
    htSix(7, "Corner:HT-60", "HT-60"),
    threeHt(8, "Goal:30-HT", "30-HT"),
    sixSevenFive(9, "Corner:60-75", "60-75"),
    sevenFiveFt(10, "Corner:75-HT", "75-HT"),
    ftDrawAdj(11, "FT", "FT"),
    g_corner(12, "Corner:", "g_corner"),
    g_booking(13, "Booking:", "g_booking"),
    g_yc(14, "YC:", "g_yc"),
    g_rc(15, "RC:", "g_rc"),
    ex_corner(16, "Corner:", "ex_corner"),
    AO_Extra(17, "AO-Extra", "AO-Extra"),
    AO_Regular(18, "AO-Regular", "AO-Regular"),
    Goal(19, "Goal", "Goal:"),
    Corner(20, "Corner", "Corner:"),
    Booking(21, "Booking", "Booking:"),
    YC(22, "YC", "YC:"),
    RC(23, "RC", "RC:"),
    EX(24, "ex_", "ex_"),
    ex_goal(25, "Goal:", "ex_goal"),
    main_switch(27, "mainSwitch", "漏单总开关"),
    Global_missed_orders(28,"GlobalMissedOrders","全局漏单"),
    Missing_label(29,"MissingLabel","标签漏单"),
    Global_switch(30,"GlobalSwitch","全局开关"),
    Batch_Settings(31,"BatchSettings","批量设置"),
    Settings(32,"Settings","默认设置"),
    Exception_batch_settings(33,"ExceptionBatchSettings","例外批量设置"),
    Single_Merchant_Settings(34,"SingleMerchantSettings","单商户设置"),
    Goal_point_warning_settings(35,"GoalPointWarningSettings","进球点预警设置"),
    League_Name(36,"LeagueName","联赛名称"),
    Number_of_Bets(37,"NumberOfBets","投注人数"),
    Single_Betting_Amount(38,"SingleBettingAmount","单笔投注额"),
    Seconds_before_goal(39,"SecondsBeforeGoal","联赛名称"),
    SET(40,"set","数据传输设置"),
    SET_DATA(26, "setData", "设置")

    ;


    private Integer id;
    private String code;
    private String name;

    public static String getNameById(Integer value) {
        AoParameterEnum[] businessModeEnums = values();
        for (AoParameterEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getId().equals(value)) {
                return businessModeEnum.getName();
            }
        }
        return null;
    }

    public static String getCodeByName(String name) {
        AoParameterEnum[] businessModeEnums = values();
        for (AoParameterEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getName().equals(name)) {
                return businessModeEnum.getCode();
            }
        }
        return null;
    }


}
