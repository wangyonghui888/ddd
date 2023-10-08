package com.panda.sport.rcs.mgr.aspect;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.OrderBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Order(-1)
public class LogTraceAspect {
    @Pointcut("execution(* com.panda.sport.data.rcs.api..*.*(..))")
    public void tracePointcut() { }

    @Around("tracePointcut()")
    public Object setTraceId(ProceedingJoinPoint joinPoint) throws Throwable{
        //1.这里获取到所有的参数值的数组
        Object[] args = joinPoint.getArgs();
        if(args!=null && args.length>0 && (args[0] instanceof Request)){
            Request<OrderBean> params = (Request<OrderBean>)args[0];
            MDC.put("X-B3-TraceId", params.getGlobalId());
        }
        return joinPoint.proceed();
    }
}
