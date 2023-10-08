package com.panda.sport.rcs.oddin.service.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.OddinOrderInfoDto;
import com.panda.sport.data.rcs.vo.oddin.TicketVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.DataSourceEnum;
import com.panda.sport.rcs.enums.MtsIsCacheEnum;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.oddin.entity.common.ThirdOrderDelayVo;
import com.panda.sport.rcs.oddin.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.oddin.entity.common.pojo.ErrorMessagePrompt;
import com.panda.sport.rcs.oddin.service.IOrderAcceptService;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import com.panda.sport.rcs.oddin.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.oddin.service.handler.TicketOrderHandler;
import com.panda.sport.rcs.pojo.RcsLabelLimitConfig;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

import static com.panda.sport.rcs.cache.CaCheKeyConstants.ODDIN_ORDER_INFO_KEY;
import static com.panda.sport.rcs.oddin.common.Constants.*;


/**
 * @author Beulah
 * @date 2023/3/21 19:21
 * @description 订单处理模式
 */

@Service
@Slf4j
public class OrderHandlerServiceImpl implements IOrderHandlerService {


    @Autowired
    TOrderDetailMapper orderDetailMapper;
    @Resource
    RedisClient redisClient;
    @Resource
    IOrderAcceptService orderAcceptService;
    @Resource
    ProducerSendMessageUtils sendMessage;
    @Resource
    RcsLabelLimitConfigMapper labelLimitConfigMapper;
    @Resource
    TOrderMapper orderMapper;
    //接拒逻辑
    @Resource(name = "orderAcceptServiceImpl")
    IOrderAcceptService acceptService;
    @Resource
    TicketOrderService ticketOrderService;
    @Resource
    TicketOrderHandler ticketOrderHandler;


    /**
     * 走内部接单
     *
     * @param vo 订单
     */
    @Override
    public void orderByPa(TicketVo vo) {
        String orderNo = vo.getId();
        log.info("::{}::{}::投注-数据商{},执行内部接单流程::", ticketOrderHandler.getGlobalIdFromCacheByOrderNo(vo.getId()), orderNo, "oddin");
        //订单最终状态
        int orderStatus = OrderInfoStatusEnum.WAITING.getCode();
        //订单处理状态
        Integer infoStatus = OrderInfoStatusEnum.RISK_PROCESSING.getCode();
        //提示
        String infoMsg = OrderInfoStatusEnum.RISK_PROCESSING.getMode();
        int isCache = MtsIsCacheEnum.ODDIN.getValue();
        ThirdOrderExt ext = new ThirdOrderExt();
        ext.setOrderNo(vo.getId());
        ext.setOrderGroup(vo.getOrderGroup());
        ext.setThird(DataSourceEnum.OD.getDataSource());
        //校验赛事，盘口状态是否有变化
        boolean matchIsChange = validateMatchStatus(ext, orderNo);
        if (matchIsChange) {
            return;
        }
        List<String> orderList = new ArrayList<>();
        orderList.add(vo.getId());
        //校验订单是否是滚去，先从缓存查，缓存没有再去查数据库
        boolean isScroll = false;
        String orderInfoKey = String.format(ODDIN_ORDER_INFO_KEY, orderNo);
        String orderInfoJson = redisClient.get(orderInfoKey);
        if (StringUtils.isNotBlank(orderInfoJson)) {
            OddinOrderInfoDto dto = JSONObject.parseObject(orderInfoJson, OddinOrderInfoDto.class);
            if (Objects.nonNull(dto)) {
                isScroll = dto.isScrollOrder();
            }
        } else {
            isScroll = orderAcceptService.orderIsScroll(orderList);
        }
        //早盘秒接
        if (!isScroll) {
            log.info("::{}::{}::投注-数据商{}内部接单-早盘秒接::",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, DataSourceEnum.OD.getDataSource());
            infoMsg = OrderInfoStatusEnum.EARLY_PASS.getMode();
            orderStatus = OrderInfoStatusEnum.EARLY_PASS.getCode();
            infoStatus = OrderInfoStatusEnum.EARLY_PASS.getCode();
            ext.setOrderStatus(orderStatus);
            updateOrder(ext, infoStatus, infoMsg, isCache);
            return;
        }
        //滚球是否秒接检查
        if (acceptService.checkScrollSpeedAccept(ext.getList(), DataSourceEnum.OD.getDataSource())) {
            log.info("::{}::{}::投注-数据商{}内部接单-滚球赛事秒接",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, DataSourceEnum.OD.getDataSource());
            infoMsg = "赛事秒接";
            infoStatus = OrderInfoStatusEnum.SCROLL_PASS.getCode();
            orderStatus = OrderInfoStatusEnum.EARLY_PASS.getCode();
            ext.setOrderStatus(orderStatus);
            updateOrder(ext, infoStatus, infoMsg, isCache);
        }
//        else {
//            log.info("::{}::{}::投注-数据商{}内部接单-后置检查",MDC.get("linkId"), orderNo, DataSourceEnum.OD.getDataSource());
//            ErrorMessagePrompt errorMessage = new ErrorMessagePrompt();
//            boolean checkStatus = orderAcceptService.dealWithData(ext.getList(), errorMessage, OrderTypeEnum.ODDIN.getPlatFrom(), 0);
//            ext.setOrderNo(orderNo);
//            int mtsIsCache = getMtsIsCache(OrderTypeEnum.ODDIN.getPlatFrom(), 3);
//            Integer code = OrderInfoStatusEnum.SCROLL_PASS.getCode();
//            String msg = OrderTypeEnum.ODDIN.getPlatFrom() + "滚球PA接单";
//            if (checkStatus) {
//                log.info("::{}::{}订单接拒,检查实时接拒信息拒单", orderNo, OrderTypeEnum.ODDIN.getPlatFrom());
//                ext.setOrderStatus(2);
//                code = OrderInfoStatusEnum.SCROLL_REFUSE.getCode();
//                msg = OrderTypeEnum.ODDIN.getPlatFrom() + "滚球PA拒单";
//            } else {
//                log.info("::{}::{}订单接拒,检查实时接拒信息接单", orderNo, OrderTypeEnum.ODDIN.getPlatFrom());
//                ext.setOrderStatus(1);
//            }
//            updateOrder(ext, code, msg, mtsIsCache);
//            notifyThirdUpdateOrder(ext);
//        }
    }

    /**
     * 校验赛事，盘口状态
     *
     * @param ext
     * @param orderNo
     */
    private boolean validateMatchStatus(ThirdOrderExt ext, String orderNo) {
        //检查盘口是否关闭
        boolean isChange = false;
        String orderInfoKey = String.format(ODDIN_ORDER_INFO_KEY, orderNo);
        String orderInfoJson = redisClient.get(orderInfoKey);
        String matchId = "";
        String marketId = "";
        if (StringUtils.isNotBlank(orderInfoJson)) {
            OddinOrderInfoDto dto = JSONObject.parseObject(orderInfoJson, OddinOrderInfoDto.class);
            if (Objects.nonNull(dto)) {
                matchId = String.valueOf(dto.getMatchId());
                marketId = String.valueOf(dto.getMarketId());
            }
        }
        ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
        //判断赛事ID/盘口IDs是否有值
        if (StringUtils.isBlank(matchId) || StringUtils.isBlank(marketId)) {
            log.info("::{}::{}::赛事状态检查,获取赛事缓存的key为空::matchId:{}::key:{}",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, matchId, orderInfoKey);
            errorMessagePrompt.setHintMsg("赛事不存在拒单");
            errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
            //通知数据商取消注单,通知业务更新注单状态
            matchErrorRejectOrder(orderNo, errorMessagePrompt, ext);
            //直接结束整个盘口检测方法
            return true;
        }
        //检测盘口/赛事状态
        boolean matchFlag = orderAcceptService.checkMatchAndMarketStatus(matchId, marketId, orderNo, errorMessagePrompt, DataSourceEnum.OD.getDataSource());
        if (matchFlag) {
            //从oddin撤单,通知业务更新注单状态,改变订单表中订单的状态
            matchErrorRejectOrder(orderNo, errorMessagePrompt, ext);
            isChange = true;
        }
        return isChange;
    }

    private void matchErrorRejectOrder(String orderNo, ErrorMessagePrompt errorMessagePrompt, ThirdOrderExt ext) {
        Request<CancelOrderDto> cancelRequest = new Request<>();
        CancelOrderDto cancelOrderDto = new CancelOrderDto();
        cancelOrderDto.setId(orderNo);
        cancelOrderDto.setSourceId(DataSourceEnum.OD.getValue());
        cancelOrderDto.setCancelReason(4);
        cancelOrderDto.setCancelReasonDetail("CANCEL_REASON_UNEXPECTED_ISSUE");
        cancelRequest.setData(cancelOrderDto);
        //通知数据商取消注单
        ticketOrderService.cancelOrder(cancelRequest);
        //第三方拒单 直接拒单
        int isCache = MtsIsCacheEnum.ODDIN.getValue();
        ext.setThirdResJson(errorMessagePrompt.getHintMsg());
        log.info("::{}::{}::投注-数据商{}拒单",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, DataSourceEnum.OD.getDataSource());
        int infoStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
        ext.setOrderStatus(2);
        ext.setThird(DataSourceEnum.OD.getDataSource());
        updateOrder(ext, infoStatus, errorMessagePrompt.getHintMsg(), isCache);
    }

    @Override
    public void orderByCache(ThirdOrderExt ext) {
        String orderNo = ext.getOrderNo();
        String third = ext.getThird();
        log.info("::{}::投注-{}执行缓存接单", orderNo, third);
        String orderInfoStatus = ACCEPTED;
        Integer infoStatus = OrderInfoStatusEnum.MTS_PASS.getCode();
        String infoMsg = third + "缓存接单成功";
        ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
        //检查赛事与盘口状态
        if (orderAcceptService.matchAndMarketCheck(orderInfoStatus, orderNo, errorMessagePrompt, third)) {
            orderInfoStatus = REJECTED;
            infoMsg = third + "拒单:" + errorMessagePrompt.getHintMsg();
            log.info("::{}::投注-{}缓存接单情况:检查赛事与盘口状态拒单:{}", orderNo, third, errorMessagePrompt.getHintMsg());
        }
        //接单前，检查一下注单是否被取消
        if (orderInfoStatus.equals(ACCEPTED)) {
            if (orderAcceptService.orderIsCanceled(orderNo)) {
                log.info("::{}::投注-{}缓存接单情况:发现订单存在之前手工取消订单操作,不再处理", orderNo, third);
                return;
            }
            infoStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
        }
        Integer thirdOrderStatus = ACCEPTED.equals(orderInfoStatus) ? 1 : 2;
        ext.setThirdOrderStatus(thirdOrderStatus);
        ext.setOrderStatus(thirdOrderStatus);
        //更新状态
        int isCache = MtsIsCacheEnum.ODDIN.getValue();

        updateOrder(ext, infoStatus, infoMsg, isCache);
    }

    @Override
    public void orderByThird(TicketVo vo) {
        log.info("::{}::第三方撤单::{}", vo.getId(), JSONObject.toJSONString(vo));
        Integer infoStatus = OrderInfoStatusEnum.MTS_PASS.getCode();
        ThirdOrderExt ext = new ThirdOrderExt();
        ext.setThirdOrderNo(vo.getId());
        ext.setOrderNo(vo.getId());
//        ext.setThirdResJson(TicketRejectEnum.getRejectReson(vo.getReject_reson().getCode()));
        ext.setThirdResJson(vo.getReject_reson().getMessage());
        //第三方拒单 直接拒单
        int isCache = MtsIsCacheEnum.ODDIN.getValue();
        ext.setThirdResJson(vo.getReject_reson().getMessage());
        log.info("::{}::投注-数据商{}拒单", vo.getId(), DataSourceEnum.OD.getDataSource());
        infoStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
        ext.setOrderStatus(2);
        ext.setThird(DataSourceEnum.OD.getDataSource());
        updateOrder(ext, infoStatus, vo.getReject_reson().getMessage(), isCache);

    }

    @Override
    public void updateOrder(ThirdOrderExt ext, Integer infoStatus, String infoMsg, Integer mtsIsCache) {
        //1.通知业务更新订单状态
        String orderNo = ext.getOrderNo();
        log.info("::{}::{}::投注-{}开始通知 业务更新订单状态,infoStatus::{}::infoMsg::{}::ext:{}",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo,infoStatus, infoMsg,JSONObject.toJSONString(ext));
        Integer orderStatus = ext.getOrderStatus();
        String third = ext.getThird();
        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", orderNo);
        map.put("status", orderStatus);
        map.put("isOddsChange", false);
        map.put("infoCode", 0);
        map.put("infoStatus", infoStatus);
        map.put("infoMsg", infoMsg);
        //第三方秒接的 排除掉接口响应的影响 直接默认为秒接
        if (ext.getOrderStatus() == 1) {
            map.put("infoStatus", 12); //一键秒接
            map.put("handleTime", System.currentTimeMillis());
        } else {
            map.put("handleTime", System.currentTimeMillis());
        }
        map.put("currentEvent", "");
        map.put("orderType", OrderTypeEnum.ODDIN.getValue());
        map.put("mtsIsCache", mtsIsCache);
        map.put("rejectReson", ext.getThirdResJson());
        Map<String, String> oddsChangeList = new HashMap<>();
        List<OrderItem> orderDetailList = null;
        OddinOrderInfoDto oddinOrderInfoDto = null;
        OrderBean orderBean = null;
        try {
            String cacheKey = String.format(ODDIN_ORDER_INFO_KEY, orderNo);
            String orderInfoStr = redisClient.get(cacheKey);
            if (StringUtils.isNotBlank(orderInfoStr) && !"null".equals(orderInfoStr)) {
                oddinOrderInfoDto = JSONObject.parseObject(orderInfoStr, OddinOrderInfoDto.class);
                if (Objects.nonNull(oddinOrderInfoDto)) {
                    orderBean = oddinOrderInfoDto.getOrderBean();
                    orderDetailList = orderBean.getItems();
                }
            }
            oddsChangeList = orderAcceptService.queryOddsRange(orderDetailList, third);

        } catch (Exception e) {
            log.error("::{}::{}::投注-{}订单获取详情异常:",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third, e);
        }
        if (oddsChangeList.size() > 0) {
            map.put("isOddsChange", true);
            //map.put("oddsChangeList", oddsChangeList);
            map.put("oddsRange", oddsChangeList);
        }
        String userGroup = "";
        if (ext.getOrderGroup() != null) {
            userGroup = "_" + ext.getOrderGroup();
        }
        String topic = RCS_BUS_THIRD_ORDER_STATUS + userGroup;
        sendMessage.sendMessage(topic + ",OTSOrder," + orderNo, map);
        log.info("::{}::{}::投注-{}订单更新推送业务TOPIC=[" + topic + "]完成,下发数据={}",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third, JSONObject.toJSONString(map));

        //3.通知风控更新订单状态
        if (CollectionUtils.isEmpty(orderDetailList)) {
            log.warn("::{}::{}::投注-{}订单未获取到订单详情,跳过",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third);
            return;
        }

        orderBean.setOrderStatus(orderStatus);
        orderBean.setValidateResult(orderStatus);
        orderBean.setReason(infoMsg);
        orderBean.setInfoStatus(orderStatus == 1 ? OrderInfoStatusEnum.MTS_PASS.getCode() : OrderInfoStatusEnum.MTS_REFUSE.getCode());
        orderBean.setOrderNo(orderNo);
        orderBean.setOddsChangeList(new ArrayList<>());
        //构建订单详情信息
//        List<OrderItem> OrderItemList = BeanCopyUtils.copyPropertiesList(orderDetailList, OrderItem.class);
        for (OrderItem item : orderDetailList) {
//            item.setOddsValue(new BigDecimal(String.valueOf(item.getOddsValue())).multiply(new BigDecimal("100000")).doubleValue());
            item.setValidateResult(orderStatus);
            item.setOrderStatus(orderStatus);
            item.setModifyTime(System.currentTimeMillis());
        }
        orderBean.setItems(orderDetailList);
        //读取女足订单缓存中的用户二级标签ids list
        if (Objects.nonNull(oddinOrderInfoDto)) {
            orderBean.setSecondaryLabelIdsList(oddinOrderInfoDto.getUserSecondLabelIdsList());
        }
        String tag = third + "_notify_update_order";
        sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE + "," + tag + "," + orderNo, orderBean);
        log.info("::{}::{}::投注-{}订单更新推送风控TOPIC=[queue_update_order]完成,下发数据={}",ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo, third, JSONObject.toJSONString(orderBean));

    }

    @Override
    public void notifyThirdUpdateOrder(ThirdOrderExt ext) {

    }

    /**
     * 判断是否缓存概率性接单
     *
     * @param ext sdk订单传参
     */
    @Override
    public boolean orderIsCache(ThirdOrderExt ext) {
        //串关 不走缓存
        if (ext.getList().size() > 1) {
            return false;
        }
        ExtendBean extendBean = ext.getList().get(0);
        String orderNo = extendBean.getOrderId();
        String third = ext.getThird();
        try {
            //标签延迟的 不走缓存
            LambdaQueryWrapper<RcsLabelLimitConfig> limitConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
            limitConfigLambdaQueryWrapper.eq(RcsLabelLimitConfig::getTagId, extendBean.getUserTagLevel());
            List<RcsLabelLimitConfig> delayList = labelLimitConfigMapper.selectList(limitConfigLambdaQueryWrapper);
            if (ObjectUtils.isNotEmpty(delayList) && ObjectUtils.isNotEmpty(delayList.get(0).getBetExtraDelay()) && delayList.get(0).getBetExtraDelay() > 0) {
                Integer delayTime = delayList.get(0).getBetExtraDelay();
                if (null != delayTime) {
                    log.info("::{}::投注-{}订单缓存判断,有标签延迟配置不走缓存接单,标签:{},延期时间:{}", orderNo, third, extendBean.getUserTagLevel(), delayTime);
                    return false;
                }
            }
            OrderItem orderItem = extendBean.getItemBean();
            Long optionId = orderItem.getPlayOptionsId();
            String oddFinally = orderItem.getOddFinally();
            String thirdOrderCache = String.format(THIRD_ORDER_CACHE, third, optionId, oddFinally, ext.getAcceptOdds());
            thirdOrderCache = redisClient.get(thirdOrderCache);
            if (StringUtils.isBlank(thirdOrderCache)) {
                log.info("::{}::投注-{}订单缓存判断,无缓存跳过", orderNo, third);
                return false;
            }
            //缓存存在的情况  概率性接单
            String orderRate = RcsLocalCacheUtils.getValue(thirdOrderCache, redisClient::get, 5 * 60 * 1000L);
            if (StringUtils.isEmpty(orderRate)) {
                orderRate = "70";
            }
            Random rd = new Random();
            int num = rd.nextInt(100);
            if (num > Integer.parseInt(orderRate)) {
                log.info("::{}::投注-{}订单缓存判断流程跳过,随机未命中:orderRate={},num={}", orderNo, third, orderRate, num);
                return false;
            }
            log.info("::{}::投注-{}订单缓存判断通过:orderRate={},num={}", orderNo, third, orderRate, num);
            return true;
        } catch (Exception e) {
            log.info("::{}::投注-{}订单缓存判断出现异常", orderNo, third, e);
            return false;
        }
    }


    /**
     * 订单缓存处理
     *
     * @param ext 订单对象
     */
    private void doCache(ThirdOrderExt ext) {
        if (ext.getList().size() != 1) {
            return;
        }
        ExtendBean detail = ext.getList().get(0);
        String orderNo = detail.getOrderId();
        String optionId = detail.getSelectId();
        String oddFinally = detail.getOdds();
        String third = ext.getThird();
        int acceptOdds = ext.getAcceptOdds();
        String thirdOrderCache = String.format(THIRD_ORDER_CACHE, third, optionId, oddFinally, acceptOdds);
        String thirdOrderExpire = redisClient.get(THIRD_ORDER_EXPIRE);
        if (org.apache.commons.lang.StringUtils.isEmpty(thirdOrderExpire)) {
            thirdOrderExpire = "2";
        }
        if (ext.getThirdOrderStatus() == 1) {
            redisClient.setExpiry(thirdOrderCache, "1", Long.valueOf(thirdOrderExpire));
            log.info("::{}::投注-{}订单新增缓存完成key={},缓存时间={}s", orderNo, third, thirdOrderCache, thirdOrderExpire);
        } else if (ext.getThirdOrderStatus() == 2) {
            redisClient.delete(thirdOrderCache);
            log.info("::{}::投注-{}订单删除缓存完成key={}", orderNo, third, thirdOrderCache);
        }
    }

    @Override
    public void addDelayOrder(ThirdOrderExt ext) {
        String third = ext.getThird();
        if (CollectionUtils.isEmpty(ext.getList())) {
            log.error("::{}::投注-订单添加到接拒队列,查询到订单信息为空", ext.getThird());
            return;
        }
        String orderId = ext.getList().get(0).getOrderId();
        ThirdOrderDelayVo delayVo = new ThirdOrderDelayVo();
        delayVo.setOrderNo(orderId);
        delayVo.setAcceptTime(System.currentTimeMillis());
        delayVo.setThird(ext.getThird());
        delayVo.setTotalMoney(ext.getPaTotalAmount());
        delayVo.setDelayTime(ext.getThirdDelay());
        delayVo.setThirdOrderNo(ext.getThirdOrderNo());
        delayVo.setList(ext.getList());
        //推送到接拒队列
        sendMessage.sendMessage(RCS_RISK_THIRD_ORDER_REJECT, third + "_ORDER_DELAY", orderId, delayVo);
    }


    @Override
    public int getMtsIsCache(String third, int num) {
        int isCache = OrderTypeEnum.MTS.getValue();
        if (OrderTypeEnum.ODDIN.getPlatFrom().equalsIgnoreCase(third)) {
            switch (num) {
                case 1:
                    isCache = MtsIsCacheEnum.ODDIN_PA.getValue();
                    break;
                case 2:
                    isCache = MtsIsCacheEnum.ODDIN_CACHE.getValue();
                    break;
                default:
                    isCache = MtsIsCacheEnum.ODDIN.getValue();
            }
        }
        return isCache;
    }

}
