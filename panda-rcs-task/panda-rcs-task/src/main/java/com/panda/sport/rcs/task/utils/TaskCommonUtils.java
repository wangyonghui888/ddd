package com.panda.sport.rcs.task.utils;

/**
 * @description:
 * @author: lithan
 * @date: 2020-07-28 20:43
 **/
public class TaskCommonUtils {

    public static long getLongValue(Object value) {
        if (null == value) {
            return 0L;
        }
        return Long.parseLong(value.toString());
    }

}