package com.panda.sport.rcs.common.exception;


import com.panda.sport.rcs.common.bean.Result;

/**
 * 自定义异常
 *
 * @author lithan
 */
public class SysException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Result<Object> result;

    public SysException(Result<Object> result) {
        super(result.getMsg());
        this.result = result;
    }

    public SysException(String message) {
        super(message);
        this.result = Result.fail(message);
    }

    public SysException(Throwable cause) {
        super(cause);
    }

    public SysException(String message, Throwable cause) {
        super(message, cause);
    }

    public Result<Object> getResult() {
        return result;
    }
}
