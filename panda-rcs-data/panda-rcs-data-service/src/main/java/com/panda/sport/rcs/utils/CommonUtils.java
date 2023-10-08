package com.panda.sport.rcs.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 通用工具类
 */
@Slf4j
public class CommonUtils {


    /**
     * 判断字符串 null 或 ‘null’
     * @param str
     * @return
     */
    public static boolean isBlankOrNull(String str){
        return StringUtils.isBlank(str) || str.equals("null");
    }

    /**
     * 判断字符串不为 null 和 ‘null’
     *
     * @param str
     * @return
     */
    public static boolean isNotBlankAndNull(String str) {
        return StringUtils.isNotBlank(str) && !str.equals("null");
    }

}
