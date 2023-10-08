package com.panda.sport.rcs.utils;

import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataRealtimeApiUtils {

	public static <T,R> Response<R> handleApi(T params ,ApiCall call){
		Request<T> request = new Request<T>();
    	request.setData(params);
		request.setDataSourceTime(System.currentTimeMillis());
    	request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_trade");
		log.info("操作开始：{}", JsonFormatUtils.toJson(request));
    	Response<R> response = call.callApi(request);
    	log.info("操作结果：{},request：{}",JSONObject.toJSONString(response),JSONObject.toJSONString(request));
    	if(response.isSuccess()) {
    		return response;
    	}
    	throw new RcsServiceException("调用融合api操作异常：" + response.getMsg());
	}
	
	public static interface ApiCall{
		
		public <R> Response<R> callApi(Request request);

	}

}
