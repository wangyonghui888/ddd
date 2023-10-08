package com.panda.sport.rcs.common.utils;

import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author lithan
 */
public class LocalDateTimeUtil {


    /** 一天多少毫秒 */
    public static final Long dayMill = 24 * 3600 * 1000L;
    /**
     * 一小时多少毫秒
     */
    public static final Long hourMill = 60* 60 * 1000L;

    public static final String DATE_PATTERN = "yyyyMMdd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_PATTERN_SIMPLE = "yyyyMMddHHmmss";
    public static final String DATE_TIME_MILLIS_PATTERN_SIMPLE = "yyyyMMddHHmmssSSS";
    public static final String ZONE = "+8";
    public static final String DATE_TIME_HOUR_PATTERN = "yyyy-MM-dd HH";
    public static final String DATE_TIME = "yyyy-MM-dd";


    /**
     * 返回当前时间格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String now() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return df.format(time);
    }

    /**
     * 返回当前时间
     *
     * @param pattern 日期格式
     * @return
     */
    public static String now(String pattern) {
        LocalDateTime time = LocalDateTime.now();
        return DateTimeFormatter.ofPattern(pattern).format(time);
    }

    /**
     * 格式化
     *
     * @return
     */
    public static String format(LocalDateTime time) {
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(time);
    }

    /**
     * 格式化
     *
     * @return
     */
    public static String format(String pattern, LocalDateTime time) {
        return DateTimeFormatter.ofPattern(pattern).format(time);
    }

    /**
     * 字符串转LocalDateTime
     *
     * @return
     */
    public static LocalDateTime format(String time, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return LocalDateTime.parse(time, formatter);
    }


    /**
     * 获取秒
     *
     * @return
     */
    public static long getSecond(LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(ZoneOffset.of(ZONE));
    }

    /**
     * 获取毫秒
     *
     * @return
     */
    public static long getMilli(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.of(ZONE)).toEpochMilli();
    }

    /**
     * 时间戳毫秒转LocalDateTime
     *
     * @return
     */
    public static LocalDateTime milliToLocalDateTime(Long mill) {
        LocalDateTime localDateTime = new Date(mill).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 时间戳毫秒yyyyMMdd
     *
     * @return
     */
    public static Long milliToDate(Long mill) {
        LocalDateTime localDateTime = new Date(mill).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        return Long.valueOf(DateTimeFormatter.ofPattern(DATE_PATTERN).format(localDateTime));
    }

    /**
     * 时间戳毫秒yyyyMMdd
     *
     * @return
     */
    public static String milliToDateTime(Long mill) {
        LocalDateTime localDateTime = new Date(mill).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(localDateTime);
    }



    public static long getDayStartTime(Long time) {
        LocalDateTime day = LocalDateTime.of(LocalDateTimeUtil.milliToLocalDateTime(time).toLocalDate(), LocalTime.MIN);
        return LocalDateTimeUtil.getMilli(day);
    }


    /**
     * 获取当前时间
     * @return
     */
    public static Long getCurrentHourTime(){
        String date = now(DATE_TIME_HOUR_PATTERN) + ":00:00";
        return DateUtil.parse(date).getTime() ;
    }

    /**
     * 获取上一小时时间
     * @return
     */
    public static Long getLastHourTime(){
        String date = now(DATE_TIME_HOUR_PATTERN) + ":00:00";
        return DateUtil.parse(date).getTime() - hourMill;
    }

    /**
     * 字符串转日期
     * @param time
     * @return
     * @throws Exception
     */
    public static Long string2Date(String time) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(time);
        return date.getTime();
    }

    /**
     * 字符串转日期
     * @param time
     * @return
     * @throws Exception
     */
    public static Long string3Date(String time) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = format.parse(time);
        return date.getTime();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(milliToDateTime(1614486505000L));
        //System.out.println(milliToLocalDateTime(System.currentTimeMillis(),"yyyyMMdd"));
    }
}

