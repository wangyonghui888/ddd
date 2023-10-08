package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.Enum.TradeEnum;
import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.mapper.StandardSportTeamMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.LanguageInternation;
import com.panda.rcs.logService.vo.MatchTeamInfo;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 操盤日誌(updateMarketTradeType)
 * 切换操盘模式
 */
@Component
public class LogUpdateMarketTradeTypeFormat extends LogFormatStrategy {

	@Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private StandardSportTeamMapper standardSportTeamMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean vo) {
        if(vo.getOperatePageCode() == null) {return null;}
        if(BaseUtils.isTrue(vo.getBeforeParams())){
            return null;
        }
        //根據不同操作頁面組裝不同格式
        switch (vo.getOperatePageCode()) {
            case 14:
                //早盤操盤
            case 15:
                //早盤操盤 次要玩法
            case 17:
                //滾球操盤
            case 18:
                //滾球操盤 次要玩法
                return updateMarketTradeTypeFormat(rcsOperateLog, vo);
        }
        return null;
    }

    private RcsOperateLog updateMarketTradeTypeFormat(RcsOperateLog rcsOperateLog, LogAllBean vo) {
        Map<String, Object> oriVo = vo.getBeforeParams();
        rcsOperateLog.setOperatePageCode(vo.getOperatePageCode());
        rcsOperateLog.setMatchId(vo.getMatchId());
        rcsOperateLog.setPlayId(vo.getCategoryId());
        rcsOperateLog.setObjectIdByObj(vo.getCategoryId());
        rcsOperateLog.setObjectNameByObj(getPlayNameZsEn(vo.getCategoryId(), Math.toIntExact(vo.getSportId())));
        rcsOperateLog.setExtObjectIdByObj(vo.getMatchManageId()==null?getMassageId(vo.getMatchId()):vo.getMatchManageId());
        rcsOperateLog.setExtObjectNameByObj(montageEnAndZsIs(vo.getTeamList(),vo.getMatchId()));
        rcsOperateLog.setBeforeValByObj(TradeEnum.getByTradeType((Integer) oriVo.get("tradeType")).getValue());
        rcsOperateLog.setAfterValByObj(TradeEnum.getByTradeType(vo.getTradeType()).getValue());
        return rcsOperateLog;
    }

    private String getMassageId(Long matchId){
        String value="";
        StandardMatchInfo matchMarketLiveBean  = standardMatchInfoMapper.selectById(matchId);
        value = null == matchMarketLiveBean ? matchId.toString() : matchMarketLiveBean.getMatchManageId();
        return value;

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
}
