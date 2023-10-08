package com.panda.sport.rcs.data.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.skywalking.apm.toolkit.trace.Trace;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataRealtimeApiUtils {

    private static Map<String, String> requestMap = new HashMap<String, String>() {{
        // A+模式赔率
        put(StandardMatchMarketDTO.class.getName(), "super_a");
    }};

    public static <T, R> Response<R> handleApi(T params, ApiCall call) {
        Request<T> request = new Request<T>();
        request.setData(params);
        request.setDataSourceTime(System.currentTimeMillis());
        if (requestMap.containsKey(params.getClass().getName())) {
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_" + requestMap.get(params.getClass().getName()) + "_trade");
        } else {
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_trade");
        }
        log.info("操作开始,参数类：{}，参数：{}", params.getClass(), JsonFormatUtils.toJson(request));
        Response<R> response = call.callApi(request);
        log.info("操作结果：{},request：{},参数类：{}", JSONObject.toJSONString(response), JSONObject.toJSONString(request), params.getClass());
        if (response.isSuccess()) {
            return response;
        }
        throw new RcsServiceException("融合异常：" + response.getMsg() + ", linkId:" + request.getLinkId());
    }

    public static <T, R> Response<R> handleApi(String linkId, T params, ApiCall call) {
        Request<T> request = new Request<>();
        request.setData(params);
        request.setDataSourceTime(System.currentTimeMillis());
        request.setLinkId(linkId);
        log.info("操作开始,参数类：{}，参数：{}", params.getClass(), JsonFormatUtils.toJson(request));
        Response<R> response = call.callApi(request);
        log.info("操作结果：{},request：{},参数类：{}", JSONObject.toJSONString(response), JSONObject.toJSONString(request), params.getClass());
        if (response.isSuccess()) {
            return response;
        }
        throw new RcsServiceException("融合异常：" + response.getMsg() + ", linkId:" + request.getLinkId());
    }

    public static interface ApiCall {

        public <R> Response<R> callApi(Request request);

    }

}
