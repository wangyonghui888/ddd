package com.panda.sport.rcs.third.aop;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.panda.sport.rcs.third.aop.annotation.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Beulah
 * @date 2023/4/6 10:55
 * @description 限流切面
 */
@Slf4j
@Aspect
@Component
public class RateLimitAop {

    private final Map<String, RateLimiter> limitMap = Maps.newConcurrentMap();


    /**
     * 环切
     *
     * @param joinPoint 切入点
     */
    @Around("@annotation(com.panda.sport.rcs.third.aop.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //拿到方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //拿limit的注解
        RateLimit limit = method.getAnnotation(RateLimit.class);
        if (limit != null) {
            //key作用：不同的接口，不同的流量控制
            String key = limit.key();
            if (StringUtils.isBlank(key)) {
                //使用类名+方法名
                Class<?> declaringClass = method.getDeclaringClass();
                key = declaringClass.getName() + "_" + method.getName();
            }

            RateLimiter rateLimiter = null;
            //验证缓存是否有命中key
            if (!limitMap.containsKey(key)) {
                // 创建令牌桶
                rateLimiter = RateLimiter.create(limit.permitsPerSecond());
                limitMap.put(key, rateLimiter);
                log.info("新建了令牌桶={}，容量={}", key, limit.permitsPerSecond());
            }
            rateLimiter = limitMap.get(key);
            // 拿令牌
            boolean acquire = rateLimiter.tryAcquire(limit.timeout(), limit.timeunit());
            // 拿不到命令，直接返回异常提示
            if (!acquire) {
                log.debug("令牌桶={}，获取令牌失败", key);
                this.responseFail(limit.msg());
                return null;
            }
        }
        return joinPoint.proceed();

    }

    /**
     * 直接向前端抛出异常
     *
     * @param msg 提示信息
     */
    private void responseFail(String msg) throws Exception {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        Map m = new HashMap<>();
        m.put("code", 500);
        m.put("msg", msg);
        writer.write(m.toString());
    }


}
