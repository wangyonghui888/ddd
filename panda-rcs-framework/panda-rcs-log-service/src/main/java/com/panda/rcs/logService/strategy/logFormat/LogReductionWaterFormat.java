package com.panda.rcs.logService.strategy.logFormat;

import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.LanguageInternation;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-特殊事件状态修改
 */
@Component
public class LogReductionWaterFormat extends LogFormatStrategy {

    @Resource
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,LogAllBean param) {
        if(BaseUtils.isTrue(param.getBeforeParams())){
            return null;
        }
        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setPlayId(param.getPlayId());

        String matchName =montageEnAndZsIs(param.getTeamList(),param.getMatchId());

        rcsOperateLog.setObjectIdByObj(param.getPlayId());
        rcsOperateLog.setObjectNameByObj(getPlayNameZsEn(param.getPlayId(), Math.toIntExact(param.getSportId())));
        rcsOperateLog.setExtObjectIdByObj(param.getMatchManageId());
        rcsOperateLog.setExtObjectNameByObj(matchName);

        rcsOperateLog.setBeforeValByObj(param.getBeforeParams().get("marketValue"));
        // 调整后的盘口值,固定是0
        rcsOperateLog.setAfterValByObj(0);
        rcsOperateLog.setParameterName(OperateLogEnum.AWAY_AUTO_CHANGE_RATE.getName());
        return rcsOperateLog;
    }


    private String getPlayName(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
    }

}
