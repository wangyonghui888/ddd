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
 * 操盤日誌(matchChangeStatusSource)
 * 切換數據源 賽事狀態源
 */
@Component
public class LogChangeStatusSourceFormat extends LogFormatStrategy {
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,LogAllBean vo) {
        if(vo == null || vo.getOperatePageCode() == null){return null;}
        if(BaseUtils.isTrue(vo.getBeforeParams())){
            return null;
        }
        //根據不同操作頁面組裝不同格式
        switch (vo.getOperatePageCode()) {
            case 13:
                //早盤賽事
            case 16:
                //滾球賽事
                return ChangeStatusFormat(rcsOperateLog, vo);
        }
        return null;
    }

    private RcsOperateLog ChangeStatusFormat(RcsOperateLog rcsOperateLog, LogAllBean vo) {
        if (Objects.nonNull(vo.getDataSouceCode()) &&
                !vo.getDataSouceCode().equals(vo.getBeforeParams().get("dataSouceCode"))) {
            rcsOperateLog.setOperatePageCode(vo.getOperatePageCode());
            rcsOperateLog.setMatchId(vo.getMatchId());
            rcsOperateLog.setObjectIdByObj(vo.getMatchManageId());
            rcsOperateLog.setObjectNameByObj(montageEnAndZsIs(vo.getTeamList(),vo.getMatchId()));
            rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
            rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
            rcsOperateLog.setBeforeValByObj(vo.getBeforeParams().get("dataSouceCode").toString());
            rcsOperateLog.setAfterValByObj(vo.getDataSouceCode());
            return rcsOperateLog;
        }
        return null;
    }

}
