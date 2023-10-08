package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.LanguageInternation;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 操盤日誌(updateTournamentTemplate)
 * 联赛模板日志-模板选择
 */
@Component
public class LogTemplateSelectionFormat extends LogFormatStrategy {

    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;
    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,LogAllBean param) {
        if(BaseUtils.isTrue(param.getBeforeParams())){
            return null;
        }
        //根據不同操作頁面組裝不同格式
        switch (param.getOperatePageCode()) {
            case 21:
                //联赛参数设置
                templateSelectionFormat(rcsOperateLog, param);
                break;
        }
        return null;
    }

    private void templateSelectionFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        //設置共用LogBean內容
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setObjectIdByObj(param.getId());
        rcsOperateLog.setObjectNameByObj(templateIdEnAndZsIs(param.getId(), param.getSportId()));
        rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());

        //早盘操盘参数模板
        if (Objects.nonNull(param.getPreLemplateName()) &&
                !param.getPreLemplateName().equals(param.getBeforeParams().get("preLemplateName"))) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, param.getBeforeParams().get("preLemplateName"), param.getPreLemplateName());
            logBean.setParameterName(OperateLogEnum.PRE_TEMPLATE.getName());
            pushMessage(logBean);
        }

        //滚球操盘参数模板
        if (Objects.nonNull(param.getLiveLemplateName()) &&
                !param.getLiveLemplateName().equals(param.getBeforeParams().get("liveLemplateName"))) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, param.getBeforeParams().get("liveLemplateName"), param.getLiveLemplateName());
            logBean.setParameterName(OperateLogEnum.LIVE_TEMPLATE.getName());
            pushMessage(logBean);
        }
    }

    private RcsOperateLog initialLogBean(RcsOperateLog sample, Object before, Object after) {
        RcsOperateLog rcsOperateLog = new RcsOperateLog();
        BeanUtils.copyProperties(sample, rcsOperateLog);
        rcsOperateLog.setBeforeValByObj(before);
        rcsOperateLog.setAfterValByObj(after);
        return rcsOperateLog;
    }

    /**
     * 查詢聯賽名稱
     * @param tournamentId
     * @param sportId
     * @return
     */
    private String getTournamentName(Long tournamentId, Integer sportId) {
        LanguageInternation tournamentName = rcsLanguageInternationMapper.getTournamentNameByIdAndSprotId(tournamentId, sportId);
        return Objects.nonNull(tournamentName) ? tournamentName.getText() : "";
    }

    private void pushMessage(RcsOperateLog rcsOperateLog) {
         rcsOperateLogMapper.insert(rcsOperateLog);
    }
}
