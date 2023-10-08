package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-同步联赛模板
 */
@Component
public class LogModifyMatchTempByLevelTempFormat extends LogFormatStrategy {
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean param) {
        if(BaseUtils.isTrue(param.getBeforeParams())){
            return null;
        }
        //根據不同操作頁面組裝不同格式
        switch (param.getOperatePageCode()) {
            case 14:
                //早盘操盘-设置
                rcsOperateLog.setOperatePageCode(110);
                break;
            case 17:
                //滚球操盘-设置
                rcsOperateLog.setOperatePageCode(111);
                break;
        }
        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setObjectIdByObj(param.getMatchManageId());

        return syncTemplateFormat(rcsOperateLog, param);
    }

    private RcsOperateLog syncTemplateFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
            rcsOperateLog.setObjectNameByObj(montageEnAndZsIs(param.getTeamList(),param.getMatchId()));
            rcsOperateLog.setExtObjectIdByObj(param.getMatchId());
            rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
            rcsOperateLog.setBeforeValByObj(param.getBeforeParams().get("templateName"));
            rcsOperateLog.setAfterValByObj(param.getTemplateName());
            return rcsOperateLog;
    }

}
