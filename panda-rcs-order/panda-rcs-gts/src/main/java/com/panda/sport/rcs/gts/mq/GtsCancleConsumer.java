package com.panda.sport.rcs.gts.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.gts.service.GtsCommonService;
import com.panda.sport.rcs.gts.service.GtsThirdApiService;
import com.panda.sport.rcs.gts.service.RcsGtsOrderExtService;
import com.panda.sport.rcs.gts.util.CopyUtils;
import com.panda.sport.rcs.gts.vo.GtsExtendBean;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.pojo.RcsGtsOrderExt;
import com.panda.sport.rcs.pojo.TOrder;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.panda.sport.rcs.gts.common.Constants.REJECTED;

/**
 * 业务主动拒单
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "queue_reject_gts_order",
        consumerGroup = "queue_reject_gts_order_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class GtsCancleConsumer implements RocketMQListener<OrderBean>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private RcsGtsOrderExtService rcsMtsOrderExtService;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    RedisClient redisClient;
    @Autowired
    GtsCommonService gtsCommonService;
    @Autowired
    GtsThirdApiService gtsThirdApiService;



    @Override
    public void onMessage(OrderBean orderBean) {
        try {
            log.info("::{}::,{}OrderRejectConsumer 业务主动拒单bean info ：{}", orderBean.getItems().get(0).getOrderNo(),this.getClass(), JSONObject.toJSON(orderBean));
            if (ObjectUtils.isEmpty(orderBean)) {
                log.info("OrderRejectConsumer业务主动拒单数据异常：");
                return ;
            }
            //订单号
            String orderNo = orderBean.getItems().get(0).getOrderNo();
            //0：待处理  1：已接单  2：拒单
            int rcsOrderStatus = 2;
            LambdaQueryWrapper<RcsGtsOrderExt> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsGtsOrderExt::getOrderNo, orderNo);
            RcsGtsOrderExt ext = rcsMtsOrderExtService.getOne(wrapper);
            if(ext == null ) {
            	log.error("::{}::订单不存在，GTS不做取消处理：{},",orderNo,JSONObject.toJSONString(orderBean));
            	return ;
            }
            Integer cancel = ext.getCancelStatus();
            if(cancel == 1) {
            	log.error("::{}::订单已取消，GTS不做重复取消处理：{},",orderNo,JSONObject.toJSONString(orderBean));
            	return ;
            }

            ext.setStatus(ext.getStatus() + ",REJECTED");
            ext.setCancelStatus(1);
            ext.setCancelId(102);
            ext.setResult(ext.getResult() + ",业务主动拒单更新");
            rcsMtsOrderExtService.updateById(ext);
            log.info("::{}::拒单ext表更新完成", orderNo);
            //通知gTS此单为拒单 跟业务沟通 cancelStatus为19表示betcancel的取消订单 不需要调用mts
            //列表扩展对象
            List<ExtendBean> extendBeanList = new ArrayList<>();
            //注单列表
            List<OrderItem> orderItemList = orderBean.getItems();
            for (OrderItem orderItem : orderItemList) {
                //构建ExtendBean对象
                ExtendBean bean = buildExtendBean(orderBean, orderItem);
                extendBeanList.add(bean);
            }
            List<GtsExtendBean> gtsExtendBeanList = CopyUtils.clone(extendBeanList, GtsExtendBean.class);
            //填充基础信息 查询第三方原始数据
            gtsCommonService.convertAllParam(gtsExtendBeanList);
            //向gts取消
            gtsThirdApiService.gtsReceiveBet(gtsExtendBeanList, Long.valueOf(ext.getPaAmount()) / 100, Integer.valueOf(orderBean.getSeriesType()), REJECTED);
            log.info("::{}::已经向gts发送取消订单", orderNo);
            log.info(ext + "业务主动拒单处理完成,订单号::{}::", orderNo);

            //更新订单拒单原因
            LambdaQueryWrapper<TOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(TOrder::getOrderNo, orderNo);
            TOrder order = orderMapper.selectOne(orderWrapper);
            order.setReason("业务gts主动拒单");
            orderMapper.updateById(order);
            log.info("::{}::拒单原因:业务主动拒单处理完成", orderNo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ;
        }
        return ;
    }

    /**
     * 构建ExtendBean 从sdk拷贝的方法
     **/
    public ExtendBean buildExtendBean(OrderBean bean, OrderItem item) {
        ExtendBean extend = new ExtendBean();
        extend.setSeriesType(bean.getSeriesType());
        extend.setItemId(item.getBetNo());
        extend.setOrderId(item.getOrderNo());
        extend.setBusId(String.valueOf(bean.getTenantId()));
        extend.setHandicap(item.getMarketValue());
        extend.setCurrentScore(item.getScoreBenchmark());
        //item  1 ：早盘 ，2： 滚球盘， 3： 冠军盘
        extend.setIsScroll(String.valueOf(item.getMatchType()).equals("2") ? "1" : "0");
        //冠军盘标识
        extend.setIsChampion(item.getMatchType().intValue() == 3 ? 1 : 0);
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(item.getPlayId() + "");
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());
        //阶段  冠军玩法走mts 可以不设置此字段
//        if (item.getMatchType() != 3) {
//            extend.setPlayType(rcsPaidConfigService.getPlayProcess(String.valueOf(item.getSportId()), String.valueOf(item.getPlayId())));
//        }
        if (item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
        } else {
            extend.setOrderMoney(0L);
            extend.setCurrentMaxPaid(0L);
        }
        extend.setTournamentLevel(item.getTurnamentLevel());
        extend.setTournamentId(item.getTournamentId());
        extend.setDateExpect(item.getDateExpect());
        extend.setDataSourceCode(item.getDataSourceCode());

        extend.setItemBean(item);

        if (StringUtils.isBlank(extend.getHandicap())) {
            extend.setHandicap("0");
        }
        if (StringUtils.isBlank(extend.getCurrentScore())) {
            extend.setCurrentScore("0:0");
        }
        extend.setSubPlayId(item.getSubPlayId());
        extend.setUserTagLevel(bean.getUserTagLevel());
        return extend;
    }
}
