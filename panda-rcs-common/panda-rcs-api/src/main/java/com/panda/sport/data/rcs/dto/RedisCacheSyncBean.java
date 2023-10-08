package com.panda.sport.data.rcs.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * 因为trade服务与risk服务这边进行redis集群拆分，所有遂于一些redis缓存数据需要进行同步，当trade发生变更需要同步发送mq在此处进行同步del或者set
 * 注意： 如果有本地缓存的需要额外在各自服务在进行特殊处理
 *
 * @Param
 * @Author Magic
 * @Date 14:00 2023/01/10
 * @return
 **/
@Data
public class RedisCacheSyncBean implements Serializable {

    /**
     * 业务类型 方便做业务特殊处理
     */
    private String type;

    /**
     * redis key
     */
    private String key;

    /**
     * redis key
     */
    private String subKey;

    /**
     * 变更后的值
     */
    private String value;

    /**
     * 有value时设置新的过期时间
     */
    private Long expiry;

    /**
     * 避免对象属性不规范 只能通过build构建
     */
    private RedisCacheSyncBean() {

    }

    /**
     * 删除缓存同步没有value
     * @param type
     * @param key
     * @return
     */
    public static RedisCacheSyncBean build(String type, String key) {
        RedisCacheSyncBean bean = new RedisCacheSyncBean();
        bean.setType(type);
        bean.setKey(key);
        return bean;
    }


    /**
     * 删除hash缓存同步没有value
     * @param type
     * @param key
     * @param subKey
     * @return
     */
    public static RedisCacheSyncBean build(String type, String key, String subKey) {
        RedisCacheSyncBean bean = new RedisCacheSyncBean();
        bean.setType(type);
        bean.setKey(key);
        bean.setSubKey(subKey);
        return bean;
    }

    /**
     * 更新缓存同步 有值与过期时间
     * @param type
     * @param key
     * @param value
     * @param expiry
     * @return
     */
    public static RedisCacheSyncBean build(String type, String key, String value, Long expiry) {
        RedisCacheSyncBean bean = new RedisCacheSyncBean();
        bean.setType(type);
        bean.setKey(key);
        bean.setValue(value);
        bean.setExpiry(expiry);
        return bean;
    }

    /**
     * 更新hash缓存同步 有子key有值与过期时间
     * @param type
     * @param key
     * @param subKey
     * @param value
     * @param expiry
     * @return
     */
    public static RedisCacheSyncBean build(String type, String key,String subKey, String value, Long expiry) {
        RedisCacheSyncBean bean = new RedisCacheSyncBean();
        bean.setType(type);
        bean.setKey(key);
        bean.setValue(value);
        bean.setSubKey(subKey);
        bean.setExpiry(expiry);
        return bean;
    }
}
