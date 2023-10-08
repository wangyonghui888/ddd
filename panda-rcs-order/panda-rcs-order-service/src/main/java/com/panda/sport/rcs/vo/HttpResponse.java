package com.panda.sport.rcs.vo;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * @Description: 接口返回数据
 * @date 2019/9/5 23:13
 * @Version 1.0
 */
@Data
public class HttpResponse<T> implements java.io.Serializable {
    public final static int SUCCESS = 200;
    public final static int FAIL = 500;

    /**
     * 默认成功
     */
    private int code = SUCCESS;

    /**
     * 默认提示语
     */
    private String msg = "成功";
    private T data = null;
    /**
     * 额外数据
     */
    private Map<String, Object> extraData = Maps.newHashMap();
    private String linkId;

    /**
     * @param code
     * @param msg
     * @param data
     */
    public HttpResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public HttpResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public HttpResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public HttpResponse() {
    }

    /**
     * 返回成功结果 默认code=200 msg="成功"
     *
     * @return
     */
    public static HttpResponse success() {
        return new HttpResponse(SUCCESS, "成功");
    }

    /**
     * 返回成功结果
     *
     * @param object
     * @return Response
     */
    public static <T> HttpResponse success(T object) {
        HttpResponse response = new HttpResponse();
        response.data = object;
        return response;
    }

    /**
     * 返回成功结果
     *
     * @param data
     * @param extraData
     * @param <T>
     * @return
     */
    public static <T> HttpResponse<T> success(T data, Map<String, Object> extraData) {
        HttpResponse<T> response = new HttpResponse<>();
        response.data = data;
        response.extraData = extraData;
        return response;
    }

    /**
     * 返回成功结果
     *
     * @param data
     * @param linkId
     * @param <T>
     * @return
     */
    public static <T> HttpResponse<T> success(T data, String linkId) {
        HttpResponse<T> response = new HttpResponse<>();
        response.data = data;
        response.linkId = linkId;
        return response;
    }

    /**
     * 返回失败结果
     *
     * @param object
     * @return Response
     */
    public static <T> HttpResponse fail(T object) {
        HttpResponse response = new HttpResponse();
        response.data = object;
        response.code = FAIL;
        return response;
    }

    /**
     * 返回失败结果
     *
     * @param msg
     * @return Response
     */
    public static <T> HttpResponse<T> failToMsg(String msg) {
        HttpResponse<T> response = new HttpResponse<>();
        response.code = FAIL;
        response.msg = msg;
        return response;
    }

    /**
     * 返回失败结果
     *
     * @param msg
     * @param linkId
     * @param <T>
     * @return
     */
    public static <T> HttpResponse<T> failToMsg(String msg, String linkId) {
        HttpResponse<T> response = new HttpResponse<>();
        response.code = FAIL;
        response.msg = msg;
        response.linkId = linkId;
        return response;
    }

    /**
     * 返回错误结果
     */
    public static <T> HttpResponse error(int code, String msg, T datas) {
        HttpResponse response = new HttpResponse();
        response.code = code;
        response.msg = msg;
        response.data = datas;
        return response;
    }

    /**
     * 返回错误结果
     */
    public static <T> HttpResponse error(int code, String msg) {
        HttpResponse response = new HttpResponse();
        response.code = code;
        response.msg = msg;
        return response;
    }

    public boolean isSuccess() {
        return SUCCESS == getCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
