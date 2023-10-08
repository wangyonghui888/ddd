//package com.panda.sport.rcs.task.config;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.RemovalListener;
//import com.google.common.cache.RemovalNotification;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author :  Administrator
// * @Project Name :  rcs-parent
// * @Package Name :  com.panda.sport.rcs.task.config
// * @Description :  TODO
// * @Date: 2020-02-06 16:14
// * @ModificationHistory Who    When    What
// * --------  ---------  --------------------------
// */
//@Slf4j
//public class GuavaCache {
//
//    /**
//     * @desction: 使用google guava缓存处理
//     */
//    private static Cache<String, Object> cache;
//
//    static {
//        cache = CacheBuilder.newBuilder()
//                //设置cache的初始大小
//                .initialCapacity(10)
//                // 缓存的最大大小
//                .maximumSize(2000)
//                //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
//                .concurrencyLevel(16)
//                //设置cache中的数据在写入之后的存活时间为7天
//                .expireAfterWrite(30, TimeUnit.SECONDS)
//                //设置缓存多久没阅读就自动清除
//                .expireAfterAccess(30, TimeUnit.SECONDS)
//                .removalListener(new RemovalListener<String, Object>() {
//                    @Override
//                    public void onRemoval(RemovalNotification<String, Object> rn) {
//                        if (log.isInfoEnabled()) {
//                            log.info("被移除缓存{}:{}", rn.getKey(), rn.getValue());
//                        }
//                    }
//                }).build();
//    }
//
//    /**
//     * @desction: 获取缓存
//     */
//    public static Object get(String key) {
//        return StringUtils.isNotEmpty(key) ? cache.getIfPresent(key) : null;
//    }
//
//    /**
//     * @desction: 放入缓存
//     */
//    public static void put(String key, Object value) {
//        if (StringUtils.isNotEmpty(key) && value != null) {
//            cache.put(key, value);
//        }
//    }
//
//    /**
//     * @desction: 移除缓存
//     */
//    public static void remove(String key) {
//        if (StringUtils.isNotEmpty(key)) {
//            cache.invalidate(key);
//        }
//    }
//
//    /**
//     * @desction: 批量删除缓存
//     */
//    public static void remove(List<String> keys) {
//        if (keys != null && keys.size() > 0) {
//            cache.invalidateAll(keys);
//        }
//    }
//
//}
