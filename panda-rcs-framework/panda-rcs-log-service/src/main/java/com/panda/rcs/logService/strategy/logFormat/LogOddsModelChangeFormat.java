package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.Enum.MatchTypeEnum;
import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import com.panda.rcs.logService.Enum.SportIdEnum;
import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.mapper.StandardSportMarketMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.*;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 操盤日誌(updateMarketOddsValue)
 * 調整賠率格式化類別
 * @author Z9-jing
 */
@Component
@Slf4j
public class LogOddsModelChangeFormat extends LogFormatStrategy {
    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean oddsModeReqVo) {
        StandardMatchInfo info= getStandardMatchInfo(oddsModeReqVo.getMatchId());
        rcsOperateLog.setOperatePageCode(MatchTypeEnum.EARLY.getId().
                equals(info.getMatchStatus())?MatchTypeEnum.OPERATE_PAGE_ZPCP_CYWF.getId():MatchTypeEnum.OPERATE_PAGE_GQCP_CYWF.getId());
        LogAllBean beforeBean=BaseUtils.mapObject(oddsModeReqVo.getBeforeParams(),LogAllBean.class);
        StringBuilder objectName = new StringBuilder(getPlayNameZs(oddsModeReqVo.getPlayId(), SportIdEnum.FOOTBALL.getId().intValue()));
        StringBuilder objectNameEn = new StringBuilder(getPlayNameEn(oddsModeReqVo.getPlayId(), SportIdEnum.FOOTBALL.getId().intValue()));
        rcsOperateLog.setMatchId(oddsModeReqVo.getMatchId());
        rcsOperateLog.setPlayId(oddsModeReqVo.getPlayId());
        rcsOperateLog.setObjectNameByObj(montageEnAndZs(objectNameEn.toString(),objectName.toString()));
        rcsOperateLog.setExtObjectIdByObj(info.getMatchManageId());
        String teamString = montageEnAndZsIs(oddsModeReqVo.getTeamList(), oddsModeReqVo.getMatchId());
        rcsOperateLog.setExtObjectNameByObj(teamString);
        if(null!=oddsModeReqVo.getMode()) {
            rcsOperateLog.setBeforeValByObj(oddsModeReqVo.getMode()==0?"锁":"开");
            rcsOperateLog.setAfterValByObj(oddsModeReqVo.getMode()==0?"开":"锁");
            rcsOperateLog.setBehavior(OperateLogOneEnum.Lock_Probability.getName());
            rcsOperateLog.setParameterName(oddsModeReqVo.getOddsType()+" - "+oddsRuleCovert(oddsModeReqVo.getOddsValue().intValue()));
            return rcsOperateLog;
        }if(null!=oddsModeReqVo.getOddsValue()&&null!=beforeBean){
            rcsOperateLog.setBeforeValByObj(oddsRuleCovert(beforeBean.getOddsValue()));
            rcsOperateLog.setAfterValByObj(oddsRuleCovert(oddsModeReqVo.getOddsValue().intValue()));
            return rcsOperateLog;
        }
        return null;
    }
    public List<StandardSportMarket> getMatchPlayOdds(RcsMatchMarketConfig config) {
        List<StandardSportMarket> playAllMarketList=standardSportMarketMapper.selectMarketOddsByMarketIds(config);
        if (CollectionUtils.isNotEmpty(playAllMarketList)) {
            playAllMarketList = playAllMarketList.stream().filter(e -> CollectionUtils.isNotEmpty(e.getMarketOddsList())).sorted(Comparator.comparing(StandardSportMarket::getPlaceNum)).collect(Collectors.toList());
        }
        return playAllMarketList;
    }
}



