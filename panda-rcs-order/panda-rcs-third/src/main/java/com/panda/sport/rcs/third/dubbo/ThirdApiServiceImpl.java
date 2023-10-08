package com.panda.sport.rcs.third.dubbo;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.third.ThirdApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.bts.ThirdBetParamDto;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;

import static com.panda.sport.rcs.third.common.Constants.LINKID;

/**
 * @author Beulah
 * @date 2023/3/20 17:15
 * @description 提供给sdk
 */
@Component
@Slf4j
@Service(connections = 5, retries = 0, timeout = 3000)
public class ThirdApiServiceImpl implements ThirdApiService {


    /**
     * 获取最大限额
     *
     * @param requestParam 请求参数
     * @return 最大限额
     */
    @Override
    public Response<Long> getMaxBetAmount(Request<ThirdBetParamDto> requestParam) {
        MDC.put(LINKID, requestParam.getGlobalId());
        Long maxBetAmount = 2000L;
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            ThirdBetParamDto data = JSONObject.parseObject(JSONObject.toJSONString(requestParam.getData()), ThirdBetParamDto.class);
            //数据商标识
            String third = data.getThird();
            String userId = data.getExtendBeanList().get(0) == null ? null : data.getExtendBeanList().get(0).getUserId();
            log.info("::{}::请求数据商{}最大限额开始:{}", userId, third, JSONObject.toJSONString(data));
            maxBetAmount = ThirdStrategyFactory.getThirdStrategy(third).getMaxBetAmount(data);
        } catch (Exception e) {
            log.error("获取数据商最大限额异常：", e);
        } finally {
            MDC.remove(LINKID);
            sw.stop();
        }
        log.info("请求数据商最大限额结束, 返回:{}, 耗时:{}", maxBetAmount, sw.getTotalTimeMillis());
        return Response.success(maxBetAmount);
    }

}
