package com.panda.sport.rcs.profit.utils;

import com.panda.sport.rcs.profit.enums.ProfitPlayIdEnum;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.utils
 * @Description :  期望值
 * @Date: 2020-02-10 9:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class ProfitUtil {
    /**
     * @Description   大小球
     * @Param [playId]
     * @Author  toney
     * @Date  10:03 2020/2/10
     * @return java.lang.Boolean
     **/
    public static Boolean checkGoalLine(Integer playId){
       //if(playId == 2 || playId == 18 || playId == 114||playId == 122)
        if(playId.compareTo(ProfitPlayIdEnum.OverUnder.getCode())==0 ||
                playId.compareTo(ProfitPlayIdEnum.Halftime_OverUnder.getCode())==0 ||
                playId.compareTo(ProfitPlayIdEnum.Corners_Total.getCode())==0 ||
                playId.compareTo(ProfitPlayIdEnum.Corners_Halftime_Total.getCode())==0)
       {
            return true;
        }
       return  false;
    }
    /**
     * @Description  判断是不是让分盘
     * @Param [playId]
     * @Author  toney
     * @Date  10:24 2020/2/10
     * @return java.lang.Boolean
     **/
    public static Boolean checkIsHandicap(Integer playId){
        if(checkGoalLine(playId)){
            return true;
        }
        if(checkAsianHandicap(playId)){
            return true;
        }
        return false;
    }
    /**
     * @Description   让球
     * @Param [playId]
     * @Author  toney
     * @Date  10:18 2020/2/10
     * @return java.lang.Boolean
     **/
    public static Boolean checkAsianHandicap(Integer playId){
        //if(playId == 4 || playId == 19|| playId == 113 || playId ==121){
        if(playId.compareTo(ProfitPlayIdEnum.Handicap.getCode())==0 ||
                playId.compareTo(ProfitPlayIdEnum.Halftime_Handicap.getCode())==0 ||
                playId.compareTo(ProfitPlayIdEnum.Corners_Handicap.getCode())==0||
                playId.compareTo(ProfitPlayIdEnum.Corners_Halftime_Handicap.getCode())==0){
            return true;
        }
        return  false;
    }
    /**
     * @Description   其它玩法，单双、双方都进球、哪个球队获得角球多、角球单/双
     * @Param [playId]
     * @Author  toney
     * @Date  11:05 2020/2/10
     * @return java.lang.Boolean
     **/
    public static Boolean CheckOther(Integer playId){
        //if(playId == 12 || playId == 15 || playId ==111 || playId ==118){
        if(playId.compareTo(ProfitPlayIdEnum.BothTeamsToScore.getCode())==0 ||
                playId.compareTo(ProfitPlayIdEnum.OddEven.getCode())==0 ||
                playId.compareTo(ProfitPlayIdEnum.Corner_1x2.getCode())==0 ||
                playId.compareTo(ProfitPlayIdEnum.Corners_OddEven.getCode())==0||
                playId.compareTo(ProfitPlayIdEnum.Corner_alftime_1x2.getCode())==0){
            return true;
        }
        return  false;
    }

    public static void main(String[] args){
        System.out.println(checkAsianHandicap(4));
    }
}
