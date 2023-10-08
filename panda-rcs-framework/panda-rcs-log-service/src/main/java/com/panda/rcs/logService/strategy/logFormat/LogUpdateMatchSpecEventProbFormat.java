package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-特殊事件赔率修改
 */
@Component
public class LogUpdateMatchSpecEventProbFormat extends LogFormatStrategy {
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean param) {
        rcsOperateLog.setMatchId(param.getTypeVal());
        rcsOperateLog.setObjectIdByObj(param.getTypeVal());
        rcsOperateLog.setBehavior("特殊事件修改赔率");
        return syncTemplateFormat(rcsOperateLog, param);
    }

    private RcsOperateLog syncTemplateFormat(RcsOperateLog rcsOperateLog,  LogAllBean param) {
        Long typeVal = param.getTypeVal();
        StandardMatchInfo matchMarketLiveBean = standardMatchInfoMapper.selectById(typeVal);
        String matchManageId = null == matchMarketLiveBean ? typeVal.toString() : matchMarketLiveBean.getMatchManageId();
        rcsOperateLog.setObjectIdByObj(matchManageId);
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setAfterValByObj(param);
        rcsOperateLog.setBeforeValByObj(param.getBeforeParams());
        rcsOperateLog.setExtObjectIdByObj(matchManageId);
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.OPERATE_SPECEVENT_GOALPROB.getLangJson());
        rcsOperateLog.setParameterName(OperateLogEnum.OPERATE_SPECEVENT_GOALPROB.getName());
        rcsOperateLog.setSportId(1);
        rcsOperateLog.setOperateTime(new Date());
        return rcsOperateLog;
    }

}
