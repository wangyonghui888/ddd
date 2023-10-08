package com.panda.sport.rcs.exeception;


import com.panda.sport.rcs.constants.RcsErrorInfoConstants;
import lombok.Data;

/**
 *  风控系统dao层异常类
 * @author kane
 * @since 2019-09-11
 * @version v1.1
 */
@Data
public class RcsDaoException extends RuntimeException{
    private String code;

    private String errorMassage;

    public RcsDaoException() {
        super(RcsErrorInfoConstants.getSysErrorInfo(RcsErrorInfoConstants.FAIL));
        this.code =String.valueOf(RcsErrorInfoConstants.FAIL);
        this.errorMassage = RcsErrorInfoConstants.getSysErrorInfo(RcsErrorInfoConstants.FAIL);
    }

    public RcsDaoException(String errorMassage) {
        super(errorMassage);
        this.code = String.valueOf(RcsErrorInfoConstants.FAIL);
        this.errorMassage = errorMassage;
    }

    public RcsDaoException(String code, String errorMassage) {
        super(errorMassage);
        this.code = code;
        this.errorMassage = errorMassage;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
