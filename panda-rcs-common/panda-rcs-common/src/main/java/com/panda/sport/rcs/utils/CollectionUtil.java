package com.panda.sport.rcs.utils;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * @ClassName CollectionUtils
 * @Description: 工具类
 * @Author Vector
 * @Date 2019/11/14
 **/
public class CollectionUtil {
    /**
     * @MethodName:
     * @Description: list去重
     * @Author: Vector
     * @Date: 2019/11/14
     **/
    public static <T> void removeDuplicate(List<T> list) {
        if(org.springframework.util.CollectionUtils.isEmpty(list)) return;
        LinkedHashSet<T> set = new LinkedHashSet<T>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
    }
}
