package com.panda.sport.rcs.mgr.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

public class StringUtil {

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static long toLong(String value, long defaultValue) {
        try {
            if (StringUtils.isNotBlank(value)) {
                return new BigDecimal(value).longValue();
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static long toLong(Supplier<String> supplier, long defaultValue) {
        try {
            String value = supplier.get();
            if (StringUtils.isNotBlank(value)) {
                return new BigDecimal(value).longValue();
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static double toDouble(String value, double defaultValue) {
        try {
            if (StringUtils.isNotBlank(value)) {
                return Double.valueOf(value);
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static BigDecimal toBigDecimal(String value, BigDecimal defaultValue) {
        try {
            if (StringUtils.isNotBlank(value)) {
                return new BigDecimal(value);
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static BigDecimal toBigDecimal(String value) {
        return toBigDecimal(value, BigDecimal.ZERO);
    }

    public static BigDecimal toBigDecimal(Supplier<String> supplier, BigDecimal defaultValue) {
        try {
            String value = supplier.get();
            if (StringUtils.isNotBlank(value)) {
                return new BigDecimal(value);
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }

}
