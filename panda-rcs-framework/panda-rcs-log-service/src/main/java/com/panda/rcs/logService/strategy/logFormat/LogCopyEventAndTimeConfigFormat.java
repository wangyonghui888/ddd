package com.panda.rcs.logService.strategy.logFormat;

import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 操盤日誌(copyEventAndTimeConfig)
 * 联赛模板日志-接拒单玩法集事件复制功能
 */
@Component
public class LogCopyEventAndTimeConfigFormat extends LogFormatStrategy {

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean config) {
        switch (config.getOperatePageCode()) {
            case 21:
                return copyEventAndTimeConfigFormat(rcsOperateLog, config);
        }

        return null;
    }

    /**
     * 初始化-來源為聯賽模板設置
     *
     * @param rcsOperateLog
     * @param param
     */
    private RcsOperateLog copyEventAndTimeConfigFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setObjectIdByObj(param.getTemplateId());//模板ID
        rcsOperateLog.setObjectNameByObj(templateIdEnAndZsIs(param.getTemplateId(),param.getSportId()));//模板名稱
        rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setBehavior(OperateLogEnum.TEMPLATE_UPDATE.getName());
        rcsOperateLog.setParameterName(OperateLogEnum.COPY_EVENT_AND_TIME_CONFIG.getName());
        rcsOperateLog.setBeforeValByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setAfterValByObj(param.getCopyCategorySetName());


        return rcsOperateLog;
    }

}
