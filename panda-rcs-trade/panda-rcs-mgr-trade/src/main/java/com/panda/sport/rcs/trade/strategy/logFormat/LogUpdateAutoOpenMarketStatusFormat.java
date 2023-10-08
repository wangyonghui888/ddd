package com.panda.sport.rcs.trade.strategy.logFormat;

import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import com.panda.sport.rcs.pojo.param.AutoOpenMarketStatusParam;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.trade.wrapper.SportMatchViewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-AO自动开盘状态修改
 */
@Service
public class LogUpdateAutoOpenMarketStatusFormat extends LogFormatStrategy {

    @Resource
    private SportMatchViewService sportMatchViewService;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, Object[] args) {
        AutoOpenMarketStatusParam param = (AutoOpenMarketStatusParam) args[0];
        Long typeVal = param.getTypeVal();
        rcsOperateLog.setMatchId(typeVal);
        rcsOperateLog.setBehavior("AO自动开盘状态修改");
        return syncTemplateFormat(rcsOperateLog, param);
    }

    private RcsOperateLog syncTemplateFormat(RcsOperateLog rcsOperateLog, AutoOpenMarketStatusParam param) {
        Long typeVal = param.getTypeVal();
        MatchMarketLiveBean matchMarketLiveBean = sportMatchViewService.queryByMatchId(typeVal, typeVal.toString());
        String matchManageId = null == matchMarketLiveBean ? typeVal.toString() : matchMarketLiveBean.getMatchManageId();
        rcsOperateLog.setObjectIdByObj(matchManageId);
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setAfterValByObj(param.getSwitchStatus()==1?"开":"关");
        rcsOperateLog.setBeforeValByObj(param.getBeforeParams().getSwitchStatus()==1?"开":"关");
        rcsOperateLog.setExtObjectIdByObj(matchManageId);
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.OPERATE_AOAUTO_OPEN.getName());
        rcsOperateLog.setParameterName(OperateLogEnum.OPERATE_AOAUTO_OPEN.getName());
        rcsOperateLog.setSportId(1);
        rcsOperateLog.setOperateTime(new Date());
        return rcsOperateLog;
    }

}
