package com.panda.sport.rcs.common;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * 常用数字格式工具类
 *
 * @author kane
 * @version 1.0
 */
@Slf4j
public class NumberUtils {

    private static final String ONE = "1";

    /**
     * 加法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 减法
     *
     * @param v1 被减数
     * @param v2 减数
     * @return
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 乘法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 除法
     *
     * @param v1 被除数
     * @param v2 除数
     * @return
     */
    public static double div(double v1, double v2, Integer scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale == null ? 3 : scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 截取指定小数位
     *
     * @param v
     * @param scale
     * @return
     */
    public static double round(double v, int scale) {
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal(ONE);
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 格式化指定数字
     *
     * @param pattern
     * @param value
     * @return
     */
    public static String decimalFormat(String pattern, double value) {
        return new DecimalFormat(pattern).format(value);
    }

    /**
     * 格式化位整数
     *
     * @param value
     * @return
     */
    public static String decimalBlankFormat(double value) {
        return new DecimalFormat("0").format(value);
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        /***  检查是否是数字    ***/
        try {
            BigDecimal decimal = new BigDecimal(value);
        } catch (Exception e) {
            /***  异常 说明包含非数字。 ***/
            return false;
        }
        return true;
    }

    /**
     * 得到指定小数位数的浮点数
     *
     * @param number
     * @param accuracy 小数位数
     * @return
     */
    public static String getAppointedDecimalDigits(Number number, int accuracy) {
        if (accuracy < 0) {
            throw new IllegalArgumentException("输入的小数位数有错");
        }
        return new BigDecimal(number + "").setScale(accuracy,
                BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 判断是否integer
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    /**
     * 小数转换成指定小数位数的百分数
     *
     * @param number
     * @param accuracy 小数位数
     * @return
     */
    public static String decimalToPercentage(Number number, int accuracy) {
        if (accuracy < 0) {
            throw new IllegalArgumentException("输入的小数位数有错");
        }
// 获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        BigDecimal bg = new BigDecimal(number + "");
        if (bg.doubleValue() == bg.longValue()) {
            accuracy = 0;
        }

// 设置百分数精确度accuracy即保留accuracy位小数
        nt.setMinimumFractionDigits(accuracy);
// 最后格式化并输出
        return nt.format(number);
    }

    /**
     * 求两个数的最大公约数
     *
     * @param m
     * @param n
     * @return
     */
    public static long getMaxCommonDivisor(long m, long n) {
        if (m == 0)
            return n;
        if (n == 0)
            return m;
        if (m < n) {
            long tmp = m;
            m = n;
            n = tmp;
        }
        while (n != 0) {
            long tmp = m % n;
            m = n;
            n = tmp;
        }
        return m;
    }

    /**
     * 小数转换成分数
     *
     * @param number
     * @return
     */
    public static String decimalToFraction(String number) {
        StringBuilder fraction = null; // 分数字符串
        if (number != null && (number = number.trim()).length() > 0) {
            if (!number
                    .matches("^[-]{0,1}(([\\d]*\\.[\\d]+)|([\\d]+([\\.]{0,1})[\\d]*))$")) {
                throw new IllegalArgumentException("要转换的数据有误！");
            }
            fraction = new StringBuilder();
            if (number.charAt(0) == '-') {
                number = number.substring(1);
                fraction.append('-');
            }
            char ch = number.charAt(0);
            if (ch == '.') {
                fraction.append("1/").append(number.replace(".", ""));
            } else if (number.charAt(number.length() - 1) == '.'
                    || !number.contains(".")) {
                fraction.append(number.replace(".", ""));
            } else {
                String[] numberArray = number.split("\\.");
                // 获取整数部分
                long intPart = Long.parseLong(numberArray[0]);
                // 获取小数部分
                long decimalPart = Long.parseLong(numberArray[1]);
                if (decimalPart == 0) {
                    fraction.append(intPart);
                } else {
                    int length = numberArray[1].length();
// 分子
                    long numerator = (long) (intPart * Math.pow(10, length) + decimalPart);
// 分母
                    long denominator = (long) Math.pow(10, length);
// 得到分子分母的最大公约数
                    getFraction(fraction, numerator, denominator);
                }

            }
            return fraction.toString();
        } else {
            throw new IllegalArgumentException("要转换的数据有误！");
        }
    }

    /**
     * 得到最简分数
     *
     * @param fraction
     * @param numerator
     * @param denominator
     */
    private static void getFraction(StringBuilder fraction, long numerator,
                                    long denominator) {
        if (fraction == null) {
            return;
        }
        long maxCommonDivisor = getMaxCommonDivisor(numerator,
                denominator);
        if (denominator == maxCommonDivisor) {
            fraction.append(numerator / maxCommonDivisor);
        } else {
            fraction.append(numerator / maxCommonDivisor).append("/")
                    .append(denominator / maxCommonDivisor);
        }
    }

    /**
     * 得到最简分数
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return
     */
    public static String getFractionInLowestTerm(long numerator, long denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("分母不能为零！");
        }

        if (numerator == 0) {
            return "0";
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (numerator < 0 && denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        } else if (numerator < 0) {
            stringBuilder.append("-");
            numerator = -numerator;
        } else if (denominator < 0) {
            stringBuilder.append("-");
            denominator = -denominator;
        }

        getFraction(stringBuilder, numerator, denominator);
        return stringBuilder.toString();
    }

    public static BigDecimal getBigDecimal(Object ob) {
        if (ob == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(String.valueOf(ob));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
    
    public static void main(String[] args) {
		System.out.println(isNumber("3+"));
	}
}
