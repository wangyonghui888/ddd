package com.panda.sport.data.rcs.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 因为trade服务与risk服务这边进行Local集群拆分，有需要在risk服务中使用到的一些Local将以RPC的方式进行提供，广播保存到每一个节点中
 *
 * @Param
 * @Author Waldkir
 * @Date 14:00 2023/03/01
 * @return
 **/
@Data
public class LocalCacheSyncBean<T> implements Serializable {

    /**
     * Local key
     */
    private String key;

    /**
     * 变更后的值
     */
    private T value;

    /**
     * 有value时设置新的过期时间
     */
    private Long expiry;

    /**
     * 避免对象属性不规范 只能通过build构建
     */
    private LocalCacheSyncBean() {

    }

    /**
     * 更新缓存同步 有值与过期时间
     * @param key
     * @param value
     * @param expiry
     * @return
     */
    public static <T> LocalCacheSyncBean build(String key, T value, Long expiry) {
        LocalCacheSyncBean bean = new LocalCacheSyncBean();
        bean.setKey(key);
        bean.setValue(value);
        bean.setExpiry(expiry);
        return bean;
    }
}
