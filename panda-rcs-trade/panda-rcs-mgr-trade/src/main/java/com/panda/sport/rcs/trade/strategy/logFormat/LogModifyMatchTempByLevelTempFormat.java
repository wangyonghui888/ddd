package com.panda.sport.rcs.trade.strategy.logFormat;

import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.trade.param.TournamentTemplateUpdateParam;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-同步联赛模板
 */
@Service
public class LogModifyMatchTempByLevelTempFormat extends LogFormatStrategy {
    @Autowired
    private IRcsTournamentTemplateService tournamentTemplateService;
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, Object[] args) {
        TournamentTemplateUpdateParam param = (TournamentTemplateUpdateParam) args[0];
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

    private RcsOperateLog syncTemplateFormat(RcsOperateLog rcsOperateLog, TournamentTemplateUpdateParam param) {
            rcsOperateLog.setObjectNameByObj(getMatchName(param.getTeamList()));
            rcsOperateLog.setExtObjectIdByObj(param.getMatchId());
            rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
            if(Objects.isNull(param.getBeforeParams().getTemplateName())){
                RcsTournamentTemplate template=tournamentTemplateService.getById(param.getBeforeParams().getCopyTemplateId());
                rcsOperateLog.setBeforeValByObj(template==null?"":template.getTemplateName());
            }else{
                rcsOperateLog.setBeforeValByObj(param.getBeforeParams().getTemplateName());
            }
            rcsOperateLog.setAfterValByObj(param.getTemplateName());
            return rcsOperateLog;
    }

}
