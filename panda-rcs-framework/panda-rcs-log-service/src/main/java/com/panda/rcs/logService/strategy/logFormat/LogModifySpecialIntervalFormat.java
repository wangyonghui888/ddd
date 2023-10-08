package com.panda.rcs.logService.strategy.logFormat;
import com.alibaba.fastjson.JSON;
import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.LanguageInternation;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 操盤日誌(modifySpecialInterval)
 * 設置-盤口參數調整-特殊抽水
 */
@Component
public class LogModifySpecialIntervalFormat extends LogFormatStrategy {
    
    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean param) {
        if(BaseUtils.isTrue(param.getBeforeParams())){
            return null;
        }
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
            default:
                rcsOperateLog.setOperatePageCode(param.getOperatePageCode());
        }

        modifySpecialIntervalFormat(rcsOperateLog, param);

        return null;
    }

    private void modifySpecialIntervalFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        String playName = getPlayNameZsEn(param.getPlayId().longValue(), param.getSportId());
        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setPlayId(param.getPlayId().longValue());
        rcsOperateLog.setObjectIdByObj(param.getPlayId());
        rcsOperateLog.setObjectNameByObj(playName);
        rcsOperateLog.setExtObjectIdByObj(param.getMatchManageId());
        rcsOperateLog.setExtObjectNameByObj(montageEnAndZsIs(param.getTeamList(),param.getMatchId()));

        //是否特殊抽水
        if (!param.getIsSpecialPumping().equals(param.getBeforeParams().get("isSpecialPumping"))) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(OperateLogEnum.SPECIAL_PUMPING.getName());
            logBean.setBeforeValByObj(getTrueFalse(Integer.parseInt(param.getBeforeParams().get("isSpecialPumping").toString())));
            logBean.setAfterValByObj(getTrueFalse(param.getIsSpecialPumping()));
            pushMessage(logBean);
        }

        //特殊抽水赔率区间(Malay Spread)
        if (!param.getSpecialOddsInterval().equals(param.getBeforeParams().get("specialOddsInterval"))) {
            insertOperateLog(param.getSpecialOddsInterval(),param.getBeforeParams().get("specialOddsInterval").toString(),
                    rcsOperateLog,OperateLogEnum.SPECIAL_ODDS_INTERVAL.getName());
        }
        //特殊抽水赔率区间状态
        if (!param.getSpecialOddsIntervalStatus().equals(param.getBeforeParams().get("specialOddsIntervalStatus"))) {
            insertOperateLog(param.getSpecialOddsIntervalStatus(),param.getBeforeParams().get("specialOddsIntervalStatus").toString(),
                    rcsOperateLog,OperateLogEnum.SPECIAL_ODDS_INTERVAL_STATUS.getName());

        }
        //
        //低赔特殊抽水赔率区间
        if (!param.getSpecialOddsIntervalLow().equals(param.getBeforeParams().get("specialOddsIntervalLow"))) {
            insertOperateLog(param.getSpecialOddsIntervalLow(),param.getBeforeParams().get("specialOddsIntervalLow").toString(),
                    rcsOperateLog,OperateLogEnum.SPECIAL_ODDS_INTERVAL_LOW.getName());
        }

        //低赔特殊抽水赔率区间
        if (!param.getSpecialBettingIntervalHigh().equals(param.getBeforeParams().get("specialBettingIntervalHigh"))) {
            insertOperateLog(param.getSpecialBettingIntervalHigh(),param.getBeforeParams().get("specialBettingIntervalHigh").toString(),
                    rcsOperateLog, OperateLogOneEnum.SPECIAL_Betting_INTERVAL_HIGH.getName());
        }
        //高赔特殊抽水赔率区间
        if (!param.getSpecialOddsIntervalHigh().equals(param.getBeforeParams().get("specialOddsIntervalHigh"))) {
            insertOperateLog(param.getSpecialOddsIntervalHigh(),param.getBeforeParams().get("specialOddsIntervalHigh").toString(),
                    rcsOperateLog,OperateLogEnum.SPECIAL_ODDS_INTERVAL_HIGH.getName());
        }
    }

    /**
     *  对比变动值生成日志
     * @param after
     * @param before
     * @param rcsOperateLog
     * @param code
     */
    public void insertOperateLog(String after,String before,RcsOperateLog rcsOperateLog,String code){
        Map<String,Object> afterMap = JSON.parseObject(after, Map.class);
        Map<String,Object> beforeMap = JSON.parseObject(before, Map.class);
         Set<String> set=  afterMap.keySet();
        for (String key:set) {
            if(!afterMap.get(key).equals(beforeMap.get(key))){
                BigDecimal afterBig=new BigDecimal(afterMap.get(key).toString());
                BigDecimal beforeBig=new BigDecimal(beforeMap.get(key).toString());
                rcsOperateLog.setParameterName(key+" : "+code);
                rcsOperateLog.setBeforeValByObj(beforeMap.get(key));
                rcsOperateLog.setAfterValByObj(afterMap.get(key));
                if(OperateLogEnum.SPECIAL_ODDS_INTERVAL_STATUS.getName().equals(code)){
                    rcsOperateLog.setBeforeValByObj(getStringTrueFalse(beforeMap.get(key).toString()));
                    rcsOperateLog.setAfterValByObj(getStringTrueFalse(afterMap.get(key).toString()));
                }
                if(afterBig.compareTo(beforeBig)!=0){
                    pushMessage(rcsOperateLog);
                }

            }

        }


    }

    /**
     * 查詢玩法名稱
     *
     * @param playId
     * @return
     */
    private String getPlayName(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
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

    private String getStringTrueFalse(String status) {
        switch (status) {
            case "0":
                return "否";
            case "1":
                return "是";
            default:
                return "";
        }
    }

}
