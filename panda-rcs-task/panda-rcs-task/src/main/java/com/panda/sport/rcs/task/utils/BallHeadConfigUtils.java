package com.panda.sport.rcs.task.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.panda.sport.rcs.enums.PeriodEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.BallHeadConfig;
import com.panda.sport.rcs.pojo.BallHeadConfigFeature;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.vo.TournamentTemplateCategoryVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Z9-elton
 */
public class BallHeadConfigUtils {
    /**
     * 返回球头设置
     *
     * @param jsonString
     * @return
     */
    public static BallHeadConfig getBallHeadConfigFromJson(String jsonString) {

        if (jsonString == null) {
            return null;
        }

        try {
            List<BallHeadConfig> ballHeadConfigList = JSONUtil.toList(JSONUtil.parseArray(jsonString), BallHeadConfig.class);

            //demo

            if (ballHeadConfigList != null && !ballHeadConfigList.isEmpty()) {
                return ballHeadConfigList.get(0);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("BallHeadConfigUtil.getBallHeadConfigFromJson: " + jsonString +
                    " ,error happen for convert to BallHeadConfig");
        }
    }

    /**
     * 获取特殊局球头配置
     *
     * @param feature 特殊局配置
     * @return
     */
    public static BallHeadConfig getBallHeadConfigFromJson(String jsonString, BallHeadConfigFeature feature) {
        try {
            List<BallHeadConfig> ballHeadConfigList = JSONUtil.toList(JSONUtil.parseArray(jsonString), BallHeadConfig.class);
            return getBallHeadConfigFromJson(ballHeadConfigList, feature);
        } catch (Exception e) {
            throw new RuntimeException("BallHeadConfigUtil.getBallHeadConfigFromJson: " + jsonString +
                    " ,error happen for convert to BallHeadConfig");
        }
    }

    /**
     * 获取特殊局球头配置
     *
     * @param feature 特殊局配置
     * @return
     */
    public static BallHeadConfig getBallHeadConfigFromJson(List<BallHeadConfig> ballHeadConfigList, BallHeadConfigFeature feature) {
        try {
            if (CollUtil.isEmpty(ballHeadConfigList)) {
                return null;
            }
            //非特殊局 直接返回
            if (ballHeadConfigList.size() == 1 || feature == null) {
                return ballHeadConfigList.get(0);
            }
            Optional<BallHeadConfig> cfg = ballHeadConfigList.stream().filter(o -> o.getFeature() == feature).findFirst();
            if (cfg.isPresent()) {
                return cfg.get();
            }
            return ballHeadConfigList.get(0);
        } catch (Exception e) {
            throw new RuntimeException("BallHeadConfigUtil.getBallHeadConfigFromJson: " + JSONUtil.toJsonStr(ballHeadConfigList) +
                    " ,error happen for convert to BallHeadConfig");
        }
    }

    /**
     * 当前只判断乒乓球赛事的大小球头设置
     *
     * @param sportId
     * @param ballHeadConfig
     * @param newMarketValue
     */
    public static boolean checkBallHeadConfig(Long sportId, BallHeadConfig ballHeadConfig, BigDecimal
            newMarketValue) {
        //让分玩法需要取绝对值，172全场让分，176某一局让分
        if (sportId.equals(new Long(172)) || (sportId.equals(new Long(176)))) {
            newMarketValue = newMarketValue.abs();
        }
        try {
            boolean isMaxHead = false;
            boolean isMinHead = false;
            //判断最大球头
            if (ballHeadConfig.getMaxBallHeadAuto()) {
                isMaxHead = true;
            } else {
                if (ballHeadConfig.getMaxBallHead() != null) {
                    isMaxHead = newMarketValue.compareTo(new BigDecimal(ballHeadConfig.getMaxBallHead())) <= 0;
                }

            }
            //判断最小球头
            if (ballHeadConfig.getMinBallHeadAuto()) {
                isMinHead = true;
            } else {
                if (ballHeadConfig.getMinBallHead() != null) {
                    isMinHead = newMarketValue.compareTo(new BigDecimal(ballHeadConfig.getMinBallHead())) >= 0;
                }
            }

            if (!isMaxHead || !isMinHead) {
                return false;
            } else {
                return true;
            }
        } catch (RuntimeException e) {
            throw new RcsServiceException("大小球头设置解析异常");
        }

    }


    /**
     * 检测附加盘口是否有超出大小球头限制
     *
     * @param sportId
     * @param marketValueList
     * @param rcsTournamentTemplatePlayMargin
     * @return true: 所用附加盘
     */
    public static boolean checkSubMarketList(Long
                                                     sportId, List<BigDecimal> marketValueList, RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargin) {
        BallHeadConfig ballHeadConfig = BallHeadConfigUtils.getBallHeadConfigFromJson(rcsTournamentTemplatePlayMargin.getBallHeadConfig());
        for (BigDecimal marketValue : marketValueList) {
            if (!checkBallHeadConfig(sportId, ballHeadConfig, marketValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测附加盘口是否有超出大小球头限制
     *
     * @param sportId
     * @param sportMarkets
     * @param rcsTournamentTemplatePlayMargin
     * @return true: 所用附加盘
     */
    public static boolean checkSubSportMarketList(Long
                                                          sportId, List<StandardSportMarket> sportMarkets, RcsTournamentTemplatePlayMargain
                                                          rcsTournamentTemplatePlayMargin) {
        BallHeadConfig ballHeadConfig = BallHeadConfigUtils.getBallHeadConfigFromJson(rcsTournamentTemplatePlayMargin.getBallHeadConfig());
        for (StandardSportMarket sportMarket : sportMarkets) {
            BigDecimal marketValue = new BigDecimal(sportMarket.getAddition1());
            if (!checkBallHeadConfig(sportId, ballHeadConfig, marketValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前最小球头的值，用于计算主盘口时判断是否需要换方向
     * 如果最小球头有具体数值按照实际设置的数值，如果是不设限就按照0.5
     *
     * @param ballHeadConfig
     * @return null 表示没有配置，非null用于比较计算
     */
    public static BigDecimal getMinBallHead(BallHeadConfig ballHeadConfig) {
        if (ballHeadConfig == null) {
            return null;
        } else if (ballHeadConfig.getMinBallHeadAuto()) {
            return new BigDecimal(0.5);
        } else {
            return new BigDecimal(ballHeadConfig.getMinBallHead());
        }
    }

    public static BallHeadConfig getMatchBallHeadConfig(StandardMatchInfo matchInfo, RcsTournamentTemplatePlayMargain playMargain) {
        BallHeadConfigFeature feature = null;
        if (SportIdEnum.isVolleyball(Long.valueOf(matchInfo.getSportId()))) {
            //排球
            //决胜局
            Integer roundType = matchInfo.getRoundType();
            //3局2胜 5局3胜 7局4胜
            if ((3 == roundType && Long.valueOf(PeriodEnum.VOLLEYBALL_THREE_9.getPeriod()).equals(matchInfo.getMatchPeriodId()))
                    || (5 == roundType && Long.valueOf(PeriodEnum.VOLLEYBALL_FIVE_9.getPeriod()).equals(matchInfo.getMatchPeriodId()))
                    || (7 == roundType && Long.valueOf(PeriodEnum.VOLLEYBALL_SEVEN_9.getPeriod()).equals(matchInfo.getMatchPeriodId()))) {
                feature = BallHeadConfigFeature.LAST;
            }

        } else if (SportIdEnum.isIceHockey(Long.valueOf(matchInfo.getSportId()))){
            //加时赛
            if (Objects.equals(Long.valueOf(PeriodEnum.ICE_HOCKEY_4.getPeriod()), matchInfo.getMatchPeriodId())) {
                feature = BallHeadConfigFeature.PLUS_TIME;
            }
        }
        return getBallHeadConfigFromJson(playMargain.getBallHeadConfig(), feature);
    }



    /**
     * 推送给融合的对象赋值球头范围
     *
     * @param vo          给融合的数据
     * @param playMargain 玩法配置
     */
    public static void setVoParam(StandardMatchInfo matchInfo, TournamentTemplateCategoryVo vo, RcsTournamentTemplatePlayMargain playMargain) {
        if(matchInfo == null){
            return;
        }
        BallHeadConfig config = getMatchBallHeadConfig(matchInfo, playMargain);
        if (config != null) {
            if (config.getMaxBallHeadAuto()) {
                vo.setMaxBallHead(new BigDecimal(999));
            } else {
                vo.setMaxBallHead(new BigDecimal(config.getMaxBallHead()));
            }
            if (config.getMinBallHeadAuto()) {
                vo.setMinBallHead(new BigDecimal(0.5));
            } else {
                vo.setMinBallHead(new BigDecimal(config.getMinBallHead()));
            }
        }
    }
}
