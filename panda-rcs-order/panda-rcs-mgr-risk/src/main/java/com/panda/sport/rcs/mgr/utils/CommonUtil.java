package com.panda.sport.rcs.mgr.utils;

import com.alibaba.nacos.common.util.UuidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Slf4j
public class CommonUtil {

    public static final String RCS_BUSINESS_LOG_SAVE = "rcs_business_log_save";
    public static final String RCS_VIRTUAL_PANDA_RATE_ENABLE = "rcs_virtual_panda_rate_enable";

    /**
     * 获取requestId or 生成唯一id
     * @return
     */
    public static String getRequestId(){
        String key = "-";
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            key = request.getHeader("request-id");
            if (StringUtils.isBlank(key)) {
                key = UuidUtils.generateUuid();
            }
        }catch (NullPointerException e){
            key = UuidUtils.generateUuid();
        }catch (Exception e){
            key = UuidUtils.generateUuid();
        }
        return key;
    }
}
