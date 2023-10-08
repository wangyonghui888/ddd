package com.panda.sport.data.rcs.api;

/**
 * @Description:请求头信息
 * @date 2019/9/5 23:13
 * @Version 1.0
 */
public class Request<T> implements java.io.Serializable {
    /**
     * 全局唯一ID
     */
    private String globalId;


    /**
     * 请求的参数数据
     */
    private T data = null;

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
