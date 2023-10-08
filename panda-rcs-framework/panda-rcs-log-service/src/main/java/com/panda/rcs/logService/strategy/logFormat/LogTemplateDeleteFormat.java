package com.panda.rcs.logService.strategy.logFormat;

import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 操盤日誌
 * 联赛模板日志需求-模板删除
 */
@Component
public class LogTemplateDeleteFormat extends LogFormatStrategy {
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean param) {
        //根據不同操作頁面組裝不同格式
        switch (param.getOperatePageCode()) {
            case 21:
                //联赛参数设置
                return templateDeleteFormat(rcsOperateLog, param);
        }
        return null;
    }

    private RcsOperateLog templateDeleteFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setObjectIdByObj(param.getId());
        templateIdEnAndZsIs(param.getId(),param.getSportId()==null?1:param.getSportId());
        rcsOperateLog.setObjectNameByObj(templateIdEnAndZsIs(param.getId(),param.getSportId()==null?1:param.getSportId()));
        rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setBeforeValByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setAfterValByObj("刪除");

        return rcsOperateLog;
    }

}
