package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.mapper.RcsOddsConvertMappingMapper;
import com.panda.rcs.logService.mapper.RcsTournamentTemplatePlayMargainMapper;
import com.panda.rcs.logService.mapper.StandardSportMarketOddsMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.LanguageInternation;
import com.panda.rcs.logService.vo.RcsTournamentTemplatePlayMargain;
import com.panda.rcs.logService.vo.StandardSportMarketOdds;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.IntStream;

/**
 * 操盤日誌(updateMarketOddsValue)
 * 調整賠率格式化類別
 */

@Slf4j
@Component
public class LogOddsChangeFormat extends LogFormatStrategy<Object> {
    @Autowired
    private  RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private RcsTournamentTemplatePlayMargainMapper rcsTournamentTemplatePlayMargainMapper;

    @Autowired
    private RcsOddsConvertMappingMapper rcsOddsConvertMappingMapper;
    @Autowired
    private StandardSportMarketOddsMapper standardSportMarketOddsMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean config ) {
        if(BaseUtils.isTrue(config.getBeforeParams())){
            return null;
        }
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
    private RcsOperateLog prematchFormat(RcsOperateLog rcsOperateLog, LogAllBean config) {
        LogAllBean before= BaseUtils.mapObject(config.getBeforeParams(),LogAllBean.class);
        //操盤主畫面 調整賠率傳入oddsChange會除100，在此還原，調水差不會
        if (!Objects.nonNull(before.getMarketDiffValue())) {
            config.setOddsChange(config.getOddsChange().multiply(new BigDecimal("100").setScale(0, RoundingMode.DOWN)));
        }
        return filterChangeType(rcsOperateLog, config);
    }

    /**
     * 次要玩法資料轉換
     */
    private RcsOperateLog subPlayFormat(RcsOperateLog rcsOperateLog, LogAllBean config) {
        LogAllBean before= BaseUtils.mapObject(config.getBeforeParams(),LogAllBean.class);
        //操盤主畫面 調整賠率傳入oddsChange會除100，在此還原，調水差不會
        if (!Objects.nonNull(before.getMarketDiffValue())) {
            config.setOddsChange(config.getOddsChange().multiply(new BigDecimal("100").setScale(0, RoundingMode.DOWN)));
        }
        return filterChangeType(rcsOperateLog, config);
    }

    /**
     * 判斷調水差還調賠率
     */
    private RcsOperateLog filterChangeType(RcsOperateLog rcsOperateLog, LogAllBean config) {
        LogAllBean before= BaseUtils.mapObject(config.getBeforeParams(),LogAllBean.class);
        rcsOperateLog.setOperatePageCode(config.getOperatePageCode());
        rcsOperateLog.setMatchId(config.getMatchId());
        rcsOperateLog.setPlayId(config.getPlayId());

        String matchName = montageEnAndZsIs(config.getTeamList(),config.getMatchId());

        //區別調整水差
        if (Objects.nonNull(before.getMarketDiffValue())) {
            return marketDiffChangeFormat(rcsOperateLog, config, matchName);
        } else {
            return oddsChangeFormat(rcsOperateLog, config, before, matchName);
        }
    }

    /**
     * 調整賠率格式
     */
    private RcsOperateLog oddsChangeFormat(RcsOperateLog rcsOperateLog, LogAllBean config,
                                           LogAllBean origConfig, String matchName) {
        //取出低賠index
        List<Map<String, Object>> oddsList = origConfig.getOddsList();
        int minOddsIndex = getMinimumOddsIndex(oddsList);
        if (minOddsIndex == -1)
            return null;
        // 紀錄賠率異動
        Map<String, Object> oddsMap = oddsList.get(minOddsIndex);
        log.info("操盤日誌-調賠率-oddsType:b:{},a:{},比對結果:{}", origConfig.getOddsType(), config.getOddsType(), oddsMap.get("oddsType"));
        BigDecimal nameExpressionValue =BigDecimal.ZERO;
        if(Objects.nonNull(oddsMap)&&Objects.nonNull(oddsMap.get("nameExpressionValue"))) {
            nameExpressionValue = new BigDecimal(oddsMap.get("nameExpressionValue").toString());
        }
        // 取得投注項 ID
        String objectId = getObjectId(origConfig.getOddsType(), oddsList);
        //String playName = getPlayName(config.getPlayId(), config.getSportId());
        String marketValue = transMarketValue(nameExpressionValue.abs());
        StringBuilder objectName = new StringBuilder().append(origConfig.getOddsType()).append(" (").append(marketValue).append(")");
        StringBuilder extObjectId = new StringBuilder().append(config.getMatchManageId()).append(" / ").append(config.getPlayId()).append(" / ").append(config.getMarketId());
        StringBuilder extObjectName = new StringBuilder(getMatchName(config.getTeamList(),config.getMatchId()))
                .append(" / ").append(getPlayNameZs(config.getPlayId(), config.getSportId())).append(" / ").append(nameExpressionValue);
        StringBuilder extObjectNameEn = new StringBuilder(getMatchNameEn(config.getTeamList(),config.getMatchId()))
                .append(" / ").append(getPlayNameEn(config.getPlayId(), config.getSportId())).append(" / ").append(nameExpressionValue);
        rcsOperateLog.setObjectIdByObj(config.getPlayId());
        rcsOperateLog.setObjectNameByObj(objectName);
        rcsOperateLog.setExtObjectIdByObj(extObjectId);
        rcsOperateLog.setExtObjectNameByObj(montageEnAndZs(extObjectNameEn.toString(),extObjectName.toString()));
        rcsOperateLog.setBeforeValByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setAfterValByObj(config.getOddsChange().setScale(0, RoundingMode.DOWN).toPlainString());
        if(config.getSportId().equals(2)||config.getSportId().equals(1)){
            Map<String, Object> updateMaps = updateInt(origConfig.getOddsType(), oddsList);
            String beforeValByObj=updateMaps.get("fieldOddsValue").toString();

            if(origConfig.getMarketType().equals("MY")){
                String oldValue=beforeValByObj;
                beforeValByObj=rcsOddsConvertMappingMapper.queryEurope(beforeValByObj);
               if(StringUtils.isEmpty(beforeValByObj)){beforeValByObj=oldValue;}
                log.info("操盤日誌-調賠率值:{}",beforeValByObj);
            }
            rcsOperateLog.setBeforeValByObj(beforeValByObj);
            RcsTournamentTemplatePlayMargain template = getRcsTournamentTemplateConfig(config);
            BigDecimal AfterValByObj= NumberUtils.createBigDecimal(beforeValByObj).add(template==null?BigDecimal.valueOf(0.02):template.getOddsAdjustRange());
            if(origConfig.getMarketType().equals("MY")){
                rcsOperateLog.setAfterValByObj(AfterValByObj.doubleValue()>1?AfterValByObj.subtract(BigDecimal.valueOf(2L)):AfterValByObj);
            }else{
                rcsOperateLog.setAfterValByObj(setAfterValByObj(rcsOperateLog.getOperateTime().getTime()
                        ,origConfig.getOddsType(),origConfig.getMarketId()));
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
    public RcsTournamentTemplatePlayMargain getRcsTournamentTemplateConfig(LogAllBean config) {
        if (NumberUtils.INTEGER_TWO.intValue() == config.getMatchType()) {
            config.setMatchType(NumberUtils.INTEGER_ZERO);
        }
        // 1.查询变化幅度
        RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargin = rcsTournamentTemplatePlayMargainMapper.queryTournamentAdjustRangeByPlayId(config);
        if (ObjectUtils.isEmpty(rcsTournamentTemplatePlayMargin) ||
                ObjectUtils.isEmpty(rcsTournamentTemplatePlayMargin.getMarketAdjustRange())) {
            throw new RcsServiceException("没有找到联赛配置");
        }
        return rcsTournamentTemplatePlayMargin;
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


    public BigDecimal setAfterValByObj(Long date,String oddsType,Long marketId){
            //根据marketId查询对应盘口所有赔率
        BigDecimal value=BigDecimal.ZERO;
        try{
            Thread.sleep(2000);
            StandardSportMarketOdds matchMarketOdds = standardSportMarketOddsMapper.queryByOddsTypeAndDateAndMarketId(oddsType,marketId);
            if(null!=matchMarketOdds){
                value=new BigDecimal(matchMarketOdds.getOddsValue()).divide(new BigDecimal(100000)).setScale(2,RoundingMode.DOWN);
            }

        }catch (Exception e){

        }
            return value;


    }


    /**
     * 水差調整 格式
     */
    private RcsOperateLog marketDiffChangeFormat(RcsOperateLog rcsOperateLog,LogAllBean config, String matchName) {
        List<Map<String, Object>> oddsList = config.getOddsList();
        //盤口值
        String marketValueView = "";
        for (Map<String, Object> oddsMap : oddsList) {
            BigDecimal nameExpressionValue = new BigDecimal(Optional.ofNullable(oddsMap.get("nameExpressionValue")).orElse("0").toString());
            marketValueView = transMarketValue(nameExpressionValue.abs());
            break;
        }
        LogAllBean before= BaseUtils.mapObject(config.getBeforeParams(),LogAllBean.class);
        if (Objects.nonNull(config.getOddsChange())) {
            BigDecimal marketDiffValue = before.getMarketDiffValue().setScale(0, RoundingMode.DOWN);
            BigDecimal oddsChange = config.getOddsChange().setScale(0, RoundingMode.DOWN);
            if (oddsChange.compareTo(marketDiffValue) != 0) {
                // 取得投注項 ID
                String objectId = getAwayBetInfo(config.getOddsList(), "id");
                String oddsType = getAwayBetInfo(oddsList, "oddsType");
                //String playName = getPlayName(config.getPlayId(), config.getSportId());
                StringBuilder objectName = new StringBuilder().append(oddsType).append(" (").append(marketValueView).append(")");
                StringBuilder extObjectId = new StringBuilder().append(config.getMatchManageId()).append(" / ").append(config.getPlayId()).append(" / ").append(config.getMarketId());
                StringBuilder extObjectName = new StringBuilder(getMatchName(config.getTeamList(),config.getMatchId()))
                        .append(" / ").append(getPlayNameZs(config.getPlayId(), config.getSportId())).append(" / ").append(marketValueView);
                StringBuilder extObjectNameEn = new StringBuilder(getMatchNameEn(config.getTeamList(),config.getMatchId()))
                        .append(" / ").append(getPlayNameEn(config.getPlayId(), config.getSportId())).append(" / ").append(marketValueView);
                rcsOperateLog.setObjectIdByObj(objectId);
                rcsOperateLog.setObjectNameByObj(objectName);
                rcsOperateLog.setExtObjectIdByObj(extObjectId);
                rcsOperateLog.setExtObjectNameByObj(montageEnAndZs(extObjectNameEn.toString(),extObjectName.toString()));
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



