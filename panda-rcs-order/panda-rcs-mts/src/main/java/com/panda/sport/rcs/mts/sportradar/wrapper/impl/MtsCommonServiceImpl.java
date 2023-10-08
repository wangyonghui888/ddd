package com.panda.sport.rcs.mts.sportradar.wrapper.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.api.CheckOddsStatusServer;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.vo.OddStatusMessagePrompt;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.constants.Constants;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketResponseHandler;
import com.panda.sport.rcs.mts.sportradar.vo.ErrorMessagePrompt;
import com.panda.sport.rcs.mts.sportradar.vo.StandardMatchMessage;
import com.panda.sport.rcs.mts.sportradar.wrapper.MtsCommonService;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.vo.odds.RcsStandardMarketDTO;
import com.sportradar.mts.sdk.api.AutoAcceptedOdds;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import com.sportradar.mts.sdk.api.interfaces.TicketCancelSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

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


    @Reference(check = false, lazy = true, retries = 1, timeout = 3000)
    private CheckOddsStatusServer checkOddsStatusServer;

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
            log.info("::{}::convertAllParamList转换getMatchId:{}  getMarketId: {} getPlayOptionsId: {} , 结果{}",bean.getItemBean().getOrderNo(),bean.getMatchId(),bean.getMarketId(),bean.getItemBean().getPlayOptionsId().toString(), JSONObject.toJSONString(map));
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
            log.error("获取第三方赛事ID Json转换异常", e);
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
     * 16666需求
     * 纯MTS：不用实时拒，MTS接单后做一次检验,盘口状态变动,非开则拒
     **/
    private boolean afterAcceptedCheck(String status, String orderNo,ErrorMessagePrompt errorMessagePrompt) {
        log.info("::{}::订单afterAcceptedCheck开始:", orderNo);
        try {
            if (!status.equals(TicketAcceptance.ACCEPTED.toString())) {
                log.info("::{}::订单afterAcceptedCheck跳过:", orderNo);
                return false;
            }
            List<TOrderDetail> tOrderDetailList = orderDetailMapper.queryOrderDetails(orderNo);
            for (TOrderDetail tOrderDetail : tOrderDetailList) {
                //赛事维度
                String matchInfoStr = redisClient.get(String.format(Constants.REDIS_MATCH_INFO, tOrderDetail.getMatchId()));
                log.info("::{}::1666需求赛事维度数据::{}",orderNo, matchInfoStr);
                if (StringUtils.isNotBlank(matchInfoStr)) {
                    StandardMatchMessage standardMatchMessage = JSONObject.parseObject(matchInfoStr, StandardMatchMessage.class);
                    //收盘状态不拒单
                    if (standardMatchMessage.getStatus() != 0&&standardMatchMessage.getStatus() != 13) {
                        if (standardMatchMessage.getStatus() == 1) {
                            errorMessagePrompt.setHintMsg("赛事封盘拒单");
                        } else if (standardMatchMessage.getStatus() == 2) {
                            errorMessagePrompt.setHintMsg("赛事关盘拒单");
                        } else if (standardMatchMessage.getStatus() == 11) {
                            errorMessagePrompt.setHintMsg("赛事锁盘拒单");
                        }
                        return true;
                    }
                }
                String matchMarketOddsStr = redisClient.hGet(String.format(Constants.REDIS_MATCH_MARKET_ODDS_NEW, tOrderDetail.getMatchId()), tOrderDetail.getPlayId().toString());
                log.info("::{}::订单:matchMarketOddsStr:{}", orderNo, matchMarketOddsStr);
                if (StringUtils.isNotBlank(matchMarketOddsStr)) {
                    List<RcsStandardMarketDTO> rcsStandardMarketDTOS = JSONObject.parseArray(matchMarketOddsStr, RcsStandardMarketDTO.class);
                    for (int i = 0; i < rcsStandardMarketDTOS.size(); i++) {
                        RcsStandardMarketDTO rcsStandardMarketDTO = rcsStandardMarketDTOS.get(i);
                            if (rcsStandardMarketDTO.getId().equals(String.valueOf(tOrderDetail.getMarketId()))) {
                                //盘口状态有变化
                                if (!rcsStandardMarketDTO.getStatus().equals(0) || rcsStandardMarketDTO.getThirdMarketSourceStatus() != 0) {
                                    log.info("::{}::1666需求成功接单判断后足球盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", tOrderDetail.getOrderNo(), rcsStandardMarketDTO.getId(), rcsStandardMarketDTO.getStatus(), tOrderDetail.getOrderStatus());
                                    errorMessagePrompt.setHintMsg("盘口状态变化拒单");
                                    return true;
                                }
                            }
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::订单afterAcceptedCheck异常:", orderNo, e);
        }
        return false;
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
    public void updateMtsOrder(String ticketId, String status, String orderNo, List<AutoAcceptedOdds> autoAcceptedOddsList, String jsonValue,
                               Integer reasonCode, String reasonMsg, Integer isCache) {

        ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
        if (afterAcceptedCheck(status, orderNo, errorMessagePrompt)) {
            status = "REJECTED";
            reasonMsg = errorMessagePrompt.getHintMsg();
            //通知MTS此单为拒单
            TicketCancelSender ticketCancelSender = TicketResponseHandler.getTicketCancelSender();
            BuilderFactory builderFactory = TicketResponseHandler.getBuilderFactory();
            TicketCancel ticketCancel = new TicketBuilderHelper(builderFactory).getTicketCancel(orderNo, "102");
            ticketCancelSender.send(ticketCancel);
            log.info("::{}::接单情况:盘口变化向mts再次取消订单,操作结束", orderNo, ticketId);
        }

        //0：待处理  1：已接单  2：拒单
        int rcsOrderStatus = 0;
        List<Map<String, Object>> oddsChangeList = new ArrayList<Map<String, Object>>();
        if (status.equals(TicketAcceptance.ACCEPTED.toString())) {
            /***************特殊处理 ,如果发现之前有手动取消订单操作,可能mts没接受成功取消的注单,那么这个时候 不再接mts的单**************************/
            if (isMtsCancle(orderNo)) {
                log.info("::{}::接单情况:发现订单存在之前手工mts取消操作,不再接单,再向mts做一次取消操作{}",orderNo,ticketId);
                // 3.通知MTS此单为拒单
                TicketCancelSender ticketCancelSender = TicketResponseHandler.getTicketCancelSender();
                BuilderFactory builderFactory = TicketResponseHandler.getBuilderFactory();
                TicketCancel ticketCancel = new TicketBuilderHelper(builderFactory).getTicketCancel(orderNo, "102");
                ticketCancelSender.send(ticketCancel);
                log.info("::{}::接单情况:向mts再次取消订单,操作结束",orderNo,ticketId);
                return;
            }
            log.info("::{}::接单情况:正常接单{}",orderNo,ticketId);
            rcsOrderStatus = 1;

            //接单时有可能赔率变化，需要将变化赔率下发
            if (autoAcceptedOddsList != null && autoAcceptedOddsList.size() > 0) {
                LambdaQueryWrapper<TOrderDetail> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TOrderDetail::getOrderNo, orderNo).orderByAsc(TOrderDetail::getId);
                List<TOrderDetail> detailList = orderDetailMapper.selectList(wrapper);
                if (detailList != null && detailList.size() > 0) {
                    for (AutoAcceptedOdds odds : autoAcceptedOddsList) {
                        //赔率没有变化
                        if (String.valueOf(odds.getRequestedOdds()).equals(String.valueOf(odds.getUsedOdds())))
                            continue;
                        TOrderDetail detail = detailList.get(odds.getSelectionIndex());
                        Map<String, Object> oddChangeMap = new HashMap<String, Object>();
                        oddChangeMap.put("betNo", detail.getBetNo());
                        oddChangeMap.put("requestedOdds", new BigDecimal(String.valueOf(odds.getRequestedOdds())).divide(new BigDecimal("10000")).toPlainString());
                        oddChangeMap.put("usedOdds", new BigDecimal(String.valueOf(odds.getUsedOdds())).divide(new BigDecimal("10000")).toPlainString());
                        oddsChangeList.add(oddChangeMap);
                    }
                }
            }
        } else if (status.equals(TicketAcceptance.REJECTED.name())) {
            log.info("::{}::{}接单情况:正常拒单{}{}", orderNo, ticketId, reasonCode, reasonMsg);
            rcsOrderStatus = 2;
        }

        // 1.发送MQ，异步通知业务处理注单状态
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderNo", orderNo);
        map.put("status", rcsOrderStatus);
        map.put("isOddsChange", false);
        if (rcsOrderStatus == 1) {
            map.put("infoStatus", OrderInfoStatusEnum.MTS_PASS.getCode());
            map.put("infoMsg", "MTS接单");
            map.put("infoCode", 0);
        } else if (rcsOrderStatus == 2) {
            map.put("infoStatus", OrderInfoStatusEnum.MTS_REFUSE.getCode());
            map.put("infoMsg", "MTS拒单:" + reasonMsg);
            map.put("infoCode", 0);
        }

        if (oddsChangeList.size() > 0) {
            map.put("isOddsChange", true);
            map.put("oddsChangeList", oddsChangeList);
        }
        map.put("currentEvent", getCodeMsg(reasonCode));
        map.put("handleTime", System.currentTimeMillis());

        //mtsIsCache 该mts订单是否走的缓存 0普通 1缓存接单 2 PA接单
        Integer mtsIsCache = 0;
        if (Long.valueOf(ticketId) < 0) {
            mtsIsCache = 1;
            map.put("mtsIsCache", mtsIsCache);
        }
        //商户不走mts的
        if (reasonCode == -101) {
            mtsIsCache = 2;
            map.put("mtsIsCache", mtsIsCache);
        }


        LambdaQueryWrapper<TOrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
        orderDetailWrapper.eq(TOrderDetail::getOrderNo, orderNo);
        List<TOrderDetail> orderDetailList = orderDetailMapper.selectList(orderDetailWrapper);

        //赔率范围处理
        Map<String, String> oddsRange = new HashMap<>();
        String defaultRange = "";
        for (TOrderDetail item : orderDetailList) {
            //根据联赛等级设置的 赔率范围
            String tournamentScope = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(item.getTournamentId())), "MTSOddsChangeValue");
            String oddsChangeStatus = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(item.getTournamentId())), "oddsChangeStatus");
            if (StringUtils.isBlank(oddsChangeStatus) || !oddsChangeStatus.equals("1")) {
                tournamentScope = defaultRange;
            }
            log.info("::{}::根据联赛等级设置的赔率 开关|范围:{}:{}:{}",item.getOrderNo(), item.getBetNo(), oddsChangeStatus, tournamentScope);
            //玩法级别的配置 开关
            String oddsScopeMatchStatus = "rcs:risk:order:oddsScope:match.%s.match_type.%s";
            oddsScopeMatchStatus = String.format(oddsScopeMatchStatus, item.getMatchId(), item.getMatchType() == 1 ? 1 : 0);
            oddsScopeMatchStatus = redisClient.get(oddsScopeMatchStatus);
            log.info("::{}::玩法级别开关:{}:{}",item.getOrderNo(), item.getBetNo(), oddsScopeMatchStatus);
            if (StringUtils.isBlank(oddsScopeMatchStatus) || oddsScopeMatchStatus.equals("null") || oddsScopeMatchStatus.equals("0")) {
                //取联赛级别的
                if (StringUtils.isNotBlank(tournamentScope) && !tournamentScope.equals("null")) {
                    oddsRange.put(item.getPlayOptionsId().toString(), tournamentScope);
                }else {
                    oddsRange.put(item.getPlayOptionsId().toString(), defaultRange);
                }
                continue;
            }
            //玩法赔率接单范围获取
            String oddsScopePlay = "rcs:risk:order:oddsScope:match.%s.play_id.%s.match_type.%s";
            oddsScopePlay = String.format(oddsScopePlay, item.getMatchId(), item.getPlayId(), item.getMatchType() == 1 ? 1 : 0);
            oddsScopePlay = redisClient.get(oddsScopePlay);
            log.info("::{}::玩法级别范围:{}:{}",item.getOrderNo(), item.getBetNo(), oddsScopePlay);
            if (StringUtils.isNotBlank(oddsScopePlay) && !oddsScopePlay.equals("null")) {
                oddsRange.put(item.getPlayOptionsId().toString(), oddsScopePlay);
            } else {
                //取联赛级别的
                if (StringUtils.isNotBlank(tournamentScope) && !tournamentScope.equals("null")) {
                    oddsRange.put(item.getPlayOptionsId().toString(), tournamentScope);
                } else {
                    oddsRange.put(item.getPlayOptionsId().toString(), defaultRange);
                }
            }
        }
        map.put("oddsRange", oddsRange);
        sendMessage.sendMessage(MqConstants.RCS_BUS_MTS_ORDER_STATUS + "," + orderNo, map);

        int mtsStatus = 0;
        if (rcsOrderStatus == 1) {
            mtsStatus = OrderInfoStatusEnum.MTS_PASS.getCode();
        } else if (rcsOrderStatus == 2) {
            mtsStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
        }
        //构建订单详情信息
        List<OrderItem> list = BeanCopyUtils.copyPropertiesList(orderDetailList, OrderItem.class);
        for (OrderItem item : list) {
            item.setOddsValue(new BigDecimal(String.valueOf(item.getOddsValue())).multiply(new BigDecimal("100000")).doubleValue());
            item.setValidateResult(rcsOrderStatus);
            item.setOrderStatus(rcsOrderStatus);
            item.setModifyTime(System.currentTimeMillis());
        }

        QueryWrapper<TOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TOrder::getOrderNo, orderNo);
        TOrder order = orderMapper.selectOne(queryWrapper);

        //极端情况会有对象延迟的情况  这种情况极少 此处做兼容
        try {
            int times=0;
            while (order == null && times < 3) {
                times++;
                Thread.sleep(2000);
                order = orderMapper.selectOne(queryWrapper);
                log.info("::{}::重新获取订单一次", order.getOrderNo());
            }
            log.info("::{}::重新获取订单对象:{}", order.getOrderNo(), order);
        } catch (Exception e) {
            log.error("::{}::获取订单异常:", order.getOrderNo(), e);
        }

        //构建订单信息
        OrderBean orderBean = new OrderBean();
        if (order != null) {
            BeanCopyUtils.copyProperties(order, orderBean);
        }else {
            log.info("::{}::入库延迟了未读取到", order.getOrderNo(), order);
        }
        orderBean.setOrderStatus(rcsOrderStatus);
        orderBean.setValidateResult(rcsOrderStatus);
        orderBean.setReason(reasonMsg);
        orderBean.setInfoStatus(mtsStatus);
        orderBean.setOrderNo(orderNo);
        orderBean.setItems(list);
        orderBean.setOddsChangeList(oddsChangeList);
        sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE + ",," + orderNo, orderBean);

        // 3.记录订单记录
        LambdaQueryWrapper<RcsMtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsMtsOrderExt::getOrderNo, orderNo);
        RcsMtsOrderExt rcsMtsOrderExt = rcsMtsOrderExtService.getOne(wrapper);
        if (rcsMtsOrderExt == null) {
            rcsMtsOrderExt = new RcsMtsOrderExt();
            rcsMtsOrderExt.setOrderNo(orderNo);
            rcsMtsOrderExt.setStatus(status);
            rcsMtsOrderExt.setResult(jsonValue);
            rcsMtsOrderExtService.addMtsOrder(rcsMtsOrderExt);
        } else {
            if (rcsMtsOrderExt.getStatus() == null) {
                rcsMtsOrderExt.setStatus(status);
            } else {
                rcsMtsOrderExt.setStatus(rcsMtsOrderExt.getStatus() + "," + status);
            }
            if (rcsMtsOrderExt.getResult() == null) {
                rcsMtsOrderExt.setResult(jsonValue);
            } else {
                rcsMtsOrderExt.setResult(rcsMtsOrderExt.getResult() + "," + jsonValue);
            }
            rcsMtsOrderExtService.updateById(rcsMtsOrderExt);
        }
        log.info("::{}::{}MTS订单回调处理完成",orderNo,ticketId);
        if (isCache == 1) {
            doCache(orderDetailList, status);
            log.info("::{}::MTS订单回调处理缓存操作完成{}", orderNo, ticketId);
        }
    }

    private void doCache(List<TOrderDetail> orderDetailList, String status) {
        if (orderDetailList.size() != 1) {
            return;
        }
        TOrderDetail detail = orderDetailList.get(0);
        String orderNo = detail.getOrderNo();
        Long optionId = detail.getPlayOptionsId();
        String oddFinally = detail.getOddFinally();
        //赔率变化模式
        String oddsChangeType = redisClient.get(String.format(Constants.MTS_ORDER_ODDSCHANGETYPE, orderNo));
        String mtsOrderCache = String.format(Constants.MTS_ORDER_CACHE, optionId, oddFinally, oddsChangeType);

        String mtsOrderExpire = redisClient.get(Constants.MTS_ORDER_EXPIRE);
        if (org.apache.commons.lang.StringUtils.isEmpty(mtsOrderExpire)) {
            mtsOrderExpire = "2";
        }
        if (status.equals(TicketAcceptance.ACCEPTED.name())) {
            redisClient.setExpiry(mtsOrderCache, "1", Long.valueOf(mtsOrderExpire));
            log.info("::{}::MTS订单新增缓存完成{}", orderNo, mtsOrderCache);
        } else if (status.equals(TicketAcceptance.REJECTED.name())){
            redisClient.delete(mtsOrderCache);
            log.info("::{}::MTS订单删除缓存完成{}", orderNo, mtsOrderCache);
        }
    }

    /**
     * 判断该订单是否有取消操作  有则返回true 没有则返回fasle
     * @param orderNo
     * @return
     */
    private boolean isMtsCancle(String orderNo){
        RcsMtsOrderExtService rcsMtsOrderExtService = SpringContextUtils.getBeanByClass(RcsMtsOrderExtService.class);
        LambdaQueryWrapper<RcsMtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsMtsOrderExt::getOrderNo, orderNo);
        RcsMtsOrderExt rcsMtsOrderExt = rcsMtsOrderExtService.getOne(wrapper);
        if (rcsMtsOrderExt == null) {
            return false;
        }
        if(rcsMtsOrderExt.getCancelStatus()!=1){
            return false;
        }
        return true;
    }

    /**
     * -401,-402,-404,-421,-423,-430,-431,-503,-504,-506   投注项无效(market_expired)
     * -511,-512,-702,-703   超出限额(over_payout)
     * @param reasonCode
     */
    private static String getCodeMsg(Integer reasonCode) {
        Integer arr[] = new Integer[]{-401,-402,-403,-404,-405,-406,-407,-408,-409,-410,-422,-423};
        if (Arrays.asList(arr).contains(reasonCode)) {
            return "market_expired";
        }
        arr  = new Integer[]{-701,-702,-703,-711,-712,-713,-721,-722,-723,};
        if (Arrays.asList(arr).contains(reasonCode)) {
            return "over_payout";
        }

        arr  = new Integer[]{-420,-421,-430,-431};
        if (Arrays.asList(arr).contains(reasonCode)) {
            return "odds_hight";
        }
        return "data_error";
    }

    public static void main(String[] args) {
        System.out.println(1==1);
    }
}
