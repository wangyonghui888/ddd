package com.panda.sport.rcs.common.exception;


import com.panda.sport.rcs.common.bean.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 通用 Api Controller 全局异常处理
 *
 * @author lithan
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * <p>
     * 自定义 REST 业务异常
     * <p>
     *
     * @param e 异常类型
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Result<Object> handleBadRequest(Exception e) {
        if (e instanceof SysException) {
            StackTraceElement s = e.getStackTrace()[0];
            log.error("业务异常:{}.{}:{} Error Message:{}", s.getClassName(), s.getMethodName(), s.getLineNumber(), e.getMessage());
            SysException sysException = (SysException) e;
            return sysException.getResult();
        }
        showException(e);
        if (e instanceof BindException) {
            BindingResult bindingResult = ((BindException) e).getBindingResult();
            return Result.fail(getBindingName(bindingResult));
        } else if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            return Result.fail(getBindingName(bindingResult));
        } else {
            log.error("global 异常 : " + e);
            return Result.fail("系统繁忙");
        }
    }

    /**
     * 打印日志
     *
     * @param e 异常
     */
    private void showException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log.info("全局捕捉异常:" + sw.toString());
            pw.flush();
            sw.flush();
            pw.close();
            sw.close();
        } catch (Exception ee) {
            log.error("全局输出日志异常:" + ee.getMessage());
            ee.printStackTrace();
        }
    }

    /**
     * 错误信息读取
     *
     * @param bindingResult
     * @return
     */
    private String getBindingName(BindingResult bindingResult) {
        if (null != bindingResult && bindingResult.hasErrors()) {
            FieldError fieldError = (FieldError) bindingResult.getAllErrors().get(0);
            Object[] obj = fieldError.getArguments();
            DefaultMessageSourceResolvable defaultMessageSourceResolvable = (DefaultMessageSourceResolvable) obj[0];
            return defaultMessageSourceResolvable.getDefaultMessage() + fieldError.getDefaultMessage();
        }
        return "参数";
    }

}
