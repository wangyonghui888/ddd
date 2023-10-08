package com.panda.sport.rcs.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ResultCode;
import com.panda.merge.dto.TradeMarketUiConfigDTO;
import com.panda.sport.manager.api.dto.ChangeLiveWeightDTO;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataRealtimeApiUtils {
	
    private static Map<String, String> requestMap = new HashMap<String, String>() {{
    	//分时margain
        put(TradeMarketUiConfigDTO.class.getName(), "task_margain");
    }};

    /**
     * 公共请求融合rpc方法
     * @param params  请求参数
     * @param matchId  标准赛事Id
     * @param playId 玩法Id，如果多个玩法，则传0
     * @param call
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Response<R> handleApi(T params, String matchId, Integer playId, ApiCall call) {
        Request<T> request = new Request<T>();
        request.setData(params);
        request.setDataSourceTime(System.currentTimeMillis());

        if (requestMap.containsKey(params.getClass().getName())) {
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_" + requestMap.get(params.getClass().getName()) + "_trade");
        } else {
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_task");
        }
        Long startTime = System.currentTimeMillis();
        String queryLinkId = "template_job_request_" + matchId + "_" + playId;
        log.info("::{}::调用融合接口，请求参数：{}", queryLinkId, JsonFormatUtils.toJson(request));
        Response<R> response = call.callApi(request);
        log.info("::{}::调用融合接口，响应数据：{}，请求耗时：{}毫秒", queryLinkId, JSONObject.toJSONString(response), System.currentTimeMillis() - startTime);
        if (response.isSuccess()) {
            return response;
        }
        throw new RcsServiceException("融合异常：" + response.getMsg() + ", linkId:" + request.getLinkId());
    }

    public static <T, R> Response<R> handleApi(T params, ApiCall call) {
        Request<T> request = new Request<T>();
        request.setData(params);
        request.setDataSourceTime(System.currentTimeMillis());
        if (requestMap.containsKey(params.getClass().getName())) {
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_" + requestMap.get(params.getClass().getName()) + "_task");
        } else {
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_task");
        }
//        request.setOperaterId(TradeUserUtils.getUserIdNoException().longValue());
        log.info("::{}::调用RPC接口开始，请求参数：{}", request.getLinkId(), JsonFormatUtils.toJson(request));
        Response<R> response = call.callApi(request);
        log.info("::{}::调用RPC接口结果，响应内容：{}", request.getLinkId(), JSONObject.toJSONString(response));
        if (response.isSuccess() || response.getCode() == ResultCode.CHANGETRADEFAILED.getCode().longValue()) {
            return response;
        }
        throw new RcsServiceException("融合异常：" + response.getMsg() + ", linkId:" + request.getLinkId());
    }

    public static interface ApiCall {
        public <R> Response<R> callApi(Request request);
    }

}
