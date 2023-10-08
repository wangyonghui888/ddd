package com.panda.sport.rcs.predict.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.operation.utils
 * @Description :  Long操作类
 * @Date: 2020-01-29 18:47
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public class LongUtil {
    public static Long parseLong(String value){
        if(value == null || value ==""){
            return 0L;
        }
        return Long.parseLong(value);
    }
}
