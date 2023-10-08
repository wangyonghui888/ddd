package com.panda.sport.rcs.trade.strategy.logFormat;

import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.pojo.LanguageInternation;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-特殊事件状态修改
 */
@Service
public class LogReductionWaterFormat extends LogFormatStrategy {

    @Resource
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, Object[] args) {
        RcsMatchMarketConfig param = (RcsMatchMarketConfig) args[0];
        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setPlayId(param.getPlayId());

        String matchName = getMatchName(param.getTeamList());

        rcsOperateLog.setObjectIdByObj(param.getPlayId());
        rcsOperateLog.setObjectNameByObj(getPlayName(param.getPlayId(), Math.toIntExact(param.getSportId())));
        rcsOperateLog.setExtObjectIdByObj(param.getMatchManageId());
        rcsOperateLog.setExtObjectNameByObj(matchName);

        rcsOperateLog.setBeforeValByObj(param.getBeforeParams().getMarketValue());
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
