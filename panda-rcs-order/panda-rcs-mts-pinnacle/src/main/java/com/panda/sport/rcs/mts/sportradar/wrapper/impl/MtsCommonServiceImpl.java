package com.panda.sport.rcs.mts.sportradar.wrapper.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.mts.sportradar.wrapper.MtsCommonService;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.*;
//import com.sportradar.mts.sdk.api.AutoAcceptedOdds;
//import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lithan
 * @description
 * @date 2020/2/5 12:17
 */
@Service
@Slf4j
public class MtsCommonServiceImpl implements MtsCommonService {

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    StandardSportTournamentMapper standardSportTournamentMapper;
    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    StandardSportMarketOddsMapper standardSportMarketOddsMapper;
    @Autowired
    RcsStandardOutrightMatchInfoMapper rcsStandardOutrightMatchInfoMapper;
    @Autowired
    RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @Autowired
    RcsMtsOrderExtService rcsMtsOrderExtService;
    @Autowired
    TOrderDetailMapper orderDetailMapper;
    @Autowired
    TOrderMapper orderMapper;

    @Override
    public void convertAllParam(List<ExtendBean> extendBeanList) {
        for (ExtendBean bean : extendBeanList) {
            Map<String, Object> map = getMatchInfo(bean.getMatchId(), bean.getMarketId(), bean.getItemBean().getPlayOptionsId().toString(), bean.getIsChampion());
            bean.setTournamentLevel(Integer.parseInt(String.valueOf(map.get("tournamentLevel"))));
//            bean.setDateExpect(String.valueOf(map.get("dateExpect")));
//            bean.setTournamentId(Long.parseLong(String.valueOf(map.get("tournamentId"))));
            bean.setThirdMatchSourceId(String.valueOf(map.get("thirdMatchSourceId")));
            bean.setSpecifiers(String.valueOf(map.get("extra_info")));
//            bean.setDataSourceCode(String.valueOf(map.get("data_source_code")));
            bean.setThirdTemplateSourceId(String.valueOf(map.get("third_template_source_id")));
            bean.setIsScroll(String.valueOf(map.get("isScroll")));
            log.info("{} convertAllParamList转换getMatchId:{}  getMarketId: {} getPlayOptionsId: {} , 结果{}",bean.getItemBean().getOrderNo(),bean.getMatchId(),bean.getMarketId(),bean.getItemBean().getPlayOptionsId().toString(), JSONObject.toJSONString(map));
        }
    }

    @Override
    public void convertSingleParam(ExtendBean bean) {
        Map<String, Object> map = getMatchInfo(bean.getMatchId(), bean.getMarketId(), bean.getItemBean().getPlayOptionsId().toString(), bean.getIsChampion());
        bean.setTournamentLevel(Integer.parseInt(String.valueOf(map.get("tournamentLevel"))));
//        bean.setDateExpect(String.valueOf(map.get("dateExpect")));
//        bean.setTournamentId(Long.parseLong(String.valueOf(map.get("tournamentId"))));
        bean.setThirdMatchSourceId(String.valueOf(map.get("thirdMatchSourceId")));
        bean.setSpecifiers(String.valueOf(map.get("extra_info")));
        bean.setDataSourceCode(String.valueOf(map.get("data_source_code")));
        bean.setThirdTemplateSourceId(String.valueOf(map.get("third_template_source_id")));
        log.info("{}convertAllParam转换getMatchId:{}  getMarketId: {} getPlayOptionsId: {} , 结果{}",bean.getItemBean().getOrderNo(),bean.getMatchId(),bean.getMarketId(),bean.getItemBean().getPlayOptionsId().toString(), JSONObject.toJSONString(map));
    }


    private Map<String, Object> getMatchInfo(String matchId, String marketId, String playOptionsId, Integer isChampion) {
        //联赛主键
        Long standardTournamentId = 0L;
        Map<String, Object> map = new HashMap<String, Object>(5);
        //冠军赛事从rcs_standard_outright_match_info表查询 第三方赛事id
        if (isChampion != null && isChampion == 1) {
            RcsStandardOutrightMatchInfo matchInfo = rcsStandardOutrightMatchInfoMapper.selectById(matchId);
            if (matchInfo == null) {
                throw new RcsServiceException("冠军赛事数据不存在");
            }
            map.put("thirdMatchSourceId", matchInfo.getThirdOutrightMatchSourceId());
            standardTournamentId = matchInfo.getStandardTournamentId();
        } else {
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(matchId);
            if (standardMatchInfo == null) {
                throw new RcsServiceException("赛事数据不存在");
            }
            //先从third_match_list_str字段获取  如果获取不到 再用thirdMatchSourceId
            String thirdMatchId = getThirdMatchIdByJson(standardMatchInfo.getThirdMatchListStr());
            if(StringUtils.isNotBlank(thirdMatchId)){
                map.put("thirdMatchSourceId", thirdMatchId);
            }else {
                map.put("thirdMatchSourceId", standardMatchInfo.getThirdMatchSourceId().toString());
            }

            standardTournamentId = standardMatchInfo.getStandardTournamentId();
        }

        StandardSportTournament standardSportTournament = standardSportTournamentMapper.selectById(standardTournamentId);
        if (null == standardSportTournament) {
            map.put("tournamentLevel", "20");
        } else {
            map.put("tournamentLevel", standardSportTournament.getTournamentLevel().toString());
        }

        StandardSportMarket standardSportMarket = standardSportMarketMapper.selectById(NumberUtils.toLong(marketId, 0));
        if (null != standardSportMarket) {
            map.put("extra_info", standardSportMarket.getExtraInfo());
            //设置是否滚球
            map.put("isScroll", standardSportMarket.getMarketType() == 0 ? "1" : "0");
        }

        StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsMapper.selectById(NumberUtils.toLong(playOptionsId, 0));
        if (null != standardSportMarketOdds) {
            String thirdTemplateSourceId = convertThirdSource(standardSportMarketOdds.getThirdTemplateSourceId(), standardSportMarketOdds.getExtraInfo());
            map.put("third_template_source_id", thirdTemplateSourceId);
        }
        return map;
    }

    /**
     * 获取第三方赛事ID 先从third_match_list_str字段获取
     * @param jsonStr
     * @return
     */
    private String getThirdMatchIdByJson(String jsonStr){
        try {
            JSONArray jsonArray = JSONArray.parseArray(jsonStr);
            for (Object o : jsonArray) {
                JSONObject json = JSONObject.parseObject(o.toString());
                if(json.getString("dataSourceCode").equals("SR")){
                    return json.getString("thirdMatchSourceId");
                }
            }
        } catch (Exception e) {
            log.info("获取赛事ID Json转换异常{},{}", e.getMessage(), e);
        }
        return "";
    }


    private String convertThirdSource(String thirdTemplateSourceId , String oddsExtraInfo) {
        if (thirdTemplateSourceId.equals("None") || !thirdTemplateSourceId.contains(":")) {
            if(StringUtils.isEmpty(oddsExtraInfo) || !oddsExtraInfo.contains("And")){
                log.error("mts获取第三方玩法和投注项 上游数据异常"+oddsExtraInfo);
                throw new RcsServiceException("mts获取第三方投注项上游数据异常");
            }
            String [] arr = oddsExtraInfo.split("And");
            thirdTemplateSourceId =String.format("SR###%s###%s",arr[0],arr[1]);
        }else {
            thirdTemplateSourceId = thirdTemplateSourceId.replace(":","###");
        }
        return thirdTemplateSourceId;
    }


    /**
     *
     * @param ticketId 第三方订单号
     * @param status 第三方订单状态
     * @param orderNo padan订单号
     * @param autoAcceptedOddsList 第三方返回赔率list
     * @param jsonValue 返回json
     * @param reasonCode 拒单code
     * @param reasonMsg 拒单描述
     * @param isCache 成功后是否走缓存
     */
//    public void updateMtsOrder(String ticketId, String status, String orderNo, List<AutoAcceptedOdds> autoAcceptedOddsList, String jsonValue,
//                               Integer reasonCode, String reasonMsg, Integer isCache) {
//        //0：待处理  1：已接单  2：拒单
//        int rcsOrderStatus = 0;
//        List<Map<String, Object>> oddsChangeList = new ArrayList<Map<String, Object>>();
//        if (status.equals(TicketAcceptance.ACCEPTED.toString())) {
//            log.info("接单情况:正常接单{}", ticketId);
//            rcsOrderStatus = 1;
//        } else if (status.equals(TicketAcceptance.REJECTED.name())) {
//            log.info("{}接单情况:正常拒单{}{}", ticketId, reasonCode, reasonMsg);
//            rcsOrderStatus = 2;
//        }
//
//        // 1.发送MQ，异步通知业务处理注单状态
//        Map<String, Object> map = Maps.newHashMap();
//        map.put("orderNo", orderNo);
//        map.put("status", rcsOrderStatus);
//        map.put("isOddsChange", false);
//        if (rcsOrderStatus == 1) {
//            map.put("infoStatus", OrderInfoStatusEnum.MTS_PASS.getCode());
//            map.put("infoMsg", "MTS接单");
//            map.put("infoCode", 0);
//        } else if (rcsOrderStatus == 2) {
//            map.put("infoStatus", OrderInfoStatusEnum.MTS_REFUSE.getCode());
//            map.put("infoMsg", "MTS拒单:" + reasonMsg);
//            map.put("infoCode", 0);
//        }
//        map.put("handleTime", System.currentTimeMillis());
//
//        LambdaQueryWrapper<TOrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
//        orderDetailWrapper.eq(TOrderDetail::getOrderNo, orderNo);
//        List<TOrderDetail> orderDetailList = orderDetailMapper.selectList(orderDetailWrapper);
//
//        //赔率范围处理
//        Map<String, String> oddsRange = new HashMap<>();
//        String defaultRange = "";
//        for (TOrderDetail item : orderDetailList) {
//            //根据联赛等级设置的 赔率范围
//            String tournamentScope = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(item.getTournamentId())), "MTSOddsChangeValue");
//            String oddsChangeStatus = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(item.getTournamentId())), "oddsChangeStatus");
//            if (StringUtils.isBlank(oddsChangeStatus) || !oddsChangeStatus.equals("1")) {
//                tournamentScope = defaultRange;
//            }
//            log.info("根据联赛等级设置的赔率 开关|范围:{}:{}:{}:{}",item.getOrderNo(), item.getBetNo(), oddsChangeStatus, tournamentScope);
//            //玩法级别的配置 开关
//            String oddsScopeMatchStatus = "rcs:risk:order:oddsScope:match.%s.match_type.%s";
//            oddsScopeMatchStatus = String.format(oddsScopeMatchStatus, item.getMatchId(), item.getMatchType() == 1 ? 1 : 0);
//            oddsScopeMatchStatus = redisClient.get(oddsScopeMatchStatus);
//            log.info("玩法级别开关:{}:{}:{}",item.getOrderNo(), item.getBetNo(), oddsScopeMatchStatus);
//            if (StringUtils.isBlank(oddsScopeMatchStatus) || oddsScopeMatchStatus.equals("null") || oddsScopeMatchStatus.equals("0")) {
//                //取联赛级别的
//                if (StringUtils.isNotBlank(tournamentScope) && !tournamentScope.equals("null")) {
//                    oddsRange.put(item.getPlayOptionsId().toString(), tournamentScope);
//                }else {
//                    oddsRange.put(item.getPlayOptionsId().toString(), defaultRange);
//                }
//                continue;
//            }
//            //玩法赔率接单范围获取
//            String oddsScopePlay = "rcs:risk:order:oddsScope:match.%s.play_id.%s.match_type.%s";
//            oddsScopePlay = String.format(oddsScopePlay, item.getMatchId(), item.getPlayId(), item.getMatchType() == 1 ? 1 : 0);
//            oddsScopePlay = redisClient.get(oddsScopePlay);
//            log.info("玩法级别范围:{}:{}:{}",item.getOrderNo(), item.getBetNo(), oddsScopePlay);
//            if (StringUtils.isNotBlank(oddsScopePlay) && !oddsScopePlay.equals("null")) {
//                oddsRange.put(item.getPlayOptionsId().toString(), oddsScopePlay);
//            } else {
//                //取联赛级别的
//                if (StringUtils.isNotBlank(tournamentScope) && !tournamentScope.equals("null")) {
//                    oddsRange.put(item.getPlayOptionsId().toString(), tournamentScope);
//                } else {
//                    oddsRange.put(item.getPlayOptionsId().toString(), defaultRange);
//                }
//            }
//        }
//        map.put("oddsRange", oddsRange);
//        sendMessage.sendMessage(MqConstants.RCS_BUS_MTS_ORDER_STATUS + "," + orderNo, map);
//
//        int mtsStatus = 0;
//        if (rcsOrderStatus == 1) {
//            mtsStatus = OrderInfoStatusEnum.MTS_PASS.getCode();
//        } else if (rcsOrderStatus == 2) {
//            mtsStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
//        }
//        //构建订单详情信息
//        List<OrderItem> list = BeanCopyUtils.copyPropertiesList(orderDetailList, OrderItem.class);
//        for (OrderItem item : list) {
//            item.setOddsValue(new BigDecimal(String.valueOf(item.getOddsValue())).multiply(new BigDecimal("100000")).doubleValue());
//            item.setValidateResult(rcsOrderStatus);
//            item.setOrderStatus(rcsOrderStatus);
//            item.setModifyTime(System.currentTimeMillis());
//        }
//
//        QueryWrapper<TOrder> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(TOrder::getOrderNo, orderNo);
//        TOrder order = orderMapper.selectOne(queryWrapper);
//        //构建订单信息
//        OrderBean orderBean = new OrderBean();
//        if (order != null) {
//            BeanCopyUtils.copyProperties(order, orderBean);
//        }
//        orderBean.setOrderStatus(rcsOrderStatus);
//        orderBean.setValidateResult(rcsOrderStatus);
//        orderBean.setReason("MTS:" + status);
//        orderBean.setInfoStatus(mtsStatus);
//        orderBean.setOrderNo(orderNo);
//        orderBean.setItems(list);
//        orderBean.setOddsChangeList(oddsChangeList);
//        sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE + ",," + orderNo, orderBean);
//
//        // 3.记录订单记录
//        LambdaQueryWrapper<RcsMtsOrderExt> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(RcsMtsOrderExt::getOrderNo, orderNo);
//        RcsMtsOrderExt rcsMtsOrderExt = rcsMtsOrderExtService.getOne(wrapper);
//        if (rcsMtsOrderExt == null) {
//            rcsMtsOrderExt = new RcsMtsOrderExt();
//            rcsMtsOrderExt.setOrderNo(orderNo);
//            rcsMtsOrderExt.setStatus(status);
//            rcsMtsOrderExt.setResult(jsonValue);
//            rcsMtsOrderExtService.addMtsOrder(rcsMtsOrderExt);
//        } else {
//            if (rcsMtsOrderExt.getStatus() == null) {
//                rcsMtsOrderExt.setStatus(status);
//            } else {
//                rcsMtsOrderExt.setStatus(rcsMtsOrderExt.getStatus() + "," + status);
//            }
//            if (rcsMtsOrderExt.getResult() == null) {
//                rcsMtsOrderExt.setResult(jsonValue);
//            } else {
//                rcsMtsOrderExt.setResult(rcsMtsOrderExt.getResult() + "," + jsonValue);
//            }
//            rcsMtsOrderExtService.updateById(rcsMtsOrderExt);
//        }
//        log.info("{}MTS订单回调处理完成", ticketId);
//
//    }


}
