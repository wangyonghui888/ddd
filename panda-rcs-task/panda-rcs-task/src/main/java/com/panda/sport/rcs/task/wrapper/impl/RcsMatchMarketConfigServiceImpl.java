package com.panda.sport.rcs.task.wrapper.impl;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.*;
import com.panda.sport.data.rcs.dto.LocalCacheSyncBean;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.*;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateEventMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mongo.MatchMarketVo;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.odds.MatchMarketPlaceConfig;
import com.panda.sport.rcs.pojo.odds.MatchOddsConfig;
import com.panda.sport.rcs.pojo.odds.MatchPlayConfig;
import com.panda.sport.rcs.pojo.tourTemplate.*;
import com.panda.sport.rcs.task.utils.SystemPreSwitchVo;
import com.panda.sport.rcs.task.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.task.wrapper.RcsCategoryPreSettlementConfigService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketOddsService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.vo.TournamentTemplateCategoryVo;
import com.panda.sport.rcs.vo.TournamentTemplatePlayVo;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.panda.sport.rcs.constants.RcsConstant.FOOTBALL_EARLY_SETTLEMENT_PLAY;

/**
 * <p>
 * 赛事设置表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-24
 */
@Service
@Slf4j
public class RcsMatchMarketConfigServiceImpl extends ServiceImpl<RcsMatchMarketConfigMapper, RcsMatchMarketConfig> implements IRcsMatchMarketConfigService {
    @Autowired
    RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Autowired
    RcsMatchMarketConfigSubMapper rcsMatchMarketConfigSubMapper;

    @Autowired
    RcsMatchMarketMarginConfigMapper rcsMatchMarketMarginConfigMapper;

    @Autowired
    RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;

    @Autowired
    RcsTournamentTemplateMapper templateMapper;

    @Autowired
    private RcsTournamentTemplatePlayMargainMapper playMargainMapper;

    @Autowired
    private RcsCategoryOddTempletMapper categoryOddTempletMapper;

    @Autowired
    RedisClient redisClient;
//    @Autowired
//    RcsOddsConvertMappingMyService rcsOddsConvertMappingMyService;

    @Autowired
    StandardSportMarketOddsService standardSportMarketOddsService;

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private RcsOddsConvertMappingMyMapper rcsOddsConvertMappingMyMapper;
    @Autowired
    private RcsOddsConvertMappingMapper rcsOddsConvertMappingMapper;

    private Map<String, String> oddsMap;
    @Autowired
    private RcsTradeConfigMapper rcsTradeConfigMapper;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketConfigApi tradeMarketConfigApi;

    @Autowired
    private ProducerSendMessageUtils sendMessage;

    @Autowired
    private RcsTournamentTemplateEventMapper templateEventMapper;

    @Autowired
    private RcsCategoryPreSettlementConfigService rcsCategoryPreSettlementConfigService;


    private static String TEMPLATE_MARKET_CONFIG_TOPIC = "TEMPLATE_MARKET_CONFIG_TOPIC";

    /**
     * 如果当前最大值大于设定的单笔最大投注限额，更新为最大限额
     *
     * @param config
     */
    private void checkAndSetMaxBetAmount(RcsMatchMarketConfig config) {
        if (config.getMaxBetAmount() != null && config.getMaxSingleBetAmount() != null) {
            if (config.getMaxBetAmount().compareTo(new BigDecimal(config.getMaxSingleBetAmount())) > 0) {
                config.setMaxBetAmount(new BigDecimal(config.getMaxSingleBetAmount()));
            }
        }
    }

    public static List<Integer> SINGLE_WIN_PLAYS = Arrays.asList(37, 41, 43, 48, 54, 60, 66, 142);
    // 篮球主要欧赔玩法
    public static List<Integer> BASKETBALL_MAIN_EU_PLAYS = Arrays.asList(37, 43, 48, 54, 60, 66, 142, 41);
    // 足球球主要欧赔玩法
    public static List<Integer> FOOTBALL_MAIN_EU_PLAYS = Arrays.asList(1, 5, 6, 3, 27, 17, 70, 69, 29, 85, 95, 16, 25, 72, 71, 149, 111, 119, 112, 126, 129, 310, 311, 326, 329, 228, 43, 142, 333, 352, 354, 355, 356, 357, 358, 361, 362);
    public static List<Integer> FOOTBALL_MOST_ODDS_TYPE_PLAYS = Arrays.asList(35, 7, 8, 9, 13, 14, 20, 21, 22, 23, 36, 68, 73, 74, 101, 102, 103, 104, 105, 106, 107, 108, 137, 141, 150, 151, 152, 223, 226, 227, 236, 238, 239, 241, 318, 319, 320, 321, 322, 323, 31, 35, 148, 222, 340, 344, 345, 346, 347, 348, 349, 350, 351, 353, 360, 361, 362);
    //3投注项和多投注项玩法
    public static List<Integer> FOOTBALL_THREE_MANY_ITEM_PLAYS = Arrays.asList(1, 3, 6, 7, 8, 9, 13, 14, 16, 17, 20, 21, 22, 23, 25, 27, 28, 29, 30, 31, 32, 35, 36, 68, 69, 70, 71, 72, 73, 74, 85, 95, 101, 102, 103, 104, 105, 106, 107, 108, 111, 112, 117, 119, 120, 125, 126, 129, 137, 141, 148, 149, 150, 151, 152, 222, 223, 224, 225, 226, 227, 228, 230, 231, 235, 236, 237, 238, 239, 241, 310, 311, 318, 319, 320, 321, 322, 323, 333, 340, 341, 342, 343, 10001, 10002, 10003, 10004, 10005, 10006, 10007, 10008, 10009, 10010, 10011, 10012, 344, 345, 346, 347, 348, 349, 350, 351, 353, 354, 355, 356, 357, 359, 360, 361, 362, 367, 368, 369, 370, 379, 380, 383);

    public static List<Integer> X_ODDS_TYPE_PLAYS = Arrays.asList(220, 221, 271, 272, 145, 146, 147, 201, 214, 215, 336, 28, 30, 109, 110, 34, 32, 33, 31, 222, 148, 233, 225, 120, 125, 230, 231, 232, 224, 235, 133, 237, 357);

    public static List<Integer> BASKETBALL_X_SCORCE_PLAYS = Lists.newArrayList(201,214,215);

    // 子玩法配置
    public static String REDIS_MATCH_MARKET_SUB_CONFIG = "rcs:redis:match:market:sub:config:%s:%s:%s";
    // 玩法配置
    public static String REDIS_MATCH_MARKET_CONFIG = "rcs:redis:match:market:config:%s:%s";

    // 子玩法配置
    private static final String REDIS_MATCH_MARKET_SUB_SECOND_CONFIG = "rcs:redis:match:market:sub:second:config:%s:%s:%s:%s";
    //public static String REDIS_MATCH_MARKET_SUB_CONFIG_PLAY = "rcs:redis:match:market:sub:config:%s:%s";
    // 玩法配置
    private static final String REDIS_MATCH_MARKET_SECOND_CONFIG = "rcs:redis:match:market:second:config:%s:%s:%s";
    // 足篮外的可操盘赛种
    public static List<Integer> OTHER_CAN_TRADE_SPORT = Lists.newArrayList(3, 4, 5, 7, 8, 9, 10);

    //网球、乒乓球
    public static List<Integer> TENNIS_PP_LIST = Arrays.asList(4, 5, 8);

    private static final Set<Long> FOOTBALL_FIFTEEN_MINUTES_SUB_PLAY = new HashSet<>(
            Arrays.asList(33L, 34L, 109L, 110L, 232L, 233L, 336L, 133L));

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFromTemplate(RcsTournamentTemplateComposeModel model) {
        insertFromTemplate(model,"syncJob");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFromTemplate(RcsTournamentTemplateComposeModel model,String messageSource) {
        String linkId = "template_job_"+ model.getMatchId()+"_"+ model.getPlayId()+"_"+ model.getMatchType();
        log.info("::{}::-时间：{}-定时任务-入参：{}",linkId, DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"), JSONObject.toJSONString(model));
        RcsTournamentTemplate template = templateMapper.selectById(model.getTemplateId());
        if (template.getSportId() == 2) {//玩法是篮球
            if (model.getHaveHandicap() != null && !model.getHaveHandicap()) {//当前没有盘口，初始化分时节点最大盘口数至玩法盘口参数
                RcsTournamentTemplatePlayMargain margin = new RcsTournamentTemplatePlayMargain();
                RcsTournamentTemplatePlayMargainRef ref = playMargainRefMapper.selectByPrimaryKey(model.getMarginRefId().intValue());
                log.info("::{}::-定时任务,当前没有盘口-分时margin信息：{}", linkId, JSONObject.toJSONString(ref));
                margin.setMarketCount(ref.getMarketCount());
                margin.setViceMarketRatio(ref.getViceMarketRatio());
                margin.setId(model.getMarginId());
                playMargainMapper.updateById(margin);
                log.info("::{}::-{}-定时任务,当前没有盘口-结束：", linkId, System.currentTimeMillis());
                return;
            }
        }
        Map<String, Object> oldMargainRefParams = new HashMap<String, Object>();
        oldMargainRefParams.put("matchId", model.getMatchId());
        oldMargainRefParams.put("matchType", model.getMatchType());
        oldMargainRefParams.put("playId", model.getPlayId());
        RcsTournamentTemplatePlayMargainRef oldMargainRef = playMargainRefMapper.queryMatchMargainRefInfo(oldMargainRefParams);
        //补充生效marginId
        RcsTournamentTemplatePlayMargain margin = new RcsTournamentTemplatePlayMargain();
        margin.setValidMarginId(model.getMarginRefId().intValue());
        margin.setId(model.getMarginId());
        playMargainMapper.updateById(margin);
        //获取玩法和分时margin
        RcsTournamentTemplatePlayMargainRef ref = playMargainRefMapper.selectByPrimaryKey(model.getMarginRefId().intValue());
        if (Objects.isNull(ref)) {
            log.info("::{}::-koala-定时任务查询:{},入参数:{},方法直接结束", linkId, JSONObject.toJSON(ref), JSONObject.toJSONString(model));
            //如果为空则直接return,不然下面逻辑会报空指针
            return;
        }
        //篮球最大盘口数放入分时节点逻辑
        if (template.getSportId() == 2) {
            log.info("::{}::-篮球修改-分时节点最大盘口数-模板信息：{}", linkId, JSONObject.toJSONString(template));
            //篮球模板玩法里最大盘口数已移入分时节点，所以将分时节点数据更新到模板玩法里
            margin.setMarketCount(ref.getMarketCount());
            margin.setViceMarketRatio(ref.getViceMarketRatio());
            playMargainMapper.updateById(margin);
            //更新查询出来的最大盘口数
            model.setMarketCount(margin.getMarketCount());
            model.setViceMarketRatio(margin.getViceMarketRatio());
            //修改最大盘口数，通知下游
            margin = playMargainMapper.selectById(margin.getId());
            TournamentTemplatePlayVo playVo = getTournamentTemplatePlayVo(template);
            TournamentTemplateCategoryVo tournamentTemplateCategoryVo = new TournamentTemplateCategoryVo();
            tournamentTemplateCategoryVo.setMarketCount(margin.getMarketCount());
            tournamentTemplateCategoryVo.setMarketNearDiff(margin.getMarketNearDiff());
            tournamentTemplateCategoryVo.setPlayId(margin.getPlayId());
            tournamentTemplateCategoryVo.setMarketNearOddsDiff(margin.getMarketNearOddsDiff());
            //加载赛事参数更新的玩法
            List<TournamentTemplateCategoryVo> categoryList = Lists.newArrayList();
            categoryList.add(tournamentTemplateCategoryVo);
            playVo.setCategoryList(categoryList);
            String linkIdByMq = CommonUtils.getLinkId("play_template_update");
            Request request = new Request();
            request.setData(playVo);
            request.setGlobalId(linkIdByMq);
            log.info("::{}::-发送mq推送联赛模板玩法数据:linkId:{} ************ Message:{}", linkId, linkIdByMq, JSONObject.toJSON(request));
            sendMessage.sendMessage("Tournament_Template_Play", linkIdByMq, String.valueOf(playVo.getStandardMatchId()), JSONObject.toJSON(request));
            try {
                // 这个Tournament_Template_Play topic需要先发出，在执行下面的代码，篮球分时节点最大盘口数才会生效。
                Thread.sleep(100l);
                log.info("发送mq推送联赛模板玩法数据后，延迟100毫秒");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        RcsTournamentTemplatePlayMargain margainConfig = playMargainMapper.selectById(ref.getMargainId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(model.getMatchId());
        log.info("::{}::-定时任务-分时margin信息：{}", linkId, JSONObject.toJSONString(ref));
        log.info("::{}::-定时任务-赛事基本信息：{}", linkId, JSONObject.toJSONString(standardMatchInfo));

        //插入rcsMatchMarketConfig
        RcsMatchMarketConfig config = BeanCopyUtils.copyProperties(ref, RcsMatchMarketConfig.class);
        config.setSportId(standardMatchInfo.getSportId());
        config.setMatchType(model.getMatchType());
        config.setPlayId(model.getPlayId());
        config.setMatchId(model.getMatchId());
        config.setMargin(new BigDecimal(Optional.ofNullable(ref.getMargain()).orElse("0")));
        config.setMaxSingleBetAmount(ref.getOrderSinglePayVal());
        checkAndSetMaxBetAmount(config);
        config.setMarketType(margainConfig.getMarketType());
        config.setCategoryPreStatus(ref.getCategoryPreStatus());
        config.setCashOutMargin(ref.getCashOutMargin());
        if (StringUtils.isNotBlank(ref.getPauseMargain())) {
            config.setTimeOutMargin(new BigDecimal(ref.getPauseMargain()));
        }
        if (!ObjectUtils.isEmpty(ref.getPauseWaitTime())) {
            config.setTimeOutWaitSeconds(ref.getPauseWaitTime());
        }
        if (!ObjectUtils.isEmpty(ref.getNormalWaitTime())) {
            config.setWaitSeconds(ref.getNormalWaitTime());
        }
        if (MarketKindEnum.Europe.getValue().equalsIgnoreCase(config.getMarketType())) {
            config.setHomeLevelFirstOddsRate(ref.getMultiOddsRate());
            config.setHomeLevelFirstMaxAmount(ref.getMultiDiffVal());
            if (config.getHomeLevelFirstMaxAmount() == null) {
                config.setHomeLevelFirstMaxAmount(ref.getHomeLevelFirstMaxAmount());
            }
            if (config.getHomeLevelFirstOddsRate() == null) {
                config.setHomeLevelFirstOddsRate(ref.getHomeLevelFirstOddsRate());
            }
        }
        //2129 网球乒乓球 子玩法ID默认使用玩法ID
        Boolean flag = Boolean.FALSE;
        if (TENNIS_PP_LIST.contains(config.getSportId().intValue())) {
            if (Tennis.isExistPlay(config.getPlayId()) || PingPong.isExistPlay(config.getPlayId())) {
                flag = Boolean.TRUE;
            }
        }

        boolean subPlayFlag = config.getSportId() == 1L && FOOTBALL_FIFTEEN_MINUTES_SUB_PLAY.contains(config.getPlayId());

        if (flag) {
            config.setSubPlayId(config.getPlayId() + "");
        }
        List<Float> list = getLimitRatioList(model.getViceMarketRatio());
        if (model.getMarketCount() == null) {
            throw new RcsServiceException(" 联赛配置的盘口数为Null,联赛：" + JSONObject.toJSONString(model));
        }
        MatchOddsConfig matchConfig = new MatchOddsConfig();
        matchConfig.setMatchId(String.valueOf(model.getMatchId()));
        matchConfig.setPlayConfigList(new ArrayList<>());
        MatchPlayConfig playConfig = new MatchPlayConfig();
        matchConfig.getPlayConfigList().add(playConfig);
        playConfig.setMarketType(config.getMarketType());
        playConfig.setRcsTournamentTemplatePlayMargain(margainConfig);

        List<RcsMatchMarketConfig> configs = new ArrayList<>();
        //2129 网球和乒乓球
        if (flag || subPlayFlag) {
            QueryWrapper<RcsMatchMarketConfigSub> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RcsMatchMarketConfigSub::getMatchId, config.getMatchId())
                    .eq(RcsMatchMarketConfigSub::getPlayId, config.getPlayId());
            List<RcsMatchMarketConfigSub> rcsMatchMarketConfigSubs = rcsMatchMarketConfigSubMapper.selectList(wrapper);
            if (rcsMatchMarketConfigSubs.size() > 0) {
                configs = BeanCopyUtils.copyPropertiesList(rcsMatchMarketConfigSubs, RcsMatchMarketConfig.class);
            }
        } else {
            QueryWrapper<RcsMatchMarketConfig> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RcsMatchMarketConfig::getMatchId, config.getMatchId())
                    .eq(RcsMatchMarketConfig::getPlayId, config.getPlayId());
            configs = rcsMatchMarketConfigMapper.selectList(wrapper);
        }

        //分时margin数据插入风控盘口配置表
        batchInsertOrUpdate(list, config, model.getMarketCount());
        //同赛事某玩法下所有盘口配置参数同步到融合
        sendDataToDataCenter(configs, config, ref.getTimeVal(), playConfig);

        //刷新sdk 赛事单注单关限额  按玩法
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("sportId", config.getSportId());
            put("dataType", "4");
            put("matchId", config.getMatchId());
            put("matchType", config.getMatchType() == 0 ? 1 : 0);
            put("playId", config.getPlayId());
            if (config.getSubPlayId() != null) {
                put("subPlayId", config.getSubPlayId());
            }
            //单注最大赔付
            put("val", ref.getOrderSinglePayVal() != null ? ref.getOrderSinglePayVal() * 100L : null);
            //玩法累计
            put("val2", ref.getUserMultiPayVal() != null ? ref.getUserMultiPayVal() * 100L : null);
            //单注最大限额
            put("val3", ref.getOrderSingleBetVal() != null ? ref.getOrderSingleBetVal() * 100L : null);
            // 单注保底投注金额
            put("val4", ref.getSingleHedgeAmount() != null ? ref.getSingleHedgeAmount().longValue() * 100L : null);

        }};
        producerSendMessageUtils.sendMessage("rcs_limit_cache_clear_sdk", "", config.getMatchId() + "_" + config.getPlayId() + "_4", map);
        log.info("::{}::分时节点生效-rcs_limit_cache_clear_sdk 缓存通知:{}", linkId, JSONObject.toJSONString(map));

        if (!FOOTBALL_MOST_ODDS_TYPE_PLAYS.contains(config.getPlayId().intValue())) {
            //去除重复的placeConfig对象
            for (int i = 0; i < matchConfig.getPlayConfigList().size(); i++) {
                Map<String, MatchMarketPlaceConfig> placeConfigMap = matchConfig.getPlayConfigList().get(i).getPlaceConfig().stream().collect(Collectors.toMap(e -> e.getMaxOdds() + "-" + e.getMinOdds() + "-" + e.getPlaceNum() + "-" + e.getSpread(), e -> e, (entity1, entity2) -> entity1));
                List<MatchMarketPlaceConfig> collect = placeConfigMap.values().stream().collect(Collectors.toList());
                matchConfig.getPlayConfigList().get(i).setPlaceConfig(collect);
            }
            String linkIdTask = CommonUtils.getLinkId("task");
            matchConfig.setMessageSource("syncJob");
            matchConfig.setLinkId(linkIdTask);
            String tag = String.format("%s_%s_syncJob", model.getMatchId(), model.getPlayId());
            // 发送到操盘统一计算，手动模式需要操盘自己计算

            // 网球、乒乓球外的可操盘赛种
            if (!OTHER_CAN_TRADE_SPORT.contains(config.getSportId().intValue()) && !flag) {
                flag = Boolean.TRUE;
            }
            if (flag) {
                producerSendMessageUtils.sendMessage("RCS_TRADE_MATCH_ODDS_CONFIG", tag, linkIdTask, matchConfig);
            }
        }

        if (oldMargainRef != null && "2".equals(oldMargainRef.getStatus())) {
            //当前节点失效，需要删除处理
            log.warn("::{}::-当前节点配置已失效，需要重新更新配置：{}", linkId, JSONObject.toJSONString(ref));
            Long refId = oldMargainRef.getId();
            playMargainRefMapper.deleteById(refId);
        } else if (oldMargainRef != null && "3".equals(oldMargainRef.getStatus())) {
            //当前节点数据有更新，需要重新更新
            RcsTournamentTemplatePlayMargainRef updateBean = new RcsTournamentTemplatePlayMargainRef();
            updateBean.setId(oldMargainRef.getId());
            updateBean.setStatus("1");
            playMargainRefMapper.updatePlayMargainRefById(updateBean);
            log.info("::{}::-当前节点数据又更新，已经更新完成：{}", linkId, JSONObject.toJSONString(oldMargainRef));
        } else {
            //当前节点数据有更新，需要重新更新
            RcsTournamentTemplatePlayMargainRef updateBean = new RcsTournamentTemplatePlayMargainRef();
            updateBean.setId(model.getMarginRefId());
            updateBean.setStatus("1");
            playMargainRefMapper.updatePlayMargainRefById(updateBean);
            log.info("::{}::-空处理，当前节点数据又更新，已经更新完成：{}", linkId, JSONObject.toJSONString(oldMargainRef));
        }
        sendPlayStatusChangeMq(config, Long.valueOf(template.getSportId()), linkId);
        log.info("::{}::-定时任务-结束：{}", linkId, System.currentTimeMillis());
    }

    @Override
    public RcsMatchMarketConfigSub queryMatchMarketConfigSub(RcsMatchMarketConfig config) {
        RcsMatchMarketConfigSub matchMarketConfigSub = null;
        String marketConfig = redisClient.hGet(String.format(REDIS_MATCH_MARKET_SUB_CONFIG, config.getMatchId(), config.getPlayId(), config.getSubPlayId()), String.valueOf(config.getMarketIndex()));
        if (StringUtils.isNotBlank(marketConfig)) {
            matchMarketConfigSub = JSONObject.parseObject(marketConfig, RcsMatchMarketConfigSub.class);
        }
        if (ObjectUtils.isEmpty(matchMarketConfigSub)) {
            matchMarketConfigSub = rcsMatchMarketConfigMapper.queryMatchMarketConfigSub(config);
        }
        return matchMarketConfigSub;
    }

    public List<Float> getLimitRatioList(String source) {
        List<Float> list = Lists.newArrayList();
        if (Strings.isNullOrEmpty(source)) {
            return null;
        }
        JSONArray array = JSONObject.parseArray(source);
        for (Object o : array) {
            String s = String.valueOf(Optional.ofNullable(o).orElse("100"));
            Float f = Float.parseFloat(s);
            list.add(f);
        }
        return list;
    }


    private TournamentTemplatePlayVo getTournamentTemplatePlayVo(RcsTournamentTemplate param) {
        TournamentTemplatePlayVo playVo = new TournamentTemplatePlayVo();
        DataSourceCodeVo weight = JSONObject.parseObject(param.getDataSourceCode(), DataSourceCodeVo.class);
        playVo.setStandardMatchId(param.getTypeVal());
        playVo.setMatchType(param.getMatchType());
        playVo.setBcWeight(weight.getBc());
        playVo.setBgWeight(weight.getBg());
        playVo.setSrWeight(weight.getSr());
        playVo.setTxWeight(weight.getTx());
        playVo.setRbWeight(weight.getRb());
        playVo.setPdWeight(weight.getPd());
        playVo.setAoWeight(weight.getAo());
        playVo.setPiWeight(weight.getPi());
        playVo.setLsWeight(weight.getLs());
        return playVo;
    }


    private void batchInsertOrUpdate(List<Float> ratios, RcsMatchMarketConfig config, Integer count) {
        String linkId = "template_job_" + config.getMatchId() + "_" + config.getPlayId() + "_" + config.getMatchType();
        Long maxAmount = config.getMaxSingleBetAmount();
        Long homeLevelFirstMaxAmount = config.getHomeLevelFirstMaxAmount();
        Long homeLevelSecondMaxAmount = config.getHomeLevelSecondMaxAmount();
        Long homeSingleMaxAmount = config.getHomeSingleMaxAmount();
        Long homeMultiMaxAmount = config.getHomeMultiMaxAmount();
        for (int i = count; i >= 1; i--) {
            UpdateWrapper<RcsMatchMarketConfig> userUpdateWrapper = new UpdateWrapper<RcsMatchMarketConfig>();
            userUpdateWrapper.eq("match_id", config.getMatchId()).eq("play_id", config.getPlayId());
            config.setMarketIndex(i);
            if (i > 1 && !CollectionUtils.isEmpty(ratios)) {
                //根据盘口数和限额比例，计算盘口限额
                Long amount = maxAmount;
                Long placeHomeLevelFirstMaxAmount = homeLevelFirstMaxAmount;
                Long placeHomeLevelSecondMaxAmount = homeLevelSecondMaxAmount;
                Long placeHomeSingleMaxAmount = homeSingleMaxAmount;
                Long placeHomeMultiMaxAmount = homeMultiMaxAmount;
                try {
                    amount = (long) (maxAmount * ratios.get(i - 2) / 100);
                    if (NumberUtils.INTEGER_ONE.intValue() == config.getSportId()) {
                        placeHomeLevelFirstMaxAmount = getLongByRatio(ratios, homeLevelFirstMaxAmount, i);
                        if (MarketKindEnum.Malaysia.getValue().equalsIgnoreCase(config.getMarketType())) {
                            placeHomeLevelSecondMaxAmount = getLongByRatio(ratios, homeLevelSecondMaxAmount, i);
                            placeHomeSingleMaxAmount = getLongByRatio(ratios, homeSingleMaxAmount, i);
                            placeHomeMultiMaxAmount = getLongByRatio(ratios, homeMultiMaxAmount, i);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    log.error("::{}::-联赛盘口配置错误:{}", linkId, e.getMessage(), e);
                }
                config.setMaxSingleBetAmount(amount);
                checkAndSetMaxBetAmount(config);
                config.setHomeLevelFirstMaxAmount(placeHomeLevelFirstMaxAmount);
                config.setHomeLevelSecondMaxAmount(placeHomeLevelSecondMaxAmount);
                config.setHomeSingleMaxAmount(placeHomeSingleMaxAmount);
                config.setHomeMultiMaxAmount(placeHomeMultiMaxAmount);
            }
            try {
                //注意：1:足球  2:篮球   主要玩法写入RcsMatchMarketConfig表，子玩法写入RcsMatchMarketConfigSub表
                //5:网球  8:乒乓球 9.排球 所有玩法写入子玩法RcsMatchMarketConfigSub表
                if (X_ODDS_TYPE_PLAYS.contains(config.getPlayId().intValue()) || OTHER_CAN_TRADE_SPORT.contains(config.getSportId().intValue())) {
                    //冠军带X玩法则数据更新到sub表中
                    RcsMatchMarketConfigSub rcsMatchMarketConfigSub = BeanCopyUtils.copyProperties(config, RcsMatchMarketConfigSub.class);
                    //bug-45155两项盘玩法调赔错误兜底，201、214子玩法仅1个盘口,防止下标错误
                    if (BASKETBALL_X_SCORCE_PLAYS.contains(config.getPlayId().intValue())) {
                        rcsMatchMarketConfigSub.setMarketIndex(1);
                    }
                    QueryWrapper<RcsMatchMarketConfigSub> subQueryWrapper = new QueryWrapper<>();
                    subQueryWrapper.eq("match_id", rcsMatchMarketConfigSub.getMatchId());
                    subQueryWrapper.eq("play_id", rcsMatchMarketConfigSub.getPlayId());
                    subQueryWrapper.eq("market_index", rcsMatchMarketConfigSub.getMarketIndex());
                    List<RcsMatchMarketConfigSub> rcsMatchMarketConfigSubs = rcsMatchMarketConfigSubMapper.selectList(subQueryWrapper);
                    List<MatchMarketVo> matchMarketVos = queryMgMatchMarketList(linkId, config);
                    if (ObjectUtils.isEmpty(rcsMatchMarketConfigSubs)) {
                        rcsMatchMarketConfigSub.setId(null);
                        //如果为空则插入
                        log.info("::{}::-定时任务-sub表初始化位置为-1的数据：{}", linkId, JSONObject.toJSONString(rcsMatchMarketConfigSub));
                        rcsMatchMarketConfigSubMapper.insert(rcsMatchMarketConfigSub);
                    } else if ((!CollectionUtils.isEmpty(matchMarketVos) && matchMarketVos.size() > rcsMatchMarketConfigSubs.size())) {
                        //15分钟玩法不跟滚球模板
                        boolean handlerFlag = false;
                        if (SportIdEnum.isFootball(config.getSportId())) {
                            handlerFlag = handlerMatchMarketConfigSubData(matchMarketVos, rcsMatchMarketConfigSubs, config, linkId);
                        }
                        if (!handlerFlag) {
                            //如果早盘已经初始化过数据则做增量更新
                            if (!CollectionUtils.isEmpty(rcsMatchMarketConfigSubs)) {
                                final int marketIndex = i;
                                Optional<RcsMatchMarketConfigSub> marketConfigSub = rcsMatchMarketConfigSubs.stream().filter(item -> item.getMarketIndex() == marketIndex).findFirst();
                                log.info("::{}::-定时任务-sub表初始化:坑位:{}存在数据,做更新操作:{}", linkId, marketIndex, JSONObject.toJSONString(rcsMatchMarketConfigSub));
                                marketConfigSub.ifPresent(matchMarketConfigSub -> {
                                    rcsMatchMarketConfigSub.setId(matchMarketConfigSub.getId());
                                    rcsMatchMarketConfigSubMapper.updateById(rcsMatchMarketConfigSub);
                                });
                            } else {
                                rcsMatchMarketConfigSub.setId(null);
                                log.info("::{}::-定时任务-sub表初始化位置为-1的数据：{}", linkId, JSONObject.toJSONString(rcsMatchMarketConfigSub));
                                rcsMatchMarketConfigSubMapper.insert(rcsMatchMarketConfigSub);
                            }
                        }

                    } else {
                        //否则则根据条件批量修改
                        log.info("::{}::-定时任务-sub表根据玩法修改所有位置数据：{}", linkId, JSONObject.toJSONString(config));
                        rcsMatchMarketConfigMapper.updateMarketConfigSub(config);
                        for (RcsMatchMarketConfigSub sub : rcsMatchMarketConfigSubs) {
                            redisClient.delete(String.format(REDIS_MATCH_MARKET_SUB_CONFIG, config.getMatchId(), config.getPlayId(), sub.getSubPlayId()));
                            //不是足球才设置
                            if (config.getSportId().intValue() != 1) {
                                RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = new RcsTournamentTemplateAcceptConfig();
                                rcsTournamentTemplateAcceptConfig.setWaitSeconds(config.getWaitSeconds());
                                String key = String.format(REDIS_MATCH_MARKET_SUB_SECOND_CONFIG, config.getMatchId(), config.getPlayId(), sub.getSubPlayId(), config.getMarketIndex());
                                JSONObject json = new JSONObject();
                                json.put("key", key);
                                json.put("value", rcsTournamentTemplateAcceptConfig);
                                producerSendMessageUtils.sendMessage("rcs_order_reject_cache_update", "", key, json);
                            }
                        }
                    }

                } else {
                    log.info("::{}::-定时任务-新增和修改玩法数据：{}", linkId, JSONObject.toJSONString(config));
                    rcsMatchMarketConfigMapper.insertOrUpdateMarketConfig(config);
                    redisClient.delete(String.format(REDIS_MATCH_MARKET_CONFIG, config.getMatchId(), config.getPlayId()));
                    //不是足球才设置
                    if (config.getSportId().intValue() != 1) {
                        RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = new RcsTournamentTemplateAcceptConfig();
                        rcsTournamentTemplateAcceptConfig.setWaitSeconds(config.getWaitSeconds());
                        JSONObject json = new JSONObject();
                        String key = String.format(REDIS_MATCH_MARKET_SECOND_CONFIG, config.getMatchId(), config.getPlayId(), config.getMarketIndex());
                        json.put("key", key);
                        json.put("value", rcsTournamentTemplateAcceptConfig);
                        producerSendMessageUtils.sendMessage("rcs_order_reject_cache_update", "", key, json);
                    }
                }
            } catch (Exception e) {
                log.error("::{}::-盘口配置表数据存储错误：{}", linkId, e.getMessage(), e);
            }
            config.setMaxSingleBetAmount(maxAmount);
            checkAndSetMaxBetAmount(config);
            config.setHomeLevelFirstMaxAmount(homeLevelFirstMaxAmount);
            config.setHomeLevelSecondMaxAmount(homeLevelSecondMaxAmount);
            config.setHomeSingleMaxAmount(homeSingleMaxAmount);
            config.setHomeMultiMaxAmount(homeMultiMaxAmount);
        }
    }

    /**
     * sub表初始化
     */
    public boolean handlerMatchMarketConfigSubData(List<MatchMarketVo> matchMarketVos, List<RcsMatchMarketConfigSub> rcsMatchMarketConfigSubs, RcsMatchMarketConfig config, String linkId) {
        if (!CollectionUtils.isEmpty(matchMarketVos)) {
            List<String> subPlayIds = rcsMatchMarketConfigSubs.stream().map(RcsMatchMarketConfigSub::getSubPlayId).collect(Collectors.toList());
            List<RcsMatchMarketConfigSub> subList = Lists.newArrayList();
            for (MatchMarketVo matchMarketVo : matchMarketVos) {
                String childMarketCategoryId = matchMarketVo.getChildMarketCategoryId();
                if (subPlayIds.contains(childMarketCategoryId) || StringUtils.isBlank(childMarketCategoryId) || "-1".equals(childMarketCategoryId)) {
                    continue;
                }
                RcsMatchMarketConfigSub matchMarketConfigSub = BeanCopyUtils.copyProperties(config, RcsMatchMarketConfigSub.class);
                matchMarketConfigSub.setId(null);
                matchMarketConfigSub.setSubPlayId(childMarketCategoryId);
                subList.add(matchMarketConfigSub);
            }
            if (CollectionUtils.isEmpty(subList)) {
                return false;
            }
            log.info("::{}::-定时任务-sub表初始化数据：{}", linkId, JSONObject.toJSONString(subList));
            rcsMatchMarketConfigMapper.insertBatchMarketConfigSub(subList);
            return true;
        }
        return false;
    }

    private Long getLongByRatio(List<Float> ratios, Long homeLevelFirstMaxAmount, int i) {
        homeLevelFirstMaxAmount = new BigDecimal(homeLevelFirstMaxAmount).multiply(new BigDecimal(ratios.get(i - 2))).divide(new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE), NumberUtils.INTEGER_TWO, BigDecimal.ROUND_DOWN).longValue();
        return homeLevelFirstMaxAmount;
    }

    private void sendDataToMQ(RcsMatchMarketConfig config) {
        String linkId = "template_job_" + config.getMatchId() + "_" + config.getPlayId() + "_" + config.getMatchType();
        producerSendMessageUtils.sendMessage(TEMPLATE_MARKET_CONFIG_TOPIC, config);

        /*Map<String, Object> map = new HashMap<String, Object>();
        //Long sportId= standardMatchInfoMapper.selectById(config.getMatchId()).getSportId();
        map.put("sportId",config.getSportId());
        map.put("dataType", "4");
        map.put("matchId", config.getMatchId());
        map.put("matchType", config.getMatchType());
        map.put("subPlayId", config.getSubPlayId());
        log.info("::{}::-RCS_LIMIT_CACHE_CLEAR_TOPIC缓存通知:{}", linkId, JSONObject.toJSONString(map));
        producerSendMessageUtils.sendMessage("RCS_LIMIT_CACHE_CLEAR_TOPIC", map);*/
        PeningOrderCacheClearVo peningOrderCacheClearVo = new PeningOrderCacheClearVo();
        peningOrderCacheClearVo.setMarginRef("marginRef");
        peningOrderCacheClearVo.setMatchId(String.valueOf(config.getMatchId()));
        peningOrderCacheClearVo.setSportId(config.getSportId().intValue());
        peningOrderCacheClearVo.setPlayId(config.getPlayId().intValue());
        //通知缓存清理
        producerSendMessageUtils.sendMessage("PENDING_ORDER_DELETECACHE", peningOrderCacheClearVo);
        log.info("::{}::-PENDING_ORDER_DELETECACHE缓存通知:{}", linkId, JSONObject.toJSONString(peningOrderCacheClearVo));
    }

    /**
     * 根据赛事，玩法id查询mongodb盘口数据
     */
    public List<MatchMarketVo> queryMgMatchMarketList(String linkId, RcsMatchMarketConfig config) {
        Criteria criteria = Criteria.where("matchId").is(String.valueOf(config.getMatchId())).and("id").is(config.getPlayId());
        Query query = new Query();
        query.addCriteria(criteria);
        MarketCategory one = mongoTemplate.findOne(query, MarketCategory.class);
        if (null == one) {
            return null;
        }
        if (CollectionUtils.isEmpty(one.getMatchMarketVoList())) {
            return null;
        }
        List<MatchMarketVo> matchMarketVoList = one.getMatchMarketVoList();
        return matchMarketVoList;
    }


    private void sendDataToDataCenter(List<RcsMatchMarketConfig> configs, RcsMatchMarketConfig config, Long timeFrame, MatchPlayConfig playConfig) {
        List<RcsMatchMarketConfig> list = Lists.newArrayList();
        //注意：1:足球  2:篮球   主要玩法写入RcsMatchMarketConfig表，子玩法写入RcsMatchMarketConfigSub表
        //5:网球  8:乒乓球 9.排球 所有玩法写入子玩法RcsMatchMarketConfigSub表
        if (X_ODDS_TYPE_PLAYS.contains(config.getPlayId().intValue()) || OTHER_CAN_TRADE_SPORT.contains(config.getSportId().intValue())) {
            //带x玩法查sub表
            QueryWrapper<RcsMatchMarketConfigSub> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RcsMatchMarketConfigSub::getMatchId, config.getMatchId())
                    .eq(RcsMatchMarketConfigSub::getPlayId, config.getPlayId());
            List<RcsMatchMarketConfigSub> subList = rcsMatchMarketConfigSubMapper.selectList(wrapper);
            if (!ObjectUtils.isEmpty(subList)) {
                list = BeanCopyUtils.copyPropertiesList(subList, RcsMatchMarketConfig.class);
                List<MatchMarketVo> mgMatchMarketList = queryMgMatchMarketList(null,config);
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    RcsMatchMarketConfig rcsMatchMarketConfig = list.get(i);
                    String subPlayId = rcsMatchMarketConfig.getSubPlayId();
                    MatchMarketVo matchMarketVo = mgMatchMarketList.get(i);
                    if (!ObjectUtils.isEmpty(matchMarketVo) ){
                        String childMarketCategoryId = matchMarketVo.getChildMarketCategoryId();
                        //玩法盘口数据与mongodb比较，如果不一致取mongodb
                        if(StringUtils.isNotBlank(childMarketCategoryId) && !subPlayId.equals(childMarketCategoryId)){
                            rcsMatchMarketConfig.setSubPlayId(childMarketCategoryId);
                            buf.append(rcsMatchMarketConfig.getPlayId()).append("_").append(rcsMatchMarketConfig.getSubPlayId()).append(";");
                        }
                    }
                }
                log.info("::{}::-定时任务-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,设置子玩法ID关系:{}",config.getMatchId().toString(),buf.toString());
            }
        } else {
            //其他玩法走原有逻辑
            QueryWrapper<RcsMatchMarketConfig> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RcsMatchMarketConfig::getMatchId, config.getMatchId())
                    .eq(RcsMatchMarketConfig::getPlayId, config.getPlayId())
                    .le(RcsMatchMarketConfig::getMarketIndex, playConfig.getRcsTournamentTemplatePlayMargain().getMarketCount());
            list = rcsMatchMarketConfigMapper.selectList(wrapper);
        }
        RcsTradeConfig rcsTradeConfig = rcsTradeConfigMapper.selectRcsTradeConfig(config.getMatchId().toString(), config.getPlayId().toString(), null);
        Integer dataSource = DataSourceTypeEnum.AUTOMATIC.getValue();
        if (!ObjectUtils.isEmpty(rcsTradeConfig) && !ObjectUtils.isEmpty(rcsTradeConfig.getDataSource())) {
            dataSource = rcsTradeConfig.getDataSource();
        }

        playConfig.setPlayId(String.valueOf(config.getPlayId()));
        playConfig.setPlaceConfig(new ArrayList<MatchMarketPlaceConfig>());

        //kir-1368-同步玩法级别提前结算给enzo
        if (SportIdEnum.isFootball(config.getSportId())) {
            ConfigCashOutTradeItemDTO configCashOutTradeItemDTO = setCashOutTradeItemDTO(config);
            producerSendMessageUtils.sendMessage("RCS_TOUR_TEMPLATE_CASHOUT_TOPIC", "plays", configCashOutTradeItemDTO.getMatchId() + "_" + configCashOutTradeItemDTO.getMarketType() + "_" + configCashOutTradeItemDTO.getMarketCategoryId(), configCashOutTradeItemDTO);
        }

        //RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig = new RcsTournamentTemplateAcceptConfig();
        //rcsTournamentTemplateAcceptConfig.setWaitSeconds(config.getWaitSeconds());

        //分时节点生效后，需要发给trade做分时节点本地缓存修改及redis修改（2022世界杯后逻辑调整）
        String cacheKey = String.format("rcs_match_template_play_margin_ref_data:%s:%s:%s", config.getMatchId(), config.getMatchType(), config.getPlayId());
        if (config.getMatchType() == 1) {
            //早盘缓存7天
            //waldkir-redis集群-发送至trade进行广播
            //补充 真实value = ref  不是config  不然少字段  暂时做删除缓存处理
            LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(cacheKey, null, 7 * 24 * 60 * 60 * 1000L);
            producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "playMarginRef_" + config.getPlayId(), config.getMatchId() + "_" + config.getPlayId(), syncBean);
            log.info("::{}::分时节点-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", config.getMatchId().toString(), JSONObject.toJSONString(syncBean));
        } else {
            //滚球缓存4小时
            //waldkir-redis集群-发送至trade进行广播
            //补充 真实value = ref  不是config  不然少字段  暂时做删除缓存处理
            LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(cacheKey, null, 4 * 60 * 60 * 1000L);
            producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "playMarginRef_" + config.getPlayId(), config.getMatchId() + "_" + config.getPlayId(), syncBean);
            log.info("::{}::分时节点-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", config.getMatchId().toString(), JSONObject.toJSONString(syncBean));
        }

        for (RcsMatchMarketConfig conf : list) {
            MatchMarketPlaceConfig placeConfig = new MatchMarketPlaceConfig();
            placeConfig.setPlaceNum(conf.getMarketIndex());
            // 设置之前的margin
            if (!CollectionUtils.isEmpty(configs)) {
                for (RcsMatchMarketConfig c : configs) {
                    if (c.getMarketIndex().intValue() == conf.getMarketIndex()) {
                        placeConfig.setOldMargin(c.getMargin().toPlainString());
                        break;
                    }
                }
            }
            conf.setMargin(config.getMargin());
            conf.setTimeOutMargin(config.getTimeOutMargin());
            conf.setDataSource(dataSource.longValue());
            conf.setMatchType(config.getMatchType());
            playConfig.getPlaceConfig().add(placeConfig);
            //构建水差
            TradeMarketUiConfigDTO dto = updateWaterToDataCenter(conf, placeConfig);
            //构建玩法提前结算开关-足球
            if (SportIdEnum.isFootball(config.getSportId())) {
                ConfigCashOutTradeItemDTO configCashOutTradeItemDTO = setCashOutTradeItemDTO(config);
                dto.setConfigCashOutTradeItemDTO(configCashOutTradeItemDTO);
            }
            conf.setSportId(config.getSportId());
            //调用融合Api
            updateMarginToDataCenter(conf, dto, placeConfig, playConfig);
            //推送MQ消息给融合
            updateMarginToDataCenterMQ(conf, dto);
            //发送到订单模块
            sendDataToMQ(conf);
//            if (MarketKindEnum.Europe.getValue().equalsIgnoreCase(config.getMarketType())) {
//                RcsMatchMarketMarginConfig model = new RcsMatchMarketMarginConfig();
//                model.setHomeMargin(conf.getMargin());
//                model.setAwayMargin(conf.getMargin());
//                model.setTieMargin(conf.getMargin());
//                // 处理盘口id为空问题
//                if (!CollectionUtils.isEmpty(playAllMarketList)){
//                    for (RcsStandardMarketDTO d:playAllMarketList){
//                        if (d.getPlaceNum().intValue() == conf.getMarketIndex()){
//                            model.setMarketId(Long.parseLong(d.getId()));
//                        }
//                    }
//                }
//                model.setMatchId(conf.getMatchId());
//                model.setPlayId(conf.getPlayId());
//                rcsMatchMarketMarginConfigMapper.insertOrUpdateMarketMarginConfig(model);
//            }
        }
    }

    /**
     * kir-1368-同步玩法级别提前结算给enzo
     *
     * @param config
     * @return
     */
    private ConfigCashOutTradeItemDTO setCashOutTradeItemDTO(RcsMatchMarketConfig config) {
        ConfigCashOutTradeItemDTO cashOutTradeItemDTO = new ConfigCashOutTradeItemDTO();
        cashOutTradeItemDTO.setMatchId(config.getMatchId());
        cashOutTradeItemDTO.setMarketType(config.getMatchType());
        cashOutTradeItemDTO.setMarketCategoryId(config.getPlayId());
        cashOutTradeItemDTO.setCategoryPreStatus(config.getCategoryPreStatus());
        cashOutTradeItemDTO.setCashOutMargin(config.getCashOutMargin());
        return cashOutTradeItemDTO;
    }

    public List<RcsCategoryOddTemplet> getTemplate(Long matchId, Integer category) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("matchId", matchId);
        map.put("playId", category);
        List<RcsCategoryOddTemplet> result = playMargainRefMapper.queryMatchOddTypeListByMatchIdAndCategoryId(map);
        return result;
    }

    private BigDecimal getConvertedOdds(String oddsValue) {
        //5：根据操盘方式转换赔率
        if (MapUtils.isEmpty(oddsMap)) {
            QueryWrapper<RcsOddsConvertMappingMy> queryWrapper = new QueryWrapper<>();
            List<RcsOddsConvertMappingMy> list = rcsOddsConvertMappingMyMapper.selectList(queryWrapper);
            oddsMap = list.stream().collect(Collectors.toMap(e -> e.getMalaysia(), e -> e.getEurope()));
        }
        String fieldOddsValue = oddsMap.get(oddsValue);
        if (StringUtils.isBlank(fieldOddsValue)) {
            fieldOddsValue = NumberUtils.INTEGER_ZERO.toString();
        }
//        String fieldOddsValue = rcsOddsConvertMappingMyService.listRcsOddsConvertMappingMy(oddsValue);
        return new BigDecimal(fieldOddsValue);
    }


    private TradeMarketUiConfigDTO updateWaterToDataCenter(RcsMatchMarketConfig config, MatchMarketPlaceConfig placeConfig) {
        TradeMarketUiConfigDTO newConfigBean = new TradeMarketUiConfigDTO();
        newConfigBean.setStandardMatchInfoId(config.getMatchId());
        newConfigBean.setMarketType(config.getMatchType());
        newConfigBean.setPlaceNum(config.getMarketIndex());
        newConfigBean.setStandardCategoryId(config.getPlayId());
        //设置最大最小配置
        if (config.getMinOdds() != null && config.getMaxOdds() != null) {
            // 统一转成欧赔
            formatMaxAndMinOdds(config);

            placeConfig.setMinOdds(config.getMinOdds().toPlainString());
            placeConfig.setMaxOdds(config.getMaxOdds().toPlainString());

            List<TradeMarketConfigItemDTO> marketConfigs = Lists.newArrayList();
            newConfigBean.setMarketConfigs(marketConfigs);

            TradeMarketConfigItemDTO marketConfigBean = new TradeMarketConfigItemDTO();
            marketConfigs.add(marketConfigBean);

            marketConfigBean.setMarketCategoryId(config.getPlayId());
            marketConfigBean.setMaxOddsValue(config.getMaxOdds().doubleValue());
            marketConfigBean.setMinOddsValue(config.getMinOdds().doubleValue());
        }
        return newConfigBean;
    }

    /**
     * @return void
     * @Description //统一转换成欧洲赔率
     * @Param [config]
     * @Author Sean
     * @Date 16:24 2020/10/24
     **/
    private void formatMaxAndMinOdds(RcsMatchMarketConfig config) {
        String linkId = CommonUtils.getLinkIdByMdc();
        if (MarketKindEnum.Malaysia.getValue().equalsIgnoreCase(config.getMarketType())) {
            try {
                String maxOdds = rcsOddsConvertMappingMapper.queryMaxOdds(config.getMaxOdds().toString());
                config.setMaxOdds(new BigDecimal(maxOdds));
                String minOdds = rcsOddsConvertMappingMapper.queryMinOdds(config.getMinOdds().toString());
                config.setMinOdds(new BigDecimal(minOdds));
            } catch (Exception e) {
                log.error("::{}::-kir-volleyball:{}", linkId, config);
            }
        }
    }

    /**
     * 构建marginlist
     *
     * @param @return 设定文件
     * @return List<MarketMarginDtlDTO>    返回类型
     * @throws
     * @Title: buildMargainList
     * @Description: TODO
     */
    public List<MarketMarginDtlDTO> buildMargainList(BigDecimal margain, Integer playId, List<RcsCategoryOddTemplet> templets, String marketType) {
        List<MarketMarginDtlDTO> margins = new ArrayList<MarketMarginDtlDTO>();
        if (MarketKindEnum.Europe.getValue().equalsIgnoreCase(marketType) && templets.size() == 2) {
            for (RcsCategoryOddTemplet templet : templets) {
                MarketMarginDtlDTO margin = new MarketMarginDtlDTO();
                margin.setOddsType(templet.getOddType());
//               margin.setTimeFrame(0l);
                margin.setMargin(Optional.ofNullable(margain).orElse(new BigDecimal("110")).doubleValue());
                margins.add(margin);
            }
        } else if (MarketKindEnum.Malaysia.getValue().equalsIgnoreCase(marketType)) {

            MarketMarginDtlDTO margin = new MarketMarginDtlDTO();
//           margin.setTimeFrame(0l);
            margin.setMargin(Optional.ofNullable(margain).orElse(new BigDecimal("0.1")).doubleValue());
            margins.add(margin);
            RcsCategoryOddTemplet templet = templets.get(templets.size() - 1);
            margin.setOddsType(templet.getOddType());
//            //独赢玩法 默认传1
//           if(RcsMatchMarketConfigServiceImpl.SINGLE_WIN_PLAYS.contains(playId)){
//               margin.setOddsType("1");
//           }
        }

        return margins;
    }

    private void updateMarginToDataCenterMQ(RcsMatchMarketConfig config, TradeMarketUiConfigDTO apiConfig) {
        String rootLinkId = "template_job_" + config.getMatchId() + "_" + config.getPlayId() + "_" + config.getMatchType();
        log.info("::{}::-同步联赛模板Margin数据，config:{}", rootLinkId, config);
        //足球 篮球
        if (!SportIdEnum.isFootball(config.getSportId()) && !SportIdEnum.isBasketball(config.getSportId())) {
            return;
        }
        Integer playId = config.getPlayId().intValue();
        //配合融合2441需求新增玩法 384 385 386 387 389 391 392 394 395 396 397 398
        List<Integer> footBallMarginPlayList = Stream.of(5, 43, 352, 142, 384, 385, 386, 387, 389, 391, 392, 394, 395, 396, 397, 398).collect(Collectors.toList());
        List<Integer> basketBallMarginPlayList = Stream.of(37, 43, 142, 48, 54, 60, 66,49, 55 ,61 ,67, 147, 200,209,210,211,212,216,219,213).collect(Collectors.toList());
        if (!FOOTBALL_THREE_MANY_ITEM_PLAYS.contains(playId) && !footBallMarginPlayList.contains(playId)
                && !basketBallMarginPlayList.contains(playId)) {
            return;
        }
        log.info("::{}::-同步联赛模板Margin数据，apiConfig:{}", rootLinkId, apiConfig);
        TradeMarketItemConfig tradeMarketItemConfig = new TradeMarketItemConfig();
        tradeMarketItemConfig.setStandardMatchInfoId(apiConfig.getStandardMatchInfoId());
        tradeMarketItemConfig.setStandardCategoryId(apiConfig.getStandardCategoryId());
        tradeMarketItemConfig.setChildStandardCategoryId(apiConfig.getChildStandardCategoryId());
        tradeMarketItemConfig.setPlaceNum(apiConfig.getPlaceNum());
        tradeMarketItemConfig.setMarketType(apiConfig.getMarketType());
        tradeMarketItemConfig.setMargin(config.getMargin());
        tradeMarketItemConfig.setMaxOddsValue(config.getMaxOdds());
        tradeMarketItemConfig.setMinOddsValue(config.getMinOdds());
        String linkId = CommonUtils.getLinkId("MARKET_ITEM_MARGIN_CONFIG");
        Request<TradeMarketItemConfig> request = new Request<>();
        request.setData(tradeMarketItemConfig);
        request.setGlobalId(linkId);
        log.info("::{}::-同步联赛模板Margin数据，发送到融合:linkId:{} ************ Message:{}", rootLinkId, linkId, JSONObject.toJSON(request));
        sendMessage.sendMessage("MARKET_ITEM_MARGIN_CONFIG", linkId, String.valueOf(apiConfig.getStandardMatchInfoId()), JSONObject.toJSON(request));
    }

    private void updateMarginToDataCenter(RcsMatchMarketConfig config, TradeMarketUiConfigDTO apiConfig, MatchMarketPlaceConfig placeConfig,
                                          MatchPlayConfig playConfig) {
        String linkId = "template_job_" + config.getMatchId() + "_" + config.getPlayId() + "_" + config.getMatchType();
        String redisKey = String.format("rcs:task:match:event:%s", config.getMatchId());
        String ev = redisClient.get(redisKey);
        if ("timeout".equals(ev)) {
            config.setMargin(config.getTimeOutMargin());
        }
        if (config.getMargin() != null) {
            playConfig.getRcsTournamentTemplatePlayMargain().setMargain(config.getMargin().toPlainString());
        }
        List<RcsCategoryOddTemplet> templets = getTemplate(config.getMatchId(), config.getPlayId().intValue());
        if (!CollectionUtils.isEmpty(templets)) {
            if (config.getMargin() != null) {
                placeConfig.setSpread(config.getMargin().toPlainString());
            } else {//设置默认值
                if (MarketKindEnum.Europe.getValue().equalsIgnoreCase(config.getMarketType())) {//多项盘
                    placeConfig.setSpread("110");
                } else {
                    placeConfig.setSpread("0.1");
                }
            }
            apiConfig.setMarketMarginDtlDTOList(buildMargainList(config.getMargin(), config.getPlayId().intValue(), templets, config.getMarketType()));
        } else {
            log.error("::{}::-定时任务-玩法没有配置投注项信息RcsCategoryOddTemplet，导致无法确定投注项oddType，bean:{}", linkId, JSONObject.toJSONString(apiConfig));
        }
        if (apiConfig.getMarketMarginDtlDTOList() == null && apiConfig.getMarketConfigs() == null) {
            log.warn("::{}::-定时任务-配置为空，不调用融合api接口{}", linkId, JSONObject.toJSONString(apiConfig));
            return;
        }
        if (MarketKindEnum.Europe.getValue().equalsIgnoreCase(config.getMarketType())) {
            apiConfig.setLinkageMode(config.getLinkageMode());
            if (ObjectUtils.isEmpty(apiConfig.getLinkageMode())) {
                if (DataSourceTypeEnum.AUTOMATIC.getValue().intValue() == config.getDataSource()) {
                    apiConfig.setLinkageMode(NumberUtils.INTEGER_ONE);
                } else {
                    apiConfig.setLinkageMode(NumberUtils.INTEGER_ZERO);
                }
            }
            List<MarketMarginGapDtlDTO> marginGapDtlDTOList = Lists.newArrayList();
            for (RcsCategoryOddTemplet dto : templets) {
                MarketMarginGapDtlDTO dtlDTO = new MarketMarginGapDtlDTO();
                dtlDTO.setOddsType(dto.getOddType());
                dtlDTO.setMargin(config.getMargin().doubleValue());
                marginGapDtlDTOList.add(dtlDTO);
            }
            if (templets.size() > 2) {
                apiConfig.setMarginGapDtlDTOList(marginGapDtlDTOList);
            }
        }
        if (FOOTBALL_MOST_ODDS_TYPE_PLAYS.contains(config.getPlayId().intValue())) {
            log.info("::{}::-定时任务-多项盘不推送margin", linkId);
            apiConfig.setMarginGapDtlDTOList(null);
            apiConfig.setMarketMarginDtlDTOList(null);
        }
        log.info("::{}::-定时任务-同步联赛模板数据，发送到融合：{}", linkId, JSONObject.toJSONString(apiConfig));
        DataRealtimeApiUtils.handleApi(apiConfig, Long.toString(apiConfig.getStandardMatchInfoId()), apiConfig.getStandardCategoryId().intValue(), new DataRealtimeApiUtils.ApiCall() {
            @Override
            public <R> Response<R> callApi(Request request) {
                return tradeMarketConfigApi.putTradeMarketUiConfig(request);
            }
        });
        log.info("::{}::-定时任务-同步联赛模板数据，发送到融合，发送完成", linkId);
    }

    /**
     * 需求-2519-提前结算-同步业务
     * 赛事级别提前结算开关/玩法提前结算开关/spread/cashOutMargin
     *
     * @param config 配置信息
     * @param linkId
     */
    private void sendPlayStatusChangeMq(RcsMatchMarketConfig config, Long sportId, String linkId) {
        //2578需求-添加篮球提前结算-同步业务流程

        //系统级别和赛事级别在trade项目会同步

        //判断玩法开关
        RcsCategoryPreSettlementConfig settlementConfig = rcsCategoryPreSettlementConfigService.getOne(Wrappers.<RcsCategoryPreSettlementConfig>lambdaQuery()
                .eq(RcsCategoryPreSettlementConfig::getSportId, sportId).eq(RcsCategoryPreSettlementConfig::getPlayId, config.getPlayId()));
        if (!ObjectUtils.isEmpty(settlementConfig)){
            switch (config.getMatchType()){
                //滚球
                case 0:
                    if (!BooleanUtil.isTrue(settlementConfig.getRollingBallStatus())){
                        log.info("::{}::{}::分时节点::提前结算::玩法级别提前结算开关未打开::{}::赛种::{}", linkId, config.getMatchId(), config, sportId);
                        return;
                    }
                    break;
                //早盘
                case 1:
                    if (!BooleanUtil.isTrue(settlementConfig.getMorningTradingStatus())){
                        log.info("::{}::{}::分时节点::提前结算::玩法级别提前结算开关未打开::{}::赛种::{}", linkId, config.getMatchId(), config, sportId);
                        return;
                    }
                    break;
                default:
                    break;
            }
        }

//        List<Long> palys = SportIdEnum.BASKETBALL.isYes(sportId) ?
//                RcsConstant.BASKETBALL_EARLY_SETTLEMENT_PLAY : FOOTBALL_EARLY_SETTLEMENT_PLAY;
//        if (!palys.contains(config.getPlayId())) {
//            log.info("::{}::{}::分时节点::提前结算::非提前结算玩法::{}::赛种::{}", linkId, config.getMatchId(), config, sportId);
//            return;
//        }
        log.info("::{}::{}::分时节点::提前结算::{}::赛种::{}", linkId, config.getMatchId(), config, sportId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sportId", config.getSportId());
        jsonObject.put("matchId", config.getMatchId());
        jsonObject.put("playId", config.getPlayId());
        jsonObject.put("updateTime", System.currentTimeMillis());
        jsonObject.put("linkId", linkId);
        jsonObject.put("categoryPreStatus", config.getCategoryPreStatus());
        if (MarketKindEnum.Europe.getValue().equalsIgnoreCase(config.getMarketType())) {
            //欧赔没有spread,使用的是cashOutMargin
            jsonObject.put("spread", "0.0");
        } else {
            //margin玩法
            jsonObject.put("spread", config.getMargin());
        }

        jsonObject.put("cashOutMargin", config.getCashOutMargin());
        String tag = config.getMatchId() + "_" + config.getPlayId();
        producerSendMessageUtils.sendMessage("RCS_MATCH_CATEGORY_CONFIG_NOTIFY", tag, linkId, jsonObject);
    }


}
