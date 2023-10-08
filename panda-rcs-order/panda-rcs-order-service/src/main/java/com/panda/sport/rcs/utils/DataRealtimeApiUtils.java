package com.panda.sport.rcs.utils;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.OutrightTradeOddsConfigDTO;
import com.panda.merge.dto.OutrightTradeProbabilityConfigDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ResultCode;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.merge.dto.TradeMarketAutoDiffConfigDTO;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.dto.TradeMarketMarginConfigDTO;
import com.panda.merge.dto.TradeMarketPlaceConfigDTO;
import com.panda.merge.dto.TradeMarketStatusConfigDTO;
import com.panda.merge.dto.TradeMarketUiConfigDTO;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class DataRealtimeApiUtils {

    private static Map<String, String> requestMap = new HashMap<String, String>() {{
        put(TradeMarketUiConfigDTO.class.getName(), "odds");
        put(StandardMatchMarketDTO.class.getName(), "market_odds");
        put(OutrightTradeOddsConfigDTO.class.getName(), "odds_status");
        put(OutrightTradeProbabilityConfigDTO.class.getName(), "probability");
    }};

    public static <T, R> Response<R> handleApi(T params, ApiCall call) {
        Request<T> request = new Request<T>();
        request.setData(params);
        request.setDataSourceTime(System.currentTimeMillis());
        if (requestMap.containsKey(params.getClass().getName())) {
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_" + requestMap.get(params.getClass().getName()) + "_risk");
        } else {
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_risk");
        }
        log.info("操作开始,参数类：{}，参数：{}", params.getClass(), JsonFormatUtils.toJson(request));
        Response<R> response = call.callApi(request);
        log.info("操作结果：{},request：{},参数类：{}", JSONObject.toJSONString(response), JSONObject.toJSONString(request), params.getClass());
        if (response.isSuccess() || response.getCode() == ResultCode.CHANGETRADEFAILED.getCode().longValue()) {
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
        if (response.isSuccess() || response.getCode() == ResultCode.CHANGETRADEFAILED.getCode().longValue()) {
            return response;
        }
        throw new RcsServiceException("融合异常：" + response.getMsg() + ", linkId:" + request.getLinkId());
    }

    public static interface ApiCall {

        public <R> Response<R> callApi(Request request);

    }

}
