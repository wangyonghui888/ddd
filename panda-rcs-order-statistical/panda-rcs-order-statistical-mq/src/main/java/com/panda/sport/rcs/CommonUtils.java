package com.panda.sport.rcs;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommonUtils {
    private static Logger log = LoggerFactory.getLogger(CommonUtils.class);

    public static final String TRACE_ID = "X-B3-TraceId";
    public static final String Link_ID = "linkId";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getTradeIdByMdc() {
        try {
            return MDC.get(TRACE_ID);
        } catch (Throwable t) {
            log.error("MDC.put 异常", t);
        }
        return getUUID();
    }

    public static String mdcPut() {
        String uuid = getUUID();
        try {
            MDC.put(TRACE_ID, uuid);
            MDC.put(Link_ID, uuid);
        } catch (Throwable t) {
            log.error("MDC.put 异常", t);
        }
        return uuid;
    }

    public static void mdcPut(String linkId) {
        try {
            MDC.put(TRACE_ID, linkId);
            MDC.put(Link_ID, linkId);
        } catch (Throwable t) {
            log.error("MDC.put 异常", t);
        }
    }

    public static void mdcPutIfAbsent() {
        try {
            String uuid;
            String linkId = MDC.get(TRACE_ID);
            if (StringUtils.isBlank(linkId)) {
                uuid = getUUID();
                MDC.put(TRACE_ID, uuid);
                MDC.put(Link_ID, uuid);
            }
        } catch (Throwable t) {
            log.error("MDC.put 异常", t);
        }
    }

    public static void mdcRemove() {
        try {
            MDC.remove(TRACE_ID);
            MDC.remove(Link_ID);
        } catch (Throwable t) {
            log.error("MDC.remove 异常", t);
        }
    }
}
