package com.panda.sport.rcs.common.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对象属性复制工具类
 *
 * @author lithan  2020-06-25 14:46:22
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

    /**
     * Ipage对象 属性拷贝
     * @return
     */
    public static <T> IPage<T> copyPage(IPage sourcePage, List<T> targetList) {
        IPage<T> page = new Page<>();
        BeanUtils.copyProperties(sourcePage,page);
        page.setRecords(targetList);
        return page;
    }

}
