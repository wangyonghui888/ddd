package com.panda.sport.rcs.limit.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.limit.*;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mapper.limit.LimitMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.vo.LimitRcsQuotaUserSingleNoteVo;
import com.panda.sport.rcs.service.IRcsTournamentTemplatePlayMargainRefService;
import com.panda.sport.rcs.service.IRcsTournamentTemplatePlayMargainService;
import com.panda.sport.rcs.service.IRcsTournamentTemplateService;
import com.panda.sport.rcs.util.CopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.limit.constants.LimitConstants.AMOUNT_UNIT;

/**
 * @Description 限额服务
 * @Author lithan
 * @Date 2020年9月16日 15:44:47
 **/
@Slf4j
@Service
public class LimitServiceImpl {

    @Autowired
    IRcsTournamentTemplateService rcsTournamentTemplateService;

    @Autowired
    IRcsTournamentTemplatePlayMargainService rcsTournamentTemplatePlayMargainService;

    @Autowired
    IRcsTournamentTemplatePlayMargainRefService rcsTournamentTemplatePlayMargainRefService;

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;

    @Autowired
    RcsQuotaMerchantSingleFieldLimitMapper rcsQuotaMerchantSingleFieldLimitMapper;

    @Autowired
    RcsQuotaUserSingleSiteQuotaMapper rcsQuotaUserSingleSiteQuotaMapper;

    @Autowired
    RcsQuotaUserSingleNoteMapper rcsQuotaUserSingleNoteMapper;

    @Autowired
    private RcsQuotaUserDailyQuotaMapper rcsQuotaUserDailyQuotaMapper;
    @Autowired
    private RcsQuotaCrossBorderLimitMapper rcsQuotaCrossBorderLimitMapper;
    @Autowired
    private RcsQuotaLimitOtherDataMapper rcsQuotaLimitOtherDataMapper;
    @Autowired
    RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
    @Autowired
    RcsMatchMarketConfigSubMapper rcsMatchMarketConfigSubMapper;
    @Autowired
    RcsLabelLimitConfigMapper rcsLabelLimitConfigMapper;
    @Autowired
    LimitMapper limitMapper;
    @Autowired
    TUserMapper userMapper;
    /**
     * 获取 玩法盘口位置 限额列表
     *
     * @return
     */
    public List<MarkerPlaceLimitAmountResVo> getMarketPlaceLimitList(MarkerPlaceLimitAmountReqVo vo) {
        Integer sportId = vo.getSportId();
        Long matchId = vo.getMatchId();
        //Integer playId = vo.getPlayId();
        //1：早盘；0：滚球
        Integer matchType = 1;

        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(matchId);
        if (ObjectUtils.isEmpty(standardMatchInfo)) {
            log.info("赛事不存在:{}", matchId);
            throw new RcsServiceException("赛事不存在:");
        }
        //0:未开赛, 1:滚球, 2:暂停，3:结束 ，4:关闭，5:取消，6:放弃，7:延迟，8:未知，9:延期，10:中断   1,2,10滚球，别的都当做早盘处理
        Integer matchSatus = standardMatchInfo.getMatchStatus();
        if (matchSatus == 1 || matchSatus == 2 || matchSatus == 10) {
            matchType = 0;
        }

        //先查操盘窗口 rcs_match_market_config
        LambdaQueryWrapper<RcsMatchMarketConfig> marketConfigWrapper = new LambdaQueryWrapper<>();
        marketConfigWrapper.eq(RcsMatchMarketConfig::getMatchId, matchId);
        //marketConfigWrapper.eq(RcsMatchMarketConfig::getPlayId, playId);
        marketConfigWrapper.ge(RcsMatchMarketConfig::getMaxSingleBetAmount, 0);
        marketConfigWrapper.select(RcsMatchMarketConfig::getMaxSingleBetAmount, RcsMatchMarketConfig::getMarketIndex, RcsMatchMarketConfig::getPlayId);
        List<RcsMatchMarketConfig> matchMarketConfigList = rcsMatchMarketConfigMapper.selectList(marketConfigWrapper);

        List<MarkerPlaceLimitAmountResVo> resList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(matchMarketConfigList)) {
            for (RcsMatchMarketConfig rcsMatchMarketConfig : matchMarketConfigList) {
                MarkerPlaceLimitAmountResVo resVo = new MarkerPlaceLimitAmountResVo();
                resVo.setSportId(sportId);
                resVo.setLimitAmount(rcsMatchMarketConfig.getMaxSingleBetAmount() == null ? new BigDecimal(Long.MAX_VALUE / 100) : new BigDecimal(rcsMatchMarketConfig.getMaxSingleBetAmount()));
                resVo.setMarketPlaceNum(rcsMatchMarketConfig.getMarketIndex());
                resVo.setPlayId(rcsMatchMarketConfig.getPlayId().intValue());
                resVo.setMatchId(vo.getMatchId());
                resVo.setMatchType(-1);
                resVo.setSubPlayId("");
                resList.add(resVo);
            }
            //log.info("操盘窗口配置返回{}", JSONObject.toJSONString(resList));
            //return resList;
        }

        //先查操盘窗口 rcs_match_market_config_sub
        LambdaQueryWrapper<RcsMatchMarketConfigSub> marketConfigSubWrapper = new LambdaQueryWrapper<>();
        marketConfigSubWrapper.eq(RcsMatchMarketConfigSub::getMatchId, matchId);
        //marketConfigWrapper.eq(RcsMatchMarketConfig::getPlayId, playId);
        marketConfigSubWrapper.ge(RcsMatchMarketConfigSub::getMaxSingleBetAmount, 0);
        marketConfigSubWrapper.select(RcsMatchMarketConfigSub::getMaxSingleBetAmount, RcsMatchMarketConfigSub::getMarketIndex, RcsMatchMarketConfigSub::getPlayId, RcsMatchMarketConfigSub::getSubPlayId);
        List<RcsMatchMarketConfigSub> matchMarketConfigSubList = rcsMatchMarketConfigSubMapper.selectList(marketConfigSubWrapper);

        if (!ObjectUtils.isEmpty(matchMarketConfigSubList)) {
            for (RcsMatchMarketConfigSub rcsMatchMarketConfig : matchMarketConfigSubList) {
                MarkerPlaceLimitAmountResVo resVo = new MarkerPlaceLimitAmountResVo();
                resVo.setSportId(sportId);
                resVo.setLimitAmount(rcsMatchMarketConfig.getMaxSingleBetAmount() == null ? new BigDecimal(Long.MAX_VALUE / 100) : new BigDecimal(rcsMatchMarketConfig.getMaxSingleBetAmount()));
                resVo.setMarketPlaceNum(rcsMatchMarketConfig.getMarketIndex());
                resVo.setPlayId(rcsMatchMarketConfig.getPlayId().intValue());
                resVo.setMatchId(vo.getMatchId());
                resVo.setMatchType(-1);
                resVo.setSubPlayId(rcsMatchMarketConfig.getSubPlayId());
                resList.add(resVo);
            }
            //log.info("操盘窗口配置返回{}", JSONObject.toJSONString(resList));
            //return resList;
        }
        if (!ObjectUtils.isEmpty(resList)) {
            log.info("操盘窗口配置返回{}", JSONObject.toJSONString(resList));
            return resList;
        }

        log.info("操盘窗口配置未查到数据");


        //返回所有盘口的限额
        List<MarkerPlaceLimitAmountResVo> list = new ArrayList<>();
        return list;

    }




    /**
     * 获取 商户限额 配置
     * 表:rcs_quota_business_limit
     *
     * @return
     */
    public RcsQuotaBusinessLimit geRcsQuotaBusinessLimit(String busId) {
        //查询有效的商户
        LambdaQueryWrapper<RcsQuotaBusinessLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsQuotaBusinessLimit::getBusinessId, busId).eq(RcsQuotaBusinessLimit::getStatus,1);
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = rcsQuotaBusinessLimitMapper.selectOne(wrapper);
        return rcsQuotaBusinessLimit;
    }

    // 商户单场限额
    public RcsQuotaMerchantSingleFieldLimitVo getRcsQuotaMerchantSingleFieldLimitData(MatchLimitDataReqVo vo) {
        RcsQuotaMerchantSingleFieldLimitVo merchantSingleFieldLimitVo = new RcsQuotaMerchantSingleFieldLimitVo();

        //先从联赛配置查 足球 篮球单独处理
        //1：早盘；
        LambdaQueryWrapper<RcsTournamentTemplate> templateWrapper = new LambdaQueryWrapper<>();
        templateWrapper.eq(RcsTournamentTemplate::getSportId, vo.getSportId());
        templateWrapper.eq(RcsTournamentTemplate::getType, 3);
        templateWrapper.eq(RcsTournamentTemplate::getTypeVal, vo.getMatchId());
        templateWrapper.eq(RcsTournamentTemplate::getMatchType, 1);
        RcsTournamentTemplate rcsTournamentTemplateEarly = rcsTournamentTemplateService.getOne(templateWrapper);
        log.info("商户单场限额查询:早盘结果:{}",JSONObject.toJSONString(rcsTournamentTemplateEarly));
        //0：滚球
        LambdaQueryWrapper<RcsTournamentTemplate> templateWrapperLive = new LambdaQueryWrapper<>();
        templateWrapperLive.eq(RcsTournamentTemplate::getSportId, vo.getSportId());
        templateWrapperLive.eq(RcsTournamentTemplate::getType, 3);
        templateWrapperLive.eq(RcsTournamentTemplate::getTypeVal, vo.getMatchId());
        templateWrapperLive.eq(RcsTournamentTemplate::getMatchType, 0);
        RcsTournamentTemplate rcsTournamentTemplateLive = rcsTournamentTemplateService.getOne(templateWrapperLive);
        log.info("商户单场限额查询:滚球结果:{}", JSONObject.toJSONString(rcsTournamentTemplateLive));
        //如果联赛设置里面存在 从联赛设置取
        if (rcsTournamentTemplateLive != null || rcsTournamentTemplateEarly != null) {

            if(rcsTournamentTemplateLive==null){
                rcsTournamentTemplateLive = new RcsTournamentTemplate();
                rcsTournamentTemplateLive.setBusinesMatchPayVal(1000000000L);
            }
            if(rcsTournamentTemplateEarly==null){
                rcsTournamentTemplateEarly = new RcsTournamentTemplate();
                rcsTournamentTemplateEarly.setBusinesMatchPayVal(1000000000L);
            }
            merchantSingleFieldLimitVo.setMatchId(vo.getMatchId());
            merchantSingleFieldLimitVo.setLiveBallPayoutLimit(rcsTournamentTemplateLive.getBusinesMatchPayVal());
            merchantSingleFieldLimitVo.setEarlyMorningPaymentLimit(rcsTournamentTemplateEarly.getBusinesMatchPayVal());
            //标识 数据来自赛事模板
            merchantSingleFieldLimitVo.setMatchId(vo.getMatchId());
            return merchantSingleFieldLimitVo;
        }

        //从配置表取
        LambdaQueryWrapper<RcsQuotaMerchantSingleFieldLimit> merchantSingleWrapper = new LambdaQueryWrapper<>();
        merchantSingleWrapper.eq(RcsQuotaMerchantSingleFieldLimit::getSportId, vo.getSportId());
        merchantSingleWrapper.eq(RcsQuotaMerchantSingleFieldLimit::getTemplateLevel, vo.getTournamentLevel());
        RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimit = rcsQuotaMerchantSingleFieldLimitMapper.selectOne(merchantSingleWrapper);
        merchantSingleFieldLimitVo = CopyUtils.clone(rcsQuotaMerchantSingleFieldLimit, RcsQuotaMerchantSingleFieldLimitVo.class);

        //没查到用未评级配置
        if (merchantSingleFieldLimitVo == null) {
            log.info("商户单场限额:使用未评级配置:{}",JSONObject.toJSONString(vo));
            LambdaQueryWrapper<RcsQuotaMerchantSingleFieldLimit> merchantSingleOtherWrapper = new LambdaQueryWrapper<>();
            merchantSingleOtherWrapper.eq(RcsQuotaMerchantSingleFieldLimit::getSportId, vo.getSportId());
            merchantSingleOtherWrapper.eq(RcsQuotaMerchantSingleFieldLimit::getTemplateLevel, -1);
            rcsQuotaMerchantSingleFieldLimit = rcsQuotaMerchantSingleFieldLimitMapper.selectOne(merchantSingleOtherWrapper);
            merchantSingleFieldLimitVo = CopyUtils.clone(rcsQuotaMerchantSingleFieldLimit, RcsQuotaMerchantSingleFieldLimitVo.class);
        }

        return merchantSingleFieldLimitVo;
    }

    // 用户单场限额
    public RcsQuotaUserSingleSiteQuotaVo getRcsQuotaUserSingleSiteQuotaData(MatchLimitDataReqVo vo) {
        log.info("用户单场限额查询:参数:{}",JSONObject.toJSONString(vo));
        RcsQuotaUserSingleSiteQuotaVo userSingleSiteQuotaVo = new RcsQuotaUserSingleSiteQuotaVo();
        //先从联赛配置查 足球 篮球单独处理
        //1：早盘；
        LambdaQueryWrapper<RcsTournamentTemplate> templateWrapper = new LambdaQueryWrapper<>();
        templateWrapper.eq(RcsTournamentTemplate::getSportId, vo.getSportId());
        templateWrapper.eq(RcsTournamentTemplate::getType, 3);
        templateWrapper.eq(RcsTournamentTemplate::getTypeVal, vo.getMatchId());
        templateWrapper.eq(RcsTournamentTemplate::getMatchType, 1);
        RcsTournamentTemplate rcsTournamentTemplateEarly = rcsTournamentTemplateService.getOne(templateWrapper);
        log.info("用户单场限额查询:早盘结果:{}",JSONObject.toJSONString(rcsTournamentTemplateEarly));
        //0：滚球
        LambdaQueryWrapper<RcsTournamentTemplate> templateWrapperLive = new LambdaQueryWrapper<>();
        templateWrapperLive.eq(RcsTournamentTemplate::getSportId, vo.getSportId());
        templateWrapperLive.eq(RcsTournamentTemplate::getType, 3);
        templateWrapperLive.eq(RcsTournamentTemplate::getTypeVal, vo.getMatchId());
        templateWrapperLive.eq(RcsTournamentTemplate::getMatchType, 0);
        RcsTournamentTemplate rcsTournamentTemplateLive = rcsTournamentTemplateService.getOne(templateWrapperLive);
        log.info("用户单场限额查询:滚球结果:{}",JSONObject.toJSONString(rcsTournamentTemplateLive));
        //如果联赛设置里面存在 从联赛设置取
        if (rcsTournamentTemplateLive != null || rcsTournamentTemplateEarly != null) {
            if(rcsTournamentTemplateLive==null){
                rcsTournamentTemplateLive = new RcsTournamentTemplate();
                rcsTournamentTemplateLive.setUserMatchPayVal(1000000000L);
            }
            if(rcsTournamentTemplateEarly==null){
                rcsTournamentTemplateEarly = new RcsTournamentTemplate();
                rcsTournamentTemplateEarly.setUserMatchPayVal(1000000000L);
            }
            userSingleSiteQuotaVo.setMatchId(vo.getMatchId());
            userSingleSiteQuotaVo.setLiveUserSingleSiteQuota(new BigDecimal(rcsTournamentTemplateLive.getUserMatchPayVal()));
            userSingleSiteQuotaVo.setEarlyUserSingleSiteQuota(new BigDecimal(rcsTournamentTemplateEarly.getUserMatchPayVal()));
            return userSingleSiteQuotaVo;
        }

        //从配置表取
        LambdaQueryWrapper<RcsQuotaUserSingleSiteQuota> userSingleWrapper = new LambdaQueryWrapper<>();
        userSingleWrapper.eq(RcsQuotaUserSingleSiteQuota::getSportId, vo.getSportId());
        userSingleWrapper.eq(RcsQuotaUserSingleSiteQuota::getTemplateLevel, vo.getTournamentLevel());
        RcsQuotaUserSingleSiteQuota userSingleSiteQuota = rcsQuotaUserSingleSiteQuotaMapper.selectOne(userSingleWrapper);
        userSingleSiteQuotaVo = CopyUtils.clone(userSingleSiteQuota, RcsQuotaUserSingleSiteQuotaVo.class);
        //没查到用未评级配置
        if (userSingleSiteQuotaVo == null) {
            log.info("用户单场限额查询:使用未评级配置:{}",JSONObject.toJSONString(vo));
            LambdaQueryWrapper<RcsQuotaUserSingleSiteQuota> userSingleOtherWrapper = new LambdaQueryWrapper<>();
            userSingleOtherWrapper.eq(RcsQuotaUserSingleSiteQuota::getSportId, vo.getSportId());
            userSingleOtherWrapper.eq(RcsQuotaUserSingleSiteQuota::getTemplateLevel, -1);
            userSingleSiteQuota = rcsQuotaUserSingleSiteQuotaMapper.selectOne(userSingleOtherWrapper);
            userSingleSiteQuotaVo = CopyUtils.clone(userSingleSiteQuota, RcsQuotaUserSingleSiteQuotaVo.class);
        }
        return userSingleSiteQuotaVo;
    }

    // 用户单注单关限额
    public List<RcsQuotaUserSingleNoteVo> getRcsQuotaUserSingleNoteData(MatchLimitDataReqVo vo) {
        log.info("用户单注单关限额查询配置,请求参数:{}", JSONObject.toJSONString(vo));
        List<LimitRcsQuotaUserSingleNoteVo> singleNoteVoList = limitMapper.getRcsTournamentTemplatePlayMargainRefList(vo.getSportId(), vo.getMatchId());
        log.info("用户单注单关限额查询配置,结果:{}", JSONObject.toJSONString(singleNoteVoList));
        List<RcsQuotaUserSingleNoteVo> voList = new ArrayList<>();
        for (LimitRcsQuotaUserSingleNoteVo bean : singleNoteVoList) {
            RcsQuotaUserSingleNoteVo singleNoteVo = new RcsQuotaUserSingleNoteVo();
            BeanUtils.copyProperties(bean, singleNoteVo);
            singleNoteVo.setMatchId(vo.getMatchId());
            //rcs_quota_user_single_note表bet_state字段 和 实体bean的isScroll字段 都是0 早盘 1滚球  这里由于定义相反了  转换统一
            singleNoteVo.setBetState(singleNoteVo.getBetState() == 0 ? 1 : 0);
            voList.add(singleNoteVo);
        }
        if (!ObjectUtils.isEmpty(singleNoteVoList)) {
            log.info("用户单注单关限额赛事配置返回:{}", JSONObject.toJSONString(voList));
            return voList;
        } else {
            log.info("用户单注单关限额赛事配置不存在,SportId:{},MatchId:{},获取非赛事配置表:rcs_quota_user_single_note", vo.getSportId(), vo.getMatchId());
        }
        LambdaQueryWrapper<RcsQuotaUserSingleNote> userSingleNoteWrapper = new LambdaQueryWrapper<>();
        userSingleNoteWrapper.eq(RcsQuotaUserSingleNote::getSportId, vo.getSportId());
        List<RcsQuotaUserSingleNote> userSingleNoteList = rcsQuotaUserSingleNoteMapper.selectList(userSingleNoteWrapper);
        //因字段替换，需要转换并原有值为null
        for (RcsQuotaUserSingleNote note:userSingleNoteList){
            note.setSinglePayLimit(note.getSingleBetLimit());
            note.setSingleBetLimit(null);
        }
        voList = CopyUtils.clone(userSingleNoteList, RcsQuotaUserSingleNoteVo.class);
        log.info("用户单注单关限额非赛事配置表返回:{}", JSONObject.toJSONString(voList));
        return voList;
    }

    /**
     * 根据赛事查询各维度限额数据
     *
     * @return
     */
    public MatchLimitDataVo getMatchLimitData(MatchLimitDataReqVo vo) {
        final List<Integer> dataTypeList = vo.getDataTypeList();
        MatchLimitDataVo matchLimitDataVo = new MatchLimitDataVo();
        /**单关相关额度处理**/
        // 商户单场限额
        if (checkDataType(dataTypeList, LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT)) {
            RcsQuotaMerchantSingleFieldLimitVo merchantSingleFieldLimitVo = getRcsQuotaMerchantSingleFieldLimitData(vo);
            //金额单位 统一转换  元转分
            merchantSingleFieldLimitVo.setLiveBallPayoutLimit(merchantSingleFieldLimitVo.getLiveBallPayoutLimit() * AMOUNT_UNIT.longValue());
            merchantSingleFieldLimitVo.setEarlyMorningPaymentLimit(merchantSingleFieldLimitVo.getEarlyMorningPaymentLimit() * AMOUNT_UNIT.longValue());
            matchLimitDataVo.setRcsQuotaMerchantSingleFieldLimitVo(merchantSingleFieldLimitVo);
        }
        // 用户单场限额
        if (checkDataType(dataTypeList, LimitDataTypeEnum.USER_SINGLE_LIMIT)) {
            RcsQuotaUserSingleSiteQuotaVo userSingleSiteQuotaVo = getRcsQuotaUserSingleSiteQuotaData(vo);
            if (userSingleSiteQuotaVo == null) {
                throw new RcsServiceException("用户单场限额未配置");
            }
            //金额单位 统一转换  元转分
            userSingleSiteQuotaVo.setEarlyUserSingleSiteQuota(userSingleSiteQuotaVo.getEarlyUserSingleSiteQuota().multiply(AMOUNT_UNIT));
            userSingleSiteQuotaVo.setLiveUserSingleSiteQuota(userSingleSiteQuotaVo.getLiveUserSingleSiteQuota().multiply(AMOUNT_UNIT));
            matchLimitDataVo.setRcsQuotaUserSingleSiteQuotaVo(userSingleSiteQuotaVo);
        }

        // 用户单注单关限额
        if (checkDataType(dataTypeList, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT)) {
            List<RcsQuotaUserSingleNoteVo> voList = getRcsQuotaUserSingleNoteData(vo);
            //金额单位 统一转换  元转分
            for (RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo : voList) {
                rcsQuotaUserSingleNoteVo.setSinglePayLimit(rcsQuotaUserSingleNoteVo.getSinglePayLimit().multiply(AMOUNT_UNIT));
                rcsQuotaUserSingleNoteVo.setSingleBetLimit(rcsQuotaUserSingleNoteVo.getSingleBetLimit()!=null?rcsQuotaUserSingleNoteVo.getSingleBetLimit().multiply(AMOUNT_UNIT):null);
                rcsQuotaUserSingleNoteVo.setCumulativeCompensationPlaying(rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying().multiply(AMOUNT_UNIT));
            }
            matchLimitDataVo.setRcsQuotaUserSingleNoteVoList(voList);
        }
        // 用户单日限额
        if (checkDataType(dataTypeList, LimitDataTypeEnum.USER_DAILY_LIMIT)) {
            List<RcsQuotaUserDailyQuotaVo> userDailyQuotaList = getUserDailyLimit(vo);
            //金额单位 统一转换  元转分
            for (RcsQuotaUserDailyQuotaVo rcsQuotaUserDailyQuotaVo : userDailyQuotaList) {
                rcsQuotaUserDailyQuotaVo.setDayCompensation(rcsQuotaUserDailyQuotaVo.getDayCompensation().multiply(AMOUNT_UNIT));
                rcsQuotaUserDailyQuotaVo.setCrossDayCompensation(rcsQuotaUserDailyQuotaVo.getCrossDayCompensation().multiply(AMOUNT_UNIT));
            }
            matchLimitDataVo.setUserDailyQuotaList(userDailyQuotaList);
        }

        /**串关相关额度处理**/
        // 串关单注赔付限额  //此处金额单位转换 在方法内处理了
        if (checkDataType(dataTypeList, LimitDataTypeEnum.SERIES_PAYMENT_LIMIT)) {
            matchLimitDataVo.setSeriesPaymentLimitMap(getSeriesSingleBetLimit(vo));
        }
        // 各投注项计入单关限额的投注比例
        if (checkDataType(dataTypeList, LimitDataTypeEnum.SERIES_RATIO)) {
            matchLimitDataVo.setSeriesRatioMap(getSeriesRatio(vo));
        }
        // 最低/最高投注额
        if (checkDataType(dataTypeList, LimitDataTypeEnum.BET_AMOUNT_LIMIT)) {
            BetAmountLimitVo betAmountLimitVo = getBetAmountLimit(vo);
            //金额转换
            betAmountLimitVo.setSeriesMinBet(betAmountLimitVo.getSeriesMinBet().multiply(AMOUNT_UNIT));
            betAmountLimitVo.setSingleMinBet(betAmountLimitVo.getSingleMinBet().multiply(AMOUNT_UNIT));
            matchLimitDataVo.setBetAmountLimitVo(betAmountLimitVo);
        }
        // 计入串关已用额度的比例
        if (checkDataType(dataTypeList, LimitDataTypeEnum.SERIES_USED_RATIO)) {
            matchLimitDataVo.setSeriesUsedRatioMap(getSeriesUsedRatio(vo));
        }
        return matchLimitDataVo;
    }

    private boolean checkDataType(List<Integer> dataTypeList, LimitDataTypeEnum dataTypeEnum) {
        return CollectionUtils.isEmpty(dataTypeList) || dataTypeList.contains(dataTypeEnum.getType());
    }

    /**
     * 获取用户单日限额
     *
     * @param reqVo
     * @return
     * @author Paca
     */
    public List<RcsQuotaUserDailyQuotaVo> getUserDailyLimit(MatchLimitDataReqVo reqVo) {
        List<RcsQuotaUserDailyQuota> list = rcsQuotaUserDailyQuotaMapper.selectList(null);
        return list.stream().map(config -> new RcsQuotaUserDailyQuotaVo(config.getSportId(), config.getDayCompensation(), config.getCrossDayCompensation())).collect(Collectors.toList());
    }

    /**
     * 获取串关单注赔付限额
     *
     * @param reqVo
     * @return Map<串关类型, 单注赔付限额>
     * @author Paca
     */
    private Map<Integer, BigDecimal> getSeriesSingleBetLimit(MatchLimitDataReqVo reqVo) {
        final Integer sportId = reqVo.getSportId();
        final Integer tournamentLevel = reqVo.getTournamentLevel();
        LambdaQueryWrapper<RcsQuotaCrossBorderLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsQuotaCrossBorderLimit::getSportId, sportId)
                .eq(RcsQuotaCrossBorderLimit::getTournamentLevel, tournamentLevel)
                .eq(RcsQuotaCrossBorderLimit::getStatus, 1);
        List<RcsQuotaCrossBorderLimit> list = rcsQuotaCrossBorderLimitMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            log.error("未配置串关单注赔付限额：MatchLimitDataReqVo={}", JsonFormatUtils.toJson(reqVo));
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.toMap(RcsQuotaCrossBorderLimit::convertType, config -> config.getQuota().multiply(AMOUNT_UNIT)));
    }

    /**
     * 各投注项计入单关限额的投注比例
     *
     * @param reqVo
     * @return Map<串关类型, 比例>
     * @author Paca
     */
    private Map<Integer, BigDecimal> getSeriesRatio(MatchLimitDataReqVo reqVo) {
        final Integer sportId = reqVo.getSportId();
        LambdaQueryWrapper<RcsQuotaLimitOtherData> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsQuotaLimitOtherData::getSportId, sportId)
                .in(RcsQuotaLimitOtherData::getType, Lists.newArrayList(4, 5, 6, 7, 8, 9, 10, 11, 12))
                .eq(RcsQuotaLimitOtherData::getStatus, 1);
        List<RcsQuotaLimitOtherData> list = rcsQuotaLimitOtherDataMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            log.error("未配置各投注项计入单关限额的投注比例：MatchLimitDataReqVo={}", JsonFormatUtils.toJson(reqVo));
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.toMap(RcsQuotaLimitOtherData::convertType, RcsQuotaLimitOtherData::getBaseValue));
    }

    /**
     * 最低/最高投注额
     *
     * @param reqVo
     * @return
     * @author Paca
     */
    private BetAmountLimitVo getBetAmountLimit(MatchLimitDataReqVo reqVo) {
        LambdaQueryWrapper<RcsQuotaLimitOtherData> wrapper = Wrappers.lambdaQuery();
        wrapper.in(RcsQuotaLimitOtherData::getType, Lists.newArrayList(2, 3))
                .eq(RcsQuotaLimitOtherData::getStatus, 1);
        List<RcsQuotaLimitOtherData> list = rcsQuotaLimitOtherDataMapper.selectList(wrapper);
        BetAmountLimitVo vo = new BetAmountLimitVo(BigDecimal.ZERO);
        if (CollectionUtils.isEmpty(list)) {
            log.error("未配置最低/最高投注额：MatchLimitDataReqVo={}", JsonFormatUtils.toJson(reqVo));
            return vo;
        }
        list.forEach(config -> {
            Integer type = config.getType();
            if (type == 2) {
                vo.setSingleMinBet(config.getBaseValue());
                vo.setSeriesMinBet(config.getBaseValue());
            } else if (type == 3) {
                vo.setSeriesMaxBetRatio(config.getBaseValue());
            }
        });
        return vo;
    }

    /**
     * 计入串关已用额度的比例
     *
     * @param reqVo
     * @return
     * @author Paca
     */
    private Map<Integer, BigDecimal> getSeriesUsedRatio(MatchLimitDataReqVo reqVo) {
        LambdaQueryWrapper<RcsQuotaLimitOtherData> wrapper = Wrappers.lambdaQuery();
        wrapper.in(RcsQuotaLimitOtherData::getType, Lists.newArrayList(103, 104, 105, 106, 107, 108, 109, 110))
                .eq(RcsQuotaLimitOtherData::getStatus, 1);
        List<RcsQuotaLimitOtherData> list = rcsQuotaLimitOtherDataMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            log.error("未配置计入串关已用额度的比例：MatchLimitDataReqVo={}", JsonFormatUtils.toJson(reqVo));
            return Maps.newHashMap();
        }
        Map<Integer, BigDecimal> map = list.stream().collect(Collectors.toMap(RcsQuotaLimitOtherData::convertSeriesUsedRatioType, RcsQuotaLimitOtherData::getBaseValue));
        // 2串1默认比例100%
        map.put(NumberUtils.INTEGER_TWO, BigDecimal.ONE);
        return map;
    }

    public Integer getUserTag(Long userId) {
        TUser user = userMapper.selectByUserId(userId);
        return user.getUserLevel();
    }

    /**
     * 通过用户ID获取用户标签
     *
     * @param userId
     * @return
     */
    public Integer getUserTagByUserId(Long userId) {
        TUser user = userMapper.selectByUserId(userId);
        if (user == null || user.getUserLevel() == null) {
            throw new RcsServiceException("用户信息或用户标签为空");
        }
        return user.getUserLevel();
    }

    /**
     * 通过赛事管理ID获取赛事信息
     *
     * @param matchManageId
     * @return
     */
    public StandardMatchInfo getMatchInfoByMatchManageId(String matchManageId) {
        LambdaQueryWrapper<StandardMatchInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StandardMatchInfo::getMatchManageId, matchManageId);
        StandardMatchInfo matchInfo = standardMatchInfoMapper.selectOne(wrapper);
        if (matchInfo == null) {
            throw new RcsServiceException("赛事不存在：matchManageId=" + matchManageId);
        }
        return matchInfo;
    }

    /**
     * 通过赛事ID获取赛事信息
     *
     * @param matchId
     * @return
     */
    public StandardMatchInfo getMatchInfoByMatchId(Long matchId) {
        StandardMatchInfo matchInfo = standardMatchInfoMapper.selectById(matchId);
        if (matchInfo == null) {
            throw new RcsServiceException("赛事不存在：matchId=" + matchId);
        }
        return matchInfo;
    }


    public String getTagPercentage(Integer tagId) {
            RcsLabelLimitConfig rcsLabelLimitConfig = rcsLabelLimitConfigMapper.selectOne(new LambdaQueryWrapper<RcsLabelLimitConfig>()
                    .eq(RcsLabelLimitConfig::getTagId, tagId)
                    .eq(RcsLabelLimitConfig::getSpecialBettingLimit, 1)
                    .last("limit 1"));
            log.info("获取标签限额符合条件数据:{}", JSONObject.toJSONString(rcsLabelLimitConfig));
            if (rcsLabelLimitConfig !=null && rcsLabelLimitConfig.getLimitPercentage() != null && 1 == rcsLabelLimitConfig.getSpecialBettingLimit()) {
                return String.valueOf(rcsLabelLimitConfig.getLimitPercentage().doubleValue() / 100);
            }else{
                //获取的时候如果为空 默认为1
                return "1";
            }
    }
}
