package com.panda.rcs.stray.limit.utils;

import com.github.pagehelper.util.StringUtil;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: gulang                   //作者
 * @CreateTime: 2023-04-03  14:51  //时间
 * @Description: TODO                //类描述
 * @Version: 1.0                     //版本
 */
public class IPUtil {

    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        //log.info("x-forwarded-for : {}", ip);
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip) && ip.indexOf(",") != -1) {
            ip = ip.split(",")[0];
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (!StringUtils.hasText(ip) || ip.indexOf(".") == -1) {
            ip = "";
        }

        if (StringUtil.isNotEmpty(ip)) {
            ip = ip.split(":")[0];
        }
        return ip;
    }
}
