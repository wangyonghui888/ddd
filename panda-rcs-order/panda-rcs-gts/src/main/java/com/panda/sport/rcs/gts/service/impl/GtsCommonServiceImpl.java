package com.panda.sport.rcs.gts.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Maps;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.tournament.TournamentTemplateByMatchService;
import com.panda.sport.data.rcs.dto.tournament.TournamentPropertyReqVo;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.gts.common.Constants;
import com.panda.sport.rcs.gts.gtsenum.GtsSportsEnum;
import com.panda.sport.rcs.gts.service.GtsCommonService;
import com.panda.sport.rcs.gts.service.RcsGtsOrderExtService;
import com.panda.sport.rcs.gts.util.SystemThreadLocal;
import com.panda.sport.rcs.gts.vo.*;
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
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.vo.odds.RcsStandardMarketDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.panda.sport.rcs.gts.common.Constants.*;
import static com.panda.sport.rcs.constants.RedisKey.REDIS_MATCH_INFO;
import static com.panda.sport.rcs.constants.RedisKey.REDIS_MATCH_MARKET_ODDS_NEW;

/**
 * @author lithan
 * @description
 * @date 2020/2/5 12:17
 */
@Service
@Slf4j
public class GtsCommonServiceImpl implements GtsCommonService {

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
    RcsLanguageInternationMapper languageInternationMapper;
    @Autowired
    RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @Autowired
    RcsGtsOrderExtService rcsGtsOrderExtService;
    @Autowired
    TOrderDetailMapper orderDetailMapper;
    @Autowired
    TOrderMapper orderMapper;


//    @Reference(check = false, lazy = true, retries = 3, timeout = 5000)
//    TournamentTemplateByMatchService tournamentTemplateByMatchService;

    /**
     * 获取gts 原始数据
     * @return
     */
    @Override
    public void convertAllParam(List<GtsExtendBean> extendBeanList) {
        for (GtsExtendBean bean : extendBeanList) {
            Map<String, String> map = getThirdData(bean.getMatchId(), bean.getMarketId(), bean.getItemBean().getPlayOptionsId().toString(), bean.getIsChampion());
            GtsBetGeniusContentVo gtsBetGeniusContentVo = JSONObject.parseObject(JSONObject.toJSONString(map), GtsBetGeniusContentVo.class);
            BookmakerContentContentVo bookmakerContentContentVo = JSONObject.parseObject(JSONObject.toJSONString(map), BookmakerContentContentVo.class);
            bean.setBetgeniusContent(gtsBetGeniusContentVo);
            bean.setBookmakerContent(bookmakerContentContentVo);
            log.info("::{}::-{}获取gts原始数据, 结果{}", SystemThreadLocal.get().get("orderNo"), bean.getItemBean().getBetNo(), JSONObject.toJSON(map));
        }
    }

    /**
     * 获取gts api 需要的参数
     * @param matchId
     * @param paMarketId
     * @param playOptionsId
     * @param isChampion
     * @return
     */
    private Map<String, String> getThirdData(String matchId, String paMarketId, String playOptionsId, Integer isChampion) {
        Map<String, String> map = new HashMap<>();
        //获取联赛id
        Long standardTournamentId = 0L;
        //冠军赛事从rcs_standard_outright_match_info表查询 第三方赛事id
        if (isChampion != null && isChampion == 1) {
            RcsStandardOutrightMatchInfo matchInfo = rcsStandardOutrightMatchInfoMapper.selectById(matchId);
            if (matchInfo == null) {
                throw new RcsServiceException("冠军赛事数据不存在");
            }
            standardTournamentId = matchInfo.getStandardTournamentId();
        } else {
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(matchId);
            if (standardMatchInfo == null) {
                throw new RcsServiceException("赛事数据不存在");
            }
            standardTournamentId = standardMatchInfo.getStandardTournamentId();
        }

        //联赛信息 用于获取 第三方联赛id 第三方联赛名称
        StandardSportTournament standardSportTournament = standardSportTournamentMapper.selectById(standardTournamentId);
        //第三方联赛id  命名根据第三方参数 命名
        String competitionId = standardSportTournament.getThirdTournamentSourceId().replace("bg:simple_tournament:","");
        //第三方联赛名称 通过国际化获取
        Long tournamentNameCode = standardSportTournament.getNameCode();
        LambdaQueryWrapper<RcsLanguageInternation> languageInternationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        languageInternationLambdaQueryWrapper.eq(RcsLanguageInternation::getNameCode, tournamentNameCode);
        RcsLanguageInternation rcsLanguageInternation = languageInternationMapper.selectOne(languageInternationLambdaQueryWrapper);
        if (rcsLanguageInternation == null) {
            throw new RcsServiceException("获取联赛名称异常");
        }
        String text = rcsLanguageInternation.getText();
        JSONObject textJson = JSONObject.parseObject(text);
        //第三方联赛名称
        String competitionName = textJson.getString("en");

        //投注项表可以获取信息 例子: BG:9883898:145519887:0:437813013	，规则：数据源+赛事id+原始盘口id+序号+原始投注项Id
        StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsMapper.selectById(NumberUtils.toLong(playOptionsId, 0));
        String thirdOddsFieldSourceId=standardSportMarketOdds.getThirdOddsFieldSourceId();
        String arr[] = thirdOddsFieldSourceId.split(":");
        //第三方赛事id
        String fixtureId = arr[1];
        //第三方盘口id
        String marketId  = arr[2];
        //第三方投注项目id
        String selectionId  = arr[4];

        /**所有name参数 用于第三方后台显示的 已和对方沟通  他们后续会自己处理  我们目前传id即可**/
        //第三方赛事名称
        String fixtureName = fixtureId;
        //第三盘口名称
        String marketName = marketId;
        //第三方投注项目名称
        String selectionName = selectionId;

        map.put("sportId", GtsSportsEnum.getByPaSportId(standardSportTournament.getSportId().intValue()).getGtsSportId().toString());
        map.put("sportName", GtsSportsEnum.getByPaSportId(standardSportTournament.getSportId().intValue()).getGtsSprotName());
        map.put("competitionId", competitionId);
        map.put("competitionName", competitionName);
        map.put("fixtureId", fixtureId);
        map.put("fixtureName", fixtureName);
        map.put("marketId", marketId);
        map.put("marketName", marketName);
        map.put("selectionId", selectionId);
        map.put("selectionName", selectionName);
        return map;
    }

    /**
     * 16666需求
     * 纯MTS：不用实时拒，MTS接单后做一次检验,盘口状态变动,非开则拒
     **/
    private boolean afterAcceptedCheck(String status, String orderNo, ErrorMessagePrompt errorMessagePrompt) {
        log.info("::{}::订单afterAcceptedCheck开始:", orderNo);
        try {
            if (!status.equals("accepted")) {
                log.info("::{}::订单afterAcceptedCheck跳过:", orderNo);
                return false;
            }
            List<TOrderDetail> tOrderDetailList = orderDetailMapper.queryOrderDetails(orderNo);
            for (TOrderDetail tOrderDetail : tOrderDetailList) {
                //赛事维度
                String matchInfoStr = redisClient.get(String.format(REDIS_MATCH_INFO, tOrderDetail.getMatchId()));
                log.info("::{}::1666需求赛事维度数据::{}", orderNo, matchInfoStr);
                if (StringUtils.isNotBlank(matchInfoStr)) {
                    StandardMatchMessage standardMatchMessage = JSON.parseObject(matchInfoStr, StandardMatchMessage.class);
                    //收盘状态不拒单
                    if (standardMatchMessage.getStatus() != 0 && standardMatchMessage.getStatus() != 13) {
                        errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                        if (standardMatchMessage.getStatus() == 1) {
                            errorMessagePrompt.setHintMsg("赛事封盘拒单");
                            errorMessagePrompt.setCurrentEvent("match_handicap_status_suspended-PA");
                            log.info("::{}::GTS投注赛事封盘拒单", tOrderDetail.getOrderNo());
                        } else if (standardMatchMessage.getStatus() == 2) {
                            errorMessagePrompt.setHintMsg("赛事关盘拒单");
                            errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
                            log.info("::{}::GTS投注赛事关盘拒单", tOrderDetail.getOrderNo());
                        } else if (standardMatchMessage.getStatus() == 11) {
                            errorMessagePrompt.setHintMsg("赛事锁盘拒单");
                            errorMessagePrompt.setCurrentEvent("match_handicap_status_lock-PA");
                            log.info("::{}::GTS投注赛事锁盘拒单", tOrderDetail.getOrderNo());
                        }
                        errorMessagePrompt.setBetNo(tOrderDetail.getBetNo());
                        return true;
                    }
                }

                String matchMarketOddsStr = redisClient.get(String.format(REDIS_MATCH_MARKET_ODDS_NEW, tOrderDetail.getPlayId().toString(), tOrderDetail.getMatchId()));
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
            log.info("::{}::订单afterAcceptedCheck异常:", orderNo);
        }
        return false;
    }

    /**
     * @param ticketId   第三方订单号
     * @param status     第三方订单状态
     * @param orderNo    padan订单号
     * @param jsonValue  返回json
     * @param reasonCode 拒单code
     * @param reasonMsg  拒单描述
     * @param isCache    成功后是否走缓存
     */
    public void updateGtsOrder(String ticketId, String status, String orderNo, String jsonValue,
                               Integer reasonCode, String reasonMsg, Integer isCache) {

        ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
        if (afterAcceptedCheck(status, orderNo, errorMessagePrompt)) {
            status = REJECTED;
            reasonMsg = errorMessagePrompt.getHintMsg();
        }

        //0：待处理  1：已接单  2：拒单
        int rcsOrderStatus = 0;
        List<Map<String, Object>> oddsChangeList = new ArrayList<Map<String, Object>>();
        if (status.equals(ACCEPTED)) {
            if (isGtsCancle(orderNo)) {
                log.info("::{}::接单情况:发现订单存在之前手工gts取消操作,不再处理", orderNo, ticketId);
                return;
            }
            log.info("::{}::接单情况:正常接单{}", orderNo, ticketId);
            rcsOrderStatus = 1;

        } else if (status.equals("REJECTED")) {
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
            map.put("infoMsg", "GTS接单");
            map.put("infoCode", 0);
        } else if (rcsOrderStatus == 2) {
            map.put("infoStatus", OrderInfoStatusEnum.MTS_REFUSE.getCode());
            map.put("infoMsg", "GTS拒单:" + reasonMsg);
            map.put("infoCode", 0);
        }

        if (oddsChangeList.size() > 0) {
            map.put("isOddsChange", true);
            map.put("oddsChangeList", oddsChangeList);
        }
        map.put("currentEvent", getCodeMsg(reasonCode));
        map.put("handleTime", System.currentTimeMillis());

        //mtsIsCache 和业务约定 该gts订单是否走的缓存 0 mts  1 mts缓存接单 2 mts-PA接单 5 gts  6 gts缓存接单 7gts-PA接单
        Integer gtsIsCache = 5;
        if (Long.valueOf(ticketId) < 0) {
            gtsIsCache = 6;
            map.put("mtsIsCache", gtsIsCache);
        }
        //商户不走gts的
        if (reasonCode == -101) {
            gtsIsCache = 7;
            map.put("mtsIsCache", gtsIsCache);
        }
        //gts 返回延迟接单的
        if (reasonCode == 200) {
            map.put("mtsIsCache", gtsIsCache);
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
//            String tournamentScope = getTournamentTemplateValue(item.getTournamentId(), "1");
//            String oddsChangeStatus =  getTournamentTemplateValue(item.getTournamentId(), "3");
            if (StringUtils.isBlank(oddsChangeStatus) || !oddsChangeStatus.equals("1")) {
                tournamentScope = defaultRange;
            }
            log.info("::{}::根据联赛等级设置的赔率 开关|范围:{}:{}:{}", item.getOrderNo(), item.getBetNo(), oddsChangeStatus, tournamentScope);
            //玩法级别的配置 开关
            String oddsScopeMatchStatus = "rcs:risk:order:oddsScope:match.%s.match_type.%s";
            oddsScopeMatchStatus = String.format(oddsScopeMatchStatus, item.getMatchId(), item.getMatchType() == 1 ? 1 : 0);
            oddsScopeMatchStatus = redisClient.get(oddsScopeMatchStatus);
            log.info("::{}::玩法级别开关:{}:{}", item.getOrderNo(), item.getBetNo(), oddsScopeMatchStatus);
            if (StringUtils.isBlank(oddsScopeMatchStatus) || oddsScopeMatchStatus.equals("null") || oddsScopeMatchStatus.equals("0")) {
                //取联赛级别的
                if (StringUtils.isNotBlank(tournamentScope) && !tournamentScope.equals("null")) {
                    oddsRange.put(item.getPlayOptionsId().toString(), tournamentScope);
                } else {
                    oddsRange.put(item.getPlayOptionsId().toString(), defaultRange);
                }
                continue;
            }
            //玩法赔率接单范围获取
            String oddsScopePlay = "rcs:risk:order:oddsScope:match.%s.play_id.%s.match_type.%s";
            oddsScopePlay = String.format(oddsScopePlay, item.getMatchId(), item.getPlayId(), item.getMatchType() == 1 ? 1 : 0);
            oddsScopePlay = redisClient.get(oddsScopePlay);
            log.info("::{}::玩法级别范围:{}:{}", item.getOrderNo(), item.getBetNo(), oddsScopePlay);
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

        int gtsStatus = 0;
        if (rcsOrderStatus == 1) {
            gtsStatus = OrderInfoStatusEnum.MTS_PASS.getCode();
        } else if (rcsOrderStatus == 2) {
            gtsStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
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
            int times = 0;
            while (order == null && times < 3) {
                times++;
                Thread.sleep(2000);
                order = orderMapper.selectOne(queryWrapper);
                log.info("::{}::,重新获取订单一次", order.getOrderNo());
            }
            log.info("::{}::,重新获取订单对象:{}  ", order.getOrderNo(), order);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //构建订单信息
        OrderBean orderBean = new OrderBean();
        if (order != null) {
            BeanCopyUtils.copyProperties(order, orderBean);
        } else {
            log.info("::{}::入库延迟了未读取到", SystemThreadLocal.get().get("orderNo"), order);
        }
        orderBean.setOrderStatus(rcsOrderStatus);
        orderBean.setValidateResult(rcsOrderStatus);
        orderBean.setReason(reasonMsg);
        orderBean.setInfoStatus(gtsStatus);
        orderBean.setOrderNo(orderNo);
        orderBean.setItems(list);
        orderBean.setOddsChangeList(oddsChangeList);
        sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE + ",," + orderNo, orderBean);

        // 3.记录订单记录
        LambdaQueryWrapper<RcsGtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsGtsOrderExt::getOrderNo, orderNo);
        RcsGtsOrderExt ext = rcsGtsOrderExtService.getOne(wrapper);
        if (ext == null) {
            ext = new RcsGtsOrderExt();
            ext.setOrderNo(orderNo);
            ext.setStatus(status);
            ext.setResult(jsonValue);
            rcsGtsOrderExtService.addGtsOrder(ext);
        } else {
            if (ext.getStatus() == null) {
                ext.setStatus(status);
            } else {
                ext.setStatus(ext.getStatus() + "," + status);
            }
            if (ext.getResult() == null) {
                ext.setResult(jsonValue);
            } else {
                ext.setResult(ext.getResult() + "," + jsonValue);
            }
            rcsGtsOrderExtService.updateById(ext);
        }
        log.info("::{}::{}gts订单处理完成", orderNo, ticketId);
        if (isCache == 1) {
            doCache(orderDetailList, status);
            log.info("::{}::gts订单回调处理缓存操作完成{}", orderNo, ticketId);
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
        String oddsChangeType = redisClient.get(String.format(GTS_ORDER_ODDSCHANGETYPE, orderNo));
        String gtsOrderCache = String.format(GTS_ORDER_CACHE, optionId, oddFinally, oddsChangeType);

        String gtsOrderExpire = redisClient.get(GTS_ORDER_EXPIRE);
        if (org.apache.commons.lang.StringUtils.isEmpty(gtsOrderExpire)) {
            gtsOrderExpire = "2";
        }
        if (status.equals("accepted")) {
            redisClient.setExpiry(gtsOrderCache, "1", Long.valueOf(gtsOrderExpire));
            log.info("::{}::gts订单新增缓存完成{}", orderNo, gtsOrderCache);
        } else if (status.equals("rejected")) {
            redisClient.delete(gtsOrderCache);
            log.info("::{}::gts订单删除缓存完成{}", orderNo, gtsOrderCache);
        }
    }

    /**
     * 判断该订单是否有取消操作  有则返回true 没有则返回fasle
     *
     * @param orderNo
     * @return
     */
    private boolean isGtsCancle(String orderNo) {
        LambdaQueryWrapper<RcsGtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsGtsOrderExt::getOrderNo, orderNo);
        RcsGtsOrderExt rcsGtsOrderExt = rcsGtsOrderExtService.getOne(wrapper);
        if (rcsGtsOrderExt == null) {
            return false;
        }
        if (rcsGtsOrderExt.getCancelStatus() != 1) {
            return false;
        }
        return true;
    }

    /**
     * @param reasonCode
     */
    private static String getCodeMsg(Integer reasonCode) {

        return "";
    }



    /**
     * rpc获取根据联赛等级设置的 赔率范围 延迟秒数
     * @param id 联赛id
     * @param type 类型 1.MTSOddsChangeValue  2.orderDelayTime  3.oddsChangeStatus
     */
    @Override
    public String getTournamentTemplateValue(Long id, String type){
        String res = "";
        try {
            Request<TournamentPropertyReqVo> reqVoRequest = new Request<>();
            TournamentPropertyReqVo vo = new TournamentPropertyReqVo();
            vo.setId(id);
            vo.setType(type);
            reqVoRequest.setData(vo);
//            Response<String> response = tournamentTemplateByMatchService.queryTournamentPropertyData(reqVoRequest);
//            res = response.getData();
        } catch (RcsServiceException e) {
            log.info("::gts rpc异常::{},{}", e.getMessage(), e);
            if(type.equals("1")){
                res = "4";
            }
            if(type.equals("2")){
                res = "5";
            }
            if(type.equals("3")){
                res = "1";
            }
        }
        return res;
    }

    @Override
    public boolean dealWithData(List<TOrderDetail> tOrderDetailList, ErrorMessagePrompt errorMessagePrompt) {
        for (int n = 0; n < tOrderDetailList.size(); n++) {
            TOrderDetail tOrderDetail = tOrderDetailList.get(n);
            //赛事维度
            String matchInfoStr = redisClient.get(String.format(Constants.REDIS_MATCH_INFO, tOrderDetail.getMatchId()));
            log.info("::{}::1666需求赛事维度数据::{}",tOrderDetail.getOrderNo(),matchInfoStr);
            if (StringUtils.isNotBlank(matchInfoStr)) {
                StandardMatchMessage standardMatchMessage = JSONObject.parseObject(matchInfoStr, StandardMatchMessage.class);
                //收盘状态不拒单
                if (standardMatchMessage.getStatus() != 0 && standardMatchMessage.getStatus() != 13) {
                    errorMessagePrompt.setInfoStatus(OrderInfoStatusEnum.SCROLL_REFUSE.getCode());
                    if (standardMatchMessage.getStatus() == 1) {
                        errorMessagePrompt.setHintMsg("赛事封盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_suspended-PA");
                        log.info("::{}::GTS投注赛事封盘拒单", tOrderDetail.getOrderNo());
                    } else if (standardMatchMessage.getStatus() == 2) {
                        errorMessagePrompt.setHintMsg("赛事关盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_deactivated-PA");
                        log.info("::{}::GTS投注赛事关盘拒单", tOrderDetail.getOrderNo());
                    } else if (standardMatchMessage.getStatus() == 11) {
                        errorMessagePrompt.setHintMsg("赛事锁盘拒单");
                        errorMessagePrompt.setCurrentEvent("match_handicap_status_lock-PA");
                        log.info("::{}::GTS投注赛事锁盘拒单", tOrderDetail.getOrderNo());
                    }
                    errorMessagePrompt.setBetNo(tOrderDetail.getBetNo());
                    return true;
                }
            }
            //盘口维度
            String matchMarketOddsStr = redisClient.get(String.format(Constants.REDIS_MATCH_MARKET_ODDS_NEW, tOrderDetail.getPlayId(),tOrderDetail.getMatchId()));
            if (StringUtils.isNotBlank(matchMarketOddsStr)) {
                String oddsScopeValue = "";
                //根据联赛等级设置的 赔率范围
                String tournamentScope = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(tOrderDetail.getTournamentId())), "MTSOddsChangeValue");
                String oddsChangeStatus = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(tOrderDetail.getTournamentId())), "oddsChangeStatus");
                if (StringUtils.isNotBlank(oddsChangeStatus) && oddsChangeStatus.equals("1")) {
                    oddsScopeValue = tournamentScope;
                }
                log.info("::{}::1666根据联赛等级设置的赔率 开关|范围:{}:{}:{}:{}::::", tOrderDetail.getOrderNo(), tOrderDetail.getBetNo(), oddsChangeStatus, oddsScopeValue);
                //玩法级别的配置 开关
                String oddsScopeMatchStatus = "rcs:risk:order:oddsScope:match.%s.match_type.%s";
                oddsScopeMatchStatus = String.format(oddsScopeMatchStatus, tOrderDetail.getMatchId(), tOrderDetail.getMatchType() == 1 ? 1 : 0);
                oddsScopeMatchStatus = redisClient.get(oddsScopeMatchStatus);
                log.info("::{}::1666玩法级别开关:{}:{}:{}::::", tOrderDetail.getOrderNo(), tOrderDetail.getBetNo(), oddsScopeMatchStatus);
                if (StringUtils.isNotBlank(oddsScopeMatchStatus) && oddsScopeMatchStatus.equals("1")) {
                    oddsScopeValue = redisClient.get(String.format(Constants.ODDS_SCOPE_KEY, tOrderDetail.getMatchId(), tOrderDetail.getPlayId(), tOrderDetail.getMatchType() == 2 ? 0 : 1));
                }
                log.info("::{}::1666需求配置的赔率:{},赛事ID:{}::",tOrderDetail.getOrderNo(),oddsScopeValue, tOrderDetail.getMatchId());
                List<StandardMarketMessage> rcsStandardMarketDTOS = JSONObject.parseArray(matchMarketOddsStr, StandardMarketMessage.class);
                log.info("::{}::1666需求订单详细表::{},{}", tOrderDetail.getOrderNo(),JSONObject.toJSONString(tOrderDetail), JSONObject.toJSONString(rcsStandardMarketDTOS));
                for (int i = 0; i < rcsStandardMarketDTOS.size(); i++) {
                    StandardMarketMessage standardMarketMessage = rcsStandardMarketDTOS.get(i);
                    log.info("::{}::1666需求下发消息::{}", tOrderDetail.getOrderNo(),JSONObject.toJSONString(standardMarketMessage));

                    if (tOrderDetail.getSportId().longValue() == SportIdEnum.FOOTBALL.getId() || tOrderDetail.getSportId().longValue() == SportIdEnum.BASKETBALL.getId()) {
                        if (StringUtils.equals(String.valueOf(standardMarketMessage.getId()),String.valueOf(tOrderDetail.getMarketId()))) {
                            //盘口状态有变化
                            if (!standardMarketMessage.getStatus().equals(0) || standardMarketMessage.getThirdMarketSourceStatus() != 0) {
                                log.info("::{}::1666需求足球盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", tOrderDetail.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getStatus(), tOrderDetail.getOrderStatus());
                                this.errorMessage(standardMarketMessage, errorMessagePrompt);
                                return true;
                            }
                        }
                    } else {
                        if (StringUtils.equals(String.valueOf(standardMarketMessage.getId()), String.valueOf(tOrderDetail.getMarketId()))) {
                            //盘口状态有变化
                            if (!standardMarketMessage.getStatus().equals(0)) {
                                log.info("::{}::1666需求其他球种盘口有变化拒单::盘口ID:{}盘口状态:{},订单状态:{}", tOrderDetail.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getStatus(), tOrderDetail.getOrderStatus());
                                this.errorMessage(standardMarketMessage, errorMessagePrompt);
                                return true;
                            }
                            //赔率有变化
                            if (StringUtils.isNotBlank(oddsScopeValue)) {
                                BigDecimal oddsScope = new BigDecimal(oddsScopeValue).divide(new BigDecimal(100), 4, BigDecimal.ROUND_DOWN);
                                BigDecimal one = new BigDecimal(1);
                                BigDecimal orderOdds =  BigDecimal.valueOf(tOrderDetail.getOddsValue());
                                BigDecimal checkOdds = BigDecimal.ZERO;
                                for (int j = 0; j < standardMarketMessage.getMarketOddsList().size(); j++) {
                                    StandardMarketOddsMessage standardMarketOddsMessage = standardMarketMessage.getMarketOddsList().get(j);
                                    log.info("::{}::1666需求获取盘口投注项赔率::{}", tOrderDetail.getOrderNo(), JSONObject.toJSONString(standardMarketOddsMessage));
                                    if (StringUtils.equals(String.valueOf(standardMarketOddsMessage.getId()), String.valueOf(tOrderDetail.getPlayOptionsId()))) {
                                        checkOdds = new BigDecimal(standardMarketOddsMessage.getOddsValue()).divide(new BigDecimal(100000), 4, BigDecimal.ROUND_DOWN);
                                    }
                                }
                                log.info("::{}::1666需求盘口赔率::订单赔率:{},配置值赔率范围:{},checkOdds:{}", tOrderDetail.getOrderNo(), orderOdds, oddsScope, checkOdds);
                                if (one.divide(orderOdds, 4, BigDecimal.ROUND_DOWN).subtract(one.divide(checkOdds, 4, BigDecimal.ROUND_DOWN)).abs().compareTo(oddsScope) > 0) {
                                    log.info("::{}::1666需求盘口赔率有变化拒单::订单赔率:{},配置值赔率范围:{},checkOdds:{}", tOrderDetail.getOrderNo(), orderOdds, oddsScope, checkOdds);
                                    errorMessagePrompt.setHintMsg("赔率变动幅度过大拒单");
                                    return true;
                                }
                            }
                        }
                        Integer matchType = tOrderDetail.getMatchType() == 1 ? 1 : 0;
                        //盘口的位置有变化
                        if (StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getMarketCategoryId()), String.valueOf(tOrderDetail.getPlayId())) &&
                                StringUtils.equalsIgnoreCase(String.valueOf(standardMarketMessage.getChildMarketCategoryId()), tOrderDetail.getSubPlayId()) &&
                                standardMarketMessage.getMarketType().equals(matchType) &&
                                tOrderDetail.getPlaceNum().equals(standardMarketMessage.getPlaceNum()) &&
                                !StringUtils.equalsIgnoreCase(String.valueOf(tOrderDetail.getMarketId()), String.valueOf(standardMarketMessage.getId()))) {
                            log.info("::{}::1666需求盘口位置有变化拒单::盘口ID:{},盘口位置:{},订单盘口位置:{}", tOrderDetail.getOrderNo(), standardMarketMessage.getId(), standardMarketMessage.getPlaceNum(), tOrderDetail.getPlaceNum());
                            errorMessagePrompt.setHintMsg("对应坑位的盘口值已变更拒单");
                            return true;
                        }
                    }
                }
            }


        }
        return false;
    }

    private void errorMessage(StandardMarketMessage standardMarketMessage, ErrorMessagePrompt errorMessagePrompt) {
        if (standardMarketMessage.getThirdMarketSourceStatus() == 1) {
            errorMessagePrompt.setHintMsg("盘口封盘(数据商)拒单");
        }
        if (standardMarketMessage.getThirdMarketSourceStatus() == 2) {
            errorMessagePrompt.setHintMsg("盘口关盘(数据商)拒单");
        }
        if (standardMarketMessage.getStatus() == 1 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口封盘拒单");
        }
        if (standardMarketMessage.getStatus() == 2 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口关盘拒单");
        }
        if (standardMarketMessage.getStatus() == 11 && standardMarketMessage.getThirdMarketSourceStatus() == 0) {
            errorMessagePrompt.setHintMsg("盘口锁盘拒单");
        }
    }

}
