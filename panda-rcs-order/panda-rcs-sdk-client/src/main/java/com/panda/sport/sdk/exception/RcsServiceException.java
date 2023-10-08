package com.panda.sport.sdk.exception;

import com.panda.sport.sdk.constant.RcsErrorInfoConstants;


/**
 * 风控系统service服务异常统一处理类
 *
 * @author kane
 * @version v1.1
 * @since 2019-09-11
 */

public class RcsServiceException extends RuntimeException {
    private Integer code;

    private String errorMassage;

    public RcsServiceException() {
        super(RcsErrorInfoConstants.getSysErrorInfo(RcsErrorInfoConstants.FAIL));
        this.code = RcsErrorInfoConstants.FAIL;
        this.errorMassage = RcsErrorInfoConstants.getSysErrorInfo(RcsErrorInfoConstants.FAIL);
    }

    public RcsServiceException(String errorMassage) {
        super(errorMassage);
        this.code = RcsErrorInfoConstants.FAIL;
        this.errorMassage = errorMassage;
    }

    public RcsServiceException(Integer code, String errorMassage) {
        super(errorMassage);
        this.code = code;
        this.errorMassage = errorMassage;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorMassage() {
        return errorMassage;
    }

    public void setErrorMassage(String errorMassage) {
        this.errorMassage = errorMassage;
    }
}
