package com.panda.sport.rcs.oddin.util;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * 对象属性复制工具类
 *
 * @author lithan  2023-01-08 10:51:59
 */
public class CopyUtils {
    /**
     * 复制集合（深度拷贝）
     *
     * @param list
     * @param tClass 指定类
     * @param <T>
     * @return
     */
    public static <T> List<T> clone(List<?> list, Class<T> tClass) {
        if (list == null) {
            return new ArrayList<T>();
        }
        String json = JSON.toJSONString(list);
        return JSON.parseArray(json, tClass);
    }

    /**
     * 复制对象
     *
     * @param object
     * @param tClass 指定类
     * @param <T>
     * @return
     */
    public static <T> T clone(Object object, Class<T> tClass) {
        String json = JSON.toJSONString(object);
        return JSON.parseObject(json, tClass);
    }

}
