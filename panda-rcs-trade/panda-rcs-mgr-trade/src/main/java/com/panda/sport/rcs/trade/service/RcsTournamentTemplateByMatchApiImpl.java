package com.panda.sport.rcs.trade.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.tournament.TournamentTemplateByMatchService;
import com.panda.sport.data.rcs.dto.LocalCacheSyncBean;
import com.panda.sport.data.rcs.dto.tournament.*;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargainRef;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.util.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * 新增联赛模板3个限额查询相关接口
 *
 * @author waldkir
 * @date 2023-01-02
 */
@Service
@Slf4j
public class RcsTournamentTemplateByMatchApiImpl implements TournamentTemplateByMatchService {

    @Autowired
    private RcsTournamentTemplateMapper rcsTournamentTemplateMapper;
    @Autowired
    private RcsTournamentTemplatePlayMargainMapper playMargainMapper;
    @Autowired
    private RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    public Response<MatchTemplateDataResVo> queryMatchTemplateData(Request<MatchTemplateDataReqVo> requestParam) throws RcsServiceException {
        log.info("::{}:: queryMatchTemplateData,查询赛事模板相关字段,参数为:{}", requestParam.getGlobalId(), JsonFormatUtils.toJson(requestParam.getData()));
        if (requestParam.getData() == null) {
            return Response.error(500, "入参为空");
        }
        if (requestParam.getData().getSportId() == null) {
            return Response.error(500, "sportId不能为空");
        }
        if (requestParam.getData().getMatchId() == null) {
            return Response.error(500, "matchId不能为空");
        }
        if (requestParam.getData().getMatchType() == null) {
            return Response.error(500, "matchType不能为空");
        }
        String timeCacheKey = String.format("rcs_match_template_data:%s:%s", requestParam.getData().getMatchId(), requestParam.getData().getMatchType());
        //获取对应缓存数据
        Object resVoCache = RcsLocalCacheUtils.timedCache.get(timeCacheKey);
        log.info("::{}:: queryMatchTemplateData,str值为:{},resVo值为:{}", requestParam.getGlobalId(),String.valueOf(resVoCache), JSONObject.toJSONString(resVoCache));
        //若未取到缓存，则入库查询拿到结果后进行设置定时缓存，再返回结果，否则直接返回结果
        if (ObjectUtils.isEmpty(resVoCache)) {
            MatchTemplateDataResVo resVo = new MatchTemplateDataResVo();
            //根据入参查出对应数据
            QueryWrapper<RcsTournamentTemplate> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(RcsTournamentTemplate::getTypeVal, requestParam.getData().getMatchId());
            queryWrapper.lambda().eq(RcsTournamentTemplate::getSportId, requestParam.getData().getSportId());
            queryWrapper.lambda().eq(RcsTournamentTemplate::getMatchType, requestParam.getData().getMatchType());
            RcsTournamentTemplate rcsTournamentTemplate = rcsTournamentTemplateMapper.selectOne(queryWrapper);
            if (!ObjectUtils.isEmpty(rcsTournamentTemplate)) {
                //若查询结果不为空，则进行构造出参
                resVo = BeanCopyUtils.copyProperties(rcsTournamentTemplate, MatchTemplateDataResVo.class);
                resVo.setMatchId(rcsTournamentTemplate.getTypeVal());
            } else {
                return Response.error(500, "未查询到对应数据");
            }
            if (requestParam.getData().getMatchType() == 1) {
                //早盘缓存7天
                //waldkir-redis集群-发送至trade进行广播
                LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(timeCacheKey, resVo, 7 * 24 * 60 * 60 * 1000L);
                producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "refreshTemplate", resVo.getMatchId().toString(), syncBean);
                log.info("::{}::刷新缓存-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", resVo.getMatchId().toString(), JSONObject.toJSONString(syncBean));
            } else {
                //滚球缓存4小时
                //waldkir-redis集群-发送至trade进行广播
                LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(timeCacheKey, resVo, 4 * 60 * 60 * 1000L);
                producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "refreshTemplate", resVo.getMatchId().toString(), syncBean);
                log.info("::{}::刷新缓存-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", resVo.getMatchId().toString(), JSONObject.toJSONString(syncBean));
            }
            log.info("::{}:: queryMatchTemplateData,查询赛事模板相关字段 ,内存key为:{},内存值为:{}", requestParam.getGlobalId(), timeCacheKey, JSONObject.toJSONString(resVo));
            return Response.success(resVo);
        } else {
            MatchTemplateDataResVo resVo = JSONObject.parseObject(JSONObject.toJSONString(resVoCache),MatchTemplateDataResVo.class);
            return Response.success(resVo);
        }
    }

    @Override
    public Response<MatchTemplatePlayMarginDataResVo> queryMatchTemplatePlayMarginData(Request<MatchTemplatePlayMarginDataReqVo> requestParam) throws RcsServiceException {
        log.info("::{}:: queryMatchTemplatePlayMarginData,查询赛事模板中指定玩法相关字段,参数为:{}", requestParam.getGlobalId(), JsonFormatUtils.toJson(requestParam.getData()));
        if (requestParam.getData() == null) {
            return Response.error(500, "入参为空");
        }
        if (requestParam.getData().getSportId() == null) {
            return Response.error(500, "sportId不能为空");
        }
        if (requestParam.getData().getMatchId() == null) {
            return Response.error(500, "matchId不能为空");
        }
        if (requestParam.getData().getMatchType() == null) {
            return Response.error(500, "matchType不能为空");
        }
        if (requestParam.getData().getPlayId() == null) {
            return Response.error(500, "playId不能为空");
        }
        String timeCacheKey = String.format("rcs_match_template_play_margin_data:%s:%s:%s", requestParam.getData().getMatchId(), requestParam.getData().getMatchType(), requestParam.getData().getPlayId());
        //获取对应缓存数据
        Object resVoCache = RcsLocalCacheUtils.timedCache.get(timeCacheKey);
        log.info("::{}:: queryMatchTemplatePlayMarginData,str值为:{},resVo值为:{}", requestParam.getGlobalId(),String.valueOf(resVoCache), JSONObject.toJSONString(resVoCache));

        //若未取到缓存，则入库查询拿到结果后进行设置定时缓存，再返回结果，否则直接返回结果
        if (ObjectUtils.isEmpty(resVoCache)) {
            MatchTemplatePlayMarginDataResVo resVo = new MatchTemplatePlayMarginDataResVo();

            //根据入参查出对应数据
            RcsMatchMarketConfig rcsMatchMarketConfig = new RcsMatchMarketConfig();
            rcsMatchMarketConfig.setMatchId(requestParam.getData().getMatchId());
            rcsMatchMarketConfig.setPlayId(Long.valueOf(requestParam.getData().getPlayId()));
            rcsMatchMarketConfig.setMatchType(requestParam.getData().getMatchType());
            RcsTournamentTemplatePlayMargain playMargin = playMargainMapper.selectPlayMarginByMatchInfo(rcsMatchMarketConfig);
            if (!ObjectUtils.isEmpty(playMargin)) {
                //若查询结果不为空，则进行构造出参
                resVo = BeanCopyUtils.copyProperties(playMargin, MatchTemplatePlayMarginDataResVo.class);
                resVo.setMatchId(requestParam.getData().getMatchId());
                resVo.setMatchType(requestParam.getData().getMatchType());
                resVo.setSportId(requestParam.getData().getSportId());
            } else {
                return Response.error(500, "未查询到对应数据");
            }
            if (requestParam.getData().getMatchType() == 1) {
                //早盘缓存7天
                //waldkir-redis集群-发送至trade进行广播
                LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(timeCacheKey, resVo, 7 * 24 * 60 * 60 * 1000L);
                producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "refreshMarginPlay", resVo.getMatchId() + "_" + resVo.getPlayId(), syncBean);
                log.info("::{}::刷新缓存-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", resVo.getMatchId().toString(), JSONObject.toJSONString(syncBean));
            } else {
                //滚球缓存4小时
                //waldkir-redis集群-发送至trade进行广播
                LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(timeCacheKey, resVo, 4 * 60 * 60 * 1000L);
                producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "refreshMarginPlay", resVo.getMatchId() + "_" + resVo.getPlayId(), syncBean);
                log.info("::{}::刷新缓存-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", resVo.getMatchId().toString(), JSONObject.toJSONString(syncBean));
            }
            log.info("::{}:: queryMatchTemplatePlayMarginData,查询赛事模板中指定玩法相关字段 ,内存key为:{},内存值为:{}", requestParam.getGlobalId(), timeCacheKey, JSONObject.toJSONString(resVo));
            return Response.success(resVo);
        } else {
            MatchTemplatePlayMarginDataResVo resVo = JSONObject.parseObject(JSONObject.toJSONString(resVoCache),MatchTemplatePlayMarginDataResVo.class);
            return Response.success(resVo);
        }
    }

    @Override
    public Response<MatchTemplatePlayMarginRefDataResVo> queryMatchTemplatePlayMarginRefData(Request<MatchTemplatePlayMarginRefDataReqVo> requestParam) throws RcsServiceException {
        log.info("::{}:: queryMatchTemplatePlayMarginRefData,查询赛事模板中指定玩法中所生效的分时节点相关字段,参数为:{}", requestParam.getGlobalId(), JsonFormatUtils.toJson(requestParam.getData()));
        if (requestParam.getData() == null) {
            return Response.error(500, "入参为空");
        }
        if (requestParam.getData().getSportId() == null) {
            return Response.error(500, "sportId不能为空");
        }
        if (requestParam.getData().getMatchId() == null) {
            return Response.error(500, "matchId不能为空");
        }
        if (requestParam.getData().getMatchType() == null) {
            return Response.error(500, "matchType不能为空");
        }
        if (requestParam.getData().getPlayId() == null) {
            return Response.error(500, "playId不能为空");
        }
        String timeCacheKey = String.format("rcs_match_template_play_margin_ref_data:%s:%s:%s", requestParam.getData().getMatchId(), requestParam.getData().getMatchType(), requestParam.getData().getPlayId());
        //获取对应缓存数据
        Object resVoCache = RcsLocalCacheUtils.timedCache.get(timeCacheKey);

        log.info("::{}:: queryMatchTemplatePlayMarginRefData,str值为:{},,resVo值为:{}", requestParam.getGlobalId(),String.valueOf(resVoCache), JSONObject.toJSONString(resVoCache));

        //若未取到缓存，则入库查询拿到结果后进行设置定时缓存，再返回结果，否则直接返回结果
        if (ObjectUtils.isEmpty(resVoCache)) {
            MatchTemplatePlayMarginRefDataResVo resVo = new MatchTemplatePlayMarginRefDataResVo();

            //根据入参查出对应数据
            RcsMatchMarketConfig rcsMatchMarketConfig = new RcsMatchMarketConfig();
            rcsMatchMarketConfig.setMatchId(requestParam.getData().getMatchId());
            rcsMatchMarketConfig.setPlayId(Long.valueOf(requestParam.getData().getPlayId()));
            rcsMatchMarketConfig.setMatchType(requestParam.getData().getMatchType());
            RcsTournamentTemplatePlayMargain playMargin = playMargainMapper.selectPlayMarginByMatchInfo(rcsMatchMarketConfig);
            if (!ObjectUtils.isEmpty(playMargin)) {
                //根据入参查出对应数据,若查询结果不为空，则进行构造出参
                if (playMargin.getValidMarginId() != null) {
                    //若有生效节点则默认取，否则则根据matchType取第一个默认生效节点
                    QueryWrapper<RcsTournamentTemplatePlayMargainRef> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(RcsTournamentTemplatePlayMargainRef::getId, playMargin.getValidMarginId());
                    RcsTournamentTemplatePlayMargainRef ref = playMargainRefMapper.selectOne(queryWrapper);
                    resVo = BeanCopyUtils.copyProperties(ref, MatchTemplatePlayMarginRefDataResVo.class);
                    resVo.setMatchId(requestParam.getData().getMatchId());
                    resVo.setMatchType(requestParam.getData().getMatchType());
                    resVo.setSportId(Long.valueOf(requestParam.getData().getSportId()));
                } else {
                    //取默认生效节点
                    QueryWrapper<RcsTournamentTemplatePlayMargainRef> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(RcsTournamentTemplatePlayMargainRef::getMargainId, playMargin.getId());
                    if (requestParam.getData().getMatchType() == 1) {
                        //早盘和滚球的第一个生效节点值不一样，所以分开取
                        queryWrapper.lambda().eq(RcsTournamentTemplatePlayMargainRef::getTimeVal, 2592000);
                    } else if (requestParam.getData().getMatchType() == 0) {
                        queryWrapper.lambda().eq(RcsTournamentTemplatePlayMargainRef::getTimeVal, 0);
                    }
                    RcsTournamentTemplatePlayMargainRef ref = playMargainRefMapper.selectOne(queryWrapper);
                    resVo = BeanCopyUtils.copyProperties(ref, MatchTemplatePlayMarginRefDataResVo.class);
                    resVo.setMatchId(requestParam.getData().getMatchId());
                    resVo.setMatchType(requestParam.getData().getMatchType());
                    resVo.setSportId(Long.valueOf(requestParam.getData().getSportId()));
                }
            } else {
                return Response.error(500, "未查询到对应数据");
            }
            if (requestParam.getData().getMatchType() == 1) {
                //早盘缓存7天
                //waldkir-redis集群-发送至trade进行广播
                LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(timeCacheKey, resVo, 7 * 24 * 60 * 60 * 1000L);
                producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "refreshMarginPlayRef", resVo.getMatchId() + "_" + resVo.getPlayId(), syncBean);
                log.info("::{}::刷新缓存-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", resVo.getMatchId().toString(), JSONObject.toJSONString(syncBean));
            } else {
                //滚球缓存4小时
                //waldkir-redis集群-发送至trade进行广播
                LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(timeCacheKey, resVo, 4 * 60 * 60 * 1000L);
                producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "refreshMarginPlayRef", resVo.getMatchId() + "_" + resVo.getPlayId(), syncBean);
                log.info("::{}::刷新缓存-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", resVo.getMatchId().toString(), JSONObject.toJSONString(syncBean));
            }
            log.info("::{}:: queryMatchTemplatePlayMarginRefData,查询赛事模板中指定玩法中所生效的分时节点相关字段 ,内存key为:{},内存值为:{}", requestParam.getGlobalId(), timeCacheKey, JSONObject.toJSONString(resVo));
            return Response.success(resVo);
        } else {
            MatchTemplatePlayMarginRefDataResVo resVo = JSONObject.parseObject(JSONObject.toJSONString(resVoCache),MatchTemplatePlayMarginRefDataResVo.class);

            return Response.success(resVo);
        }
    }

    @Override
    public Response<String> queryTournamentPropertyData(Request<TournamentPropertyReqVo> requestParam) throws RcsServiceException {
        log.info("::{}:: queryTournamentPropertyData,根据联赛id查询相关联赛相关数据,参数为:{}", requestParam.getGlobalId(), JsonFormatUtils.toJson(requestParam.getData()));
        if (requestParam.getData().getId() == null) {
            return Response.error(500, "联赛id不能为空");
        }
        if (requestParam.getData().getType() == null) {
            return Response.error(500, "查询类型不能为空");
        }
        String timeCacheKey = String.format("rcs:tournament:property:%s:%s", requestParam.getData().getId(), requestParam.getData().getType());
        String msg = Objects.nonNull(RcsLocalCacheUtils.timedCache.get(timeCacheKey))
                ? String.valueOf(RcsLocalCacheUtils.timedCache.get(timeCacheKey))
                : null;
        log.info("::{}:: queryTournamentPropertyData,msg值为:{}", requestParam.getGlobalId(), msg);
        //若未取到缓存，则从redis取（数据库中未存）
        if (StringUtils.isBlank(msg)) {
            String redisValue = Objects.nonNull
                    (redisUtils.get(String.format("rcs:tournament:property:%s%s", requestParam.getData().getId(), requestParam.getData().getType())))
                    ? redisUtils.get(String.format("rcs:tournament:property:%s%s", requestParam.getData().getId(), requestParam.getData().getType()))
                    : null;
            if (StringUtils.isBlank(redisValue)) {
                //如果redis不存在，则redis再存一份默认数据，再返回结果
                String value = "";
                if (requestParam.getData().getType().equals("MTSOddsChangeValue")) {
                    value = "4";
                } else if (requestParam.getData().getType().equals("orderDelayTime")) {
                    value = "5";
                } else if (requestParam.getData().getType().equals("oddsChangeStatus")) {
                    value = "1";
                }
                redisUtils.set(String.format("rcs:tournament:property:%s%s", requestParam.getData().getId(), requestParam.getData().getType()), value);
                LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(timeCacheKey, value, 7 * 24 * 60 * 60 * 1000L);
                producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "refreshTournamentPropertyData", requestParam.getData().getId().toString(), syncBean);
                log.info("::{}::刷新缓存-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", requestParam.getData().getId().toString(), JSONObject.toJSONString(syncBean));

                log.info("::{}:: queryTournamentPropertyData,redis中没值,往redis(key: {} )和内存(key: {} )中存储值,值为:{}",
                        requestParam.getGlobalId(),
                        String.format("rcs:tournament:property:%s%s", requestParam.getData().getId(), requestParam.getData().getType()),
                        timeCacheKey,
                        value);
                return Response.success(value);
            } else {
                //若redis存在，则存7天缓存后返回结果
                LocalCacheSyncBean syncBean = LocalCacheSyncBean.build(timeCacheKey, redisValue, 7 * 24 * 60 * 60 * 1000L);
                producerSendMessageUtils.sendMessage("RCS_TRADE_LOCAL_CACHE_SYNC", "refreshTournamentPropertyData", requestParam.getData().getId().toString(), syncBean);
                log.info("::{}::刷新缓存-发送消息至RCS_TRADE_LOCAL_CACHE_SYNC,数据为:{}", requestParam.getData().getId().toString(), JSONObject.toJSONString(syncBean));
                log.info("::{}:: queryTournamentPropertyData,从redis中取值,redisKey为:{},值为:{}",
                        requestParam.getGlobalId(),
                        String.format("rcs:tournament:property:%s%s", requestParam.getData().getId(), requestParam.getData().getType()),
                        msg);
                return Response.success(redisValue);
            }
        } else {
            return Response.success(msg);
        }
    }
}
