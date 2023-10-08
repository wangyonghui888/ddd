package com.panda.sport.data.rcs.api;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Description:请求头信息
 * @date 2019/9/5 23:13
 * @Version 1.0
 */
@Data
public class Request<T> implements java.io.Serializable {
    /**
     * 全局唯一ID
     */
    private String globalId;


    /**
     * 请求的参数数据
     */
    private T data = null;


}
