package com.panda.sport.rcs.utils;

import com.google.common.collect.ImmutableMap;
import com.panda.sport.rcs.log.LogFilter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 操盤日誌工具類
 */
public class OperateLogUtils {

    /**
     * 操作頁面對照Map
     */
    public static Map<Integer, String> operatePageMap = ImmutableMap.<Integer, String>builder()
            .put(13, "早盘赛事")
            .put(14, "早盘操盘")
            .put(15, "早盘操盘-次要玩法")
            .put(16, "滚球赛事")
            .put(17, "滚球操盘")
            .put(18, "滚球操盘-次要玩法")
            .put(21, "联赛参数设置")
            .put(100, "早盘操盘-调价窗口")
            .put(101, "早盘操盘-次要玩法-调价窗口")
            .put(102, "滚球操盘-调价窗口")
            .put(103, "滚球操盘-次要玩法-调价窗口")
            .put(110, "早盘操盘-设置")
            .put(111, "滚球操盘-设置")
            .put(120,"玩法集管理-足球")
            .put(211,"早盘管理-早盘操盘-乒乓球-赛事设置")
            .put(212,"滚球管理-滚球操盘-乒乓球-赛事设置")
            .put(112,"AO FB")
            .build();


    /**
     * 得到用主户id
     */
    public static String getUserId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String userId = request.getHeader("user-id");
            if (userId == null) userId = "";
            return userId;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 得到local_times
     */
    public static Date getLocalTimes() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String localTimes = request.getParameter("local_times");
            if (localTimes != null) {
                return new Date(Long.parseLong(localTimes));
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String getIpAddr() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return LogFilter.getIpAddr(request);
        } catch (Exception e) {
        }
        return null;
    }
}
