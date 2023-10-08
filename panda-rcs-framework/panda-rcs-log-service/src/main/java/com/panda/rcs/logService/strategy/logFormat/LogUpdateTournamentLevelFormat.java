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

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 操盤日誌(updateTournamentLevel)
 * 聯賽模板-联赛属性
 */
@Component
public class LogUpdateTournamentLevelFormat extends LogFormatStrategy {

    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;

    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,  LogAllBean param) {
        if(BaseUtils.isTrue(param.getBeforeParams())){
            return null;
        }
        //根據不同操作頁面組裝不同格式
        switch (param.getOperatePageCode()) {
            case 21:
                //联赛参数设置
                updateTournamentConfigFormat(rcsOperateLog, param);
                break;
        }
        return null;
    }

    private void updateTournamentConfigFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        //設置共用LogBean內容
        rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        rcsOperateLog.setObjectIdByObj(param.getId());
        rcsOperateLog.setObjectNameByObj(templateIdEnAndZsIs(param.getId(), Math.toIntExact(param.getSportId())));
        rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());

        //等级选择
        if (Objects.nonNull(param.getLevel()) &&
                !param.getLevel().equals(param.getBeforeParams().get("level"))) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, param.getBeforeParams().get("level"), param.getLevel());
            logBean.setParameterName(OperateLogEnum.TOURNAMENT_LEVEL.getName());
            pushMessage(logBean);
        }

        //是否热门
        if (Objects.nonNull(param.getIsPopular()) &&
                !param.getIsPopular().equals(param.getBeforeParams().get("isPopular"))) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, getTrueFalse(Integer.parseInt(param.getBeforeParams().get("isPopular").toString())), getTrueFalse(param.getIsPopular()));
            logBean.setParameterName(OperateLogEnum.IS_POPULAR.getName());
            pushMessage(logBean);
        }
        //目标咬度
        if (Objects.nonNull(param.getTargetProfitRate()) &&
                !param.getTargetProfitRate().equals(param.getBeforeParams().get("targetProfitRate"))) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, new BigDecimal(param.getBeforeParams().get("targetProfitRate").toString()).stripTrailingZeros(), param.getTargetProfitRate().stripTrailingZeros());
            logBean.setParameterName(OperateLogEnum.TARGET_PROFIT_RATE.getName());
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
     *
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

    /**
     * 轉換true false 1:是 0:否
     *
     * @param status
     * @return
     */
    private String getTrueFalse(Integer status) {
        switch (status) {
            case 0:
                return "否";
            case 1:
                return "是";
            default:
                return "";
        }
    }
}
