package com.panda.rcs.logService.strategy.logFormat;

import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
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
public class LogWaterDiffRelevanceFormat extends LogFormatStrategy {
    @Resource
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean param) {
        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setPlayId(param.getPlayId());
        String matchName = montageEnAndZsIs(param.getTeamList(),param.getMatchId());
        rcsOperateLog.setObjectIdByObj(param.getPlayId());
        rcsOperateLog.setObjectNameByObj(getPlayNameZsEn(param.getPlayId(), Math.toIntExact(param.getSportId())));
        rcsOperateLog.setExtObjectIdByObj(param.getMatchId());
        rcsOperateLog.setExtObjectNameByObj(matchName);
        rcsOperateLog.setBeforeValByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setAfterValByObj(getStatusName(param.getRelevanceType()));
        rcsOperateLog.setParameterName(OperateLogEnum.MARGIN_GAP_LINKING.getName());
        return rcsOperateLog;
    }


    private String getPlayName(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
    }

    /**
     * 根據盤口狀態碼轉換名稱
     *
     * @param relevanceType
     * @return
     */
    public static String getStatusName(Integer relevanceType) {
        switch (relevanceType) {
            case 0:
                return OperateLogEnum.EDIT_VALUE_GB.getName();
            case 1:
                return OperateLogEnum.EDIT_VALUE_KQ.getName();
            default:
                return OperateLogEnum.NONE.getName();
        }
    }
}
