package com.panda.merge.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Project Name :  panda_data_service
 * @Package Name :  com.panda.sports.manager.utils
 * @Description :  用于时间转换
 * @Date: 2019-08-02 11:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class TimeUtils {
    /**
     * 一小时多少毫秒
     **/
    private static long millsSecondPerHour = 3600 * 1000;

    /**
     * 一小时多少秒
     **/
    private static long secondPerHour = 3600;

    /**
     * 一小时多少微秒
     **/
    private static long microSecondPerHour = 3600 * 1000 * 1000;

    /**
     * @return long
     * @Description 将GMT时间转为指定时区的时间
     * @Param gmtTime:    格林威治时间，单位毫秒
     * timeZone:   时区数。 如果是 格林威治时间线 往东（美洲方向），则该数字小于0；往西（亚洲方向），数字大于0，跨时区个数是该数字的具体值。
     * @Author dorich
     * @Date 2019/8/2
     **/
    public static long timeMillsSecondsTimeZone(Long gmtTime, int timeZone) {
        return gmtTime + timeZone * millsSecondPerHour;
    }

    /**
     * 将制定时间转换为 西4区时间(UTC-4时间)
     *
     * @param gmtTime UTC时间，精确到毫秒
     * @return long
     * @description 将制定时间转换为 西4区时间(UTC-4时间)
     * @author dorich
     * @date 2019/8/14
     **/
    public static long timeMillsSecondsToPlus4TimeZone(Long gmtTime) {
        return timeMillsSecondsTimeZone(gmtTime, -4);
    }

    /**
     * @param gmtTime  需要转换的UTC时间
     * @param timeZone 时区个数
     * @return long
     * @Description 将GMT时间转为指定时区的时间
     * @Param gmtTime:    格林威治时间，单位秒
     * timeZone:   时区数。 如果是 格林威治时间线 往东（美洲方向），则该数字小于0；往西（亚洲方向），数字大于0，跨时区个数是该数字的具体值。
     * @author dorich
     * @date 2019/8/9
     **/
    public static long timeSecondsTimeZone(Long gmtTime, int timeZone) {
        return gmtTime + timeZone * secondPerHour;
    }

    /**
     * @return long
     * @Description 本地的东八区时间转换为GMT时间
     * @Param
     * @Author dorich
     * @Date 2019/8/2
     **/
    public static long millsSecondsEast8ZoneGmt() {
        return System.currentTimeMillis() - 8 * millsSecondPerHour;
    }

    /**
     * @return long
     * @Description 将GMT时间转为指定时区的时间
     * @Param gmtTime:    格林威治时间，单位微秒
     * timeZone:   时区数。 如果是 格林威治时间线 往东（美洲方向），则该数字小于0；往西（亚洲方向），数字大于0，跨时区个数是该数字的具体值。
     * @Author dorich
     * @Date 2019/8/2
     **/
    public static long timeMicroSecondsTimeZone(Long gmtTime, int timeZone) {
        return gmtTime + timeZone * microSecondPerHour;
    }

    /**
     * 格式化时间
     * yyyy-MM-dd HH:mm
     *
     * @param date yyyy-MM-dd HH:mm
     * @return
     */
    public static String yyyyMMddHHmm(Date date) {
        if (date == null) {
            return null;
        }
        return format(date, "yyyy-MM-dd HH:mm");
    }

    /**
     * 格式化时间
     * yyyy-MM-dd HH
     *
     * @param date yyyy-MM-dd HH
     * @return
     */
    public static String yyyyMMddHH(Date date) {
        if (date == null) {
            return null;
        }
        return format(date, "yyyy-MM-dd HH");
    }

    /**
     * 格式化时间
     * yyyy-MM-dd HH
     *
     * @return
     */
    public static String yyyy_MM_dd_HH() {
        Date date = new Date(millsSecondsEast8ZoneGmt());
        return format(date, "yyyy-MM-dd HH");
    }

    /**
     * 格式化时间
     * yyyy-MM-dd HH
     *
     * @return
     */
    public static String yyyyMMddHH() {
        Date date = new Date();
        return format(date, "yyyyMMddHH");
    }

    /**
     * 格式化时间
     * yyyy-MM-dd HH
     *
     * @return
     */
    public static String yyyy_MM_dd() {
        Date date = new Date();
        return format(date, "yyyy-MM-dd");
    }


    /**
     * 获取精确到秒的时间戳
     *
     * @return 秒为单位的时间戳
     */
    public static int genSecondTimestamp() {
        Date date = new Date();
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }


    /**
     * 函数介绍：根据默认模式包日期对象转换成日期字符串 参数：date ,日期对象；parttern,日期字符格式 返回值：日期字符串
     */
    public static String format(Date date, String parttern) {
        if (date == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(parttern);
        return df.format(date);
    }
}