package com.panda.sport.data.rcs.api;

/**
 * @Description: 接口返回数据
 * @date 2019/9/5 23:13
 * @Version 1.0
 */
public class Response<T> implements java.io.Serializable {
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
     * @param code
     * @param msg
     * @param data
     */
    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Response() {
    }
    /**
     * 返回成功结果 默认code=200 msg="成功"
     *
     * @return
     */
    public static Response success() {
        return new Response(SUCCESS,"成功");
    }

    /**
     * 返回成功结果
     *
     * @param object
     * @return Response
     */
    public static <T> Response success(T object) {
        Response response = success();
        response.data = object;
        return response;
    }

    /**
     * 返回失败结果
     *
     * @param object
     * @return Response
     */
    public static <T> Response fail(T object) {
        Response response = new Response();
        response.msg = "失败";
        response.data = object;
        response.code = FAIL;
        return response;
    }

    /**
     * 返回错误结果
     */
    public static <T> Response error(int code, String msg, T datas) {
        Response response = new Response();
        response.code = code;
        response.msg = msg;
        response.data = datas;
        return response;
    }

    /**
     * 返回错误结果
     */
    public static <T> Response error(int code, String msg) {
        Response response = new Response();
        response.code = code;
        response.msg = msg;
        return response;
    }

    public boolean isSuccess() {
        return SUCCESS ==getCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
