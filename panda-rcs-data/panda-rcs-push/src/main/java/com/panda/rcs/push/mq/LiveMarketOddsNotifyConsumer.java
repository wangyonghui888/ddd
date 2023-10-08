package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.rcs.push.cache.MatchInfoCache;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.constant.BaseConstant;
import com.panda.rcs.push.entity.enums.MatchTypeEnums;
import com.panda.rcs.push.entity.enums.SportEnum;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.LiveStandardMarketMessageVO;
import com.panda.rcs.push.entity.vo.LiveStandardMarketOddsMessageVO;
import com.panda.rcs.push.entity.vo.LiveStandardMatchMarketMessageVO;
import com.panda.rcs.push.entity.vo.MatchPlayCacheVo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.rcs.push.utils.Gzip;

import com.panda.sport.rcs.common.OddsValueConvertUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.mongo.I18nBean;
import com.panda.sport.rcs.utils.NameExpressionValueUtils;
import com.panda.sport.rcs.utils.PlayTemplateUtils;
import com.panda.sport.rcs.utils.TwoLevelCacheUtil;
import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.wrapper.LanguageInternationalService;
import com.panda.sport.rcs.wrapper.RcsOddsConvertMappingService;
import com.panda.sport.rcs.wrapper.RcsTradeConfigService;
import com.panda.sport.rcs.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.wrapper.StandardSportTeamService;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 标准赔率推送
 * Topic=STANDARD_MARKET_ODDS
 * Group=RCS_PUSH_STANDARD_MARKET_ODDS_GROUP
 * 对应指令-> 30005
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "STANDARD_MARKET_ODDS",
        consumerGroup = "RCS_PUSH_STANDARD_MARKET_ODDS_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class LiveMarketOddsNotifyConsumer implements RocketMQListener<Request<LiveStandardMatchMarketMessageVO>>, RocketMQPushConsumerLifecycleListener {

    //public static final String WS_MATCH_COMPARE = "wsMatchCompare";

    private static final String RCS_WS_KEY_CACHE_KEY = RedisKeys.RCS_WS_KEY_CACHE_KEY;
    private static final String REDIS_CACHE_CATEGORY_KEY = "rcs:category_name_code:%s:%s";
    private static final String CATEGORY_LANGUAGE = "categoryLanguage:";

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_ODDS;

    @Autowired
    RcsOddsConvertMappingService rcsOddsConvertMappingService;

    @Autowired
    TwoLevelCacheUtil twoLevelCacheUtil;

    @Autowired
    private StandardSportTeamService standardSportTeamService;

    @Autowired
    private StandardSportMarketService standardSportMarketService;

    @Autowired
    private LanguageInternationalService languageInternationalService;

    @Autowired
    private ClientManageService clientManageService;

    @Autowired
    private RcsTradeConfigService rcsTradeConfigService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(96);
        defaultMQPushConsumer.setConsumeThreadMax(128);
    }

    private boolean msgCheck(Request<LiveStandardMatchMarketMessageVO> msg) {
        String linkId = msg.getLinkId();
        if ("HeartBeat".equalsIgnoreCase(msg.getDataType())) {
            log.warn("::{}::心跳数据不做处理！", linkId);
            return false;
        }

        Long dataSourceTime = msg.getDataSourceTime();
        if (dataSourceTime == null) {
            log.warn("::{}::没有带时间戳，当前数据不做更新！", linkId);
            return false;
        }

        LiveStandardMatchMarketMessageVO data = msg.getData();
        if (data == null) {
            log.warn("::{}::赔率数据为空！", linkId);
            return false;
        }

        Integer sportId = data.getSportId();
        Long matchId = data.getStandardMatchInfoId();
        if (CollectionUtils.isEmpty(data.getMarketList())) {
            log.warn("::{}::无盘口数据：matchId={}", linkId, matchId);
            return false;
        }

        if (!BaseConstant.TRADER_SPORT_LISTS.contains(sportId) && MatchTypeEnums.ATCH_TYPE_BASE.getKey().equals(data.getMatchType())) {
            log.info("::{}::标准赔率-球种未开放操盘，赔率不予推送", linkId);
            return false;
        }
        return true;
    }

    @Override
    public void onMessage(Request<LiveStandardMatchMarketMessageVO> msg) {
        if (!msgCheck(msg)) {
            return;
        }
        try {
            String linkId = msg.getLinkId();
            LiveStandardMatchMarketMessageVO matchMarketMessageVO = msg.getData();
            log.info("::{}::STANDARD_MARKET_ODDS标准赔率推送->赛事id{}", linkId, matchMarketMessageVO.getStandardMatchInfoId());
            Long dataSourceTime = msg.getDataSourceTime();
            Integer sportId = matchMarketMessageVO.getSportId();
            //组装实体
            List<LiveStandardMarketMessageVO> marketList = matchMarketMessageVO.getMarketList();
            if (CollectionUtils.isEmpty(marketList)) {
                marketList = new ArrayList<>();
            }
            MatchPlayCacheVo matchPlayCacheVo = MatchInfoCache.matchPlayMap.get(Long.toString(msg.getData().getStandardMatchInfoId()));
            LiveStandardMatchMarketMessageVO standardMatchMarketMessageVO = new LiveStandardMatchMarketMessageVO();
            standardMatchMarketMessageVO.setDataSourceTime(dataSourceTime);
            List<LiveStandardMarketMessageVO> newList = new ArrayList<>();
            standardMatchMarketMessageVO.setStandardMatchInfoId(matchMarketMessageVO.getStandardMatchInfoId());
            standardMatchMarketMessageVO.setStandardTournamentId(matchMarketMessageVO.getStandardTournamentId());
            standardMatchMarketMessageVO.setModifyTime(matchMarketMessageVO.getModifyTime());
            standardMatchMarketMessageVO.setDataSourceCode(matchMarketMessageVO.getDataSourceCode());
            standardMatchMarketMessageVO.setStatus(matchMarketMessageVO.getStatus());
            standardMatchMarketMessageVO.setMarketCategoryId(new HashSet<>());

            for (LiveStandardMarketMessageVO standardMarketMessageVO : marketList) {
                standardMarketMessageVO.setOddsMetric(standardMarketMessageVO.getPlaceNum());
                standardMarketMessageVO.setOldThirdMarketSourceStatus(Objects.isNull(standardMarketMessageVO.getOldThirdMarketSourceStatus()) ? 0 : standardMarketMessageVO.getOldThirdMarketSourceStatus());
                newList.add(standardMarketMessageVO);
            }

            if (CollectionUtils.isEmpty(newList)) {
                return;
            }

            // 需要被排出的玩法id集合
            Set<Long> excludeMarketCategoryId = new HashSet<>();

            for (LiveStandardMarketMessageVO market : newList) {
                if (matchPlayCacheVo != null && matchPlayCacheVo.getPlayUpdateTime() != null && matchPlayCacheVo.getPlayUpdateTime().get(market.getMarketCategoryId()) != null && msg.getDataSourceTime() < matchPlayCacheVo.getPlayUpdateTime().get(market.getMarketCategoryId())) {
                    log.info("::{}::数据已失效,matchId={},当前玩法={},缓存时间={},当前消费数据时间={}", msg.getLinkId(), matchMarketMessageVO.getStandardMatchInfoId(), market.getMarketCategoryId(), matchPlayCacheVo.getPlayUpdateTime().get(market.getMarketCategoryId()), msg.getDataSourceTime());
                    excludeMarketCategoryId.add(market.getMarketCategoryId());
                }
            }
            // 盘口对应的玩法所有的盘口都过滤掉
            newList = newList.stream().filter(t -> !excludeMarketCategoryId.contains(t.getMarketCategoryId())).collect(Collectors.toList());

            Long matchId = matchMarketMessageVO.getStandardMatchInfoId();
            Map<String, I18nBean> teamMap = standardSportTeamService.selectTeamsByMatchId(matchId);
            //设置盘口值
            for (LiveStandardMarketMessageVO market : newList) {
                Long playId = market.getMarketCategoryId();
                if (PlayTemplateUtils.isShowMarketName(playId)) {
                    market.setOddsName(getMarketName(String.valueOf(sportId), market, teamMap));
                }

                if (market.getThirdMarketSourceStatus() == null) {
                    market.setThirdMarketSourceStatus(TradeStatusEnum.OPEN.getStatus());
                }
                if (market.getPaStatus() == null) {
                    market.setPaStatus(TradeStatusEnum.OPEN.getStatus());
                }
                if (market.getPlaceNumStatus() == null) {
                    market.setPlaceNumStatus(TradeStatusEnum.OPEN.getStatus());
                }
                if (market.getStatus() == null) {
                    market.setStatus(TradeStatusEnum.OPEN.getStatus());
                }
                if(market.getStatus() != null && market.getThirdMarketSourceStatus() != TradeStatusEnum.SEAL.getStatus()){
                    if(market.getPlaceNumStatus() != market.getStatus()){
                        market.setPlaceNumStatus(market.getStatus());
                    }
                }
                standardMatchMarketMessageVO.getMarketCategoryId().add(String.valueOf(playId));
                market.setStandardMatchInfoId(standardMatchMarketMessageVO.getStandardMatchInfoId());
                List<LiveStandardMarketOddsMessageVO> marketOddsList = market.getMarketOddsList();
                if (CollectionUtils.isEmpty(marketOddsList)) {
                    continue;
                }
                for (LiveStandardMarketOddsMessageVO standardMarketOddsMessage : marketOddsList) {
                    String displayOddsVal = OddsValueConvertUtils.convertAndDefaultDisply(null, standardMarketOddsMessage.getPaOddsValue());
                    String nextLevelOdds = rcsOddsConvertMappingService.getNextLevelOdds(displayOddsVal);
                    standardMarketOddsMessage.setNextLevelOddsValue(nextLevelOdds);
                    standardMarketOddsMessage.setFieldOddsValue(displayOddsVal);
                    String nameExpressionValue = NameExpressionValueUtils.getNameExpressionValue(playId.intValue(), standardMarketOddsMessage.getOddsType(), market.getAddition1());
                    standardMarketOddsMessage.setNameExpressionValue(nameExpressionValue);
                    standardMarketOddsMessage.setOrderOdds(PlayTemplateUtils.getMarketOddsSortNo(playId, standardMarketOddsMessage.getOddsType(), standardMarketOddsMessage.getOrderOdds()));
                }
                marketOddsList = marketOddsList.stream().sorted(Comparator.comparingInt(LiveStandardMarketOddsMessageVO::getOrderOdds)).collect(Collectors.toList());
                market.setMarketOddsList(marketOddsList);
            }
            standardMatchMarketMessageVO.setMarketList(newList);

            List<String> playIds = new ArrayList<>(standardMatchMarketMessageVO.getMarketCategoryId());
            String msgId = UUID.randomUUID().toString();

            //斯诺克赔率进行排序
            if (SportEnum.SPORT_SNOOKER.getKey().equals(msg.getData().getSportId()) && null != msg.getData().getMarketList()) {
                msg.getData().getMarketList().stream().filter(m -> "204".equals(Long.toString(m.getMarketCategoryId())) && null != m.getMarketOddsList() && m.getMarketOddsList().size() > 0).collect(Collectors.toList()).forEach(ms -> {
                    Collections.reverse(ms.getMarketOddsList());
                });
            }
            if(null != matchMarketMessageVO.getMatchType() && matchMarketMessageVO.getMatchType() == 1){
                log.info("::{}::赛事标准赔率冠军玩法兜底前数据", msg.getLinkId());
                //兜底冠军玩法根据盘口数据源判断不正确问题
                String dataSource = standardMatchMarketMessageVO.getDataSourceCode();
                Long marketId = newList.get(0).getId();
                String[] marketTradeRecord = rcsTradeConfigService.getDataSource(msg.getLinkId(), matchId, marketId, dataSource);
                if (null != marketTradeRecord) {
                    standardMatchMarketMessageVO.getMarketList().forEach(market -> {
                        market.setDataSourceCode(marketTradeRecord[0]);
                        market.setStatus(Integer.valueOf(marketTradeRecord[1]));
                    });
                }
            }

            //缓存时间更新
            Map<Long, List<LiveStandardMarketMessageVO>> currMatchPlayGroupMap = msg.getData().getMarketList().stream().collect(Collectors.groupingBy(LiveStandardMarketMessageVO::getMarketCategoryId));
            if (matchPlayCacheVo == null) {
                matchPlayCacheVo = new MatchPlayCacheVo();
                matchPlayCacheVo.setMatchId(Long.toString(matchId));
                matchPlayCacheVo.setCreateTime(System.currentTimeMillis());
            }

            for (Map.Entry<Long, List<LiveStandardMarketMessageVO>> entry : currMatchPlayGroupMap.entrySet()) {
                if (matchPlayCacheVo.getPlayUpdateTime() != null && matchPlayCacheVo.getPlayUpdateTime().containsKey(entry.getKey()) && matchPlayCacheVo.getPlayUpdateTime().get(entry.getKey()) > msg.getDataSourceTime()) {
                    log.info("::{}::盘口赔率数据-最新消费数据小于缓存中时间，不予处理，当前玩法={}, 缓存时间={}，当前消费数据时间={}", msg.getLinkId(), entry.getKey(), matchPlayCacheVo.getPlayUpdateTime().get(entry.getKey()), msg.getDataSourceTime());
                    continue;
                }

                if (matchPlayCacheVo.getPlayUpdateTime() != null) {
                    matchPlayCacheVo.getPlayUpdateTime().put(entry.getKey(), msg.getDataSourceTime());
                } else {
                    Map<Long, Long> initMap = new HashMap<>();
                    initMap.put(entry.getKey(), msg.getDataSourceTime());
                    matchPlayCacheVo.setPlayUpdateTime(initMap);
                }
            }

            /*log.info("::{}::赛事标准赔率处理后数据->赛事id：{},玩法：{}", linkId,matchMarketMessageVO.getStandardMatchInfoId(), JSON.toJSONString(playIds));
            String respGzipStr = Gzip.compress(JSONObject.toJSONString(standardMatchMarketMessageVO));
            clientManageService.sendMessage(subscriptionEnums, Long.toString(matchId), playIds, ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), respGzipStr, 0, msg.getLinkId(), msgId, null));
*/

            MatchInfoCache.matchPlayMap.put(Long.toString(msg.getData().getStandardMatchInfoId()), matchPlayCacheVo);

        } catch (Exception e) {
            log.error("::{}::赛事标准赔率消费数据，异常信息：", msg.getLinkId(), e);
        }
    }

    private String getMarketName(String sportId, LiveStandardMarketMessageVO market, Map<String, I18nBean> teamMap) {
        Long playId = market.getMarketCategoryId();
        String categoryNameCode = getCategoryNameCode(sportId, String.valueOf(playId));
        I18nBean i18nBean = new I18nBean();
        if (StringUtils.isNotBlank(categoryNameCode)) {
            List<I18nItemVo> list = languageInternationalService.getCachedNamesByCode(Long.parseLong(categoryNameCode));
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(i18nItemVo -> {
                    String type = i18nItemVo.getLanguageType();
                    String text = i18nItemVo.getText();
                    if ("zs".equals(type)) {
                        i18nBean.setZs(text);
                    }
                    if ("en".equals(type)) {
                        i18nBean.setEn(text);
                    }
                    if ("jc".equals(type)) {
                        i18nBean.setJc(text);
                    }
                    if ("zh".equals(type)) {
                        i18nBean.setZh(text);
                    }
                });
            }
        }
        PlayTemplateUtils.handleMarketName(playId, i18nBean, teamMap, market.getAddition1(), market.getAddition2(), market.getAddition3());
        if (StringUtils.isNotBlank(i18nBean.getZs())) {
            return i18nBean.getZs();
        }
        if (StringUtils.isNotBlank(i18nBean.getEn())) {
            return i18nBean.getEn();
        }
        return market.getOddsName();
    }

    private String getCategoryNameCode(String sportId, String playId) {
        String key = CATEGORY_LANGUAGE+sportId+":"+playId;
        String categoryNameCode = twoLevelCacheUtil.get(key, key1 -> {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("marketCategoryId", playId);
            map.put("sportId", sportId);
            Map<String, Object> info = standardSportMarketService.queryMatchMarketInfo(map);
            if (info != null && info.containsKey("market_name_code")) {
                return String.valueOf(info.get("market_name_code"));
            }
            return "";
        });
        return categoryNameCode;
    }
}
