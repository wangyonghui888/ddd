package com.panda.sport.rcs.exeception;

import com.panda.sport.rcs.constants.RcsErrorInfoConstants;
import lombok.Data;

/**
 * 风控系统service服务异常统一处理类
 *
 * @author kane
 * @version v1.1
 * @since 2019-09-11
 */
@Data
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
}
