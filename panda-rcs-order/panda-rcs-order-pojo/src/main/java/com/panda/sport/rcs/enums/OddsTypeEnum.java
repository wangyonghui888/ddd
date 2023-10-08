package com.panda.sport.rcs.enums;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.enums
 * @Description : 投注项类型枚举
 * @Author : Paca
 * @Date : 2020-08-01 13:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface OddsTypeEnum {

    String AND = "And";

    String OTHER = "Other";

    String HOME = "1";

    String AWAY = "2";

    String DRAW = "X";

    String OVER = "Over";

    String UNDER = "Under";

    String NONE = "None";

    String OWN_GOAL = "OwnGoal";

    String ODD = "Odd";

    String EVEN = "Even";

    String YES = "Yes";

    String NO = "No";

    static boolean isHome(String oddsType) {
        return HOME.equals(oddsType);
    }

    static boolean isAway(String oddsType) {
        return AWAY.equals(oddsType);
    }

    static boolean isOver(String oddsType) {
        return OVER.equalsIgnoreCase(oddsType);
    }

    static boolean isUnder(String oddsType) {
        return UNDER.equalsIgnoreCase(oddsType);
    }

    static boolean isOdd(String oddsType) {
        return ODD.equalsIgnoreCase(oddsType);
    }

    static boolean isEven(String oddsType) {
        return EVEN.equalsIgnoreCase(oddsType);
    }

    static boolean isYes(String oddsType) {
        return YES.equalsIgnoreCase(oddsType);
    }

    static boolean isNo(String oddsType) {
        return NO.equalsIgnoreCase(oddsType);
    }

    static boolean isHomeOddsType(String oddsType) {
        return isHome(oddsType) || isOver(oddsType) || isOdd(oddsType) || isYes(oddsType);
    }

    static boolean isAwayOddsType(String oddsType) {
        return isAway(oddsType) || isUnder(oddsType) || isEven(oddsType) || isNo(oddsType);
    }

    static String getHomeOddsTypeByPlayId(Long playId) {
        if (Basketball.isHandicap(playId)) {
            return HOME;
        }
        if (Basketball.isTotal(playId)) {
            return OVER;
        }
        if (Basketball.isOddEven(playId)) {
            return ODD;
        }
        if (Basketball.isYesNo(playId)) {
            return YES;
        }
        return HOME;
    }

    static String getHomeOddsTypeByOddsType(String oddsType) {
        if (isHome(oddsType) || isAway(oddsType)) {
            return HOME;
        }
        if (isOver(oddsType) || isUnder(oddsType)) {
            return OVER;
        }
        if (isOdd(oddsType) || isEven(oddsType)) {
            return ODD;
        }
        if (isYes(oddsType) || isNo(oddsType)) {
            return YES;
        }
        return oddsType;
    }

    static boolean isTotalOddsType(String oddsType) {
        return isOver(oddsType) || isUnder(oddsType);
    }
}
