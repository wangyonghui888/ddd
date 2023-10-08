package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.Enum.SportIdEnum;
import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.mapper.StandardSportMarketMapper;
import com.panda.rcs.logService.mapper.StandardSportTeamMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.vo.*;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 操盤日誌(updateMarketOddsValue)
 * 調整賠率格式化類別
 */
@Component
@Slf4j
public class LogSubOddsChangeFormat extends LogFormatStrategy {

	@Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private StandardSportTeamMapper standardSportTeamMapper;

    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,  LogAllBean oddsValueVo) {
        Long[] subPlays = new Long[]{361L, 362L, 363L, 364L, 365L, 366L, 7L, 20L, 74L, 341L, 342L};
        if(!Arrays.asList(subPlays).contains(oddsValueVo.getPlayId())){
            return null;
        }
        List<OddsValueVo> oddsValueList = oddsValueVo.getOddsValueList();
        OddsValueVo valueVo = oddsValueList.get(0);
        StandardSportMarketOdds orgOddsValueVo = null;
        RcsMatchMarketConfig config = new RcsMatchMarketConfig();
        config.setMatchId(oddsValueVo.getMatchId());
        config.setPlayId(oddsValueVo.getPlayId());
        List<StandardSportMarket> playAllMarketList = getMatchPlayOdds(config);
        for(StandardSportMarket rcsStandardMarketDTO : playAllMarketList){
            for(StandardSportMarketOdds oddsDTO : rcsStandardMarketDTO.getMarketOddsList()){
                if(valueVo.getOddsType().equals(oddsDTO.getOddsType())){
                    orgOddsValueVo = oddsDTO;
                    break;
                }
            }
            if(null != orgOddsValueVo){
                break;
            }
        }
        //List<MatchTeamInfo> teamList = standardSportTeamMapper.queryTeamListByMatchId(config.getMatchId());
        //String matchName = getMatchName(teamList,config.getMatchId());
        //String playName = getPlayName(config.getPlayId(), SportIdEnum.FOOTBALL.getId().intValue());
        StringBuilder objectName = new StringBuilder(getPlayNameZs(config.getPlayId(), SportIdEnum.FOOTBALL.getId().intValue()))
                .append(" / ").append(orgOddsValueVo.getOddsType());
        StringBuilder objectNameEn = new StringBuilder(getPlayNameEn(config.getPlayId(), SportIdEnum.FOOTBALL.getId().intValue()))
                .append(" / ").append(orgOddsValueVo.getOddsType());
        StringBuilder extObjectId = new StringBuilder().append(config.getMatchId()).append(" / ").append(config.getPlayId()).append(" / ").append(oddsValueVo.getMarketId());
        StringBuilder extObjectName = new StringBuilder(getMatchName(config.getTeamList(),config.getMatchId()))
                .append(" / ").append(getPlayNameZs(config.getPlayId(), SportIdEnum.FOOTBALL.getId().intValue())).append(" / ");
        StringBuilder extObjectNameEn = new StringBuilder(getMatchNameEn(config.getTeamList(),config.getMatchId())).append(" / ").append(getPlayNameEn(config.getPlayId(), SportIdEnum.FOOTBALL.getId().intValue())).append(" / ");

        rcsOperateLog.setOperatePageCode(15);
        rcsOperateLog.setMatchId(oddsValueVo.getMatchId());
        rcsOperateLog.setPlayId(oddsValueVo.getPlayId());
        rcsOperateLog.setObjectIdByObj(orgOddsValueVo.getId());
        rcsOperateLog.setObjectNameByObj(montageEnAndZs(objectNameEn.toString(),
                objectName.toString()));
        rcsOperateLog.setExtObjectIdByObj(extObjectId);
        rcsOperateLog.setExtObjectNameByObj(montageEnAndZs(extObjectNameEn.toString(),
                extObjectName.toString()));
        rcsOperateLog.setBeforeValByObj(oddsRuleCovert(orgOddsValueVo.getOddsValue()));
        rcsOperateLog.setAfterValByObj(valueVo.getValue());
        return rcsOperateLog;
    }

    public List<StandardSportMarket> getMatchPlayOdds(RcsMatchMarketConfig config) {
        List<StandardSportMarket> playAllMarketList=standardSportMarketMapper.selectMarketOddsByMarketIds(config);
        if (CollectionUtils.isNotEmpty(playAllMarketList)) {
            playAllMarketList = playAllMarketList.stream().filter(e -> CollectionUtils.isNotEmpty(e.getMarketOddsList())).sorted(Comparator.comparing(StandardSportMarket::getPlaceNum)).collect(Collectors.toList());
        }
        return playAllMarketList;
    }



    /**
     * 查詢玩法名稱
     *
     * @param playId
     * @return
     */
    private String getPlayName(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
    }
}



