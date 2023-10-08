package com.panda.sport.sdk.strategy;

import com.alibaba.fastjson.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.virtual.VirtualApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.virtual.*;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.service.impl.*;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.util.*;

/**
 * @author gulang
 * @date 2023/5/17 11:40
 * @description virtual投注限额策略
 */
@Singleton
public class VirtualOrderStrategy implements IOrderStrategy {

    private static final Logger logger = LoggerFactory.getLogger(VirtualOrderStrategy.class);
    @Inject
    LimitConfigService limitConfigService;

    @Inject
    VirtualApiService virtualApiService;


    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean, MatrixForecastVo matrixForecastVo) {
        logger.info("::{}::虚拟赛事投注订单信息:{}", orderBean.getOrderNo(), JSON.toJSONString(orderBean));
        Map<String, Object> resultMap = new HashMap<>();
        //第三方投注
        Request<BetReqVo> request = new Request<>();
        BetReqVo bet = new BetReqVo();
        bet.setOrderNo(orderBean.getOrderNo());
        bet.setTenantId(orderBean.getTenantId());
        bet.setUserId(orderBean.getUid());
        bet.setTotalStake(orderBean.getOrderAmountTotal());
        bet.setSeriesType(orderBean.getSeriesType());
        bet.setDeviceType(orderBean.getDeviceType());
        bet.setIp(orderBean.getIp());
        bet.setBetTime(orderBean.getCreateTime());
        bet.setCurrencyCode(orderBean.getCurrencyCode());
        bet.setIpArea(orderBean.getIpArea());
        bet.setVipLevel(orderBean.getVipLevel());
        bet.setBetTime(orderBean.getCreateTime());
        List<BetItemReqVo> itemReqVoList = new ArrayList<>();
        if(orderBean.getItems().size()>=0){
            for(OrderItem item :orderBean.getItems()){
                BetItemReqVo betItemReqVo = new BetItemReqVo();
                betItemReqVo.setBetNo(item.getBetNo());
                betItemReqVo.setSportId(item.getSportId());
                betItemReqVo.setPlayListId(item.getPlayOptionsId());
                betItemReqVo.setEventId(item.getEventId());
//                betItemReqVo.setMarketId(item.getMarketId().toString());
                betItemReqVo.setMarketId(item.getVrMarketId());
                //detail.setPlayOptionsId(item.getOddId());
//                betItemReqVo.setOddId(item.getPlayOptionsId().toString());
                betItemReqVo.setOddId(item.getVrOddId());
                betItemReqVo.setOddValue(item.getOddsValue().toString());
                //detail.setBetAmount(item.getStake());
                betItemReqVo.setStake(item.getBetAmount());
                betItemReqVo.setSportName(item.getSportName());
                betItemReqVo.setMatchInfo(item.getMatchInfo());
                betItemReqVo.setPlayOptionsName(item.getPlayOptionsName());
                betItemReqVo.setMaxWinAmount(item.getMaxWinAmount().longValue());
                itemReqVoList.add(betItemReqVo);
            }
        }
        bet.setOrderItemList(itemReqVoList);
        bet.setFpId(orderBean.getFpId());
        bet.setOpenMiltSingle(orderBean.getOpenMiltSingle());
        request.setData(bet);
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<BetResVo> response =  virtualApiService.bet(request);
        logger.info("::{}::VirtualOrderStrategy#checkOrder第三方投注数据:{}", orderBean.getOrderNo(), JSON.toJSONString(response));
        if(response.getCode()==200 && response.getData().getOrderStatus()==1){
            //风控订单状态：0-等待 1--接单 2--拒单
            resultMap.put("status", 1);
            resultMap.put("infoStatus", OrderInfoStatusEnum.EARLY_PASS.getCode());
            resultMap.put("infoMsg", "已接单");
            resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_OK);
            resultMap.put(orderBean.getOrderNo(), true);
            return resultMap;
        }else{
            //风控订单状态：0-等待 1--接单 2--拒单
            resultMap.put("status", 2);
            resultMap.put("infoStatus", OrderInfoStatusEnum.EARLY_REFUSE.getCode());
            resultMap.put("infoMsg", "早盘拒单");
            resultMap.put("infoCode", SdkConstants.ORDER_ERROR_CODE_RISK);
            resultMap.put(orderBean.getOrderNo(), true);
            return resultMap;
        }
    }

    @Override
    public List<RcsBusinessPlayPaidConfigVo> getMaxBetAmount(List<ExtendBean> list, OrderBean orderBean) {
        String indexKey = org.springframework.util.StringUtils.isEmpty(orderBean.getOrderNo()) ? String.valueOf(orderBean.getUid()) : orderBean.getOrderNo();
        logger.info("::{}::虚拟赛事限额VirtualOrderStrategy#getMaxBetAmount参数:{}", indexKey, JSON.toJSONString(orderBean));
        List<RcsBusinessPlayPaidConfigVo> resultList =new  ArrayList();
        Request<BetAmountLimitReqVo> request = new Request<>();
        BetAmountLimitReqVo betAmountLimitReqVo = new BetAmountLimitReqVo();
        betAmountLimitReqVo.setSeriesType(orderBean.getSeriesType());
        betAmountLimitReqVo.setTenantId(orderBean.getTenantId());
        betAmountLimitReqVo.setUserId(orderBean.getUid());
        request.setData(betAmountLimitReqVo);
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<List<BetAmountLimitResVo>> response = virtualApiService.getBetAmountLimit(request);
        logger.info("::{}::VirtualOrderStrategy#getMaxBetAmount第三方限额resp:{}",indexKey, JSON.toJSONString(response));
        if(response.isSuccess() && response.getData().size()>0){
            List<BetAmountLimitResVo> betVirList = response.getData();
            for(BetAmountLimitResVo vo:betVirList){
                RcsBusinessPlayPaidConfigVo resVo = new RcsBusinessPlayPaidConfigVo();
                resVo.setType(vo.getSeriesType().toString());
                resVo.setPlayMaxPay(Long.valueOf(vo.getMaxStake().toString()));
                resVo.setMinBet(Long.valueOf(vo.getMinStake().toString()));
                resultList.add(resVo);
            }
            return resultList;
        }else{
            return resultList;
        }
    }

    @Override
    public int orderType() {
        return OrderTypeEnum.VIRTUAL.getValue();
    }

}
