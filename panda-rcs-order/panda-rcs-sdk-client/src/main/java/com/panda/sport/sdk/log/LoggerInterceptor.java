//package com.panda.sport.sdk.log;
//
//import org.aopalliance.intercept.MethodInterceptor;
//import org.aopalliance.intercept.MethodInvocation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//
//public class LoggerInterceptor implements MethodInterceptor {
//
//	private static final Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);
//
//    @Override
//    public Object invoke(MethodInvocation invocation) throws Throwable {
//        Object obj = null;
//        StackTraceElement[] element = null;
//        long startTime = System.currentTimeMillis();
//        NotWriteLog anno = invocation.getMethod().getAnnotation(NotWriteLog.class);
//        String className = invocation.getMethod().getDeclaringClass().getName();
//    	String methodName = className + "." + invocation.getMethod().getName();
//    	Object[] params = invocation.getArguments();
//    	String paramsJson = JSONObject.toJSONString(params, SerializerFeature.IgnoreErrorGetter);
//
//        try {
//        	if(anno == null ) {
//        		log.info("exe methodName :{},paramsJson:{}",methodName,paramsJson);
//        	}
//            obj = invocation.proceed();// 执行服务
//        }catch (Exception e) {
//        	element = e.getStackTrace();
//        	log.error(e.getMessage(),e);
//        	throw e;
//		} finally {
//			if(anno == null ) {
//				 long endTime = System.currentTimeMillis();
//		            String result = null;
//		            if(obj != null) {
//		            	result = JSONObject.toJSONString(obj, SerializerFeature.IgnoreErrorGetter);
//		            }
//		            if(element == null) {
//		            	log.info("exe methodName :{},paramsJson:{},return:{},exeTime:{}",methodName,paramsJson,result, endTime - startTime);
//		            }else {
//		            	log.info("exe methodName :{},paramsJson:{},return:{},exception:{},exeTime:{}",
//		            			methodName,paramsJson,result,element, endTime - startTime);
//		            }
//			}
//
//        }
//        return obj;
//    }
//}
