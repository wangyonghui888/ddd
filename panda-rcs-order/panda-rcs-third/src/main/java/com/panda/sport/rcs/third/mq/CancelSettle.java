package com.panda.sport.rcs.third.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.third.entity.common.CancelSettleBean;
import com.panda.sport.rcs.third.entity.common.pojo.RcsGtsOrderExt;
import com.panda.sport.rcs.third.entity.gts.GtsBetGeniusContentVo;
import com.panda.sport.rcs.third.entity.gts.GtsBetReceiverCache;
import com.panda.sport.rcs.third.entity.gts.GtsBetReceiverRequestVo;
import com.panda.sport.rcs.third.mapper.RcsGtsOrderExtMapper;
import com.panda.sport.rcs.third.service.third.impl.GTSServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.panda.sport.rcs.third.common.Constants.GTS_SETTLE_INFO;


/**
 * @author Beulah
 * @date 2023/8/15 13:31
 * @description 取消结算处理
 */
@Slf4j
@Service
@TraceCrossThread
@RocketMQMessageListener(
        topic = RcsConstant.RISK_ORDER_STATUS_OPERATE,
        consumerGroup = "risk_order_status_operate_third",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class CancelSettle implements RocketMQListener<CancelSettleBean>, RocketMQPushConsumerLifecycleListener {

    @Resource
    RedisClient redisClient;
    @Autowired
    GTSServiceImpl gtsService;
    @Resource
    RcsGtsOrderExtMapper rcsGtsOrderExtMapper;



    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(CancelSettleBean msg) {
        try {
            if (msg == null) {
                log.warn("::接收到CancelSettle结算消息为空，不处理::");
                return;
            }
            String orderNo = msg.getOrderNo();
            //operateType操作类型 1：结算；2：结算回滚，3：注单取消，4：取消回滚
            log.info("::{}::接收到CancelSettle结算mq,实体bean={}", orderNo, JSONObject.toJSONString(msg));
            Optional.of(msg.getOperateType());
            if (msg.getOperateType() == 2) {
                //todo
                String receiveBetKey = String.format(GTS_SETTLE_INFO, orderNo);
                //不是gts的过滤
                String receiveBetStr = redisClient.get(receiveBetKey);
                if (StringUtils.isBlank(receiveBetStr)) {
                    return;
                }
                GtsBetReceiverCache cache = JSON.parseObject(receiveBetStr, new TypeReference<GtsBetReceiverCache>() {
                });
                log.info("::{}:: 订单结算mq,缓存数据={}", orderNo, JSONObject.toJSONString(cache));
                GtsBetReceiverRequestVo gtsBetReceiverRequestVo = new GtsBetReceiverRequestVo();
                BeanUtils.copyProperties(cache, gtsBetReceiverRequestVo);
                gtsBetReceiverRequestVo.setStatus("Open");
                //gtsBetReceiverRequestVo.setBetPlacedTimestampUTC(getUtcTime());
                gtsBetReceiverRequestVo.setBetUpdatedTimestampUTC(getUtcTime());

                if (!CollectionUtils.isEmpty(gtsBetReceiverRequestVo.getLegs())) {
                    gtsBetReceiverRequestVo.getLegs().forEach(e -> {
                        e.setStatus("Open");
                    });
                }
                //算出当时的zhe k
                //获取金额
                /*LambdaQueryWrapper<RcsGtsOrderExt> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RcsGtsOrderExt::getOrderNo, orderNo);
                //防止首次入库失败
                RcsGtsOrderExt ext = rcsGtsOrderExtMapper.selectOne(wrapper);*/
                gtsService.gtsSettleReceive(gtsBetReceiverRequestVo);
            }

            log.info("::{}:: 订单结算通知完成", orderNo);


        } catch (Exception ex) {
            log.error("::{}:: 接收到结算mq:{}", msg.getOrderNo(), ex.getMessage(), ex);
        }
    }
    private static String getUtcTime() {
        LocalDateTime localDateTime = new Date(System.currentTimeMillis()).toInstant().atOffset(ZoneOffset.of("+0")).toLocalDateTime();
        return localDateTime.toString() + "Z";
    }



}

