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
import javax.annotation.Resource;
import java.util.Date;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-AO自动开盘状态修改
 */
@Component
public class LogUpdateAutoOpenMarketStatusFormat extends LogFormatStrategy {

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,LogAllBean param) {
        if(BaseUtils.isTrue(param.getBeforeParams())){
            return null;
        }
        Long typeVal = param.getTypeVal();
        rcsOperateLog.setMatchId(typeVal);
        rcsOperateLog.setBehavior("AO自动开盘状态修改");
        return syncTemplateFormat(rcsOperateLog, param);
    }

    private RcsOperateLog syncTemplateFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        Long typeVal = param.getTypeVal();
        StandardMatchInfo matchMarketLiveBean = standardMatchInfoMapper.selectById(typeVal);
        String matchManageId = null == matchMarketLiveBean ? typeVal.toString() : matchMarketLiveBean.getMatchManageId();
        rcsOperateLog.setObjectIdByObj(matchManageId);
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setAfterValByObj(param.getSwitchStatus()==1?"开":"关");
        rcsOperateLog.setBeforeValByObj(Integer.parseInt(param.getBeforeParams().get("switchStatus").toString())==1?"开":"关");
        rcsOperateLog.setExtObjectIdByObj(matchManageId);
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.OPERATE_AOAUTO_OPEN.getLangJson());
        rcsOperateLog.setParameterName(OperateLogEnum.OPERATE_AOAUTO_OPEN.getName());
        rcsOperateLog.setSportId(1);
        rcsOperateLog.setOperateTime(new Date());
        return rcsOperateLog;
    }

}
