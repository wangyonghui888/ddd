package com.panda.sport.rcs.third.service.third.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.common.ThirdResultVo;
import com.panda.sport.rcs.third.entity.gts.GtsBetAssessmentLegsVo;
import com.panda.sport.rcs.third.entity.gts.GtsBetAssessmentResVo;
import com.panda.sport.rcs.third.enums.OrderStatusEnum;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.third.util.encrypt.ZipStringUtils;
import com.panda.sport.rcs.third.util.http.AsyncHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static com.panda.sport.rcs.third.common.Constants.GTS_BET_PLACED_ORDER_NO;

@Slf4j
@Component
public class GtsCallBack implements FutureCallback<HttpResponse> {

    @Resource
    IOrderHandlerService iOrderHandlerService;
    @Resource
    RedisClient redisClient;


    @Override
    public void completed(HttpResponse result) {
        log.info("请求GTS响应状态：->" + result.getStatusLine());
        try {
            log.info("当前请求状态：" + AsyncHttpClient.poolManager.getTotalStats() + ", response=" + EntityUtils.toString(result.getEntity()));
        } catch (IOException e) {
            log.error("===================", e);
        }
        try {
            GtsBetAssessmentResVo assessmentResVo = JSONObject.parseObject(EntityUtils.toString(result.getEntity()), GtsBetAssessmentResVo.class);
            ThirdResultVo resultVo = new ThirdResultVo();
            int delayTime = getDelayTime(assessmentResVo.getBetDelay());
            resultVo.setDelay(delayTime);
            resultVo.setThirdRes(JSONObject.toJSONString(assessmentResVo));
            resultVo.setThirdNo(assessmentResVo.getBetId());
            if (assessmentResVo.getIsBetAllowed()) {
                resultVo.setThirdOrderStatus(OrderStatusEnum.ACCEPTED.getCode());
            } else {
                if (delayTime > 0) {
                    //有延迟时间，等待
                    resultVo.setThirdOrderStatus(OrderStatusEnum.WAITING.getCode());
                } else {
                    resultVo.setThirdOrderStatus(OrderStatusEnum.REJECTED.getCode());
                }
            }
            String msg = assessmentResVo.getRejectReason() != null ? assessmentResVo.getRejectReason().getReasonCode() : null;
            if (msg == null) {
                List<GtsBetAssessmentLegsVo> legs = assessmentResVo.getLegs();
                msg = CollectionUtils.isEmpty(legs) ? null : legs.get(0).getRejectReason() == null ? null : legs.get(0).getRejectReason().getReasonCode();
            }
            resultVo.setReasonMsg(msg);
            resultVo.setErrorCode(assessmentResVo.getRejectReason() != null ? assessmentResVo.getRejectReason().getReasonCode() : null);

            String redisKey = String.format(GTS_BET_PLACED_ORDER_NO, resultVo.getThirdNo());
            //如果本地缓存不存在则从redis取数据
            String values = redisClient.get(redisKey);
            if (org.apache.commons.lang.StringUtils.isNotBlank(values)) {
                //进行解压缩操作
                values = ZipStringUtils.gunzip(values);
            }
            ThirdOrderExt ext = JSONObject.parseObject(values, ThirdOrderExt.class);
            iOrderHandlerService.checkOrderAfter(resultVo, ext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void failed(Exception ex) {

    }

    @Override
    public void cancelled() {

    }

    private int getDelayTime(String betDelay) {
        if (StringUtils.isNotEmpty(betDelay)) {
            String[] arr = betDelay.split(":");
            int hour = Integer.parseInt(arr[0]);
            int minute = Integer.parseInt(arr[1]);
            int second = Integer.parseInt(arr[2]);
            return hour * 60 * 60 + minute * 60 + second;
        }
        return 0;
    }

}
