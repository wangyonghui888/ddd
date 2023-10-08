package com.panda.sport.rcs.utils.i18n;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.panda.sport.rcs.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.utils
 * @Description : 通用工具类
 * @Author : Paca
 * @Date : 2020-10-07 14:30
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public final class CommonUtils {

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getLinkId() {
        return getUUID() + "_trade";
    }

    public static String getLinkId(String suffix) {
        return getLinkId() + "_" + suffix;
    }

    public static BigDecimal toBigDecimal(String value, BigDecimal defaultValue) {
        try {
            if (StringUtils.isNotBlank(value)) {
                return new BigDecimal(value);
            }
        } catch (Exception e) {
            log.error("转换BigDecimal异常", e);
        }
        return defaultValue;
    }

    public static BigDecimal toBigDecimal(String value) {
        return toBigDecimal(value, BigDecimal.ZERO);
    }

    public static Integer toInteger(String value, Integer defaultValue) {
        try {
            if (StringUtils.isNotBlank(value)) {
                return new Integer(value);
            }
        } catch (Exception e) {
            log.error("转换Integer异常", e);
        }
        return defaultValue;
    }

    public static boolean isNumber(String value) {
        try {
            new BigDecimal(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInteger(BigDecimal value) {
        try {
            value.intValueExact();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void sleep(TimeUnit timeUnit, long timeout) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            log.error("sleep异常", e);
        }
    }
}
