package com.panda.sport.rcs.trade.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.ConfigCashOutTradeItemDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.TradeMarketUiConfigDTO;
import com.panda.sport.data.rcs.dto.tournament.StandardMarketSellQueryDto;
import com.panda.sport.manager.api.IMarketCategorySellApi;
import com.panda.sport.manager.api.dto.ConfirmMarketCategorySellDTO;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.MarketKindEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.constants.TradeConstant;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargainRef;
import com.panda.sport.rcs.trade.enums.LinkedTypeEnum;
import com.panda.sport.rcs.trade.enums.MatchTypeEnum;
import com.panda.sport.rcs.trade.param.TournamentTemplatePlayMargainRefParam;
import com.panda.sport.rcs.trade.service.DistanceSwitchServer;
import com.panda.sport.rcs.trade.service.MongoDbService;
import com.panda.sport.rcs.trade.service.TradeModeService;
import com.panda.sport.rcs.trade.service.TradeStatusService;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.wrapper.RcsStandardSportMarketSellService;
import com.panda.sport.rcs.trade.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsMatchTemplateModifyService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplatePlayMargainService;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.utils.StringUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.panda.sport.rcs.constants.RedisKey.EXPRIY_TIME_7_DAYS;
import static com.panda.sport.rcs.pojo.constants.TradeConstant.FOOTBALL_EARLY_SETTLEMENT_PLAY;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-trade-group
 * @Package Name :  com.panda.sport.rcs.trade.service.impl
 * @Description :  TODO
 * @Date: 2022-07-09 10:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OnSaleCommonServer {
    @Reference(check = false, lazy = true, retries = 1, timeout = 100000)
    private IMarketCategorySellApi marketCategorySellApi;
    @Reference(check = false, lazy = true, retries = 3, timeout = 5000)
    private ITradeMarketConfigApi tradeMarketConfigApi;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IRcsMatchTemplateModifyService rcsMatchTemplateModifyService;
    @Autowired
    private RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
    @Resource
    private IRcsTournamentTemplatePlayMargainService rcsTournamentTemplatePlayMargainService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    protected ProducerSendMessageUtils producerSendMessageUtils;
    @Resource
    private TradeStatusService tradeStatusService;

    private final DistanceSwitchServer distanceSwitchServerImpl;
    private final RcsTournamentTemplateMapper templateMapper;
    private final TradeModeService tradeModeService;
    private final RcsStandardSportMarketSellService rcsStandardSportMarketSellService;
    @Autowired
    private MongoDbService mongoDbService;

    public void confirmMarketCategorySell(StandardMarketSellQueryDto standardMarketSellQueryVo) {
        //调用融合开售接口
        ConfirmMarketCategorySellDTO dto = BeanCopyUtils.copyProperties(standardMarketSellQueryVo, ConfirmMarketCategorySellDTO.class);
        String linkId = CommonUtil.getRequestId(standardMarketSellQueryVo.getMatchId(),standardMarketSellQueryVo.getPlayId());
        log.info("::{}::RPC调用[开售]请求参数::{}", linkId, JSON.toJSONString(dto));
        dto.setOperateTime(System.currentTimeMillis());
        Response<String> response = DataRealtimeApiUtils.handleApi(dto, new DataRealtimeApiUtils.ApiCall() {
            @Override
            @Trace
            public <R> Response<R> callApi(Request request) {
                request.setLinkId(request.getLinkId() + "_sold");
                Response<String> rs = marketCategorySellApi.confirmMarketCategorySell(request);
                return (Response<R>) rs;
            }
        });
        log.info("::{}::RPC调用[开售]响应参数::{}", linkId, JSON.toJSONString(response));
        if (response.isSuccess()) {
            //更新赛事模板赔率源权重优先级和玩法状态
            rcsStandardSportMarketSellService.updatePlayMarginIsSellByPlayId(standardMarketSellQueryVo);

            //kir-开售赛事时也需要同步最新的（赛事级别的提前结算开关）状态给融合
            if (SportIdEnum.isFootball(standardMarketSellQueryVo.getSportId())) {
                QueryWrapper<RcsTournamentTemplate> wrapper = new QueryWrapper<>();
                wrapper.eq("type_val", standardMarketSellQueryVo.getMatchId());
                wrapper.eq("type", 3);
                wrapper.eq("sport_id", standardMarketSellQueryVo.getSportId());
                wrapper.eq("match_type", standardMarketSellQueryVo.getMarketType().equals("PRE") ? 1 : 0);
                RcsTournamentTemplate temp = templateMapper.selectOne(wrapper);
                TradeMarketUiConfigDTO tradeMarketUiConfigDTO = this.getCommonClass(temp);
                log.info("::{}::RPC调用[提前结算开关]请求参数::{}", linkId, JSON.toJSONString(tradeMarketUiConfigDTO));
                Response<String> responseConfig = DataRealtimeApiUtils.handleApi(tradeMarketUiConfigDTO, new DataRealtimeApiUtils.ApiCall() {
                    @Override
                    public <R> Response<R> callApi(Request request) {
                        return tradeMarketConfigApi.putTradeMarketUiConfig(request);
                    }
                });
                log.info("::{}::RPC调用[提前结算开关]响应参数::{}", linkId, JSON.toJSONString(responseConfig));
                //1852发送开关状态给融合
                distanceSwitchServerImpl.sendDistanceSwitch(temp);
                rcsMatchTemplateModifyService.sendMatchPreStatus(temp, linkId);

                int matchType = standardMarketSellQueryVo.getMarketType().equals("PRE") ? 1 : 0;
                sendPlayStatusChangeMq(standardMarketSellQueryVo.getMarketCategoryIds(), temp, linkId, matchType);
                //发送提前结算玩法，开关封锁默认为开
                tradeStatusService.sendPlayStatusChangeMq(linkId, FOOTBALL_EARLY_SETTLEMENT_PLAY,
                        standardMarketSellQueryVo.getMatchId(),
                        standardMarketSellQueryVo.getSportId(), TradeStatusEnum.OPEN.getStatus());
            }
            tradeModeService.basketballPlaySaleSwitchLinkage(standardMarketSellQueryVo.getSportId(), standardMarketSellQueryVo.getMatchId(), standardMarketSellQueryVo.getMarketCategoryIds(), LinkedTypeEnum.PLAY_SALE);

            //早盘赛事关盘处理&&
            if (Arrays.asList("PRE","LIVE").contains(standardMarketSellQueryVo.getMarketType())){
                String key = String.format("rcs:trade:match:sell:key:matchId:%s", standardMarketSellQueryVo.getMatchId());
                String marketType = redisClient.get(key);

                if(StringUtils.isBlank(marketType)){
                    if("PRE".equals(standardMarketSellQueryVo.getMarketType())){
                        //保存75天
                        redisClient.setExpiry(key,"PRE", TimeUnit.DAYS.toSeconds(75));
                        StandardMatchInfo standardMatchInfo = standardMatchInfoService.selectById(standardMarketSellQueryVo.getMatchId());
                       // MatchMarketLiveBean matchInfo = mongoDbService.getMatchInfo(standardMarketSellQueryVo.getMatchId(), 0);
                        log.info("::{}::开售赛事的操盘方::{}",standardMatchInfo.getId(),standardMatchInfo.getPreRiskManagerCode());
                        if("OTS".equals(standardMatchInfo.getPreRiskManagerCode())){
                            rcsStandardSportMarketSellService.confirmStandardMarketSellThenOpen(standardMarketSellQueryVo,linkId);
                            return;
                        }

                    }
                    rcsStandardSportMarketSellService.confirmStandardMarketSellThenClose(standardMarketSellQueryVo,linkId);
                }
            }
        }
    }

    public TradeMarketUiConfigDTO getCommonClass(RcsTournamentTemplate temp) {
        ConfigCashOutTradeItemDTO cashOutTradeItemDTO = new ConfigCashOutTradeItemDTO();
        cashOutTradeItemDTO.setMatchId(temp.getTypeVal());
        cashOutTradeItemDTO.setMatchPreStatus(temp.getMatchPreStatus());
        cashOutTradeItemDTO.setPendingOrderStatus(temp.getPendingOrderStatus());
        cashOutTradeItemDTO.setMarketType(temp.getMatchType());
        cashOutTradeItemDTO.setDataSourceCode(CommonUtil.getDataSourceCode(temp.getEarlySettStr()));
        TradeMarketUiConfigDTO dto = new TradeMarketUiConfigDTO();
        dto.setConfigCashOutTradeItemDTO(cashOutTradeItemDTO);
        dto.setStandardMatchInfoId(temp.getTypeVal());
        return dto;
    }

    /**
     * 2519-提前结算优化-开售时将玩法级margin配置发送业务
     * @param marketCategoryIds 开售的玩法
     * @param temp 赛事模板
     * @param linkId
     * @param matchType 1：早盘  0：滚球
     */
    private void sendPlayStatusChangeMq(List<Long> marketCategoryIds, RcsTournamentTemplate temp, String linkId, int matchType){
        log.info("::{}::{}::开售-提前结算::matchType::{}", linkId, temp.getTypeVal(), matchType);
        if(matchType == 0){
            //滚球开售没有给玩法，所以这里用所有支持提前结算的玩法
            marketCategoryIds = CollUtil.newCopyOnWriteArrayList(TradeConstant.FOOTBALL_EARLY_SETTLEMENT_PLAY);
        }
        if(CollUtil.isEmpty(marketCategoryIds)){
            log.info("::{}::{}::开售-提前结算::玩法空", linkId, temp.getTypeVal());
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getById(temp.getTypeVal());

        for(Long playId: marketCategoryIds){
            //2.根据模板ID和玩法ID查询marginId
            RcsTournamentTemplatePlayMargain playMargain =
                    rcsTournamentTemplatePlayMargainService.get(temp.getId(), playId, matchType);
            if(ObjectUtil.isNull(playMargain)){
                log.info("::{}::{}::开售-提前结算::玩法级同步::playMargain is null::tempId::{}::{}",
                        linkId, temp.getTypeVal(), temp.getId(), playId + "_" + matchType);
                continue;
            }
            RcsTournamentTemplatePlayMargainRef lastMargainRef = null;
            Date beginTime = DateUtil.date(standardMatchInfo.getBeginTime());
            Date nowTime = new Date();

            long timeVal = DateUtil.between(beginTime, nowTime, DateUnit.SECOND);

            if(matchType == 0){
                //滚球时用secondsMatchStart字段
                timeVal = standardMatchInfo.getSecondsMatchStart();
            }
            TournamentTemplatePlayMargainRefParam param = new TournamentTemplatePlayMargainRefParam();
            param.setMargainId(playMargain.getId());
            param.setTimeVal(timeVal);
            param.setMatchType(matchType);
            if (param.getMatchType().equals(MatchTypeEnum.EARLY.getId())) {
                lastMargainRef = playMargainRefMapper.selectPreCurrtPlayMargainRef(param);
            } else if (param.getMatchType().equals(MatchTypeEnum.LIVE.getId())) {
                lastMargainRef = playMargainRefMapper.selectLiveCurrPlayMargainRef(param);
            }
            if(lastMargainRef == null){
                log.info("::{}::{}::开售-提前结算::玩法级同步::margin is null::marginId::{}::{}",
                        linkId, temp.getTypeVal(), playMargain.getId(), param);
                continue;
            }

            if(!TradeConstant.FOOTBALL_EARLY_SETTLEMENT_PLAY.contains(playId)){
                log.info("::{}::{}::开售-提前结算::分时节点::提前结算::非提前结算玩法::{}",
                        linkId, temp.getTypeVal(), playMargain.getPlayId());
                continue;
            }
            log.info("::{}::{}::开售-提前结算::分时节点::提前结算", linkId, temp.getTypeVal());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sportId", temp.getSportId());
            jsonObject.put("matchId", temp.getTypeVal());
            jsonObject.put("playId", playMargain.getPlayId());
            jsonObject.put("updateTime", System.currentTimeMillis());
            jsonObject.put("linkId", linkId);
            jsonObject.put("categoryPreStatus", lastMargainRef.getCategoryPreStatus());
            if (MarketKindEnum.Europe.getValue().equalsIgnoreCase(playMargain.getMarketType())) {
                //欧赔没有spread,使用的是cashOutMargin
                jsonObject.put("spread", "0.0");
            } else {
                jsonObject.put("spread", lastMargainRef.getMargain());
            }
            jsonObject.put("cashOutMargin", lastMargainRef.getCashOutMargin());
            String tag = temp.getTypeVal() + "_" + playMargain.getPlayId();
            producerSendMessageUtils.sendMessage("RCS_MATCH_CATEGORY_CONFIG_NOTIFY", tag, linkId, jsonObject);
        }
    }
}
