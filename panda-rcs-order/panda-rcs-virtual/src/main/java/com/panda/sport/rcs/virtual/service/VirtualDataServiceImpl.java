package com.panda.sport.rcs.virtual.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.virtual.BetItemReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetReqVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.virtual.RcsOrderVirtual;
import com.panda.sport.rcs.pojo.virtual.RcsOrderVirtualDetail;
import com.panda.sport.rcs.pojo.virtual.RcsVirtualOrderExt;
import com.panda.sport.rcs.service.*;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.virtual.constants.Constants;
import com.panda.sport.rcs.virtual.third.client.model.TicketTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 虚拟赛事 数据操作
 * @Author lithan
 * @Date 2020-12-22 14:38:26
 **/
@Slf4j
@Service
public class VirtualDataServiceImpl {
    @Autowired
    IRcsTournamentTemplateService rcsTournamentTemplateService;
    @Autowired
    IRcsVirtualUserService virtualUserService;
    @Autowired
    IRcsVirtualOrderExtService virtualOrderExtService;
    @Autowired
    IRcsOrderVirtualService orderVirtualService;
    @Autowired
    IRcsOrderVirtualDetailService orderVirtualDetailService;
    @Autowired
    RcsSwitchService rcsSwitchService;

    /**
     * 保存第三方订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveThirdOrder(BetReqVo reqVo, Integer virtualUserId, String requestParam) {
        try {
            //rcs_virtual_order_ext表
            RcsVirtualOrderExt virtualOrder = new RcsVirtualOrderExt();
            virtualOrder.setCreateTime(System.currentTimeMillis());
            virtualOrder.setOrderNo(reqVo.getOrderNo());
            virtualOrder.setOrderStatus(0);
            virtualOrder.setRequestParam(requestParam);
            virtualOrder.setUserId(reqVo.getUserId());
            virtualOrder.setVirtualUserId(virtualUserId);
            virtualOrder.setPaAmount(reqVo.getTotalStake());
            virtualOrder.setVirtualAmount(getExchangeAmount(reqVo.getTotalStake(), reqVo.getTenantId()));
            virtualOrderExtService.saveOrUpdate(virtualOrder);
            log.info("::{}::保存第三方订单成功", virtualOrder.getOrderNo());

            //rcs_order_virtual表
            RcsOrderVirtual order = new RcsOrderVirtual();
            order.setOrderNo(reqVo.getOrderNo());
            order.setUid(reqVo.getUserId());
            order.setOrderStatus(0);
            order.setProductCount(Long.valueOf(reqVo.getOrderItemList().size()));
            order.setSeriesType(reqVo.getSeriesType());
            order.setProductAmountTotal(reqVo.getTotalStake());
            order.setOrderAmountTotal(reqVo.getTotalStake());
            order.setDeviceType(reqVo.getDeviceType());
            order.setIp(reqVo.getIp());
            order.setTenantId(reqVo.getTenantId());
            order.setCurrencyCode(reqVo.getCurrencyCode());
            order.setIpArea(reqVo.getIpArea());
            order.setThirdStatus("");
            order.setReason("");
            order.setVipLevel(reqVo.getVipLevel());
            order.setBetTime(reqVo.getBetTime());
            order.setCreateTime(System.currentTimeMillis());
            orderVirtualService.save(order);
            log.info("::{}::保存rcs_order_virtual成功", virtualOrder.getOrderNo());

            //rcs_order_virtual_detail表
            for (BetItemReqVo item : reqVo.getOrderItemList()) {
                RcsOrderVirtualDetail detail = new RcsOrderVirtualDetail();
                detail.setBetNo(detail.getBetNo());
                detail.setOrderNo(reqVo.getOrderNo());
                detail.setUid(reqVo.getUserId());
                detail.setSportId(item.getSportId());
                detail.setSportName(item.getSportName());
                detail.setMatchInfo(item.getMatchInfo());
                detail.setTournamentId(item.getPlayListId());
                detail.setMatchId(item.getEventId());
                detail.setMarketId(item.getMarketId());
                detail.setPlayOptionsId(item.getOddId());
                detail.setPlayOptionsName(item.getPlayOptionsName());
                detail.setOddsValue(item.getOddValue());
                detail.setBetAmount(item.getStake());

                detail.setMaxWinAmount(item.getMaxWinAmount());
                detail.setSeriesType(reqVo.getSeriesType());
                detail.setBetTime(reqVo.getBetTime());
                detail.setCreateTime(System.currentTimeMillis());
                detail.setBetNo(item.getBetNo());
                detail.setOrderStatus(0);
                orderVirtualDetailService.save(detail);
                log.info("::{}保存rcs_order_virtual成功,{}", virtualOrder.getOrderNo(), item.getBetNo());
            }
        } catch (Exception e) {
            log.info("::{}::保存第三方订单异常,{},{}", reqVo.getOrderNo(), e.getMessage(), e);
            throw new RcsServiceException("保存第三方订单异常" + e.getMessage());
        }
    }

    /**
     * 更新第三方订单
     */
    @Transactional
    public void updateThirdOrder(String orderNo, TicketTransaction ticketRes) {
        ticketRes.getTicket().setAdvancedInfo(null);
        try {
            LambdaUpdateWrapper<RcsVirtualOrderExt> extUpdateWrapper = new LambdaUpdateWrapper<>();
            extUpdateWrapper.set(RcsVirtualOrderExt::getTicketId, ticketRes.getTicket().getTicketId());
            extUpdateWrapper.set(RcsVirtualOrderExt::getRemark, "成功");
            extUpdateWrapper.set(RcsVirtualOrderExt::getOrderStatus, 1);
            extUpdateWrapper.set(RcsVirtualOrderExt::getResponseStatus, ticketRes.getTicket().getStatus().getValue());
            extUpdateWrapper.set(RcsVirtualOrderExt::getTransactionId, ticketRes.getTransaction().get(0).getTransactionId());
            extUpdateWrapper.set(RcsVirtualOrderExt::getResponseParam, JSONObject.toJSONString(ticketRes));
            extUpdateWrapper.eq(RcsVirtualOrderExt::getOrderNo, orderNo);
            virtualOrderExtService.update(extUpdateWrapper);
            log.info("::{}::更新rcs_virtual_order_ext成功", orderNo);

            LambdaUpdateWrapper<RcsOrderVirtual> orderUpdateWrapper = new LambdaUpdateWrapper<>();
            orderUpdateWrapper.set(RcsOrderVirtual::getOrderStatus, 1);
            orderUpdateWrapper.set(RcsOrderVirtual::getThirdStatus, ticketRes.getTicket().getStatus().getValue());
            orderUpdateWrapper.set(RcsOrderVirtual::getReason, "第三方接单");
            orderUpdateWrapper.set(RcsOrderVirtual::getModifyTime, System.currentTimeMillis());
            orderUpdateWrapper.eq(RcsOrderVirtual::getOrderNo, orderNo);
            orderVirtualService.update(orderUpdateWrapper);
            log.info("::{}::更新rcs_order_virtual成功", orderNo);

            LambdaUpdateWrapper<RcsOrderVirtualDetail> detailUpdateWrapper = new LambdaUpdateWrapper<>();
            detailUpdateWrapper.set(RcsOrderVirtualDetail::getOrderStatus, 1);
            detailUpdateWrapper.set(RcsOrderVirtualDetail::getModifyTime, System.currentTimeMillis());
            detailUpdateWrapper.eq(RcsOrderVirtualDetail::getOrderNo, orderNo);
            orderVirtualDetailService.update(detailUpdateWrapper);
            log.info("::{}::更新rcs_order_virtual_detail成功", orderNo);

        } catch (Exception e) {
            log.info("::{}::更新第三方订单异常,{},{}", orderNo, e.getMessage(), e);
            throw new RcsServiceException("更新第三方订单异常");
        }
    }

    /**
     * 更新第三方订单
     */
    @Transactional
    public void updateThirdOrder(List<String> orderNo, String message, Integer status, String responseStatus) {
        log.info("::{}::updateThirdOrder更新,{},{},{}", orderNo, message, status, responseStatus);
        try {
            LambdaUpdateWrapper<RcsVirtualOrderExt> extUpdateWrapper = new LambdaUpdateWrapper<>();
            extUpdateWrapper.in(RcsVirtualOrderExt::getOrderNo, orderNo);
            extUpdateWrapper.set(ObjectUtils.isNotEmpty(message), RcsVirtualOrderExt::getRemark, message);
            extUpdateWrapper.set(ObjectUtils.isNotEmpty(status), RcsVirtualOrderExt::getOrderStatus, status);
            extUpdateWrapper.set(ObjectUtils.isNotEmpty(responseStatus), RcsVirtualOrderExt::getResponseStatus, responseStatus);
            virtualOrderExtService.update(extUpdateWrapper);
            log.info("::{}::更新rcs_virtual_order_ext完成", orderNo);

            LambdaUpdateWrapper<RcsOrderVirtual> orderUpdateWrapper = new LambdaUpdateWrapper<>();
            orderUpdateWrapper.set(ObjectUtils.isNotEmpty(status), RcsOrderVirtual::getOrderStatus, status);
            orderUpdateWrapper.set(ObjectUtils.isNotEmpty(message), RcsOrderVirtual::getReason, message);
            orderUpdateWrapper.set(RcsOrderVirtual::getModifyTime, System.currentTimeMillis());
            orderUpdateWrapper.in(RcsOrderVirtual::getOrderNo, orderNo);
            orderVirtualService.update(orderUpdateWrapper);
            log.info("::{}::更新rcs_order_virtual完成", orderNo);

            LambdaUpdateWrapper<RcsOrderVirtualDetail> detailUpdateWrapper = new LambdaUpdateWrapper<>();
            detailUpdateWrapper.set(ObjectUtils.isNotEmpty(status), RcsOrderVirtualDetail::getOrderStatus, status);
            detailUpdateWrapper.set(RcsOrderVirtualDetail::getModifyTime, System.currentTimeMillis());
            detailUpdateWrapper.in(RcsOrderVirtualDetail::getOrderNo, orderNo);
            orderVirtualDetailService.update(detailUpdateWrapper);
            log.info("::{}::更新rcs_order_virtual_detail完成", orderNo);

        } catch (Exception e) {
            log.info("::{}::更新第三方订单异常,{},{}", orderNo, e.getMessage(), e);
            throw new RcsServiceException("更新第三方订单异常");
        }
    }

    /**
     * 按照比例给金额 ， 默认是0.3
     * todo 这里是否算得商户折扣
     **/
    public Long getExchangeAmount(Long betAmount, Long tenantId) {
        try {
            String val = null;
            String switchStatus = rcsSwitchService.getMissOrderSwitchStatus();
            if (StringUtils.isNotEmpty(switchStatus) && switchStatus.equals(YesNoEnum.Y.getValue().toString())) {
                val = "1";
                log.info("::{}商户折扣功能关闭,采用默认比例:{}", tenantId, val);
            }
            RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);
            val = redisClient.get(String.format(Constants.VIRTUAL_AMOUNT_RATE, tenantId));
            if (StringUtils.isBlank(val)) {
                val = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
            }
            if (StringUtils.isBlank(val)) {
                val = "0.3";
            }
            return new BigDecimal(String.valueOf(betAmount)).multiply(new BigDecimal(val)).longValue();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return betAmount;
    }
}

