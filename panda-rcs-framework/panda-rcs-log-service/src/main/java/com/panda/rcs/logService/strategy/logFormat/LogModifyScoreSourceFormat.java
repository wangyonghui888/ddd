package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.Enum.ScoreSourceEnum;
import com.panda.rcs.logService.mapper.StandardMatchTeamRelationMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 操盤日誌(modifyTemplate)
 * 操盤設置-比分源
 */
@Component
public class LogModifyScoreSourceFormat extends LogFormatStrategy {

    @Autowired
    private StandardMatchTeamRelationMapper matchTeamRelationMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,LogAllBean param) {
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
        return modifyScoreSourceFormat(rcsOperateLog, param);
    }

    private RcsOperateLog modifyScoreSourceFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        if ((param.getScoreSource() == param.getBeforeParams().get("scoreSource")) ||
                (Objects.nonNull(param.getScoreSource()) && !param.getScoreSource().equals(param.getBeforeParams().get("scoreSource"))))
        {
            rcsOperateLog.setMatchId(param.getMatchId());
            rcsOperateLog.setObjectIdByObj(param.getMatchManageId());
            rcsOperateLog.setObjectIdByObj(param.getMatchManageId());
            rcsOperateLog.setObjectNameByObj(montageEnAndZsIs(param.getTeamList(),param.getMatchId()));
            rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
            rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
            rcsOperateLog.setParameterName(OperateLogEnum.SCORE_SOURCE.getName());
            rcsOperateLog.setBeforeValByObj(transScoreSource(Integer.parseInt(param.getBeforeParams().get("scoreSource").toString())));
            rcsOperateLog.setAfterValByObj(transScoreSource(param.getScoreSource()));
            return rcsOperateLog;
        }
        return null;
    }

    /**
     * 用賽事ID查詢比賽名稱
     *
     * @param matchId
     * @return
     */
    private String queryMatchName(Integer matchId) {
        List<Map<String, Object>> teamList = matchTeamRelationMapper.selectByMatchId(matchId);
        String home = "", away = "";
        for (Map<String, Object> map : teamList) {
            String position = Optional.ofNullable(map.get("match_position")).orElse("").toString();
            String text = Optional.ofNullable(map.get("text")).orElse("").toString();
            if ("home".equals(position)) {
                home = text;
            } else if ("away".equals(position)) {
                away = text;
            }
        }
        return home + " VS " + away;
    }

    /**
     * 比分源轉換
     *
     * @param scoreSource
     * @return
     */
    private String transScoreSource(Integer scoreSource) {
        if (Objects.nonNull(scoreSource)) {

            switch (scoreSource) {
                case 1:
                    return ScoreSourceEnum.SR.getName();
                case 2:
                    return ScoreSourceEnum.UOF.getName();
            }
        } else {
            return "自動匹配";
        }
        return String.valueOf(scoreSource);
    }
}
