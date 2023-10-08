package com.panda.sport.rcs.trade.aspect;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.log.annotion.OperateLog;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.trade.strategy.logFormat.*;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * OperateLog 操盤日誌AOP
 */
@Slf4j
@Aspect
@Component
public class OperateLogAspect {
    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(20, 40, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000), new ThreadPoolExecutor.CallerRunsPolicy());
    private final ProducerSendMessageUtils sendMessage;
    @Autowired
    public OperateLogAspect(ProducerSendMessageUtils sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Around("@annotation(com.panda.sport.rcs.log.annotion.OperateLog)")
    public Object operLog(ProceedingJoinPoint joinPoint) throws Throwable {

        beforeProcess(joinPoint);
        Object resultObj = joinPoint.proceed();
        afterProcess(joinPoint, resultObj);
        return resultObj;
    }

    private void beforeProcess(ProceedingJoinPoint joinPoint) {
    }

    private void afterProcess(ProceedingJoinPoint joinPoint, Object resultObj) {
        try {
            HttpResponse result = (HttpResponse) resultObj;
            if (200 == result.getCode()) {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = signature.getMethod();
                OperateLog annotation = method.getAnnotation(OperateLog.class);
                RcsOperateLog preLogBean = new RcsOperateLog(annotation);
                pool.execute(() -> {
                    try {
                        LogParameters logParameters=new LogParameters();
                        logParameters.setLog(preLogBean);
                        logParameters.setArgs(joinPoint.getArgs());
                        logParameters.setMethodName(method.getName());
                        log.info("操盤日誌-流程開始");
                        sendMessage.sendMessage("rcs_log_operate", "", CommonUtil.getRequestId() + "", logParameters);
                    } catch (Exception e) {
                        log.info("::{}::操盤日誌-策略流程异常{}::{}", CommonUtil.getRequestId(), e.getMessage());
                    } finally {
                        log.info("操盤日誌-流程結束");
                    }
                });
            }
        } catch (Exception e) {
            log.error("::{}::{}", CommonUtil.getRequestId(), e.getMessage(), e);
        }
    }



}
