package com.panda.sport.rcs.pojo;



import java.io.Serializable;

/**
 * 风控系统所有的pojo的基类
 * @author kane
 * @since 2019-09-04
 * @version v1.1
 * @param
 */
public abstract class RcsBaseEntity implements Serializable,Cloneable {

    /**
     * 全局唯一ID
     */
    private String globalId;


}
