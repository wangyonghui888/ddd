package com.panda.sport.rcs.utils.i18n;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.factory
 * @Description : Bean 工厂
 * @Author : Paca
 * @Date : 2020-08-02 10:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class BeanFactory {

    private static final I18nBean DRAW_I18N = new I18nBean().setZs("平局").setEn("Draw").setJc("平局").setZh("平局");

    private static final I18nBean OTHER_I18N = new I18nBean().setZs("其他").setEn("Other").setJc("其他").setZh("其他");

    private static final I18nBean OVER_I18N = new I18nBean().setZs("大").setEn("Over").setJc("大").setZh("大");

    private static final I18nBean UNDER_I18N = new I18nBean().setZs("小").setEn("Under").setJc("小").setZh("小");

    private static final I18nBean HOME_TEAM = new I18nBean().setZs("主队").setEn("Home team").setJc("主队").setZh("主隊");

    private static final I18nBean AWAY_TEAM = new I18nBean().setZs("客队").setEn("Away team").setJc("客队").setZh("客隊");

    private static final I18nBean NONE_I18N = new I18nBean().setZs("无进球").setEn("None").setJc("无进球").setZh("無進球");

    private static final I18nBean OWN_GOAL_I18N = new I18nBean().setZs("乌龙球").setEn("own goal").setJc("乌龙球").setZh("烏龍球");

    public static I18nBean getDrawI18n() {
        return DRAW_I18N;
    }

    public static I18nBean getOtherI18n() {
        return OTHER_I18N;
    }

    public static I18nBean getOverI18n() {
        return OVER_I18N;
    }

    public static I18nBean getUnderI18n() {
        return UNDER_I18N;
    }

    public static I18nBean getHomeTeam() {
        return HOME_TEAM;
    }

    public static I18nBean getAwayTeam() {
        return AWAY_TEAM;
    }

    public static I18nBean getNoneI18n() {
        return NONE_I18N;
    }

    public static I18nBean getOwnGoalI18n() {
        return OWN_GOAL_I18N;
    }

}
