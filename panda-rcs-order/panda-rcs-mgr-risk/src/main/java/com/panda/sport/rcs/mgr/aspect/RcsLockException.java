package com.panda.sport.rcs.mgr.aspect;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.aspect
 * @Description :  rcs lock
 * @Date: 2020-04-01 15:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class RcsLockException extends RuntimeException {
    public RcsLockException() {
    }

    public RcsLockException(String message) {
        super(message);
    }

    public RcsLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public RcsLockException(Throwable cause) {
        super(cause);
    }

    public RcsLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
