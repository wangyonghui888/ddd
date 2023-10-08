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
 * 操盤日誌(modifyMatchPayVal)
 * 設置-商戶/用戶 單場賠付限額
 */
@Component
public class LogModifyMatchPayValFormat extends LogFormatStrategy {
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
            default:
                rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        }

        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setObjectIdByObj(param.getMatchManageId());
        rcsOperateLog.setObjectNameByObj(montageEnAndZsIs(param.getTeamList(),param.getMatchId()));
        rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());

        if (Objects.nonNull(param.getBusinesMatchPayVal())) {
            return updateBusinesMatchPayFormat(rcsOperateLog, param);
        } else if (Objects.nonNull(param.getUserMatchPayVal())) {
            return updateUserMatchPayFormat(rcsOperateLog, param);
        } else {
            return null;
        }
    }

    private RcsOperateLog updateBusinesMatchPayFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        if (!param.getBusinesMatchPayVal().equals(param.getBeforeParams().get("businesMatchPayVal"))) {
            rcsOperateLog.setParameterName(OperateLogEnum.BUSINES_MATCH_PAY_VAL.getName());
            rcsOperateLog.setBeforeValByObj(param.getBeforeParams().get("businesMatchPayVal"));
            rcsOperateLog.setAfterValByObj(param.getBusinesMatchPayVal());
            return rcsOperateLog;
        }
        return null;
    }

    private RcsOperateLog updateUserMatchPayFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        if (!param.getUserMatchPayVal().equals(param.getBeforeParams().get("userMatchPayVal"))) {
            rcsOperateLog.setParameterName(OperateLogEnum.USER_MATCH_PAY_VAL.getName());
            rcsOperateLog.setBeforeValByObj(param.getBeforeParams().get("userMatchPayVal"));
            rcsOperateLog.setAfterValByObj(param.getUserMatchPayVal());
            return rcsOperateLog;
        }
        return null;
    }
}
