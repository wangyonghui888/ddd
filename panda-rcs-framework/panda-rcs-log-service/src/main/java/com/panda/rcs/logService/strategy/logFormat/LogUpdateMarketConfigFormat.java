package com.panda.rcs.logService.strategy.logFormat;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import com.panda.rcs.logService.Enum.TradeStatusEnum;
import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.mapper.RcsMatchMarketConfigMapper;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.LanguageInternation;
import com.panda.rcs.logService.vo.RcsMatchMarketConfig;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 操盤日誌(updateMatchMarketConfig)
 * 調價窗口設定
 */
@Slf4j
@Component
public class LogUpdateMarketConfigFormat extends LogFormatStrategy {

    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;
    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;


    //獨贏玩法
    private List<Long> singlePlayIdList = Arrays.asList(1L, 17L);

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean config) {
        if(BaseUtils.isTrue(config.getBeforeParams())){
            return null;
        }
        rcsOperateLog.setOperatePageCode(config.getOperatePageCode());
        rcsOperateLog.setMatchId(config.getMatchId());
        rcsOperateLog.setPlayId(config.getPlayId());

        filterDiffTypeToLog(rcsOperateLog, config);
        return null; //清空，避免原先邏輯多發送一次MQ
    }

    private void filterDiffTypeToLog(RcsOperateLog rcsOperateLog, LogAllBean newConfig) {
        LogAllBean oriConfig = BaseUtils.mapObject(newConfig.getBeforeParams(),LogAllBean.class);

        //开关封锁
        if (Objects.nonNull(newConfig.getMarketStatus()) &&
                !newConfig.getMarketStatus().equals(oriConfig.getMarketStatus())) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, getTradeStatusName(oriConfig.getMarketStatus()), getTradeStatusName(newConfig.getMarketStatus()));
            transOperatePageCode(newConfig, logBean);
            logBean.setBehavior(OperateLogEnum.MARKET_STATUS.getName());
            logBean.setParameterName(OperateLogEnum.NONE.getName());
            logBean.setObjectIdByObj(newConfig.getMarketId());
            logBean.setObjectNameByObj(transMarketValue(newConfig.getHomeMarketValue().abs()));
            //extObjectId = 赛事ID/玩法ID
            //String playName = getPlayName(newConfig.getPlayId(), newConfig.getSportId());
            //StringBuilder matchName = new StringBuilder().append(newConfig.getHome()).append(" VS ").append(newConfig.getAway());
            StringBuilder extObjectId = new StringBuilder().append(newConfig.getMatchManageId()==null?
                            getMassageId(newConfig.getMatchId()):newConfig.getMatchManageId())
                    .append(" / ").append(newConfig.getPlayId());
            StringBuilder extObjectName = new StringBuilder().append(getMatchName(newConfig.getTeamList(),newConfig.getMatchId()))
                    .append(" / ").append(getPlayNameZs(newConfig.getPlayId(), newConfig.getSportId()));
            StringBuilder extObjectNameEn = new StringBuilder().append(getMatchNameEn(newConfig.getTeamList(),newConfig.getMatchId()))
                    .append(" / ").append(getPlayNameEn(newConfig.getPlayId(), newConfig.getSportId()));
            logBean.setExtObjectIdByObj(extObjectId);
            logBean.setExtObjectNameByObj(montageEnAndZs(extObjectNameEn.toString(),extObjectName.toString()));
            pushMessage(logBean);
        }
        //Malay Spread
        if (Objects.nonNull(newConfig.getMargin()) &&
                newConfig.getMargin().compareTo(oriConfig.getMargin()) != 0) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getMargin(), newConfig.getMargin());
            logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
            logBean.setParameterName(OperateLogOneEnum.MALAY_SPREAD.getName());
            configChangeFormat(logBean, newConfig);
            pushMessage(logBean);
        }

        //最大投注最大赔付
        if (Objects.nonNull(newConfig.getMaxSingleBetAmount()) &&
                newConfig.getMaxSingleBetAmount().compareTo(oriConfig.getMaxSingleBetAmount()) != 0) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getMaxSingleBetAmount(), newConfig.getMaxSingleBetAmount());
            logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
            logBean.setParameterName(OperateLogEnum.MAX_SINGLE_BET_AMOUNT.getName());
            configChangeFormat(logBean, newConfig);
            pushMessage(logBean);
        }
        //最小賠率
        if (Objects.nonNull(newConfig.getMinOdds()) &&
                newConfig.getMinOdds().compareTo(oriConfig.getMinOdds()) != 0) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getMinOdds(), newConfig.getMinOdds());
            logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
            //獨贏玩法為欧赔 其餘為马来赔
            if (singlePlayIdList.contains(oriConfig.getPlayId())) {
                logBean.setParameterName(OperateLogEnum.MIN_DECIMAL_ODDS.getName());
            } else {
                logBean.setParameterName(OperateLogEnum.MIN_MALAY_ODDS.getName());
            }
            configChangeFormat(logBean, newConfig);
            pushMessage(logBean);
        }

        //最大賠率
        if (Objects.nonNull(newConfig.getMaxOdds()) &&
                newConfig.getMaxOdds().compareTo(oriConfig.getMaxOdds()) != 0) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getMaxOdds(), newConfig.getMaxOdds());
            logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
            //獨贏玩法為欧赔 其餘為马来赔
            if (singlePlayIdList.contains(oriConfig.getPlayId())) {
                logBean.setParameterName(OperateLogEnum.MAX_DECIMAL_ODDS.getName());
            } else {
                logBean.setParameterName(OperateLogEnum.MAX_MALAY_ODDS.getName());
            }
            configChangeFormat(logBean, newConfig);
            pushMessage(logBean);
        }

        //自動水差
        BigDecimal oriAwayAutoChangeRate = Objects.nonNull(oriConfig.getAwayAutoChangeRate()) ? new BigDecimal(oriConfig.getAwayAutoChangeRate()) : BigDecimal.ZERO;
        BigDecimal newAwayAutoChangeRate = Objects.nonNull(newConfig.getAwayAutoChangeRate()) ? new BigDecimal(newConfig.getAwayAutoChangeRate()) : BigDecimal.ZERO;
        if (newAwayAutoChangeRate.compareTo(oriAwayAutoChangeRate) != 0) {
            RcsOperateLog oddsLog = initialLogBean(rcsOperateLog, oriConfig.getAwayAutoChangeRate(), newConfig.getAwayAutoChangeRate());
            oddsLog.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
            oddsLog.setParameterName(OperateLogEnum.AWAY_AUTO_CHANGE_RATE.getName());
            configChangeFormat(oddsLog, newConfig);
            pushMessage(oddsLog);
        }
        //M无盤口值
         if(Objects.isNull(newConfig.getMarketId())&&Objects.isNull(oriConfig.getMarketId())){
             RcsOperateLog oddsLog = initialLogBean(rcsOperateLog, "-", OperateLogEnum.MARKET_CREATE.getName());
             oddsLog.setBehavior(OperateLogEnum.MARKET_CREATE.getName());
             oddsLog.setOperatePageCode(newConfig.getOperatePageCode());
             oddsLog.setExtObjectName(montageEnAndZsIs(newConfig.getTeamList(),newConfig.getMatchId()));
             oddsLog.setObjectName(getPlayNameZsEn(newConfig.getPlayId(), newConfig.getSportId()));
             oddsLog.setObjectId(newConfig.getPlayId().toString());
             pushMessage(oddsLog);
         }

        //平衡值 0 投注额 1 投注额/赔付值组合
        if (Objects.nonNull(newConfig.getBalanceOption()) &&
                !newConfig.getBalanceOption().equals(oriConfig.getBalanceOption())) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, transBalanceOption(oriConfig.getBalanceOption()), transBalanceOption(newConfig.getBalanceOption()));
            logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
            logBean.setParameterName(OperateLogEnum.BALANCE_OPTION_RULE.getName());
            configChangeFormat(logBean, newConfig);
            pushMessage(logBean);
        }

        //联动模式：0(否),1(是)
        if (Objects.nonNull(newConfig.getLinkageMode()) &&
                !newConfig.getLinkageMode().equals(oriConfig.getLinkageMode())) {
            RcsOperateLog logBean = initialLogBean(rcsOperateLog, getTrueFalse(oriConfig.getLinkageMode()), getTrueFalse(newConfig.getLinkageMode()));
            logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
            logBean.setParameterName(OperateLogEnum.LINKAGE_MODE.getName());
            configChangeFormat(logBean, newConfig);
            pushMessage(logBean);
        }

        //跳赔规则 0 累计/单枪 1 差额累计
        if (Objects.nonNull(newConfig.getOddChangeRule())) {

            if (!newConfig.getOddChangeRule().equals
                    (oriConfig.getOddChangeRule())) {
                RcsOperateLog logBean = initialLogBean(rcsOperateLog, transOddChangeRule(oriConfig.getOddChangeRule()), transOddChangeRule(newConfig.getOddChangeRule()));
                logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                logBean.setParameterName(OperateLogEnum.ODD_CHANGE_RULE.getName());
                configChangeFormat(logBean, newConfig);
                pushMessage(logBean);
            }

            //累计/单枪
            if (0 == newConfig.getOddChangeRule()) {
                //限额（单枪跳分）
                if (Objects.nonNull(newConfig.getHomeSingleMaxAmount()) &&
                        !newConfig.getHomeSingleMaxAmount().equals(oriConfig.getHomeSingleMaxAmount())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeSingleMaxAmount(), newConfig.getHomeSingleMaxAmount());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.HOME_SINGLE_MAX_AMOUNT.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //限额（累计跳分）
                if (Objects.nonNull(newConfig.getHomeMultiMaxAmount()) &&
                        !newConfig.getHomeMultiMaxAmount().equals(oriConfig.getHomeMultiMaxAmount())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeMultiMaxAmount(), newConfig.getHomeMultiMaxAmount());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.HOME_MULTI_MAX_AMOUNT.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //累计上盘变化（单枪跳分）
                if (Objects.nonNull(newConfig.getHomeSingleOddsRate()) &&
                        !newConfig.getHomeSingleOddsRate().equals(oriConfig.getHomeSingleOddsRate())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeSingleOddsRate(), newConfig.getHomeSingleOddsRate());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.HOME_SINGLE_ODDS_RATE.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //累计上盘变化（累计跳分）
                if (Objects.nonNull(newConfig.getHomeMultiOddsRate()) &&
                        !newConfig.getHomeMultiOddsRate().equals(oriConfig.getHomeMultiOddsRate())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeMultiOddsRate(), newConfig.getHomeMultiOddsRate());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.HOME_MULTI_ODDS_RATE.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //累计下盘变化（单枪跳分）
                if (Objects.nonNull(newConfig.getAwaySingleOddsRate()) &&
                        !newConfig.getAwaySingleOddsRate().equals(oriConfig.getAwaySingleOddsRate())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getAwaySingleOddsRate(), newConfig.getAwaySingleOddsRate());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.AWAY_SINGLE_ODDS_RATE.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //累计下盘变化（累计跳分）
                if (Objects.nonNull(newConfig.getAwayMultiOddsRate()) &&
                        !newConfig.getAwayMultiOddsRate().equals(oriConfig.getAwayMultiOddsRate())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getAwayMultiOddsRate(), newConfig.getAwayMultiOddsRate());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.AWAY_MULTI_ODDS_RATE.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //差额累计
            } else if (1 == newConfig.getOddChangeRule()) {
                //一级累计跳分限额值
                if (Objects.nonNull(newConfig.getHomeLevelFirstMaxAmount()) &&
                        !newConfig.getHomeLevelFirstMaxAmount().equals(oriConfig.getHomeLevelFirstMaxAmount())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelFirstMaxAmount(), newConfig.getHomeLevelFirstMaxAmount());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.HOME_LEVEL_FIRST_MAX_AMOUNT.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //二级累计跳分限额值
                if (Objects.nonNull(newConfig.getHomeLevelSecondMaxAmount()) &&
                        !newConfig.getHomeLevelSecondMaxAmount().equals(oriConfig.getHomeLevelSecondMaxAmount())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelSecondMaxAmount(), newConfig.getHomeLevelSecondMaxAmount());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.HOME_LEVEL_SECOND_MAX_AMOUNT.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //累计上盘变化（一级累计跳分）
                if (Objects.nonNull(newConfig.getHomeLevelFirstOddsRate()) &&
                        !newConfig.getHomeLevelFirstOddsRate().equals(oriConfig.getHomeLevelFirstOddsRate())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelFirstOddsRate(), newConfig.getHomeLevelFirstOddsRate());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.HOME_LEVEL_FIRST_ODDS_RATE.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //累计下盘变化（一级累计跳分）
                if (Objects.nonNull(newConfig.getAwayLevelFirstOddsRate()) &&
                        !newConfig.getAwayLevelFirstOddsRate().equals(oriConfig.getAwayLevelFirstOddsRate())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getAwayLevelFirstOddsRate(), newConfig.getAwayLevelFirstOddsRate());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.AWAY_LEVEL_FIRST_ODDS_RATE.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //累计上盘变化（二级累计跳分）
                if (Objects.nonNull(newConfig.getHomeLevelSecondOddsRate()) &&
                        !newConfig.getHomeLevelSecondOddsRate().equals(oriConfig.getHomeLevelSecondOddsRate())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelSecondOddsRate(), newConfig.getHomeLevelSecondOddsRate());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.HOME_LEVEL_SECOND_ODDS_RATE.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }

                //累计上盘变化（二级累计跳分）
                if (Objects.nonNull(newConfig.getAwayLevelSecondOddsRate()) &&
                        !newConfig.getAwayLevelSecondOddsRate().equals(oriConfig.getAwayLevelSecondOddsRate())) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getAwayLevelSecondOddsRate(), newConfig.getAwayLevelSecondOddsRate());
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.AWAY_LEVEL_SECOND_ODDS_RATE.getName());
                    configChangeFormat(logBean, newConfig);
                    pushMessage(logBean);
                }
                // 篮球日志
                if (Objects.nonNull(newConfig.getSportId()) && newConfig.getSportId() == 2) {
                    //限额
                    if (Objects.nonNull(newConfig.getHomeLevelFirstMaxAmount()) &&
                            !newConfig.getHomeLevelFirstMaxAmount().equals(oriConfig.getHomeLevelFirstMaxAmount())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelFirstMaxAmount(), newConfig.getHomeLevelFirstMaxAmount());
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.MAX_AMOINT.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //二级累计跳分限额值
                    if (Objects.nonNull(newConfig.getHomeLevelSecondMaxAmount()) &&
                            !newConfig.getHomeLevelSecondMaxAmount().equals(oriConfig.getHomeLevelSecondMaxAmount())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelSecondMaxAmount(), newConfig.getHomeLevelSecondMaxAmount());
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.HOME_LEVEL_SECOND_MAX_AMOUNT.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳分机制-倍数跳分
                    if (Objects.nonNull(newConfig.getIsMultipleJumpOdds()) &&
                            !newConfig.getIsMultipleJumpOdds().equals(oriConfig.getIsMultipleJumpOdds())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getIsMultipleJumpOdds()==1?"开":"关",
                                newConfig.getIsMultipleJumpOdds()==1?"开":"关");
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.TRIGGER_POINT_MULTIPLE_CHANGE_ODDS.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳分机制-二级累值跳分
                    if (Objects.nonNull(newConfig.getIsOpenJumpOdds()) &&
                            !newConfig.getIsOpenJumpOdds().equals(oriConfig.getIsOpenJumpOdds())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getIsOpenJumpOdds()==1?"开":"关",
                                newConfig.getIsOpenJumpOdds()==1?"开":"关");
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.TRIGGER_POINT_LEVEL_2_ACCUMULATIVE_JUMP_POINTS.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳盘机制-倍数跳盘
                    if (Objects.nonNull(newConfig.getIsMultipleJumpMarket()) &&
                            !newConfig.getIsMultipleJumpMarket().equals(oriConfig.getIsMultipleJumpMarket())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getIsMultipleJumpMarket()==1?"开":"关",
                                newConfig.getIsMultipleJumpMarket()==1?"开":"关");
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.AUTO_LINE_CHANGE_MULTIPLE_CHANGE_HANDICAP.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳盘机制-二级累值跳盘
                    if (Objects.nonNull(newConfig.getIsOpenJumpMarket()) &&
                            !newConfig.getIsOpenJumpMarket().equals(oriConfig.getIsOpenJumpMarket())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getIsOpenJumpMarket()==1?"开":"关",
                                newConfig.getIsOpenJumpMarket()==1?"开":"关");
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.AUTO_LINE_CHANGE_LEVEL_2_ACCUMULATED_CHANGE.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳盘机制-累计下盘变化-下限
                    if (Objects.nonNull(newConfig.getAwayLevelFirstMarketRate()) &&
                            !newConfig.getAwayLevelFirstMarketRate().equals(oriConfig.getAwayLevelFirstMarketRate())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getAwayLevelFirstMarketRate(), newConfig.getAwayLevelFirstMarketRate());
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.AUTO_LINE_CHANGE_CUMULATIVE_LOWER_CHANGE_LEVEL_1_ACCUMULATED_CHANGE.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳盘机制-累计下盘变化-上限
                    if (Objects.nonNull(newConfig.getAwayLevelSecondMarketRate()) &&
                            !newConfig.getAwayLevelSecondMarketRate().equals(oriConfig.getAwayLevelSecondMarketRate())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getAwayLevelSecondMarketRate(), newConfig.getAwayLevelSecondMarketRate());
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.AUTO_LINE_CHANGE_CUMULATIVE_LOWER_CHANGE_LEVEL_2_ACCUMULATED_CHANGE.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳盘机制-累计上盘变化-下限
                    if (Objects.nonNull(newConfig.getHomeLevelFirstMarketRate()) &&
                            !newConfig.getHomeLevelFirstMarketRate().equals(oriConfig.getHomeLevelFirstMarketRate())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelFirstMarketRate(), newConfig.getHomeLevelFirstMarketRate());
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.AUTO_LINE_CHANGE_CUMULATIVE_UPPER_CHANGE_LEVEL_1_ACCUMULATED_CHANGE.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳盘机制-累计上盘变化-上限
                    if (Objects.nonNull(newConfig.getHomeLevelSecondMarketRate()) &&
                            !newConfig.getHomeLevelSecondMarketRate().equals(oriConfig.getHomeLevelSecondMarketRate())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelSecondMarketRate(), newConfig.getHomeLevelSecondMarketRate());
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.AUTO_LINE_CHANGE_CUMULATIVE_UPPER_CHANGE_LEVEL_2_ACCUMULATED_CHANGE.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳盘机制-限额-下限
                    if (Objects.nonNull(newConfig.getLevelFirstMarketAmount()) &&
                            !newConfig.getLevelFirstMarketAmount().equals(oriConfig.getLevelFirstMarketAmount())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getLevelFirstMarketAmount(), newConfig.getLevelFirstMarketAmount());
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.AUTO_LINE_CHANGE_LEVEL_1_ACCUMULATED_CHANGE_LIMIT.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                    //自动跳盘机制-限额-上限
                    if (Objects.nonNull(newConfig.getLevelSecondMarketAmount()) &&
                            !newConfig.getLevelSecondMarketAmount().equals(oriConfig.getLevelSecondMarketAmount())) {
                        RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getLevelSecondMarketAmount(), newConfig.getLevelSecondMarketAmount());
                        logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                        logBean.setParameterName(OperateLogEnum.AUTO_LINE_CHANGE_LEVEL_2_ACCUMULATED_CHANGE_LIMIT.getName());
                        configChangeFormat(logBean, newConfig);
                        pushMessage(logBean);
                    }
                }
            }
        } else {
            //限额
            if (Objects.nonNull(newConfig.getHomeLevelFirstMaxAmount()) &&
                    !newConfig.getHomeLevelFirstMaxAmount().equals(oriConfig.getHomeLevelFirstMaxAmount())) {
                RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelFirstMaxAmount(), newConfig.getHomeLevelFirstMaxAmount());
                logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                logBean.setParameterName(OperateLogEnum.MAX_AMOINT.getName());
                configChangeFormat(logBean, newConfig);
                pushMessage(logBean);
            }

            //概率变化(%)
            if (Objects.nonNull(newConfig.getHomeLevelFirstOddsRate()) &&
                    !newConfig.getHomeLevelFirstOddsRate().equals(oriConfig.getHomeLevelFirstOddsRate())) {
                RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriConfig.getHomeLevelFirstOddsRate(), newConfig.getHomeLevelFirstOddsRate());
                logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                logBean.setParameterName(OperateLogEnum.HOME_LEVEL_FIRST_ODDS_RATE_PERCENTAGE.getName());
                configChangeFormat(logBean, newConfig);
                pushMessage(logBean);

            }
        }

        //處裡oddsList內資料
        for (int i = 0; i < oriConfig.getOddsList().size(); i++) {
            Map<String, Object> oriOddsMap = oriConfig.getOddsList().get(i);
            Map<String, Object> newOddsMap = newConfig.getOddsList().get(i);

            //水差(自動模式才有水差)
            if (oriConfig.getDataSource().equals(0)) {
                BigDecimal oriMarketDiffValue = new BigDecimal(Optional.ofNullable(oriOddsMap.get("marketDiffValue")).orElse("0").toString());
                BigDecimal newMarketDiffValue = new BigDecimal(Optional.ofNullable(newOddsMap.get("marketDiffValue")).orElse("0").toString());
                if (oriMarketDiffValue.compareTo(newMarketDiffValue) != 0) {
                    RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriMarketDiffValue, newMarketDiffValue);
                    logBean.setBehavior(OperateLogEnum.CONFIG_UPDATE.getName());
                    logBean.setParameterName(OperateLogEnum.MARKET_DIFF.getName());
                    oddsChangeFormat(logBean, newConfig, newOddsMap);
                    pushMessage(logBean);
                }
            }

            //賠率
            BigDecimal oriOdds = getOddsBySpecialCase(oriConfig, oriOddsMap);
            BigDecimal newOdds = getOddsBySpecialCase(oriConfig, newOddsMap);
            if (oriOdds.compareTo(newOdds) != 0) {
                 newOdds=setOddsValue(newOdds);
                RcsOperateLog logBean = initialLogBean(rcsOperateLog, oriOdds, newOdds);
                logBean.setBehavior(OperateLogEnum.ODDS_UPDATE.getName());
                logBean.setParameterName(OperateLogEnum.NONE.getName());
                oddsChangeFormat(logBean, newConfig, newOddsMap);
                pushMessage(logBean);
            }

            //盤口值
            BigDecimal oriMarketValue = new BigDecimal(Optional.ofNullable(oriOddsMap.get("nameExpressionValue")).orElse("").toString());
            BigDecimal newMarketValue = new BigDecimal(Optional.ofNullable(newOddsMap.get("nameExpressionValue")).orElse("").toString());
            if (oriMarketValue.compareTo(newMarketValue) != 0) {
                RcsOperateLog logBean = initialLogBean(rcsOperateLog, transMarketValue(oriMarketValue), transMarketValue(newMarketValue));
                logBean.setBehavior(OperateLogEnum.MARKET_UPDATE.getName());
                logBean.setParameterName(OperateLogEnum.NONE.getName());
                oddsChangeFormat(logBean, newConfig, newOddsMap);
                pushMessage(logBean);
            }
        }
    }
    //赔率格式优化
    public BigDecimal setOddsValue(BigDecimal value){
      if(value.compareTo(BigDecimal.valueOf(3))==-1){
           return value;
      }else if(value.compareTo(BigDecimal.valueOf(5))==-1){
      BigDecimal newValue=value.setScale(1,BigDecimal.ROUND_DOWN).add(new BigDecimal(0.05));
      if(value.compareTo(newValue)>-1){
          return newValue.setScale(2, BigDecimal.ROUND_DOWN);
      }else{
          return value.setScale(1,BigDecimal.ROUND_DOWN);
      }
      }else if(value.compareTo(BigDecimal.valueOf(10))==-1){
         return value.setScale(1,BigDecimal.ROUND_DOWN);

      }else if(value.compareTo(BigDecimal.valueOf(20))==-1){
          BigDecimal newValue=new BigDecimal(value.intValue()).add(new BigDecimal(0.5));
          if(value.compareTo(newValue)>-1){
              return newValue;
          }else{
              return value.setScale(1,BigDecimal.ROUND_DOWN);
          }
      }else{
          return new BigDecimal(value.intValue());
      }
    }

    /**
     * 特殊規則判斷取賠率
     *
     * @param config
     * @param oddsMap
     * @return
     */
    private BigDecimal getOddsBySpecialCase(LogAllBean config, Map<String, Object> oddsMap) {
        //特殊抽水啟用
        if (Objects.nonNull(config.getIsSpecialPumping())&& config.getIsSpecialPumping() == 1) {
            return new BigDecimal(Optional.ofNullable(oddsMap.get("tsfieldOddsValue")).orElse("0").toString());
        } else {
            return new BigDecimal(Optional.ofNullable(oddsMap.get("fieldOddsValue")).orElse("0").toString());
        }
    }

    /**
     * 賠率/盤口格式
     *
     * @param logBean
     * @param newConfig
     * @param newHomeMap
     */
    private void oddsChangeFormat(RcsOperateLog logBean,LogAllBean newConfig, Map<String, Object> newHomeMap) {
        String oddsType = String.valueOf(newHomeMap.getOrDefault("oddsType", ""));
        // extObjectId = 赛事ID / 玩法ID
        //String playName = getPlayName(newConfig.getPlayId(), newConfig.getSportId());
        StringBuilder extObjectId = new StringBuilder().append(newConfig.getMatchManageId()==null?getMassageId(newConfig.getMatchId()):newConfig.getMatchManageId())
                .append(" / ").append(newConfig.getPlayId());
        StringBuilder extObjectName = new StringBuilder()
                .append(getMatchName(newConfig.getTeamList(),newConfig.getMatchId()))
                .append(" / ").append(getPlayNameZs(newConfig.getPlayId(), newConfig.getSportId()));
        StringBuilder extObjectNameEn = new StringBuilder()
                .append(getMatchNameEn(newConfig.getTeamList(),newConfig.getMatchId()))
                .append(" / ").append(getPlayNameEn(newConfig.getPlayId(), newConfig.getSportId()));
        logBean.setObjectIdByObj(newConfig.getMarketId());
        String marketValue = transMarketValue(newConfig.getHomeMarketValue().abs());
        StringBuilder objectName = new StringBuilder().append(oddsType).append("(").append(marketValue).append(")");
        logBean.setObjectNameByObj(objectName);
        logBean.setExtObjectIdByObj(extObjectId);
        logBean.setExtObjectNameByObj(montageEnAndZs(extObjectNameEn.toString(),
                extObjectName.toString()));
    }

    /**
     * 調整盤口設定格式
     *
     * @param logBean
     * @param newConfig
     */
    private void configChangeFormat(RcsOperateLog logBean, LogAllBean newConfig) {
        logBean.setObjectIdByObj(newConfig.getPlayId());
        String marketValue = transMarketValue(newConfig.getHomeMarketValue().abs());
        StringBuilder objectName = new StringBuilder().append(getPlayNameZs(newConfig.getPlayId(), newConfig.getSportId())).append("(").append(marketValue).append(")");
        StringBuilder objectNameEn = new StringBuilder().append(getPlayNameEn(newConfig.getPlayId(), newConfig.getSportId())).append("(").append(marketValue).append(")");
        logBean.setObjectNameByObj(montageEnAndZs(objectNameEn.toString(),objectName.toString()));
        logBean.setExtObjectIdByObj(newConfig.getMatchManageId()==null?getMassageId(newConfig.getMatchId()):newConfig.getMatchManageId());
        //StringBuilder extObjectName = new StringBuilder().append(newConfig.getHome()).append(" VS ").append(newConfig.getAway());
        logBean.setExtObjectNameByObj(montageEnAndZsIs(newConfig.getTeamList(),newConfig.getMatchId()));
    }

    private RcsOperateLog initialLogBean(RcsOperateLog sample, Object before, Object after) {
        RcsOperateLog rcsOperateLog = new RcsOperateLog();
        BeanUtils.copyProperties(sample, rcsOperateLog);
        rcsOperateLog.setBeforeValByObj(before==null?"-":before);
        rcsOperateLog.setAfterValByObj(after==null?"-":after);
        return rcsOperateLog;
    }

    private void pushMessage(RcsOperateLog rcsOperateLog) {
        rcsOperateLogMapper.insert(rcsOperateLog);
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
     * 跳分機制中文轉換
     */
    private String transOddChangeRule(Integer oddChangeRule) {
        switch (oddChangeRule) {
            case 0:
                return OperateLogEnum.SINGLE_ODD_CHANGE_AMOUNT.getName();
            case 1:
                return OperateLogEnum.DIFF_ODD_CHANGE_AMOUNT.getName();
        }
        return String.valueOf(oddChangeRule);
    }

    private String transBalanceOption(Integer balanceOption) {
        switch (balanceOption) {
            case 0:
                return OperateLogEnum.DIFF_ORDER_VAL.getName();
            case 1:
                return OperateLogEnum.DIFF_ORDER_AND_PAY_VAL_COMBINE.getName();
        }
        return String.valueOf(balanceOption);
    }

    /**
     * 根據盤口狀態碼轉換名稱
     *
     * @param stateCode
     * @return
     */
    public static String getTradeStatusName(Integer stateCode) {
        switch (stateCode) {
            case 0:
                return TradeStatusEnum.OPEN.getName();
            case 2:
                return TradeStatusEnum.CLOSE.getName();
            case 1:
                return TradeStatusEnum.SEAL.getName();
            case 11:
                return TradeStatusEnum.LOCK.getName();
            case 12:
                return TradeStatusEnum.DISABLE.getName();
            case 13:
                return TradeStatusEnum.END.getName();
            default:
                return "";
        }
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
    private String getMassageId(Long matchId){
        String value="";
        StandardMatchInfo matchMarketLiveBean  = standardMatchInfoMapper.selectById(matchId);
        value = null == matchMarketLiveBean ? matchId.toString() : matchMarketLiveBean.getMatchManageId();
        return value;

    }

    /**
     * 將操盤頁面代碼進行轉換
     *
     * @param newConfig
     * @param logBean
     */
    private void transOperatePageCode(LogAllBean newConfig, RcsOperateLog logBean) {
        switch (newConfig.getOperatePageCode()) {
            case 100:
                //早盘操盘
                logBean.setOperatePageCode(14);
                break;
            case 101:
                //早盘操盘-次要玩法
                logBean.setOperatePageCode(15);
                break;
            case 102:
                //滚球操盘
                logBean.setOperatePageCode(17);
            case 103:
                //滚球操盘-次要玩法
                logBean.setOperatePageCode(18);
                break;
        }
    }
}
