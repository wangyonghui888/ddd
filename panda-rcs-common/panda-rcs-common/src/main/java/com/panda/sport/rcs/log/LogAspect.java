package com.panda.sport.rcs.log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.sport.rcs.log.annotion.NotWriteLog;
import com.panda.sport.rcs.log.annotion.WriteLog;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
@ConditionalOnProperty(name = "rcs.log.switch", matchIfMissing = true)
public class LogAspect {

	@Pointcut("execution(* com.panda.sport.rcs.service..*.*(..))" +
			"||execution(* com.panda.sport.rcs.mapper..*.*(..))" +
			"||execution(* com.panda.sport.rcs.core.cache.client..*.*(..))" +
			"||execution(* com.panda.sport.rcs.task..*.*(..))" +
			"||execution(* com.panda.sport.data.realtime.api..*.*(..))" +
			"||execution(* com.panda.sport.rcs.websocket.service..*.*(..))" +
			"||execution(* com.panda.sport.rcs.mgr.wrapper..*.*(..))" +
			"||execution(* com.panda.sport.rcs.data.mapper..*.*(..))" +
			"||execution(* com.panda.sport.rcs.data.service..*.*(..))" +
			"||execution(* com.panda.sport.rcs.mts.sportradar..*.*(..))" +
			"||execution(* com.panda.sport.rcs.trade.wrapper..*.*(..))" +
			"||execution(* com.panda.sport.rcs.task.wrapper..*.*(..))" +
			"||execution(* com.panda.sport.rcs.websocket.wrapper..*.*(..))" +
			"||execution(* com.panda.sport.rcs.limit.service..*.*(..))" +
			"||execution(* org.springframework.data.mongodb.core.MongoTemplate*.*(..))" +
			"||execution(* com.panda.sport.data.rcs.api..*.*(..))"
			)
    public void logAspectPointCut(){};
    /**
     * 是否打印日志
     * true  是
     * false  否
     * @param method
     * @param point
     * @return
     */
    private boolean isPrintLog(Method method,JoinPoint point) {
    	NotWriteLog notWriteLog = method.getAnnotation(NotWriteLog.class);
    	if(notWriteLog != null) return false;
    	
    	WriteLog writeLog = point.getTarget().getClass().getAnnotation(WriteLog.class);
    	if(writeLog != null) {
    		WriteLog methodWriteLog = method.getAnnotation(WriteLog.class);
    		if(methodWriteLog != null) return true;
    		else return false;
    	}
    	return true;
    }

	@Before(value = "logAspectPointCut()" )
	public void before(JoinPoint point) {
		MethodSignature sign = (MethodSignature) point.getSignature();
		Method method = sign.getMethod();
		if(!isPrintLog(method, point)) return;

		LogContext context = LogContext.getContext();
		int index = context.addIndex();
		if(!context.isClean() && index == 1) {
			context.setCurrnetClean(true);
			context.setCleanIndex(index);
		}

		Map<Integer, Map<String, Object>> methodMap = context.getMethodsInfo();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", System.currentTimeMillis());
		methodMap.put(index, map);

		Object[] args = point.getArgs();

		//log.info("method:{},args:{}",point.getTarget().getClass() + "." + method.getName(),JSONObject.toJSONString(args, SerializerFeature.IgnoreErrorGetter));
	}

	@AfterReturning(pointcut="logAspectPointCut()",returning = "result")
	public void afterReturning(JoinPoint point,Object result) {
		MethodSignature sign = (MethodSignature) point.getSignature();
		Method method = sign.getMethod();
		if(!isPrintLog(method, point)) return;

		LogContext context = LogContext.getContext();
		int index = context.subIndex();

		Object[] args = point.getArgs();

		Map<String, Object> methodMap = context.getMethodsInfo().get(index);
		Long startTime = Long.parseLong(String.valueOf(methodMap.get("startTime")));
		
		//log.info("method:{},args:{},return:{},exeTime:{}ms",point.getTarget().getClass() + "." + method.getName(),JSONObject.toJSONString(args, SerializerFeature.IgnoreErrorGetter),JSONObject.toJSONString(result, SerializerFeature.IgnoreErrorGetter),System.currentTimeMillis() - startTime);
//		if(context.isCurrnetClean() && context.getCleanIndex() == index) {
//			LogContext.remove();
//		}
	}

}
