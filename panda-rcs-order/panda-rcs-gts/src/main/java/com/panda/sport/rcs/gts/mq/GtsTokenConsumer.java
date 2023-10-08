package com.panda.sport.rcs.gts.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.gts.common.Constants;
import com.panda.sport.rcs.gts.service.GtsCommonService;
import com.panda.sport.rcs.gts.service.GtsThirdApiService;
import com.panda.sport.rcs.gts.service.RcsGtsOrderExtService;
import com.panda.sport.rcs.gts.task.GtsOrderDelayTask;
import com.panda.sport.rcs.gts.util.SystemThreadLocal;
import com.panda.sport.rcs.gts.vo.GtsAuthorizationVo;
import com.panda.sport.rcs.gts.vo.GtsBetResultVo;
import com.panda.sport.rcs.gts.vo.GtsMerchantOrder;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.pojo.RcsGtsOrderExt;
import com.panda.sport.rcs.pojo.RcsLabelLimitConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.panda.sport.rcs.gts.common.Constants.*;

/**
 * token刷新消费
 *
 * @author lithan
 * @date 2023-01-07 18:28:02
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = GTS_TOKEN_TOPIC,
        consumerGroup = "rcs_gts_token_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class GtsTokenConsumer implements RocketMQListener<GtsAuthorizationVo>, RocketMQPushConsumerLifecycleListener {

    @Resource
    RedisClient redisClient;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(GtsAuthorizationVo authorizationVo) {
        //刷新本机token
        RcsLocalCacheUtils.getValue(String.format(GTS_TOKEN, authorizationVo.getType()), redisClient::get, 10 * 60 * 1000L);
        log.info("::{}::刷新token一次", JSONObject.toJSON(authorizationVo));
    }


}