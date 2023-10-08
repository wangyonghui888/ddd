package com.panda.rcs.logService.strategy.logFormat;

import com.panda.rcs.logService.Enum.AutoCloseMarketEnum;
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
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 操盤日誌(modifyPlayMargain)
 * 設置-盤口參數調整-最大盤口數/盤口賠付預警/支持串關/賠率(水差)變動幅度/自動關盤時間設置
 */
@Component
public class LogModifyPlayMargainFormat extends LogFormatStrategy {

	@Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;

    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;


    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,  LogAllBean param ) {
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

        rcsOperateLog.setMatchId(param.getMatchId());
        rcsOperateLog.setObjectIdByObj(param.getPlayId());

        return getRcsOperateLog(rcsOperateLog, param);
    }

    private RcsOperateLog getRcsOperateLog(RcsOperateLog rcsOperateLog, LogAllBean param) {
        String playName = getPlayNameZsEn(param.getPlayId().longValue(), param.getSportId());
        rcsOperateLog.setObjectNameByObj(playName);
        rcsOperateLog.setExtObjectIdByObj(param.getMatchManageId());
        rcsOperateLog.setExtObjectNameByObj(montageEnAndZsIs(param.getTeamList(),param.getMatchId()));

        //最大盤口數/盤口賠付預警
        if (Objects.nonNull(param.getMarketCount())) {
            marketSettingFormat(rcsOperateLog, param, playName);

            //支持串關
        } else if (Objects.nonNull(param.getIsSeries())) {
            return isSeriesFormat(rcsOperateLog, param, playName);

            //賠率(水差)變動幅度
        } else if (Objects.nonNull(param.getOddsAdjustRange())) {
            return oddsAdjustRangeFormat(rcsOperateLog, param, playName);

            //自動關盤時間設置
        } else if (Objects.nonNull(param.getAutoCloseMarket())) {
            autoCloseMarketFormat(rcsOperateLog, param, playName);
            //自动开盘时间
        } else if (Objects.nonNull(param.getAutoOpenMarket())) {
            openCloseMarketFormat(rcsOperateLog, param, playName);
            //手动操盘相邻盘口差
        }

        else if (Objects.nonNull(param.getManualMarketNearDiff())) {
            manualMarketNearDiffFormat(rcsOperateLog, param, playName);

            //出涨自动封盘
        } else if (Objects.nonNull(param.getIfWarnSuspended())) {
            return ifWarnSuspendedFormat(rcsOperateLog, param, playName);

            // 相邻盘口赔率分差
        } else if (Objects.nonNull(param.getMarketNearDiff())) {
            marketNearDiffFormat(rcsOperateLog, param, playName);
            // 预约投注开关
        } else if (Objects.nonNull(param.getPendingOrderStatus())) {
            return pendingOrderStatusFormat(rcsOperateLog, param, playName);

            // 跳水最大值
        } else if (Objects.nonNull(param.getOddsMaxValue())) {
            return oddsMaxValueFormat(rcsOperateLog, param, playName);

            // 拒单赔率百分比差值
        } else if (Objects.nonNull(param.getOddsChangeValue())) {
            oddsChangeValueFormat(rcsOperateLog, param, playName);
        }
        return null;
    }

    /**
     * 最大盤口數/盤口賠付預警 格式
     *
     * @param rcsOperateLog
     * @param param
     */
    private void marketSettingFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {

        if (!param.getMarketCount().equals(param.getBeforeParams().get("marketCount"))) {
            RcsOperateLog marketCountLog = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, marketCountLog);
            marketCountLog.setParameterName(OperateLogEnum.MARKET_COUNT.getName());
            marketCountLog.setBeforeValByObj(param.getBeforeParams().get("marketCount"));
            marketCountLog.setAfterValByObj(param.getMarketCount());
            pushMessage(marketCountLog);
        }

        if (Objects.nonNull(param.getMarketWarn()) &&
                !param.getMarketWarn().equals(param.getBeforeParams().get("marketWarn"))) {
            RcsOperateLog marketWarnLog = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, marketWarnLog);
            marketWarnLog.setParameterName(OperateLogEnum.MARKET_WARN.getName());
            marketWarnLog.setBeforeValByObj(param.getBeforeParams().get("marketWarn"));
            marketWarnLog.setAfterValByObj(param.getMarketWarn());
            pushMessage(marketWarnLog);
        }

        if (Objects.nonNull(param.getViceMarketRatio()) &&
                !param.getViceMarketRatio().equals(param.getBeforeParams().get("viceMarketRatio"))) {
            RcsOperateLog viceMarketRatioLog = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, viceMarketRatioLog);
            viceMarketRatioLog.setParameterName(OperateLogEnum.VICE_MARKET_RATIO.getName());
            viceMarketRatioLog.setBeforeValByObj(param.getBeforeParams().get("viceMarketRatio"));
            viceMarketRatioLog.setAfterValByObj(param.getViceMarketRatio());
            pushMessage(viceMarketRatioLog);
        }
    }

    /**
     * 支持串關 格式
     *
     * @param rcsOperateLog
     * @param param
     * @return
     */
    private RcsOperateLog isSeriesFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {
        if (!param.getIsSeries().equals(param.getBeforeParams().get("isSeries"))) {
            rcsOperateLog.setParameterName(OperateLogEnum.IS_SERIES.getName());
            rcsOperateLog.setBeforeValByObj(getIsSeriesName(Integer.parseInt(param.getBeforeParams().get("isSeries").toString())));
            rcsOperateLog.setAfterValByObj(getIsSeriesName(param.getIsSeries()));
            return rcsOperateLog;
        }
        return null;
    }

    private RcsOperateLog ifWarnSuspendedFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {
        if (!param.getIfWarnSuspended().equals(param.getBeforeParams().get("ifWarnSuspended"))) {
            rcsOperateLog.setParameterName(OperateLogEnum.AUTO_PAUSE_INCREASING.getName());
            rcsOperateLog.setBeforeValByObj(getIsSeriesName(Integer.parseInt(param.getBeforeParams().get("isSeries").toString())));
            rcsOperateLog.setAfterValByObj(getIsSeriesName(param.getIsSeries()));
            return rcsOperateLog;
        }
        return null;
    }

    private RcsOperateLog oddsMaxValueFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {
        if (!param.getOddsMaxValue().equals(param.getBeforeParams().get("oddsMaxValue"))) {
            rcsOperateLog.setParameterName(OperateLogEnum.MAXIMUM_SPREAD_DROP.getName());
            rcsOperateLog.setBeforeValByObj((param.getBeforeParams().get("oddsMaxValue")));
            rcsOperateLog.setAfterValByObj((param.getOddsMaxValue()));
            return rcsOperateLog;
        }
        return null;
    }

    private RcsOperateLog pendingOrderStatusFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {
            rcsOperateLog.setParameterName(OperateLogEnum.BET_BOOKING_SWITCH.getName());
            rcsOperateLog.setBeforeValByObj(getSeriesName(param.getPendingOrderStatus()));
            rcsOperateLog.setAfterValByObj(getIsSeriesName(param.getPendingOrderStatus()));
            return rcsOperateLog;
    }

    private void oddsChangeValueFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {
        if (Objects.nonNull(param.getOddsChangeStatus()) &&
                !param.getOddsChangeStatus().equals(param.getBeforeParams().get("oddsChangeStatus"))) {
            RcsOperateLog marketWarnLog = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, marketWarnLog);
            marketWarnLog.setParameterName(OperateLogEnum.ODDS_DIFF_PERCENTAGE.getName());
            rcsOperateLog.setBeforeValByObj(getIsSeriesName(Integer.parseInt(param.getBeforeParams().get("oddsChangeStatus").toString())));
            rcsOperateLog.setAfterValByObj(getIsSeriesName(param.getOddsChangeStatus()));
            pushMessage(marketWarnLog);
        }

        if (Objects.nonNull(param.getOddsChangeValue()) &&
                !param.getOddsChangeValue().equals(param.getBeforeParams().get("oddsChangeValue"))) {
            RcsOperateLog viceMarketRatioLog = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, viceMarketRatioLog);
            viceMarketRatioLog.setParameterName(OperateLogEnum.ODDS_DIFF_PERCENTAGE.getName());
            viceMarketRatioLog.setBeforeValByObj(param.getBeforeParams().get("oddsChangeValue"));
            viceMarketRatioLog.setAfterValByObj(param.getOddsChangeValue());
            pushMessage(viceMarketRatioLog);
        }
    }
    /**
     * 賠率(水差)變動幅度 格式
     *
     * @param rcsOperateLog
     * @param param
     * @return
     */
    private RcsOperateLog oddsAdjustRangeFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {

        // 赔率源盤口差变动幅度
          if (Objects.nonNull(param.getMarketAdjustRange())
                  &&!param.getMarketAdjustRange().equals(param.getBeforeParams().get("marketAdjustRange"))) {
              rcsOperateLog.setParameterName(OperateLogOneEnum.MARKET_ADJUST_RANGE.getName());
              rcsOperateLog.setBeforeValByObj(param.getBeforeParams().get("marketAdjustRange"));
              rcsOperateLog.setAfterValByObj(param.getMarketAdjustRange());
              pushMessage(rcsOperateLog);
          }
        if (!param.getOddsAdjustRange().equals(param.getBeforeParams().get("oddsAdjustRange"))) {
            rcsOperateLog.setParameterName(OperateLogEnum.ODDS_ADJUST_RANGE.getName());
            rcsOperateLog.setBeforeValByObj(param.getBeforeParams().get("oddsAdjustRange"));
            rcsOperateLog.setAfterValByObj(param.getOddsAdjustRange());
            return rcsOperateLog;
        }
        return null;
    }

    /**
     * 自动关盘时间设置 格式
     *
     * @param rcsOperateLog
     * @param param
     * @return
     */
    private void autoCloseMarketFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {

        //自动关盘时间设置
        if (!param.getAutoCloseMarket().equals(param.getBeforeParams().get("autoCloseMarket"))) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(OperateLogEnum.AUTO_CLOSE_MARKET.getName());
            logBean.setBeforeValByObj(AutoCloseMarketEnum.getValue(Integer.parseInt(param.getBeforeParams().get("autoCloseMarket").toString())));
            logBean.setAfterValByObj(AutoCloseMarketEnum.getValue(param.getAutoCloseMarket()));
            pushMessage(logBean);
        }
        //比赛进程时间
        if (Objects.nonNull(param.getMatchProgressTime()) &&
                !param.getMatchProgressTime().equals(param.getBeforeParams().get("matchProgressTime"))) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(AutoCloseMarketEnum.getValue(param.getAutoCloseMarket()) + OperateLogEnum.MATCH_PROGRESS_TIME.getName());
            logBean.setBeforeValByObj(secondToTime(Long.parseLong(param.getBeforeParams().get("matchProgressTime").toString())));
            logBean.setAfterValByObj(secondToTime(param.getMatchProgressTime()));
            pushMessage(logBean);
        }
        //补时时间
        if (Objects.nonNull(param.getInjuryTime()) &&
                !param.getInjuryTime().equals(param.getBeforeParams().get("injuryTime"))) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(AutoCloseMarketEnum.getValue(param.getAutoCloseMarket()) + OperateLogEnum.INJURY_TIME.getName());
            logBean.setBeforeValByObj(secondToTime(Long.parseLong(param.getBeforeParams().get("injuryTime").toString())));
            logBean.setAfterValByObj(secondToTime(param.getInjuryTime()));
            pushMessage(logBean);
        }
    }

    /**
     * 自动开盘时间设置 格式
     *
     * @param rcsOperateLog
     * @param param
     * @return
     */
    private void openCloseMarketFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {
        //自动开盘时间设置
        if (!param.getAutoOpenMarket().equals(param.getBeforeParams().get("autoOpenMarket"))||
                !param.getAutoOpenTime().equals(param.getBeforeParams().get("autoOpenTime"))
        ) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(OperateLogOneEnum.AUTO_OPEN_MARKET.getName());
            if(null!=param.getBeforeParams() &&null!=param.getBeforeParams().get("autoOpenMarket")) {
                logBean.setBeforeValByObj(AutoCloseMarketEnum.getValue(Integer.parseInt(param.getBeforeParams().get("autoOpenMarket").toString()))
                        + " 时间: " + secondToTime(Long.parseLong((param.getBeforeParams().get("autoOpenTime")==null?"0":param.getBeforeParams().get("autoOpenTime").toString()))));
            }
            logBean.setAfterValByObj(AutoCloseMarketEnum.getValue(param.getAutoOpenMarket()) +" 时间: "+
                    secondToTime(Long.parseLong((param.getAutoOpenTime()==null?"0":param.getAutoOpenTime().toString()))));
            pushMessage(logBean);
        }




    }



    /**
     * 手动操盘相邻盘口差 格式
     *
     * @param rcsOperateLog
     * @param param
     * @param playName
     */
    private void manualMarketNearDiffFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {

        //手动操盘相邻盘口差
        if (!param.getManualMarketNearDiff().equals(param.getBeforeParams().get("manualMarketNearDiff"))) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(OperateLogEnum.MANUAL_MARKET_NEAR_DIFF.getName());
            logBean.setBeforeValByObj(param.getBeforeParams().get("manualMarketNearDiff"));
            logBean.setAfterValByObj(param.getManualMarketNearDiff());
            pushMessage(logBean);
        }
        //手动操盘相邻盘口赔率差
        if (Objects.nonNull(param.getManualMarketNearOddsDiff()) &&
                !param.getManualMarketNearOddsDiff().equals(param.getBeforeParams().get("manualMarketNearOddsDiff"))) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(OperateLogEnum.MANUAL_MARKET_NEAR_ODDS_DIFF.getName());
            logBean.setBeforeValByObj(param.getBeforeParams().get("manualMarketNearOddsDiff"));
            logBean.setAfterValByObj(param.getManualMarketNearOddsDiff());
            pushMessage(logBean);
        }
    }

    /**
     * 手动操盘相邻盘口差 格式
     *
     * @param rcsOperateLog
     * @param param
     * @param playName
     */
    private void marketNearDiffFormat(RcsOperateLog rcsOperateLog, LogAllBean param, String playName) {

        //手动操盘相邻盘口差
        if (!param.getMarketNearDiff().equals(param.getBeforeParams().get("marketNearDiff"))) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(OperateLogEnum.MANUAL_MARKET_NEAR_DIFF.getName());
            logBean.setBeforeValByObj(param.getBeforeParams().get("marketNearDiff"));
            logBean.setAfterValByObj(param.getMarketNearDiff());
            pushMessage(logBean);
        }
        //手动操盘相邻盘口赔率差
        if (Objects.nonNull(param.getMarketNearOddsDiff()) &&
                !param.getMarketNearOddsDiff().equals(param.getBeforeParams().get("marketNearOddsDiff"))) {
            RcsOperateLog logBean = new RcsOperateLog();
            BeanUtils.copyProperties(rcsOperateLog, logBean);
            logBean.setParameterName(OperateLogEnum.MANUAL_MARKET_NEAR_ODDS_DIFF.getName());
            logBean.setBeforeValByObj(param.getBeforeParams().get("marketNearOddsDiff"));
            logBean.setAfterValByObj(param.getMarketNearOddsDiff());
            pushMessage(logBean);
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

    /**
     * 轉換狀態碼
     *
     * @param isSeries
     * @return
     */
    private String getIsSeriesName(Integer isSeries) {
        switch (isSeries) {
            case 0:
                return "否";
            case 1:
                return "是";
            default:
                return "";
        }
    }

    /**
     * 轉換狀態碼
     *
     * @param isSeries
     * @return
     */
    private String getSeriesName(Integer isSeries) {
        switch (isSeries) {
            case 1:
                return "否";
            case 0:
                return "是";
            default:
                return "";
        }
    }

    private void pushMessage(RcsOperateLog rcsOperateLog) {
        rcsOperateLogMapper.insert(rcsOperateLog);
    }

}
