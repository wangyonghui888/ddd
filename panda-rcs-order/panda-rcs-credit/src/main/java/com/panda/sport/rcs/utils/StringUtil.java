package com.panda.sport.rcs.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.net.URL;
import java.util.UUID;
import java.util.function.Supplier;

public class StringUtil {

    private StringUtil() {

    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
     * "jar:file:/home/whf/foo.jar!cn/fh" -> "/home/whf/foo.jar"
     */
    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    /**
     * "cn.fh.lightning" -> "cn/fh/lightning"
     *
     * @param name
     * @return
     */
    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    /**
     * "Apple.class" -> "Apple"
     */
    public static String trimExtension(String name) {
        int pos = name.lastIndexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }

    /**
     * /application/home -> /home
     *
     * @param uri
     * @return
     */
    public static String trimURI(String uri) {
        String trimmed = uri.substring(1);
        int splashIndex = trimmed.indexOf('/');

        return trimmed.substring(splashIndex);
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
