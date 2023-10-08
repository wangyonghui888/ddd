package com.panda.sport.rcs.oddin.enums;

/**
 * oddin grpc链接状态 1：链接正常 2：断连
 *
 * @author Z9-conway
 */

public enum GrpcConnectionStatusEnum {
    CONNECTED(1, "已连接"),
    DISCONNECTED(2, "已断连"),
    ;
    private Integer code;
    private String value;

    GrpcConnectionStatusEnum(Integer code, String value) {
        this.value = value;
        this.code = code;
    }


    public String getValue() {
        return this.value;
    }

    public Integer getCode() {
        return code;
    }


}
