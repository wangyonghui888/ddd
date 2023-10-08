package com.panda.sport.rcs.oddin.service.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.OddinOrderInfoDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.entity.Bet;
import com.panda.sport.data.rcs.vo.oddin.TicketResponseBetInfo;
import com.panda.sport.data.rcs.vo.oddin.TicketStateVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.oddin.config.NacosParameter;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderDj;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderTy;
import com.panda.sport.rcs.oddin.entity.ots.TicketMaxStake;
import com.panda.sport.rcs.oddin.enums.DataSourceEnum;
import com.panda.sport.rcs.oddin.grpc.FutureGrpcService;
import com.panda.sport.rcs.oddin.grpc.handler.TicketGrpcHandler;
import com.panda.sport.rcs.oddin.service.handler.RcsOddinOrderDjHandler;
import com.panda.sport.rcs.oddin.service.handler.RcsOddinOrderTyHandler;
import com.panda.sport.rcs.oddin.service.handler.TicketOrderHandler;
import com.panda.sport.rcs.oddin.util.SendMessageUtils;
import com.panda.sport.rcs.oddin.util.cache.RcsLocalCacheUtils;
import com.panda.sport.rcs.service.RcsSwitchService;
import com.panda.sport.rcs.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.panda.sport.rcs.cache.CaCheKeyConstants.ODDIN_ORDER_INFO_KEY;
import static com.panda.sport.rcs.oddin.common.Constants.*;
import static com.panda.sport.rcs.oddin.common.Constants.OTS_CACHE_AMOUNT_RATE;

@Slf4j
@Service
public class TicketOrderHandlerImpl implements TicketOrderHandler {
    @Resource
    private NacosParameter nacosParameter;
    @Resource
    private SendMessageUtils rocketProducer;
    @Resource
    private FutureGrpcService grpcFutureService;
    @Resource
    private RcsOddinOrderDjHandler rcsOddinOrderDjHandler;
    @Resource
    private RcsOddinOrderTyHandler rcsOddinOrderTyHandler;
    @Resource
    private TicketGrpcHandler ticketGrpcHandler;
    @Resource
    private RedisClient redisClient;
    @Resource
    RcsSwitchService rcsSwitchService;

    /**
     * 获取商户折扣
     *
     * @param tenantId 商户id
     * @param sourceId 下游来源: 1:DJ 2:TY
     * @return
     */
    @Override
    public String getDiscount(Long tenantId, Integer sourceId) {
//        RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);
        String key = "";
        String val = "";
        //获取打折开关的状态
        String switchStatus = rcsSwitchService.getMissOrderSwitchStatus();
        if (StringUtils.isNotEmpty(switchStatus) && switchStatus.equals(YesNoEnum.Y.getValue().toString())) {
            val = "1";/*nacosParameter.getTenantDefaultDiscount();*/
            log.info("::{}商户折扣功能关闭，采用默认比例:{}", tenantId, val);
            return val;
        }
        //根据sourceId判断是体育注单请求还是电竞的
        if (DataSourceEnum.DJ.getCode().equals(sourceId)) {
            //获取本地缓存中的
            Object o = RcsLocalCacheUtils.timedCache.get(DJ_RTS_CACHE_AMOUNT_RATE);
            if (Objects.nonNull(o)) {
                val = o.toString();
            } else {
                //从redis中拿
                key = String.format(DJ_RTS_AMOUNT_RATE, tenantId);
                val = redisClient.get(key);
                if (StringUtils.isBlank(val)) {
                    key = DJ_OTS_AMOUNT_RATE_ALL;
                    val = redisClient.get(key);
                }
            }
            //redis缓存中也没有数据,直接取默认值
            if (StringUtils.isBlank(val)) {
                val = nacosParameter.getTenantDefaultDiscount();
                log.info("::{}商户折扣比例信息为空，采用默认比例:{}", tenantId, val);
            }
            //本地缓存商户折扣数据为1分钟
            RcsLocalCacheUtils.timedCache.put(DJ_RTS_CACHE_AMOUNT_RATE, val, 60 * 1000L);
        }
        //体育获取商户折扣也是一样的逻辑
        if (DataSourceEnum.TY.getCode().equals(sourceId)) {
            Object o = RcsLocalCacheUtils.timedCache.get(OTS_CACHE_AMOUNT_RATE);
            if (Objects.nonNull(o)) {
                val = o.toString();
            } else {
                key = String.format(OTS_AMOUNT_RATE, tenantId);
                val = redisClient.get(key);
                if (StringUtils.isBlank(val) || "null".equals(val)) {
                    key = OTS_AMOUNT_RATE_ALL;
                    val = redisClient.get(key);
                }
            }
            if (StringUtils.isBlank(val) || "null".equals(val)) {
                val = nacosParameter.getTenantDefaultDiscount();
                log.info("::{}商户折扣比例信息为空，采用默认比例:{}", tenantId, val);
            }
            RcsLocalCacheUtils.timedCache.put(OTS_CACHE_AMOUNT_RATE, val, 60 * 1000L);
        }
        return val;
    }

    /**
     * 校验注单最大限额是否大于下单金额
     *
     * @param dto
     * @param discount
     * @param reqState
     */
    @Override
    public void verificationMaxState(TicketDto dto, String discount, int reqState) {
        log.info("====注单，uid:{}, orderNo:{} -开始校验最大限额==", dto.getCustomer().getId(), dto.getId());
        TicketDto maxStateDto = new TicketDto();
        BeanCopyUtils.copyProperties(dto, maxStateDto);
        Float oddMaxState = getOddMaxState(maxStateDto);
        oddMaxState = new BigDecimal(TEN_THOUSAND).multiply(new BigDecimal(oddMaxState)).floatValue();
        log.info("==uid：{},orderNo：{},下注金额大于最大限额,下注金额：{},最大限额 {}", dto.getCustomer().getId(), dto.getId(), reqState, String.valueOf(oddMaxState));
//        if (new BigDecimal(reqState).compareTo(new BigDecimal(oddMaxState).multiply(new BigDecimal(TEN_THOUSAND))) > 0) {
//            log.info("==uid：{},orderNo：{},下注金额大于最大限额,下注金额：{},最大限额 {}", dto.getCustomer().getId(), dto.getId(), reqState, oddMaxState);
//            throw new RcsServiceException("下注金额大于最大限额");
//        }
    }

    private Float getOddMaxState(TicketDto maxStateDto) {
        TicketStateVo ticketStateVo = null;
        log.info("====注单校验限额，uid:{}, orderNo:{} -开始获取最大限额==", maxStateDto.getCustomer().getId(), maxStateDto.getId());
        TicketMaxStake.TicketMaxStakeResponse ticketMaxStakeResponse = grpcFutureService.queryMaxBetMoneyBySelect(maxStateDto);
        ticketStateVo = ticketGrpcHandler.queryMaxBetMoneyBySelect(ticketMaxStakeResponse);
        Float oddMaxState = 0F;
        Map<String, TicketResponseBetInfo> map = ticketStateVo.getBet_info();
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, TicketResponseBetInfo> entry : ticketStateVo.getBet_info().entrySet()) {
                TicketResponseBetInfo betInfo = entry.getValue();
                oddMaxState = betInfo.getReoffer().getStake();
            }
        }
        log.info("====注单校验限额，uid:{}, orderNo:{} -获取最大限额为：{}==", maxStateDto.getCustomer().getId(), maxStateDto.getId(), oddMaxState);
        return oddMaxState;
    }

    /**
     * TY早盘下注同时发mq进行监听整个oddin注单的响应时间
     *
     * @param dto
     */
    @Override
    public void sendEarlyCancelMq(TicketDto dto) {
        try {
            CancelOrderDto cancelOrderDto = new CancelOrderDto();
            cancelOrderDto.setId(dto.getId());
            cancelOrderDto.setCancelReason(1);
            cancelOrderDto.setCancelReasonDetail("CANCEL_REASON_TICKET_TIMEOUT");
            cancelOrderDto.setBetTime(System.currentTimeMillis());
            cancelOrderDto.setSourceId(DataSourceEnum.TY.getCode());
//            RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);
            String redisKey = com.panda.sport.rcs.enums.DataSourceEnum.OD.getDataSource().concat("-").concat(dto.getId());
            redisClient.setExpiry(redisKey, ORDER_BETTING, EARLY_ORDER_BETTING_TIME);
            rocketProducer.sendDelayMessage("rcs_risk_oddin_pre_order_reject", "pre_order_check", dto.getId(), cancelOrderDto);
            log.info("::{}::{}::TY早盘注单进行中状态加入到缓存并下发[rcs_risk_oddin_pre_order_reject]处理", MDC.get("linkId"), dto.getId());
        } catch (Exception e) {
            log.info("::{}::{}::TY早盘注单进行中状态加入到缓存并下发[rcs_risk_oddin_pre_order_reject]处理异常", MDC.get("linkId"), dto.getId(), e);
        }
    }

    /**
     * 先保存订单信息
     *
     * @param dto
     */
    @Override
    public void saveRcsGtsOrderExt(TicketDto dto) {
        try {
            //判断订单是电竞or 体育业务 SourceId(1是电竞 2是体育)
            if (CollectionUtils.isNotEmpty(dto.getBets())) {
                if (dto.getSourceId() == 1) {
                    for (Bet bet : dto.getBets()) {
                        RcsOddinOrderDj order = new RcsOddinOrderDj();
                        //订单编号
                        order.setOrderNo(dto.getId());
                        //订单状态
                        order.setStatus("INIT");
                        //注单请求金额
                        order.setAmount(new BigDecimal(TEN_THOUSAND).multiply(new BigDecimal(bet.getStake().getValue())).intValue());
                        //注单优惠后的实际金额
                        order.setRealAmount(bet.getRealAmount());
                        //创建时间
                        order.setCreateTime(new Date());
                        //第三方标识
                        order.setThirdName("ODDIN");
                        //商户ID
                        order.setTenantId(dto.getLocation_id());
                        //请求赔率
                        order.setRequesteOdds(new BigDecimal(TEN_THOUSAND).multiply(new BigDecimal(dto.getSelections().get(bet.getSelections()).getOdds())).intValue());
                        //股权类型
                        order.setBetStakeType(bet.getStake().getType().toString());
                        //selectionID
                        order.setSelectionId(bet.getSelections());
                        //商家折扣率
                        order.setDiscount(bet.getDiscount());
                        //用户ID
                        order.setUid(Long.valueOf(dto.getCustomer().getId()));
                        rcsOddinOrderDjHandler.save(order);
                    }
                }
                if (dto.getSourceId() == 2) {
                    for (Bet bet : dto.getBets()) {
                        RcsOddinOrderTy order = new RcsOddinOrderTy();
                        //订单编号
                        order.setOrderNo(dto.getId());
                        //订单状态
                        order.setStatus("INIT");
                        //注单请求金额
                        order.setAmount(new BigDecimal(TEN_THOUSAND).multiply(new BigDecimal(bet.getStake().getValue())).intValue());
                        //注单优惠后的实际金额
                        order.setRealAmount(bet.getRealAmount());
                        //创建时间
                        order.setCreateTime(new Date());
                        //第三方标示
                        order.setThirdName("ODDIN");
                        //商户ID
                        order.setTenantId(dto.getLocation_id());
                        //请求赔率
                        order.setRequesteOdds(new BigDecimal(TEN_THOUSAND).multiply(new BigDecimal(dto.getSelections().get(bet.getSelections()).getOdds())).intValue());
                        //股权类型
                        order.setBetStakeType(bet.getStake().getType().toString());
                        //selectionID
                        order.setSelectionId(bet.getSelections());
                        //商家折扣率
                        order.setDiscount(bet.getDiscount());
                        //用户ID
                        order.setUid(Long.valueOf(dto.getCustomer().getId()));
                        rcsOddinOrderTyHandler.save(order);
                    }
                }
            }
        } catch (NumberFormatException e) {
            log.error("::{}::投注-注单插入数据库出错,", dto.getId(), e);
        }
    }

    /**
     * 删除早盘缓存
     *
     * @param orderNo
     */
    @Override
    public void removeEarlyOrderBettingStatus(String orderNo) {
        try {
//            RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);
            String redisKey = com.panda.sport.rcs.enums.DataSourceEnum.OD.getDataSource().concat("-").concat(orderNo);
            redisClient.delete(redisKey);
            log.info("::{}::{}::TY早盘注单状态redis缓存清理", getGlobalIdFromCacheByOrderNo(orderNo), orderNo);
        } catch (Exception e) {
            log.error("::{}::{}::TY早盘注单状态redis缓存清理异常", getGlobalIdFromCacheByOrderNo(orderNo), orderNo, e);
        }
    }

    /**
     * 根据orderNo从缓存中获取全局唯一链路id
     *
     * @param orderNo
     * @return
     */
    @Override
    public String getGlobalIdFromCacheByOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return null;
        }
        String key = String.format(ODDIN_ORDER_INFO_KEY, orderNo);
        String orderInfoJson = redisClient.get(key);
        if (StringUtils.isNotBlank(orderInfoJson)) {
            OddinOrderInfoDto dto = JSONObject.parseObject(orderInfoJson, OddinOrderInfoDto.class);
            if (Objects.nonNull(dto)) {
                return dto.getGlobalId();
            }
        }
        return null;
    }

}
