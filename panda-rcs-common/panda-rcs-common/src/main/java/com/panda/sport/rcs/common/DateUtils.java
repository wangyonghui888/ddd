package com.panda.sport.rcs.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
public class DateUtils {

    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_YYYY_MM_DD_HH = "yyyyMMddHH";
    public static final String DATE_HH_MM_SS = "HH:mm:ss";
    public static final String DATE_HH_MM = "HH:mm";

    public static final String DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    public static final FastDateFormat DATE_FORMAT_yyyy_MM_dd = FastDateFormat.getInstance("yyyy-MM-dd");
    public static final FastDateFormat DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = FastDateFormat.getInstance(DATE_YYYY_MM_DD_HH_MM_SS);


    public static final FastDateFormat DAY_DATE_FORMAT_HH_MM_SS = FastDateFormat.getInstance(DATE_YYYY_MM_DD_HH_MM_SS);
    public static final FastDateFormat DAY_DATE_FORMAT = FastDateFormat.getInstance(YYYY_MM_DD);


    public static final int TIME_TYPE_MONTH = 0;
    public static final int TIME_TYPE_DAY = 1;
    public static final int TIME_TYPE_HOUR = 2;
    public static final int TIME_TYPE_MINUTE = 3;
    public static final int TIME_TYPE_SECOND = 4;

    public static boolean compareTimeMinute(long date1, long date2, double n) {
        long cha = Math.abs(date1 - date2);
        double result = n * (1000 * 60 * 60 * 60);
        return result <= cha;
    }

    /**
     * 比较两个时间是否相差超过n小时
     */
    public static boolean compareTime(long date1, long date2, double n) {
        long cha = Math.abs(date1 - date2);
        double result = n * (1000 * 60 * 60);
        return result <= cha;
    }

    /**
     * 比较两个时间是否相差几小时
     */
    public static double compareTime(long date1, long date2) {
        long cha = Math.abs(date1 - date2);
        long result = 1000 * 60 * 60;
        return (double) cha / (double) result;
    }


    /**
     * 时间相加比较
     *
     * @param date1
     * @param refundmentPeroid
     * @param time1
     * @return
     */
    public static boolean addcompareTime(long date1, Double refundmentPeroid, long time1) {
        double result = refundmentPeroid * (1000 * 60 * 60);
        Long date = Math.round(result) + date1;
        return date > time1;
    }

    public static final String convertFormatDateToString(FastDateFormat fastDateFormat, Date date) {
        return fastDateFormat.format(date);
    }

    /**
     * 两个日期相差的月数
     *
     * @param from
     * @param to
     * @return
     * @throws ParseException
     */
    public static int dateDiffMonth(String from, String to)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(to));
        int year1 = c.get(Calendar.YEAR);
        int month1 = c.get(Calendar.MONTH);
        c.setTime(sdf.parse(from));
        int year2 = c.get(Calendar.YEAR);
        int month2 = c.get(Calendar.MONTH);
        int result;
        if (year1 == year2) {
            result = month1 - month2;
        } else {
            result = 12 * (year1 - year2) + month1 - month2;
        }
        return result;
    }

    /**
     * 两个日期相差的天数
     *
     * @param from
     * @param to
     * @return
     */
    public static int dateDiffDay(Date from, Date to) {
        return (int) ((to.getTime() - from.getTime()) / (1000 * 60 * 60 * 24));
    }


    /**
     * 两个时间相差的小时数（整数）
     *
     * @param from
     * @param to
     * @return
     */
    public static int dateDiffHour(Date from, Date to) {
        return (int) ((to.getTime() - from.getTime()) / (1000 * 60 * 60));
    }

    /**
     * 两个时间相差的小时数(小数)
     *
     * @param from
     * @param to
     * @return
     */
    public static double dateBetweenHour(Date from, Date to) {
        return (double) ((to.getTime() - from.getTime()) / (1000 * 60 * 60));
    }

    public static Date getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date strToDate(String dateStr) {
        Date date = null;
        try {
            date = DAY_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            log.debug(e.getMessage());
        }
        return date;
    }

    public static Date strToDate(String dateStr, FastDateFormat sdf) {
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date addMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static int getYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    public static int getMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 将date（日期）和time（时间）转化为时间长串
     */
    public static Date setDateAndTimeToDate(Date date, Date time) {

        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(time);

        calendarDate.set(Calendar.HOUR, calendarTime.get(Calendar.HOUR));
        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
        calendarDate.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND));

        return calendarDate.getTime();
    }

    /**
     * 获取昨天凌晨时间
     *
     * @param
     * @throws ParseException
     */
    public static Date getYesterdayMorningDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) - 1));
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取指定时间的那天 00:00:00.000 的时间
     *
     * @param date
     * @return
     */
    public static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定时间的那天 23:59:59.999 的时间
     *
     * @param date
     * @return
     */
    public static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date dateStrToDate(String strDate) {
        ParsePosition pos = new ParsePosition(0);
        Date strToDate = DAY_DATE_FORMAT_HH_MM_SS.parse(strDate, pos);
        return strToDate;
    }

    public static final Date getLMTomorrowDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 6) {
            date = getTomorrowToDate();
        }
        return date;
    }

    /**
     * 计算后一天时间
     *
     * @return
     */
    public static final Date getTomorrowToDate(Date date) {
        Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
        ca.setTime(date == null ? new Date() : date); // 设置时间为当前时间
        ca.add(Calendar.DATE, 1);// 
        return ca.getTime();
    }

    /**
     * 计算后一天时间
     *
     * @return
     */
    public static final Date getTomorrowToDate() {
        Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
        ca.setTime(new Date()); // 设置时间为当前时间
        ca.add(Calendar.DATE, 1);// 
        return ca.getTime();
    }

    /**
     * 计算前一天时间
     *
     * @return
     */
    public static final Date getYestodayToDate(Date date) {
        Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
        ca.setTime(date == null ? new Date() : date); // 设置时间为当前时间
        ca.add(Calendar.DATE, -1);// 
        return ca.getTime();
    }

    /**
     * 计算前一天时间
     *
     * @return
     */
    public static final Date getYestodayToDate() {
        Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
        ca.setTime(new Date()); // 设置时间为当前时间
        ca.add(Calendar.DATE, -1);// 
        return ca.getTime();
    }


    public static final String getTimeStirngMMDDHHMM(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-DD HH:MM");
        return simpleDateFormat.format(date);
    }

    public static int getHourByDate(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinuteByDate(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 比较两个日期，date1大于date2返回ture，否则返回false
     *
     * @param bequals TODO
     * @return
     */
    public static boolean dateCompare(Date date1, Date date2, boolean bequals) {
        boolean isRetrun = false;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate1 = fmt.format(date1.getTime());
        String strDate2 = fmt.format(date2.getTime());
        try {
            if (bequals) {
                isRetrun = (fmt.parse(strDate1).getTime() >= fmt.parse(strDate2).getTime());
            } else {
                isRetrun = (fmt.parse(strDate1).getTime() > fmt.parse(strDate2).getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isRetrun;
    }

    /**
     * 比较两个日期，date1大于date2返回ture，否则返回false
     *
     * @param bequals TODO
     * @return
     */
    public static boolean dateTimeCompare(Date date1, Date date2, boolean bequals) {
        boolean isRetrun = false;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String strDate1 = fmt.format(date1.getTime());
        String strDate2 = fmt.format(date2.getTime());
        try {
            if (bequals) {
                isRetrun = (fmt.parse(strDate1).getTime() >= fmt.parse(strDate2).getTime());
            } else {
                isRetrun = (fmt.parse(strDate1).getTime() > fmt.parse(strDate2).getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isRetrun;
    }

    public static boolean timeCompare() {
        boolean flag = false;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {

            System.out.println("--------" + sdf.parse("00:00:00").getTime());
            System.out.println("--------" + sdf.parse("00:00:01").getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 获取某一年第几周的第一天的日期
     *
     * @param year
     * @param week
     * @return
     */
    public static Date getDateByYearOfWeek(Integer year, Integer week) {
        Calendar calFirstDayOfTheYear = new GregorianCalendar(year, Calendar.JANUARY, 1);
        calFirstDayOfTheYear.add(Calendar.DATE, 7 * (week - 1));
        return calFirstDayOfTheYear.getTime();
    }

    /**
     * 返回增加n天的日期
     *
     * @param date
     */
    public static Date addNDay(Date date, Integer n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, n);
        return calendar.getTime();
    }

    /**
     * 返回增加n小时的日期
     *
     * @param date
     */
    public static Date addNHour(Date date, Integer n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, n);
        return calendar.getTime();
    }

    /**
     * 返回增加n分钟的日期
     *
     * @param date
     */
    public static Date addNMinute(Date date, Integer n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, n);
        return calendar.getTime();
    }

    /**
     * 返回周几
     *
     * @param date
     * @return
     */
    public static Integer getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int number = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String[] str = {"7", "1", "2", "3", "4", "5", "6",};
        return Integer.parseInt(str[number]);
    }

    /**
     * 返回日期集合
     *
     * @return
     */
    public static Set<String> getDateList(Date date_start, Date date_end) {
        Set<String> set = new HashSet<String>();
        Calendar cd = Calendar.getInstance();
        while (date_start.getTime() <= date_end.getTime()) {
            set.add(DATE_FORMAT_yyyy_MM_dd.format(date_start));
            cd.setTime(date_start);
            cd.add(Calendar.DATE, 1);
            date_start = cd.getTime();
        }
        return set;
    }

    /**
     * 计算两个特定格式时间之间的时间差
     *
     * @param startTime
     * @param endTime
     * @param format
     * @param str
     * @return
     */
    public static Long dateDiff(String startTime, String endTime,
                                String format, String str) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh + day * 24;// 计算差多少小时
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
            if (str.equalsIgnoreCase("h")) {
                return hour;
            } else {
                return min;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (str.equalsIgnoreCase("h")) {
            return hour;
        } else {
            return min;
        }
    }

    /**
     * 比较两个日期(不带时分秒)，date1大于date2返回ture，否则返回false
     *
     * @param beequals TODO
     * @return
     */
    public static boolean compare(Date date1, Date date2, boolean beequals) {
        boolean isRetrun = false;
        FastDateFormat fdf = DATE_FORMAT_yyyy_MM_dd;
        String strDate1 = fdf.format(date1.getTime());
        String strDate2 = fdf.format(date2.getTime());
        try {
            if (beequals) {
                isRetrun = (fdf.parse(strDate1).getTime() >= fdf.parse(strDate2).getTime());
            } else {
                isRetrun = (fdf.parse(strDate1).getTime() > fdf.parse(strDate2).getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isRetrun;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
    public static String dayForWeek(String pTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(pTime));
            int dayForWeek = 0;
            if (c.get(Calendar.DAY_OF_WEEK) == 1) {
                dayForWeek = 7;
            } else {
                dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            }
            String result = "";
            switch (dayForWeek) {
                case 1:
                    result = "周一";
                    break;
                case 2:
                    result = "周二";
                    break;
                case 3:
                    result = "周三";
                    break;
                case 4:
                    result = "周四";
                    break;
                case 5:
                    result = "周五";
                    break;
                case 6:
                    result = "周六";
                    break;
                case 7:
                    result = "周日";
                    break;
                default:
                    result = "";
                    break;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dayForWeeks(String pTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(pTime));
            int dayForWeek = 0;
            if (c.get(Calendar.DAY_OF_WEEK) == 1) {
                dayForWeek = 7;
            } else {
                dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            }
            String result = "";
            switch (dayForWeek) {
                case 1:
                    result = "1";
                    break;
                case 2:
                    result = "2";
                    break;
                case 3:
                    result = "3";
                    break;
                case 4:
                    result = "4";
                    break;
                case 5:
                    result = "5";
                    break;
                case 6:
                    result = "6";
                    break;
                case 7:
                    result = "7";
                    break;
                default:
                    result = "0";
                    break;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * long转date类型
     *
     * @param millSec
     * @return
     */
    public static Date transferLongToDate(Long millSec) {
        String dateFormat = "yyyy-MM-dd";//1464577593426
        Date date = new Date(millSec);
        return date;
    }


    /**
     * long转string类型
     *
     * @param millSec
     * @return
     */
    public static String transferLongToDateString(Long millSec) {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    public static String transferLongToDateStrings(Long millSec) {
        if (millSec == null) {
            return null;
        }
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /*
   时间转字符串
    */
    public static String transferDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(date);
        return str;
    }

    /**
     * 时间（小时）转字符串
     *
     * @param date
     * @return
     */
    public static String transferHourDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }

    public static Date tranferStringToDate2(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*
    字符串转时间
     */
    public static Date tranferStringToDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取日期的年
     *
     * @param lo
     * @return
     */
    public static short getYear(long lo) {
        Date date = new Date(lo);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        short year = (short) calendar.get(Calendar.YEAR);
        return year;
    }

    public static short getMonth(long lo) {
        Date date = new Date(lo);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        short month = (short) (calendar.get(Calendar.MONTH) + 1);
        return month;
    }

    /**
     * 返回中文字符串
     *
     * @param strDate
     * @return
     */
    public static String tranferStringToZHCN(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf1.format(date);
    }


    public static String tranferStringToZHCN(Date date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf1.format(date);
    }


    public static Date tranferDateStringToDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取当前时间（格式：MM-dd-HH-mm）
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_YYYY_MM_DD_HH_MM_SS);
        return sdf1.format(new Date());
    }

    /**
     * 获取标准格式的当前时间
     *
     * @return
     */
    public static String getStandardCurrentTime() {
        String now = DAY_DATE_FORMAT.format(new Date());
        return now;
    }

    /**
     * 获得当天0点时间
     */
    public static Date getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得当天0点时间
     */
    public static Date getTargetTimesmorning(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得当天24点时间
     */
    public static Date getTargetTimesnight(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得当天24点时间
     */
    public static Date getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得本周一0点时间
    public static Date getTimesWeekmorning(Date date) {
        Calendar cal = Calendar.getInstance(Locale.CHINESE);
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // 获得本周日24点时间
    public static Date getTimesWeeknight(Date date) {
        Calendar cal = Calendar.getInstance(Locale.CHINESE);
        cal.setTime(getTimesWeekmorning(date));
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // 获得本月第一天0点时间
    public static Date getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }


    public static int getWeekOfMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        String dateString = sdf.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            calendar.setTime(sdf.parse(dateString));
            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
            return weekOfMonth;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前时间所在周的周一
     */
    public static Date getMondayOfWeek(Date date, int n) {
        Calendar cal = Calendar.getInstance(Locale.CHINESE);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        //n为推迟的周数，1本周，-1向前推迟一周，2下周，依次类推
        cal.add(Calendar.DATE, n * 7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取当前时间所在周的周日
     */
    public static Date getSundayOfWeek(Date date, int n) {
        Calendar cal = Calendar.getInstance(Locale.CHINESE);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        //n为推迟的周数，1本周，-1向前推迟一周，2下周，依次类推
        cal.add(Calendar.DATE, n * 7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @return
     */
    public static int getSecondTimestampOne(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.parseInt(timestamp.substring(0, length - 3));
        } else {
            return 0;
        }
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @param date
     * @return
     */
    public static int getSecondTimestampTwo(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.parseInt(timestamp);
    }

    public static Date StringToDate(String dateTime){
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
        Date date =null;
        try {
            date = fmt.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;
    }

    //自由获取指定时间后的下n个周的周几
    public static Date getRecentlyDateByWeek(Date date, int week, int cycleCount) {
        Calendar cal = Calendar.getInstance(Locale.CHINESE);
        week++;
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_YEAR, cycleCount);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY),
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
        cal.set(Calendar.DAY_OF_WEEK, week);
        return cal.getTime();
    }


    //Date时间格式化
    public static Date getDateFormat(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dStr = format.format(date);
        Date d = null;
        try {
            d = format.parse(dStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return d;
    }

    /*
   时间转字符串(yyyy-MM-dd HH:mm:ss)
    */
    public static String changeDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }

    public static String changeDateToString(Date date, int value) {
        String str = null;
        if (value == 1) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
            str = sdf.format(date);
        } else if (value == 2) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            str = sdf.format(date);
        }

        return str;
    }

    public static String getStringYMD(Integer day) {
        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, day);//取当前日期+day天.

        //通过格式化输出日期
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(cal.getTime());
    }

    /**
     * 时间转字符串YYYY_MM_DD
     *
     * @param date
     * @return
     */
    public static String DateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        String str = sdf.format(date);
        return str;
    }

    public static Date getDate(String param) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = sf.parse(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间转字符串YYYYMMDD
     *
     * @param date
     * @return
     */
    public static String DateToString_YYYYMMDD(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD);
        String str = sdf.format(date);
        return str;
    }

    /**
     * jdk8新特性--DateToString
     *
     * @param date
     * @return
     */
    public static String DateToString_NEW(java.time.LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 判断当前时间对于的账户时间期号
     */
    public static String getTimeExpect(Long time) {
        Date date = new Date(time);
        date = org.apache.commons.lang3.time.DateUtils.addHours(date, -12);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    public static String getTimeExpect(Long time ,String format) {
        Date date = new Date(time);
        date = org.apache.commons.lang3.time.DateUtils.addHours(date, -12);

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 判断当前时间对于的账户时间期号
     */
    public static String getDateExpect(Long time) {
        Date date = new Date(time);
        date = org.apache.commons.lang3.time.DateUtils.addHours(date, -12);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String parseDate(Long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }


    /**
     * 获取下一天中午十二点
     */
    public static long getTomorrowMidDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * @return
     * @Description //获取当前账务时间的开始时间
     * @Param
     * @Author kimi
     * @Date 2019/12/4
     **/
    public static long getBeginTime(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        int i = instance.get(Calendar.HOUR_OF_DAY);
        instance.set(Calendar.HOUR_OF_DAY, 12);
        if (i < 12) {
            instance.add(Calendar.DATE, -1);
        }
        return instance.getTimeInMillis();
    }

    /**
     * @return
     * @Description //获取当前时间的结束时间
     * @Param
     * @Author kimi
     * @Date 2019/12/4
     **/
    public static long getEndTime(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        int i = instance.get(Calendar.HOUR_OF_DAY);
        instance.set(Calendar.HOUR_OF_DAY, 12);
        if (i >= 12) {
            instance.add(Calendar.DATE, 1);
        }
        return instance.getTimeInMillis();
    }

    /**
     * @return
     * @Description //获取某个日期的12点
     * @Param -n 前n天
     * @Author holly
     * @Date 2019/12/24
     **/
    public static long getTwelveOfDiffDay(int diffDay) {
        long time = getTomorrowMidDay();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        instance.add(Calendar.DATE, diffDay - 1);
        return instance.getTimeInMillis();
    }

    /**
     * 增加指定分钟数
     *
     * @param time
     * @param mins
     * @return
     */
    public static long addMinutes(long time, int mins) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        instance.add(Calendar.MINUTE, mins);
        return instance.getTimeInMillis();
    }

    /**
     * 根据具体年份周数获取日期范围
     *
     * @param year
     * @param week
     * @param targetNum
     * @return
     */
    public static String getWeekDays(int year, int week, int targetNum) {
        if (week + targetNum > 52) {
            year++;
            week += targetNum - 52;
        } else if (week + targetNum <= 0) {
            year--;
            week += targetNum - 52;
        } else {
            week += targetNum;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String beginDate = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = sdf.format(cal.getTime());
        return beginDate + " " + endDate;
    }

    public static String addDate(String timeParam, long day) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 日期格式
        Date date = dateFormat.parse(timeParam); // 指定日期

        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time += day; // 相加得到新的毫秒数
        Date newDate = new Date(time);
        return dateFormat.format(newDate); // 将毫秒数转换成日期
    }

    /**
     * @return java.lang.Long
     * @Description //年月日时间字符串   加上十二小时
     * @Param [time]
     * @Author kimi
     * @Date 2019/12/31
     **/
    public static Long stringToDateAddTwelveHour(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 由于只有十二个月，所以可以枚举每个月的天数
     * 需要单独考虑的是当前年份是否是闰年&&当前月份是否大月2月，如果都满足，则在总天数上+1
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static int whichDay(int year, int month, int day) {
        final int[] monthes = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int i = 0, num = 0;
        while (i < month - 1)
            num += monthes[i++];
        num += day;
        if (month < 3)
            return num;
        return num + isLeap(year);
    }

    /**
     * 判断输入的当前年份是否是闰年,是闰年则返回1，否则返回0；
     */
    public static int isLeap(int year) {
        if (year % 4 == 0 && year % 100 != 0)
            return 1;
        if (year % 400 == 0)
            return 1;
        else return 0;
    }

    public static void main(String[] args) {
        System.out.println(25%28+1);
    }
}
