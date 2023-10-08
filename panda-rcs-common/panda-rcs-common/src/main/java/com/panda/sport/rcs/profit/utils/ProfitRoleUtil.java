package com.panda.sport.rcs.profit.utils;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.profit.utils
 * @Description :  业务规则
 * @Date: 2020-03-02 12:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class ProfitRoleUtil {
    private static String MARKET_ODDS_VALUE_SPLIT = "/";

    /**
     * 让球主队矩阵规则判断
     *
     * @param m                 主队分数
     * @param n                 客队分数
     * @param marketValueString
     * @return 1:赢  2：赢半  3:输 4：输半 5：走水           "-2.25"
     */
    public static Integer HandicapHomeMatrix1(Integer m, Integer n, String marketValueString) {
        String[] marketOddsValues = marketValueString.split(MARKET_ODDS_VALUE_SPLIT);
        /*** "0:0:" ***/
        Integer winTimes = 0;
        Integer lostTimes = 0;
        Integer drawTimes = 0;
        for (String marketOddsValueString : marketOddsValues) {
            Double marketOddsValue = Double.parseDouble(marketOddsValueString);
            if ("-".equals(marketValueString.substring(0, 1))) {
                marketOddsValue = Math.abs(marketOddsValue) * -1;
            }
            if (m + marketOddsValue > n) {
                winTimes++;
            } else if (m + marketOddsValue == n) {
                drawTimes++;
            } else {
                lostTimes++;
            }
        }
        //全赢
        if (winTimes == marketOddsValues.length) {
            return 1;
        }                                                                                  //赢半
        if (marketOddsValues.length > 1 && winTimes == marketOddsValues.length - 1) {
            return 2;
        }
        //全输
        if (lostTimes == marketOddsValues.length) {
            return 3;
        }

        //输半
        if (marketOddsValues.length > 1 && lostTimes == marketOddsValues.length - 1) {
            return 4;
        }

        //走水
        if (drawTimes == marketOddsValues.length) {
            return 5;
        }
        return 0;
    }

    /**
     * 让球客队矩阵规则判断
     *
     * @param m                 主队分数
     * @param n                 客队分数
     * @param marketValueString
     * @return 1:赢  2：赢半  3:输 4：输半 5：走水
     */
    public static Integer HandicapAwayMatrix1(Integer m, Integer n, String marketValueString, String scoreBenchmark) {
        String[] marketOddsValues = marketValueString.split(MARKET_ODDS_VALUE_SPLIT);


        Integer winTimes = 0;
        Integer lostTimes = 0;
        Integer drawTiems = 0;

        for (String marketOddsValueString : marketOddsValues) {
            Double marketOddsValue = Double.parseDouble(marketOddsValueString);
            if ("-".equals(marketValueString.substring(0, 1))) {
                marketOddsValue = Math.abs(marketOddsValue) * -1;
            }
            if (n + marketOddsValue - m > 0) {
                winTimes++;
            } else if (n + marketOddsValue - m < 0) {
                lostTimes++;
            } else {
                drawTiems++;
            }
        }
        //全赢
        if (winTimes == marketOddsValues.length) {
            return 1;
        }

        //赢半
        if (marketOddsValues.length > 1 && winTimes == marketOddsValues.length - 1) {
            return 2;
        }


        //全输
        if (lostTimes == marketOddsValues.length) {
            return 3;
        }

        //输半
        if (marketOddsValues.length > 1 && lostTimes == marketOddsValues.length - 1) {
            return 4;
        }

        //走水
        if (drawTiems == marketOddsValues.length) {
            return 5;
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println("m=0;n=0;-0/0.5;" + HandicapAwayMatrix1(0, 0, "-0/0.5", ""));
        /*System.out.println("m=0;n=0;-0/0.5;" +  HandicapAwayMatrix1(0, 0, "-0/0.5"));
        System.out.println("m=0;n=0;+0/0.5;" +  HandicapAwayMatrix1(0, 0, "+0/0.5"));
        System.out.println("m=0;n=0;-0.5/1;" +  HandicapAwayMatrix1(0, 0, "-0.5/1"));
        System.out.println("m=0;n=0;+0.5/1;" +  HandicapAwayMatrix1(0, 0, "+0.5/1"));

        System.out.println("m=0;n=0;+0;" +  HandicapAwayMatrix1(0, 0, "0"));*/

    }
}
