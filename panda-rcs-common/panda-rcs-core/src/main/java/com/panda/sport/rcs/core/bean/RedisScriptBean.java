package com.panda.sport.rcs.core.bean;

import lombok.Data;

@Data
public class RedisScriptBean<T>  {

    /**
     * 脚本摘要
     */
    private volatile String sha1;
    /**
     * 脚本KEY
     */
    private String scriptKey;
    
    private String scriptSource;
    
    private Class<T> resultType;

}
