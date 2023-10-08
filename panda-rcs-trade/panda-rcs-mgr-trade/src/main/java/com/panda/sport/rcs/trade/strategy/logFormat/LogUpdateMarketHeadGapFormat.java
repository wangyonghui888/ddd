package com.panda.sport.rcs.trade.strategy.logFormat;

import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.LanguageInternation;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.trade.strategy.LogFormatStrategy;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplatePlayMargainService;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 操盤日誌(modifyMatchTempByLevelTemp)
 * 設置-特殊事件状态修改
 */
@Service
public class LogUpdateMarketHeadGapFormat extends LogFormatStrategy {

    @Resource
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Resource
    private ProducerSendMessageUtils sendMessage;
    @Resource
    private IRcsTournamentTemplatePlayMargainService rcsTournamentTemplatePlayMargainService;

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

        rcsOperateLog.setParameterName(OperateLogEnum.NONE.getName());

        RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargin = rcsTournamentTemplatePlayMargainService.getRcsTournamentTemplateConfig(param);
        // 球头改变独赢关盘
        BigDecimal marketAdjustRange = rcsTournamentTemplatePlayMargin.getMarketAdjustRange();

        // +-,会更改所有盘口值,都需要记录
        for (BigDecimal d : param.getBeforeParams().getMarketValues()) {
            boolean home = true;
            String team = "H";
            if (d.compareTo(BigDecimal.ZERO) > 0) {
                // 客队调整盘口也显示-
                team = "A-";
                home = false;
            }
            rcsOperateLog.setBeforeValByObj(team + d);

            BigDecimal newMarketValue = getAfterVal(d, param, marketAdjustRange);
            // 如果前后的盘口值出现正负数,则主客队让球头
            boolean change = (d.compareTo(BigDecimal.ZERO) > 0 && newMarketValue.compareTo(BigDecimal.ZERO) <= 0) || (d.compareTo(BigDecimal.ZERO) < 0 && newMarketValue.compareTo(BigDecimal.ZERO) >= 0);
            rcsOperateLog.setAfterValByObj(change ? home ? "A-" + newMarketValue.stripTrailingZeros().toPlainString() : "H" + newMarketValue.stripTrailingZeros().toPlainString() : team + newMarketValue.stripTrailingZeros().toPlainString());
            pushMessage(rcsOperateLog);
        }
        return null;
    }

    private void pushMessage(RcsOperateLog rcsOperateLog) {
        sendMessage.sendMessage("rcs_log_operate", "", "", rcsOperateLog);
    }

    /**
     *
     这里设置的值大于1并且是整数的情况下，
     等价于幅度=1，操作了三次，全场让球没有0盘和0.5盘，其他半场让球玩法只是没有0盘，

     这里设置的值大于1并且是非整数的情况下，
     等价于幅度=0.5，操作了（设置值/0.5）次，全场让球没有0盘和0.5盘，其他半场让球玩法只是没有0盘，
     这种就相当于开平盘了，

     1.5-1 = 0.5 0.5不符合要求
     0.5-1 = -0.5   -0.5不符合要求
     -0.5-1 = -1.5    -1.5符合要求   所以计算的盘口值就是-1.5
     */
    private BigDecimal getAfterVal(BigDecimal beforeVal, RcsMatchMarketConfig param, BigDecimal marketAdjustRange){
        int loop = marketAdjustRange.stripTrailingZeros().toPlainString().contains(".") ? marketAdjustRange.divide(new BigDecimal("0.5"), 0, RoundingMode.HALF_UP).intValue() : marketAdjustRange.intValue();
        BigDecimal multi = marketAdjustRange.stripTrailingZeros().toPlainString().contains(".") ? new BigDecimal("0.5") : BigDecimal.ONE;
        // 客队让球,-就是1.5 + 数据源盘口差变动幅度 ->3, +就是1.5 + 数据源盘口差变动幅度 ->3,
        if (beforeVal.compareTo(BigDecimal.ZERO) > 0) {
            multi = multi.multiply(param.getMarketHeadGapOpt());
        }else {
            // -9 + (-1)*-1/1*数据源盘口差变动幅度
            multi = param.getMarketHeadGapOpt().multiply(new BigDecimal(NumberUtils.INTEGER_MINUS_ONE)).multiply(multi);
        }
        BigDecimal afterVal = BigDecimal.ZERO;
        Long playId = param.getPlayId();
        while(loop > 0) {
            afterVal = beforeVal.add(multi);
            beforeVal = afterVal;
            if (Basketball.Main.FULL_TIME.getHandicap().equals(playId)) {
//              39号玩法不能出现正负0.5和0球头
                if (afterVal.abs().compareTo(BigDecimal.ONE) < 0) {
                    continue;
                }
                loop--;
            } else {
//              19 上半场让分，46 第1节让分，52 第2节让分，58 第3节让分，64 第4节让分，143 下半场让分  不能出现0球头
                if (afterVal.abs().compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                loop--;
            }
        }
        return afterVal;
    }

    private String getPlayName(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
    }
}
