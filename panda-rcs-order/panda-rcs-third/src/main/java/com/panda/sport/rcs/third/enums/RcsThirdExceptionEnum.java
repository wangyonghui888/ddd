package com.panda.sport.rcs.third.enums;

/**
 *  当前项目自定义风控异常量类，方便处理各种内部场景
 *  1.通用组合   前四位错误码 不需要补码
 *  2.非通用组合 前四位数据商+后三位业务错误码,数据商从第四位往前递增
 *  错误码和错误信息定义类 1. 错误码定义规则为5为数字 2. 前三位表示数据商，最后三位表示业务错误码。
 * @author vere
 * @date 2023-05-28
 * @version 1.0.0
 */
public enum RcsThirdExceptionEnum {

    /**
     * 系统 前四位
     */
    SYS_SUCCESS(200,"成功"),
    HTTP_READ_TIME_OUT(202,"读取网络数据超时"),
    SYS_INTERNAL_ERROR(500,"系统内部错误"),
    HTTP_SYNTAX_ERROR(400,"语法错误"),
    HTTP_UN_AUTHORIZATION(401,"token无权限"),
    HTTP_ACCESS_DENIED(403,"资源不可用"),
    HTTP_NOT_FOUND(404,"无法找到指定资源地址"),
    HTTP_SERVER_NOT_FOUND(503,"服务不可用"),
    HTTP_GATEWAY_TIME_OUT(504,"网关超时"),
    HTTP_OTHER_ERROR(506,"其他异常")

    /**
     * 业务码 后四位的 第一位表示 数据商，后三位具体业务错误码，后三位从0开始往前补位
     */

    /**
     * 红猫错误码
     */
    ;
    RcsThirdExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
    /**
     * 状态码
     */
    private int code;
    /**
     * 状态描述
     */
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
