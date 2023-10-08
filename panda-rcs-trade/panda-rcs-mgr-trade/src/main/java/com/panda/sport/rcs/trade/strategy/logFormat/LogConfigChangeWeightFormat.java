package com.panda.sport.rcs.trade.strategy.logFormat;

import com.panda.sport.data.rcs.dto.tournament.StandardMarketSellQueryDto;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.pojo.LanguageInternation;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-数据源权重设置
 */
@Service
public class LogConfigChangeWeightFormat extends LogFormatStrategy {
    @Resource
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, Object[] args) {
        StandardMarketSellQueryDto param = (StandardMarketSellQueryDto) args[0];
        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setPlayId(param.getPlayId());

        String matchName = getMatchName(param.getTeamList());

        rcsOperateLog.setObjectIdByObj(param.getMatchId());
        rcsOperateLog.setObjectNameByObj(matchName);
        rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());

//        rcsOperateLog.setBeforeValByObj(param.getOld());
//        rcsOperateLog.setAfterValByObj(param.getNewSourceCode());
        rcsOperateLog.setParameterName(OperateLogEnum.NONE.getName());
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
