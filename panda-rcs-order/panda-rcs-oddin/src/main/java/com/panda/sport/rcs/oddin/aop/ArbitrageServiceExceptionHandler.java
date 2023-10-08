package com.panda.sport.rcs.oddin.aop;

import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.oddin.util.DjMqUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import java.util.List;

/**
 * 异常统一处理
 * @author Z9-conway
 */
@Slf4j
@RestControllerAdvice
public class ArbitrageServiceExceptionHandler {
    @Resource
    private RedisClient redisClient;
    @Resource
    private DjMqUtils djMqUtils;

    @ExceptionHandler({RcsServiceException.class})
    public Response handleServiceException(RcsServiceException e) throws Exception {
        log.info(e.getMessage(), e);
        return Response.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常拦截
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error(e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        ObjectError firstError = null;
        if (bindingResult != null && bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            firstError = errors.get(0);
        }
        int defaultErrorCode = Response.FAIL;
        String defaultErrorMsg = "参数错误";
        Response apiResponse = Response.error(defaultErrorCode, defaultErrorMsg);
        if(firstError != null){
            String errorMsg = firstError.getDefaultMessage();
            if(StringUtils.isNotBlank(errorMsg)){
                try{
//                    ArbitrageResponseCode errorResCode = ArbitrageResponseCode.getResponseCode(Integer.parseInt(errorMsg));
//                    if(errorResCode != null){
//                        apiResponse = ApiResponse.create(errorResCode.getCode(), errorResCode.getMsg());
//                    }
                    apiResponse = Response.error(Response.FAIL, errorMsg);
                }catch (NumberFormatException e3){
                    log.error(e.getMessage(), e);
                    apiResponse = Response.error(defaultErrorCode, errorMsg);
                }
            }

        }

        return apiResponse;
    }


}
