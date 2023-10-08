package com.panda.sport.rcs.trade.strategy.logFormat;

import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.pojo.dto.SpecEventChangeDTO;
import com.panda.sport.rcs.pojo.param.AutoOpenMarketStatusParam;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.trade.wrapper.SportMatchViewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-特殊事件状态修改
 */
@Service
public class LogMatchSpecEventFormat extends LogFormatStrategy {

    @Resource
    private SportMatchViewService sportMatchViewService;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, Object[] args) {
        SpecEventChangeDTO param = (SpecEventChangeDTO) args[0];
        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setObjectIdByObj(param.getMatchId());
        rcsOperateLog.setBehavior("操盘手手动确认修改赛事特殊事件");
        return syncTemplateFormat(rcsOperateLog, param);
    }

    private RcsOperateLog syncTemplateFormat(RcsOperateLog rcsOperateLog, SpecEventChangeDTO param) {
        Long typeVal = param.getMatchId();
        MatchMarketLiveBean matchMarketLiveBean = sportMatchViewService.queryByMatchId(typeVal, typeVal.toString());
        String matchManageId = null == matchMarketLiveBean ? typeVal.toString() : matchMarketLiveBean.getMatchManageId();
        rcsOperateLog.setObjectIdByObj(matchManageId);
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setAfterValByObj(param);
        rcsOperateLog.setBeforeValByObj(param.getBeforeParams());
        rcsOperateLog.setAfterValByObj(param);
        rcsOperateLog.setExtObjectIdByObj(matchManageId);
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.OPERATE_SPECEVENT_SWITCH.getName());
        rcsOperateLog.setParameterName(OperateLogEnum.OPERATE_SPECEVENT_SWITCH.getName());
        rcsOperateLog.setSportId(1);
        rcsOperateLog.setOperateTime(new Date());
        return rcsOperateLog;
    }

}
