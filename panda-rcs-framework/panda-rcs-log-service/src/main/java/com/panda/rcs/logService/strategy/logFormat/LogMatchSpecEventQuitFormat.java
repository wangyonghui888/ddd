package com.panda.rcs.logService.strategy.logFormat;

import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-特殊事件状态修改
 */
@Component
public class LogMatchSpecEventQuitFormat extends LogFormatStrategy {
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,  LogAllBean param) {
        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setObjectIdByObj(param.getMatchId());
        rcsOperateLog.setBehavior("操盘手手动确认退出特殊事件");
        return syncTemplateFormat(rcsOperateLog, param);
    }

    private RcsOperateLog syncTemplateFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        Long typeVal = param.getMatchId();
        StandardMatchInfo matchMarketLiveBean = standardMatchInfoMapper.selectById(typeVal);
        String matchManageId = null == matchMarketLiveBean ? typeVal.toString() : matchMarketLiveBean.getMatchManageId();
        rcsOperateLog.setObjectIdByObj(matchManageId);
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        //rcsOperateLog.setAfterValByObj(param);
        rcsOperateLog.setBeforeValByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setAfterValByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectIdByObj(matchManageId);
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.OPERATE_SPECEVENT_QUIT.getLangJson());
        rcsOperateLog.setParameterName(OperateLogEnum.OPERATE_SPECEVENT_QUIT.getName());
        rcsOperateLog.setSportId(1);
        rcsOperateLog.setOperateTime(new Date());
        return rcsOperateLog;
    }

}
