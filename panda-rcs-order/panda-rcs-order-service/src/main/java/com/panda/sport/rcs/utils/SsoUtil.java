package com.panda.sport.rcs.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

public class SsoUtil {
    public SsoUtil() {
    }

    public static Integer getUserId(HttpServletRequest request) {
        String userId = request.getHeader("user-id");
        if (org.apache.commons.lang3.StringUtils.isEmpty(userId)) {
            userId = request.getParameter("user-id");
        }

        return !org.apache.commons.lang3.StringUtils.isEmpty(userId) && !"undefined".equals(userId) ? Integer.parseInt(userId) : null;
    }

    public static Integer getAppId(HttpServletRequest request) {
        String appId = request.getHeader("app-id");
        if (org.apache.commons.lang3.StringUtils.isEmpty(appId)) {
            appId = request.getParameter("app-id");
        }

        return !StringUtils.isEmpty(appId) && !"undefined".equals(appId) ? Integer.parseInt(appId) : null;
    }

    public static boolean isNeedFilter(String uri, List<String> excludeUrls) {
        Iterator var3 = excludeUrls.iterator();

        while(var3.hasNext()) {
            String includeUrl = (String)var3.next();
            if (uri.contains(includeUrl)) {
                return true;
            }
        }

        return false;
    }
}
