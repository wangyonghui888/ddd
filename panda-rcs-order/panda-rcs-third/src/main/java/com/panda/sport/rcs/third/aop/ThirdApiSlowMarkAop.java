package com.panda.sport.rcs.third.aop;

import com.panda.sport.rcs.third.aop.annotation.SlowMark;
import com.panda.sport.rcs.third.util.cache.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author Beulah
 * @date 2023/4/6 14:13
 * @description 用于监控响应延迟的第三方接口
 */
@Slf4j
@Component
@Aspect
public class ThirdApiSlowMarkAop {


    @Pointcut("@annotation(com.panda.sport.rcs.third.aop.annotation.SlowMark)")
    public void annotationPointcut() {}

    @Around("annotationPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //拿到方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //拿到标记注解
        SlowMark mark = method.getAnnotation(SlowMark.class);
        if (mark != null) {
            //使用类名+方法名
            Class<?> declaringClass = method.getDeclaringClass();
            String key = mark.url();
            if (StringUtils.isBlank(key)) {
                key = declaringClass.getName() + "_" + method.getName();
            }
            StopWatch sw = new StopWatch();
            sw.start("请求:" + key);
            Object proceed = joinPoint.proceed();
            sw.stop();
            long totalTimeMillis = sw.getTotalTimeMillis();
            if (totalTimeMillis >= mark.delay()) {
                key = mark.third() + ":" + key;
                //监控频率转换成毫秒 在rate毫秒内出现多少次查询缓慢的情况
                long rate = mark.rate() * 1000L;
                Integer delayNum = RcsLocalCacheUtils.incBy(key + ":num", 1, mark.cache() * 60 * 60 * 1000L);
                //记录该接口开始出现缓慢的时间点
                if (delayNum == 1) {
                    RcsLocalCacheUtils.timedCache.put(key + ":start", System.currentTimeMillis(), rate + 1000);
                }
                //5次之后记录一个redis标识,该第三方接口在规定的时间预警，并启动缓存查询
                Object cacheTime = RcsLocalCacheUtils.timedCache.get(key + ":start");
                long time = rate;
                if (Objects.nonNull(cacheTime)) {
                    time = System.currentTimeMillis() - (long) cacheTime;
                }
                if (time <= rate && delayNum == mark.num()) {
                    RcsLocalCacheUtils.timedCache.put(key + ":delay", 1, mark.cache() * 60 * 60L);
                    log.warn("请求第三方数据商:{}接口:{}在规定时间:{}秒内出现缓慢次数:{}预警:", mark.third(), mark.url(), mark.rate() * 60, delayNum);
                }
            }
            return proceed;
        }
        return joinPoint.proceed();
    }
}
