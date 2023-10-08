package com.panda.sport.rcs.trade.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.aocollect.common.utils.BigDecimalUtils;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.enums.TradeEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.constants.TradeConstant;
import com.panda.sport.rcs.pojo.dto.utils.SubPlayUtil;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.trade.enums.LinkedTypeEnum;
import com.panda.sport.rcs.trade.param.TournamentTemplatePlayMargainRefParam;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.util.MarginUtils;
import com.panda.sport.rcs.trade.vo.tourTemplate.TournamentTemplatePlayMargainRefVo;
import com.panda.sport.rcs.trade.wrapper.RcsOddsConvertMappingMyService;
import com.panda.sport.rcs.trade.wrapper.RcsOddsConvertMappingService;
import com.panda.sport.rcs.trade.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.trade.wrapper.impl.MarketStatusServiceImpl;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplatePlayMargainService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplateService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.TradeUserUtils;
import com.panda.sport.rcs.vo.MarketStatusUpdateVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Description //操盘的一些校验
 * @Param
 * @Author sean
 * @Date 2021/1/9
 * @return
 **/
@Service
@Slf4j
public class OddsRangeService {
    @Autowired
    private RcsOddsConvertMappingService rcsOddsConvertMappingService;
    @Autowired
    private TradeVerificationService tradeVerificationService;
    @Resource
    private IRcsTournamentTemplateService rcsTournamentTemplateService;
    @Resource
    private IRcsTournamentTemplatePlayMargainService rcsTournamentTemplatePlayMargainService;
    @Resource
    private StandardMatchInfoService standardMatchInfoService;

    /**
     * @return java.math.BigDecimal
     * @Description //根据赔率获取spread
     * @Param [odds, json]
     * @Author  sean
     * @Date   2021/8/13
     * @return java.math.BigDecimal
     **/
    public static BigDecimal getSpreadByOdds(String odds, JSONObject json){
        BigDecimal spread = new BigDecimal(TradeConstant.DEFAULT_MY_SPREAD);
        for (String key : json.keySet()){
            String[] ks = key.split("-");
            if (Double.valueOf(odds).doubleValue() >= Double.valueOf(ks[0]) &&
                    Double.valueOf(odds).doubleValue() <= Double.valueOf(ks[1])){
                spread = new BigDecimal(json.getString(key));
                break;
            }
        }

        return spread;
    }

//    public static void main(String[] args) {
//        getSpreadByOdds(getEuOdds(184000),JSONObject.parseObject("{\"1.01-1.19\":0.06,\"1.20-1.39\":0.06,\"1.40-1.59\":0.08,\"1.60-1.79\":0.1,\"1.80-1.85\":0.12,\"1.86-2.00\":0.14}"));
//    }

    /**
     * @return void
     * @Description //跟赔率和特殊抽水重新计算赔率
     * @Param [oddsList, config]
     * @Author  sean
     * @Date   2021/8/13
     * @return void
     **/
    public void caluSpecialOddsBySpread(List<StandardMarketOddsDTO> oddsList, RcsMatchMarketConfig config) {
        if (config.getIsSpecialPumping() == 1){
            StandardMarketOddsDTO odds0 = oddsList.get(NumberUtils.INTEGER_ZERO);
            StandardMarketOddsDTO odds1 = oddsList.get(NumberUtils.INTEGER_ONE);
            log.info("::{}::兼容没有margin的问题odds0={}", CommonUtil.getRequestId(config.getMatchId(), config.getPlayId()), JSONObject.toJSONString(odds0));
            if (ObjectUtils.isEmpty(config.getMargin()) && odds0.getMargin() != null) {
                log.info("::{}::兼容没有margin的问题config={}", CommonUtil.getRequestId(config.getMatchId(), config.getPlayId()), JSONObject.toJSONString(config));
                config.setMargin(new BigDecimal(odds0.getMargin().toString()));
            }
            String oddsType = config.getOddsType();
            if (odds0.getOddsValue().intValue() != odds1.getOddsValue()){
                oddsType = oddsList.stream().filter(e -> e.getOddsValue().intValue() == Math.min(odds0.getOddsValue(),odds1.getOddsValue())).findFirst().get().getOddsType();
            }
            StandardMarketOddsDTO odds = oddsList.stream().filter(e -> e.getOddsValue().intValue() == Math.min(odds0.getOddsValue(),odds1.getOddsValue())).findFirst().get();
//            String oddsValue = rcsOddsConvertMappingService.getMyOdds(odds.getOddsValue());
            BigDecimal spread = getSpreadByOdds(getEuOdds(odds.getOddsValue()),JSONObject.parseObject(config.getSpecialOddsInterval()));
            for (StandardMarketOddsDTO oddsDTO : oddsList){
//                oddsDTO.setMargin(config.getMargin().doubleValue());
                if (!oddsDTO.getOddsType().equalsIgnoreCase(oddsType)){
                    BigDecimal od = MarginUtils.caluOddsBySpread(new BigDecimal(rcsOddsConvertMappingService.getMyOdds(odds.getOddsValue())),spread);
                    oddsDTO.setOddsValue(rcsOddsConvertMappingService.getEUOddsInteger(od.toPlainString()));
                }
//                else {
//                    oddsDTO.setOddsValue(oddsDTO.getOddsValue());
//                }
            }
        }
    }

    public void caluSpreadOddsBySpread(List<StandardMarketOddsDTO> marketOddsList, RcsMatchMarketConfig config) {
        if (config.getIsSpecialPumping() == 0){
            StandardMarketOddsDTO odds0 = marketOddsList.get(NumberUtils.INTEGER_ZERO);
//            StandardMarketOddsDTO odds1 = marketOddsList.get(NumberUtils.INTEGER_ONE);
//            String oddsType = config.getOddsType();
//            if (odds0.getOddsValue().intValue() != odds1.getOddsValue()){
//                oddsType = marketOddsList.stream().filter(e -> e.getOddsValue().intValue() == Math.min(odds0.getOddsValue(),odds1.getOddsValue())).findFirst().get().getOddsType();
//            }
//            StandardMarketOddsDTO odds = marketOddsList.stream().filter(e -> e.getOddsValue().intValue() == Math.min(odds0.getOddsValue(),odds1.getOddsValue())).findFirst().get();
////            String oddsValue = rcsOddsConvertMappingService.getMyOdds(odds.getOddsValue());
//            BigDecimal spread = getSpreadByOdds(getEuOdds(odds.getOddsValue()),JSONObject.parseObject(config.getSpecialOddsInterval()));
            StandardMarketOddsDTO odds = getLowOdds(marketOddsList,config);
            BigDecimal spread = getSpreadByOdds(getEuOdds(odds.getOddsValue()),JSONObject.parseObject(config.getSpecialOddsInterval()));
            if (ObjectUtils.isEmpty(config.getMargin())){
                log.info("::{}::兼容没有margin的问题config={}",CommonUtil.getRequestId(config.getMatchId(),config.getPlayId()),JSONObject.toJSONString(config));
                config.setMargin(new BigDecimal(odds0.getMargin().toString()));
            }
            if (spread.compareTo(config.getMargin()) == 0){
                return;
            }
            for (StandardMarketOddsDTO oddsDTO : marketOddsList){
//                oddsDTO.setMargin(config.getMargin().doubleValue());
                if (!oddsDTO.getOddsType().equalsIgnoreCase(odds.getOddsType())){
                    BigDecimal od = MarginUtils.caluOddsBySpread(new BigDecimal(rcsOddsConvertMappingService.getMyOdds(oddsDTO.getOddsValue())),spread);
                    oddsDTO.setOddsValue(rcsOddsConvertMappingService.getEUOddsInteger(od.toPlainString()));
                }
//                else {
//                    oddsDTO.setOddsValue(oddsDTO.getOddsValue());
//                }
            }
        }
    }

    public static String getEuOdds(Integer oddsValue) {
        return new BigDecimal(oddsValue).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE), NumberUtils.INTEGER_TWO, BigDecimal.ROUND_DOWN).toPlainString();
    }

    /**
     * @return com.panda.sport.rcs.pojo.StandardSportMarketOdds
     * @Description //计算低赔
     * @Param [oddsList, config]
     * @Author  sean
     * @Date   2021/8/31
     * @return com.panda.sport.rcs.pojo.StandardSportMarketOdds
     **/
    public StandardMarketOddsDTO getLowOdds(List<StandardMarketOddsDTO> oddsList, RcsMatchMarketConfig config){
        StandardMarketOddsDTO odds0 = oddsList.get(NumberUtils.INTEGER_ZERO);
        StandardMarketOddsDTO odds1 = oddsList.get(NumberUtils.INTEGER_ONE);
        StandardMarketOddsDTO odds = odds0;
        if (odds0.getOddsValue().intValue() == odds1.getOddsValue()){
            String oddsType = config.getOddsType();
            if (odds0.getOddsType().equalsIgnoreCase(oddsType)){
                odds = odds0;
            }else {
                odds = odds1;
            }
        }else {
            odds = oddsList.stream().filter(e -> e.getOddsValue().intValue() == Math.min(odds0.getOddsValue(),odds1.getOddsValue())).findFirst().get();
        }
        return odds;
    }

    /**
     * @return java.math.BigDecimal
     * @Description //根据低赔计算特殊spread
     * @Param [oddsList, config, templatePlayMargain]
     * @Author  sean
     * @Date   2021/8/31
     * @return java.math.BigDecimal
     **/
    public BigDecimal getSpicalSpread(List<StandardMarketOddsDTO> oddsList, RcsMatchMarketConfig config){
        StandardMarketOddsDTO odds = getLowOdds(oddsList,config);
        BigDecimal spread = getSpreadByOdds(getEuOdds(odds.getOddsValue()),JSONObject.parseObject(config.getSpecialOddsInterval()));
        return spread;
    }

    /**
     * 特殊抽水需求2293
     *
     * @param marketOddsList
     * @param config
     */
    public void caluSpreadOddsBySpreadNew(List<StandardMarketOddsDTO> marketOddsList, RcsMatchMarketConfig config) {
        log.info("{}::特殊抽水", config.getMatchId());
        if (config.getIsSpecialPumping() != 0) {
            return;
        }
        Optional<StandardMarketOddsDTO> odds1Optional = marketOddsList.stream().filter(o -> OddsTypeEnum.isHomeOddsType(o.getOddsType())).findFirst();
        Optional<StandardMarketOddsDTO> odds2Optional = marketOddsList.stream().filter(o -> OddsTypeEnum.isAwayOddsType(o.getOddsType())).findFirst();
        StandardMarketOddsDTO odds1;
        StandardMarketOddsDTO odds2;
        if(!odds1Optional.isPresent() || !odds2Optional.isPresent()) {
            odds1 = marketOddsList.get(0);
            odds2 = marketOddsList.get(1);
        } else {
            odds1 = odds1Optional.get();
            odds2 = odds2Optional.get();
        }
        /*if(!odds1Optional.isPresent()){
            odds1 = marketOddsList.get(0);
        } else {
            odds1 = odds1Optional.get();
        }
        if(!odds2Optional.isPresent()){
            odds2 = marketOddsList.get(1);
        } else {
            odds2 = odds2Optional.get();
        }*/

//        StandardMarketOddsDTO odds1 = marketOddsList.stream().filter(o -> OddsTypeEnum.isHomeOddsType(o.getOddsType())).findFirst().get();
//        StandardMarketOddsDTO odds2 = marketOddsList.stream().filter(o -> OddsTypeEnum.isAwayOddsType(o.getOddsType())).findFirst().get();
        log.info("{}::特殊抽水::{}::{}", config.getMatchId(), JSONObject.toJSONString(odds1), JSONObject.toJSONString(odds2));
        //第1个投注项-特殊抽水后马来赔率
        BigDecimal odds1Special = new BigDecimal(rcsOddsConvertMappingService.getMyOdds(odds1.getOddsValue()));
        //第2个投注项-特殊抽水后马来赔率
        BigDecimal odds2Special = new BigDecimal(rcsOddsConvertMappingService.getMyOdds(odds2.getOddsValue()));
        // 第1个投注项-原始马来赔率
       // BigDecimal odds1Original = new BigDecimal(rcsOddsConvertMappingService.getMyOdds(odds1.getOriginalOddsValue()));
        // 第2个投注项-原始马来赔率
       // BigDecimal odds2Original = new BigDecimal(rcsOddsConvertMappingService.getMyOdds(odds2.getOriginalOddsValue()));

        //当前赛事的分时节点spread
        //1.查询赛事模板ID
        RcsTournamentTemplate tournamentTemplate = rcsTournamentTemplateService.queryByMatchId(config.getMatchId(), config.getMatchType());
        //2.根据模板ID和玩法ID查询marginId
        RcsTournamentTemplatePlayMargain playMargain =
                rcsTournamentTemplatePlayMargainService.get(tournamentTemplate.getId(), config.getPlayId(), config.getMatchType());
        //3.根据marginId查找分时节点
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.selectById(config.getMatchId());
        Date beginTime = DateUtil.date(standardMatchInfo.getBeginTime());
        Date nowTime = new Date();
        long timeVal = DateUtil.between(beginTime, nowTime, DateUnit.SECOND);

        if(config.getMatchType().equals(0)){
            //滚球时用secondsMatchStart字段
            timeVal = standardMatchInfo.getSecondsMatchStart();
        }

        TournamentTemplatePlayMargainRefParam param = new TournamentTemplatePlayMargainRefParam();
        param.setMargainId(playMargain.getId());
        param.setTimeVal(timeVal);
        param.setMatchType(config.getMatchType());
        TournamentTemplatePlayMargainRefVo margainRefVo = rcsTournamentTemplateService.queryTournamentTemplatePlayMargin(param);

        log.info("{}::特殊抽水::{}::{}", config.getMatchId(), JSONObject.toJSONString(param),JSONObject.toJSONString(margainRefVo));
        BigDecimal spread = new BigDecimal(margainRefVo.getMargain());

        if (odds1Special.doubleValue() > 0 && odds2Special.abs().doubleValue() > odds1Special.doubleValue()) {
            BigDecimal temp = odds1Special;
            BigDecimal odds2SpecialNew;
            if (temp.add(spread).doubleValue() < 1){
                odds2SpecialNew = temp.add(spread).multiply(new BigDecimal(NumberUtils.INTEGER_MINUS_ONE));
            } else {
                odds2SpecialNew = new BigDecimal(NumberUtils.INTEGER_TWO).subtract(temp.add(spread));
            }
            odds2.setOddsValue(rcsOddsConvertMappingService.getEUOddsInteger(odds2SpecialNew.toPlainString()));
            log.info("{}::特殊抽水::odds2::{}", config.getMatchId(), odds2SpecialNew.toPlainString());
            return;
        }
        BigDecimal temp = odds2Special;
        BigDecimal odds1SpecialNew;
        if(temp.add(spread).doubleValue() < 1){
            odds1SpecialNew = temp.add(spread).multiply(new BigDecimal(NumberUtils.INTEGER_MINUS_ONE));
        } else {
            odds1SpecialNew = new BigDecimal(NumberUtils.INTEGER_TWO).subtract(temp.add(spread));
        }
        odds1.setOddsValue(rcsOddsConvertMappingService.getEUOddsInteger(odds1SpecialNew.toPlainString()));
        log.info("{}::特殊抽水::odds1::{}", config.getMatchId(), odds1SpecialNew.toPlainString());
    }
}
