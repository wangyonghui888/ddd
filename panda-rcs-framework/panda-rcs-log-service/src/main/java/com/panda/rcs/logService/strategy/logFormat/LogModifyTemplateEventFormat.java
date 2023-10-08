package com.panda.rcs.logService.strategy.logFormat;

import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.Objects;

/**
 * 操盤日誌(modifyEvent)
 * -設置-誰先開球/角球/進球/事件
 */
@Component
public class LogModifyTemplateEventFormat extends LogFormatStrategy {

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
            case 17:
                //滚球操盘-设置
                rcsOperateLog.setOperatePageCode(111);
            default:
                rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        }

        return modifyTemplateEventFormat(rcsOperateLog, param);
    }

    private RcsOperateLog modifyTemplateEventFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        if (Objects.nonNull(param.getEventHandleTime()) & Objects.nonNull(param.getSettleHandleTime()) &&
                (!param.getEventHandleTime().equals(param.getBeforeParams().get("eventHandleTime")) ||
                        !param.getSettleHandleTime().equals(param.getBeforeParams().get("settleHandleTime")))) {
            rcsOperateLog.setMatchId(param.getMatchId());
            rcsOperateLog.setObjectIdByObj(param.getMatchManageId());
            rcsOperateLog.setObjectNameByObj(montageEnAndZsIs(param.getTeamList(),param.getMatchId()));
            rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
            rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
            switch (param.getEventCode()) {
                case "kick_off_team":
                    rcsOperateLog.setParameterName(OperateLogEnum.KICK_OFF_TEAM.getName());
                case "goal":
                    rcsOperateLog.setParameterName(OperateLogEnum.GOAL.getName());
                case "corner":
                    rcsOperateLog.setParameterName(OperateLogEnum.CORNER.getName());
                case "fa_card":
                    rcsOperateLog.setParameterName(OperateLogEnum.FA_CARD.getName());
            }
            rcsOperateLog.setBeforeValByObj(secondToTime(param.getEventHandleTime()) + "-" + secondToTime(param.getSettleHandleTime()));
            rcsOperateLog.setAfterValByObj(secondToTime((Long) param.getBeforeParams().get("eventHandleTime")) + "-" + secondToTime((Long) param.getBeforeParams().get("settleHandleTime")));

            return rcsOperateLog;
        }
        return null;
    }
}
