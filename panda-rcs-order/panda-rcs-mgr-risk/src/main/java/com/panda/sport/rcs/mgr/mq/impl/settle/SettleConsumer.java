package com.panda.sport.rcs.mgr.mq.impl.settle;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.constant.RcsCacheContant;
import com.panda.sport.rcs.vo.SettleItemPO;
import com.panda.sport.rcs.mgr.operation.settlement.CalcSettledAdapter;
import com.panda.sport.rcs.mgr.utils.CopyUtils;
import com.panda.sport.rcs.mgr.utils.StringUtil;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.mq.impl.settle
 * @Description :  结算处理
 * @Date: 2020-12-03 下午 5:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic =RcsConstant.OSMC_SETTLE_RESULT,
        consumerGroup =RcsConstant.OSMC_SETTLE_RESULT,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class SettleConsumer implements RocketMQListener<SettleItem>, RocketMQPushConsumerLifecycleListener {
  @Autowired
  private ProducerSendMessageUtils sendMessage;

  @Autowired
  private ITOrderService orderService;

  @Autowired
  private CalcSettledAdapter calcSettledAdapter;
  @Autowired
  private ProducerSendMessageUtils producerSendMessageUtils;

  @Override
  public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
    defaultMQPushConsumer.setConsumeThreadMin(8);
    defaultMQPushConsumer.setConsumeThreadMax(12);
  }

  public SettleConsumer() {
//    super("OSMC_SETTLE_RESULT", "");
  }

  @Override
  public void onMessage(SettleItem src) {
    try {
      String traceId = StringUtil.getUUID();
      MDC.put("X-B3-TraceId", traceId);
      log.info("::{}:: 接收到结算mq,实体bean{}",src.getOrderNo(),JSONObject.toJSONString(src));
      if (src == null) {
        log.warn("::{}::没有找到相订单信息:SettleItem实体bean:{}",src.getOrderNo(),src);
        return  ;
      }
//      if (NumberUtils.INTEGER_TWO.equals(order.getLimitType())) {
//        log.info("发送信用模式结算队列：orderNo ::{}::",orderNo);
//        producerSendMessageUtils.sendMessage(RcsConstant.RCS_CREDIT_SETTLE_RESULT, orderNo, traceId, src);
//        return  ;
//      }
      handleCalcSettled(src, traceId);
    } catch (Exception e) {
      log.error("::{}::没有找到相订单信息{}",src.getOrderNo(),e.getMessage());
    }
  }
  /**
   * @Author toney
   * @Date 2020/12/4 下午 3:01
   * @Description 结算处理
   * @param src
   * @param traceId
   * @Return void
   * @Exception
   */
  public void handleCalcSettled(SettleItem src, String traceId) {
    try {

      String orderNo = src.getOrderNo();
      TOrder order = orderService.getOrderInfo(orderNo);
      if(Objects.isNull(order)){
        log.warn(" ::{}::无订单信息 ",orderNo);
        return;
      }
      src.setBetTime(order.getCreateTime());
      src.setSeriesType(order.getSeriesType());
      if(CollectionUtils.isEmpty(src.getOrderDetailRisk()) || src.getOrderDetailRisk().size() ==0){
        log.warn(" ::{}::无订单明细信息", src.getOrderNo());
        return;
      }
      Integer matchType = src.getOrderDetailRisk().get(0).getMatchType();
      if ( null != matchType && 3 == matchType) {
        producerSendMessageUtils.sendMessage(RcsConstant.RCS_CREDIT_SETTLE_RESULT, src.getOrderNo(), traceId, src);
        log.info("::{}::发送冠军玩法结算队列完成",orderNo);
        return;
      }
      SettleItemPO settleItemPO= CopyUtils.clone(src, SettleItemPO.class);
      settleItemPO.setLimitType(1);
      List<TOrderDetail> orderDetailList=order.getOrderDetailList();
      calcSettledAdapter.handle(src, orderDetailList);
      //是单关，并且是风控操盘的订单，发送到sdk服务做矩阵计算
      if (orderDetailList.size() == 1 && src.getIsSuccess()) {
          sendMessage.sendMessage(RcsConstant.OSMC_SETTLE_RESULT_SDK, traceId, src.getOrderNo(), settleItemPO);
          log.info("::{}::结算单关通知sdk完成", src.getOrderNo());
      }
      // 串关，发送消息队列，处理已用限额回滚
      if (orderDetailList.size() > 1 && src.getSeriesType() > 1 && src.getIsSuccess() && checkSettleType(src)) {
        sendMessage.sendMessage(RcsConstant.OSMC_SETTLE_RESULT_SDK, traceId, src.getOrderNo(), settleItemPO);
        log.info("::{}::结算串关通知sdk完成",src.getOrderNo());
      }
    } catch (Exception e) {
      log.error("::{}:: 结算MQ异常：{}",src.getOrderNo(), e.getMessage());
    }
  }

  /**
   * @Author toney
   * @Date 2020/12/3 下午 9:33
   * @Description 判断是不是单关类型
   * @param settleItem
   * @Return boolean
   * @Exception
   */
  private boolean checkSettleType(SettleItem settleItem) {
    return settleItem.getSettleType() != null && (settleItem.getSettleType() == 0 || settleItem.getSettleType() == 1);
  }
}
