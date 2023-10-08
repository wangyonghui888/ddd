package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.Enum.MatchTypeEnum;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-数据源权重设置
 */
@Component
public class LogConfigChangeWeightFormat extends LogFormatStrategy {
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,LogAllBean param) {
        Long matchId=param.getMatchId();
        StandardMatchInfo info= getStandardMatchInfo(matchId);
        if(MatchTypeEnum.EARLY.getId().equals(info==null?0:info.getMatchStatus())){
            rcsOperateLog.setOperatePageCode(MatchTypeEnum.OPERATE_PAGE_ZPSS.getId());
        }else {
            rcsOperateLog.setOperatePageCode(MatchTypeEnum.OPERATE_PAGE_GQSS.getId());
        }
        rcsOperateLog.setMatchId(matchId);
        rcsOperateLog.setPlayId(param.getPlayId());
        String matchName = montageEnAndZsIs(param.getTeamList(),matchId);
        rcsOperateLog.setObjectIdByObj(info==null?matchId:info.getMatchManageId());

        rcsOperateLog.setObjectNameByObj(matchName);
        rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setBeforeValByObj(param.getOld());
        rcsOperateLog.setAfterValByObj(param.getNewValue());
        rcsOperateLog.setParameterName(OperateLogEnum.NONE.getName());
        return rcsOperateLog;
    }





}
