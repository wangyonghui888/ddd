package com.panda.sport.rcs.third.task;


import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.third.config.GtsInitConfig;
import com.panda.sport.rcs.third.entity.gts.GtsAuthorizationVo;
import com.panda.sport.rcs.third.util.http.HttpUtil;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.panda.sport.rcs.third.common.Constants.GTS_TOKEN;
import static com.panda.sport.rcs.third.common.Constants.GTS_TOKEN_TOPIC;

/**
 * gts Token定时刷新
 * 分布式任务实现类似分布式锁的效果
 */
@Component
@Slf4j
@JobHandler(value = "gtsTokenRefreshJobHandler")
public class GtsTokenRefreshJobHandler extends IJobHandler {

    @Resource
    GtsInitConfig gtsConfig;
    @Autowired
    RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("GTS刷新TOKEN开始....");
        //投注鉴权token
        getToken(gtsConfig.getBetAssessClientId(), gtsConfig.getBetAssessClientCecret()
                , gtsConfig.getGrantType(), gtsConfig.getTokenUrl(), 1);
        //确认鉴权token
        getToken(gtsConfig.getBetReceiverClientId(), gtsConfig.getBetReceiverClientCecret()
                , gtsConfig.getGrantType(), gtsConfig.getTokenUrl(), 2);
        log.info("GTS刷新TOKEN结束....");
        return ReturnT.SUCCESS;
    }

    private void getToken(String clientId, String clientSecret, String grantType, String url, Integer type) {
        //组装请求参数
        Map<String, String> map = new HashMap<>();
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("grant_type", grantType);
        log.info("::刷新[GTS]token请求参数::{}", JSONObject.toJSON(map));
        try {
            String data = HttpUtil.post(url, map, true);
            log.error("::刷新[GTS]token返回::{}", data);
            GtsAuthorizationVo vo = JSONObject.parseObject(data, GtsAuthorizationVo.class);
            vo.setType(type);
            vo.setRefreshTime(System.currentTimeMillis());
            String tokenKey = String.format(GTS_TOKEN, type);
            //拿到token存缓存 ExpiresIn是接口返回的token失效时间 提前10分钟过期 避免临界点问题
            redisClient.setExpiry(tokenKey, JSONObject.toJSONString(vo), vo.getExpiresIn() - 10 * 60L);
            //更新所有节点缓存
            sendMessage.sendMessage(GTS_TOKEN_TOPIC, vo);
        } catch (Exception e) {
            log.error("::刷新[GTS]token异常::", e);
        }
    }


}
