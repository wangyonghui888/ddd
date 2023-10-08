package com.panda.sport.rcs.trade.strategy.logFormat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.mapper.RcsOddsConvertMappingMapper;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.constants.TradeConstant;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.trade.service.TradeCommonService;
import com.panda.sport.rcs.trade.service.TradeMarketSetServiceImpl;
import com.panda.sport.rcs.trade.service.TradeOddsCommonService;
import com.panda.sport.rcs.trade.service.TradeVerificationService;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.trade.wrapper.RcsTradeConfigService;
import com.panda.sport.rcs.trade.wrapper.impl.MatchTradeConfigServiceImpl;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplatePlayMargainService;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import com.panda.sport.rcs.utils.MarketUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 操盤日誌(updateMarketOddsValue)
 * 調整賠率格式化類別
 */

@Slf4j
@Service
public class LogOddsChangeFormat extends LogFormatStrategy<Object> {
    private final RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private IRcsTournamentTemplatePlayMargainService rcsTournamentTemplatePlayMargainService;

    @Autowired
    private RcsOddsConvertMappingMapper rcsOddsConvertMappingMapper;

    @Autowired
    private IRcsMatchMarketConfigService rcsMatchMarketConfigService;
    @Autowired
    private RcsTradeConfigService rcsTradeConfigService;

    @Autowired
    private TradeCommonService tradeCommonService;

    @Autowired
    private TradeMarketSetServiceImpl tradeMarketSetService;

    @Autowired
    private TradeOddsCommonService tradeOddsCommonService;
    @Autowired
    private MatchTradeConfigServiceImpl matchTradeConfigService;

    @Autowired
    private TradeVerificationService tradeVerificationService;

    @Autowired
    public LogOddsChangeFormat(RcsLanguageInternationMapper rcsLanguageInternationMapper) {
        this.rcsLanguageInternationMapper = rcsLanguageInternationMapper;
    }

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, Object[] args) {
        RcsMatchMarketConfig config = (RcsMatchMarketConfig) args[0];

        //根據不同操作頁面組裝不同格式
        switch (config.getOperatePageCode()) {
            case 14: //早盤操盤
            case 17: //滾球操盤
                return prematchFormat(rcsOperateLog, config);
            case 15: //早盤操盤 次要玩法
            case 18: //滾球操盤 次要玩法
                return subPlayFormat(rcsOperateLog, config);
            case 102:
            case 103:
                //在LogUpdateMarketConfigFormat中處裡
                break;
        }

        return null;
    }

    /**
     * 早盤資料轉換
     */
    private RcsOperateLog prematchFormat(RcsOperateLog rcsOperateLog, RcsMatchMarketConfig config) {
        //操盤主畫面 調整賠率傳入oddsChange會除100，在此還原，調水差不會
        if (!Objects.nonNull(config.getBeforeParams().getMarketDiffValue())) {
            config.setOddsChange(config.getOddsChange().multiply(new BigDecimal("100").setScale(0, RoundingMode.DOWN)));
        }
        return filterChangeType(rcsOperateLog, config);
    }

    /**
     * 次要玩法資料轉換
     */
    private RcsOperateLog subPlayFormat(RcsOperateLog rcsOperateLog, RcsMatchMarketConfig config) {
        config.setOddsChange(config.getOddsChange().multiply(new BigDecimal("100").setScale(0, RoundingMode.DOWN)));
        return filterChangeType(rcsOperateLog, config);
    }

    /**
     * 判斷調水差還調賠率
     */
    private RcsOperateLog filterChangeType(RcsOperateLog rcsOperateLog, RcsMatchMarketConfig config) {
        RcsMatchMarketConfig oriConfig = config.getBeforeParams();
        rcsOperateLog.setOperatePageCode(config.getOperatePageCode());
        rcsOperateLog.setMatchId(config.getMatchId());
        rcsOperateLog.setPlayId(config.getPlayId());

        String matchName = getMatchName(config.getTeamList());

        //區別調整水差
        if (Objects.nonNull(config.getBeforeParams().getMarketDiffValue())) {
            return marketDiffChangeFormat(rcsOperateLog, config, matchName);
        } else {
            return oddsChangeFormat(rcsOperateLog, config, oriConfig, matchName);
        }
    }

    /**
     * 調整賠率格式
     */
    private RcsOperateLog oddsChangeFormat(RcsOperateLog rcsOperateLog, RcsMatchMarketConfig config,
                                           RcsMatchMarketConfig origConfig, String matchName) {
        //取出低賠index
        List<Map<String, Object>> oddsList = origConfig.getOddsList();
        int minOddsIndex = getMinimumOddsIndex(oddsList);
        if (minOddsIndex == -1)
            return null;
        // 紀錄賠率異動
        Map<String, Object> oddsMap = oddsList.get(minOddsIndex);
        log.info("操盤日誌-調賠率-oddsType:b:{},a:{},比對結果:{}", origConfig.getOddsType(), config.getOddsType(), oddsMap.get("oddsType"));
        BigDecimal nameExpressionValue = new BigDecimal(Optional.ofNullable(oddsMap.get("nameExpressionValue")).orElse("0").toString());
        // 取得投注項 ID
        String objectId = getObjectId(origConfig.getOddsType(), oddsList);
        String playName = getPlayName(config.getPlayId(), config.getSportId());
        String marketValue = transMarketValue(nameExpressionValue.abs());
        StringBuilder objectName = new StringBuilder().append(origConfig.getOddsType()).append(" (").append(marketValue).append(")");
        StringBuilder extObjectId = new StringBuilder().append(config.getMatchManageId()).append(" / ").append(config.getPlayId()).append(" / ").append(config.getMarketId());
        StringBuilder extObjectName = new StringBuilder(matchName).append(" / ").append(playName).append(" / ").append(nameExpressionValue);
        rcsOperateLog.setObjectIdByObj(objectId);
        rcsOperateLog.setObjectNameByObj(objectName);
        rcsOperateLog.setExtObjectIdByObj(extObjectId);
        rcsOperateLog.setExtObjectNameByObj(extObjectName);
        rcsOperateLog.setBeforeValByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setAfterValByObj(config.getOddsChange().setScale(0, RoundingMode.DOWN).toPlainString());
        if(config.getSportId().equals(2)){
            Map<String, Object> updateMaps = updateInt(origConfig.getOddsType(), oddsList);
            String beforeValByObj=updateMaps.get("fieldOddsValue").toString();
            if(origConfig.getMarketType().equals("MY")){
                beforeValByObj=rcsOddsConvertMappingMapper.queryEurope(beforeValByObj);
               if(StringUtils.isEmpty(beforeValByObj)){beforeValByObj="1";}
                rcsOperateLog.setBeforeValByObj(beforeValByObj);
            }
            RcsTournamentTemplatePlayMargain template = rcsTournamentTemplatePlayMargainService.getRcsTournamentTemplateConfig(config);
            BigDecimal AfterValByObj= NumberUtils.createBigDecimal(beforeValByObj).add(template==null?BigDecimal.valueOf(0.02):template.getOddsAdjustRange());
            if(origConfig.getMarketType().equals("MY")){
                rcsOperateLog.setAfterValByObj(AfterValByObj.doubleValue()>1?AfterValByObj.subtract(BigDecimal.valueOf(2L)):AfterValByObj);
            }
        }
        log.warn("rcsOperateLog={}", rcsOperateLog);
        return rcsOperateLog;
    }

    public Map<String, Object> updateInt(String oddsType, List<Map<String, Object>> oddsList){
       for (Map<String, Object> map : oddsList) {
           if(map.get("oddsType").equals(oddsType)){
               return map;
           }
       }
      return null;
    }



    /**
     *
     * @param
     * @param
     * @return
     */
    public String setBeforeValByObj(String europe){
        return rcsOddsConvertMappingMapper.queryEurope(europe) ;
    }

    public BigDecimal setAfterValByObj(RcsMatchMarketConfig config,List<Map<String, Object>> oddsList){
        // 获取水差
        RcsMatchMarketMarginConfig marketMarginConfig = rcsMatchMarketConfigService.getMarketWaterDiff(config);
        RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargin = rcsTournamentTemplatePlayMargainService.getRcsTournamentTemplateConfig(config);
        //根据marketId查询对应盘口所有赔率
        List<StandardSportMarketOdds> oddsVoList = tradeOddsCommonService.getMatchMarketOdds(config);
        // 获取受让方
        String oddsType = "";
        if (NumberUtils.INTEGER_ONE.intValue() == marketMarginConfig.getSportId()) {
            config.setRelevanceType(NumberUtils.INTEGER_ZERO);
            oddsType = tradeVerificationService.getOddsType(oddsVoList);
        }else{
            // 获取玩法投注项
            oddsType = tradeVerificationService.getBasketBallUnderOddsType(oddsVoList.get(NumberUtils.INTEGER_ZERO).getOddsType());
        }
        setAutoRatioAndMargin(config,marketMarginConfig,oddsType,rcsTournamentTemplatePlayMargin);
        BigDecimal marketDiffValue=BigDecimal.ZERO;
        for (Map<String, Object> map : oddsList) {
            if(Objects.nonNull(map.get("marketDiffValue"))){
                marketDiffValue=NumberUtils.createBigDecimal(map.get("marketDiffValue").toString());
            }
        }
       return NumberUtils.createBigDecimal(config.getAwayAutoChangeRate()).subtract(marketDiffValue);
    }

    /**
     * @return java.lang.String
     * @Description //计算水差或者margin
     * @Param [config, marketMarginConfig]
     * @Author sean
     * @Date 2020/12/3
     **/
    private String setAutoRatioAndMargin(RcsMatchMarketConfig config, RcsMatchMarketMarginConfig marketMarginConfig, String oddsType, RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargin) {
        // 球头改变独赢关盘
        BigDecimal oddsAdjustRange = rcsTournamentTemplatePlayMargin.getOddsAdjustRange();
        oddsAdjustRange = oddsAdjustRange.multiply(config.getOddsChange());
        BigDecimal autoRatio = new BigDecimal(marketMarginConfig.getAwayAutoChangeRate());
        String msg = setAutoRatio(config, autoRatio, oddsAdjustRange, oddsType, rcsTournamentTemplatePlayMargin.getOddsMaxValue());
        return msg;
    }

    /**
     * @return void
     * @Description 计算自动水差
     * @Param [config]
     * @Author Sean
     * @Date 15:01 2020/10/7
     **/
    public String setAutoRatio(RcsMatchMarketConfig config, BigDecimal autoRatio, BigDecimal oddsAdjustRange, String oddsType, BigDecimal oddsMaxValue) {
        String msg = null;
        if (StringUtils.isNotEmpty(oddsType)) {
            if (StringUtils.isEmpty(config.getOddsType())) {
                if (ObjectUtils.isEmpty(config.getOddsChange())) {
                    config.setOddsChange(new BigDecimal(NumberUtils.DOUBLE_ZERO));
                }
                autoRatio = config.getOddsChange().divide(new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE), NumberUtils.INTEGER_TWO, BigDecimal.ROUND_DOWN);
            } else {
                if (oddsType.equalsIgnoreCase(config.getOddsType())) {
                    autoRatio = autoRatio.add(oddsAdjustRange);
                } else {
                    autoRatio = autoRatio.subtract(oddsAdjustRange);
                }
            }
            if (autoRatio.abs().compareTo(oddsMaxValue) == NumberUtils.INTEGER_ONE){
                msg = String.format(TradeConstant.ODDS_OUT_OF_LIMIT,oddsMaxValue.negate().toPlainString(),oddsMaxValue.toPlainString(),autoRatio.toPlainString());
            }
            config.setAwayAutoChangeRate(autoRatio.toString());
            log.info("::{}::,计算水差完成config={}", CommonUtil.getRequestId(config.getMatchId(), config.getPlayId()),JSONObject.toJSONString(config));
        }
        return msg;
    }

    /**
     * 水差調整 格式
     */
    private RcsOperateLog marketDiffChangeFormat(RcsOperateLog rcsOperateLog, RcsMatchMarketConfig config, String matchName) {
        List<Map<String, Object>> oddsList = config.getOddsList();
        //盤口值
        String marketValueView = "";
        for (Map<String, Object> oddsMap : oddsList) {
            BigDecimal nameExpressionValue = new BigDecimal(Optional.ofNullable(oddsMap.get("nameExpressionValue")).orElse("0").toString());
            marketValueView = transMarketValue(nameExpressionValue.abs());
            break;
        }

        if (Objects.nonNull(config.getOddsChange())) {
            BigDecimal marketDiffValue = config.getBeforeParams().getMarketDiffValue().setScale(0, RoundingMode.DOWN);
            BigDecimal oddsChange = config.getOddsChange().setScale(0, RoundingMode.DOWN);
            if (oddsChange.compareTo(marketDiffValue) != 0) {
                // 取得投注項 ID
                String objectId = getAwayBetInfo(config.getOddsList(), "id");
                String oddsType = getAwayBetInfo(oddsList, "oddsType");
                String playName = getPlayName(config.getPlayId(), config.getSportId());
                StringBuilder objectName = new StringBuilder().append(oddsType).append(" (").append(marketValueView).append(")");
                StringBuilder extObjectId = new StringBuilder().append(config.getMatchManageId()).append(" / ").append(config.getPlayId()).append(" / ").append(config.getMarketId());
                StringBuilder extObjectName = new StringBuilder(matchName).append(" / ").append(playName).append(" / ").append(marketValueView);
                rcsOperateLog.setObjectIdByObj(objectId);
                rcsOperateLog.setObjectNameByObj(objectName);
                rcsOperateLog.setExtObjectIdByObj(extObjectId);
                rcsOperateLog.setExtObjectNameByObj(extObjectName);
                rcsOperateLog.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                rcsOperateLog.setParameterName(OperateLogEnum.MARKET_DIFF.getName());

                rcsOperateLog.setBeforeValByObj(marketDiffValue.toPlainString());
                rcsOperateLog.setAfterValByObj(oddsChange.toPlainString());
                return rcsOperateLog;
            }
        }

        return null;
    }

    /**
     * 查詢玩法名稱
     */
    private String getPlayName(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
    }

    /**
     * 從客隊投注項取出對應參數 (目前僅調水差會使用)
     */
    private static String getAwayBetInfo(List<Map<String, Object>> list, String key) {
        Map<String, Object> tempMap = list.get(list.size() - 1);
        return String.valueOf(tempMap.getOrDefault(key, ""));
    }

    /**
     * 查表找出低賠index
     */
    private static int getMinimumOddsIndex(List<Map<String, Object>> oddsList) {
        if(CollectionUtils.isEmpty(oddsList)){
            return -1;
        }
        return IntStream.range(0, oddsList.size()).boxed()
            .min(Comparator.comparing(i -> new BigDecimal(Optional.ofNullable(oddsList.get(i).get("paOddsValue")).orElse("0").toString())))
            .orElse(-1);
    }

    /**
     * 取得投注項 ID
     * @param oddsType config 裡傳來的 oddsType，需 -1 後才能套用在 oddsList 上
     * @param oddsList 由 List 包裝起來的 OddsMap，內含的 ID 則為投注項 ID
     * @return 由 config 下的 oddsType 指定的投注項 ID
     */
    private static String getObjectId(String oddsType, List<Map<String, Object>> oddsList) {
        try {
            int idx = Integer.parseInt(oddsType) - 1;
            return Optional.ofNullable(oddsList.get(idx).get("id")).orElse("").toString();
        } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
            return "";
        }
    }
}



