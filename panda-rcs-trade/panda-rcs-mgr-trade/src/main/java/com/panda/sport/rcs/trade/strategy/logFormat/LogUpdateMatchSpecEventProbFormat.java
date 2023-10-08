package com.panda.sport.rcs.trade.strategy.logFormat;

import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import com.panda.sport.rcs.pojo.dto.SpecEventChangeDTO;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.trade.wrapper.SportMatchViewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-特殊事件赔率修改
 */
@Service
public class LogUpdateMatchSpecEventProbFormat extends LogFormatStrategy {
    @Resource
    private SportMatchViewService sportMatchViewService;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, Object[] args) {
        RcsSpecEventConfig param = (RcsSpecEventConfig) args[0];
        rcsOperateLog.setMatchId(param.getTypeVal());
        rcsOperateLog.setObjectIdByObj(param.getTypeVal());
        rcsOperateLog.setBehavior("特殊事件修改赔率");
        return syncTemplateFormat(rcsOperateLog, param);
    }

    private RcsOperateLog syncTemplateFormat(RcsOperateLog rcsOperateLog, RcsSpecEventConfig param) {
        Long typeVal = param.getTypeVal();
        MatchMarketLiveBean matchMarketLiveBean = sportMatchViewService.queryByMatchId(typeVal, typeVal.toString());
        String matchManageId = null == matchMarketLiveBean ? typeVal.toString() : matchMarketLiveBean.getMatchManageId();
        rcsOperateLog.setObjectIdByObj(matchManageId);
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setAfterValByObj(param);
        rcsOperateLog.setBeforeValByObj(param.getBeforeParams());
        rcsOperateLog.setExtObjectIdByObj(matchManageId);
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.OPERATE_SPECEVENT_GOALPROB.getName());
        rcsOperateLog.setParameterName(OperateLogEnum.OPERATE_SPECEVENT_GOALPROB.getName());
        rcsOperateLog.setSportId(1);
        rcsOperateLog.setOperateTime(new Date());
        return rcsOperateLog;
    }

}
