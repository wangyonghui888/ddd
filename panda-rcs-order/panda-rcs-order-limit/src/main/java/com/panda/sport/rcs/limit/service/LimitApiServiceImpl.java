package com.panda.sport.rcs.limit.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.limit.*;
import com.panda.sport.data.rcs.dto.special.RcsUserSpecialBetLimitConfigDTO;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigSubMapper;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.limit.LimitMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.limit.RcsUserSpecialBetLimitConfigVo;
import com.panda.sport.rcs.pojo.vo.UserReferenceLimitVo;
import com.panda.sport.rcs.service.IRcsUserConfigNewService;
import com.panda.sport.rcs.util.CopyUtils;
import com.panda.sport.rcs.util.RealTimeControlUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.limit.constants.LimitConstants.AMOUNT_UNIT;

/**
 * @Description 限额api dubbo接口
 * @Param
 * @Author lithan
 * @Date 2020-09-13 16:11:20
 * @return
 **/
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class LimitApiServiceImpl implements LimitApiService {
    private static final String USER_LABEL_CONFIG = "rcs:special:user:order:delay:sencond:config:%s";
    @Autowired
    LimitServiceImpl limitService;

    @Autowired
    LimitMapper limitMapper;

    @Autowired
    RcsQuotaBusinessLimitMapper businessLimitMapper;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    IRcsUserConfigNewService rcsUserConfigNewService;
    @Autowired
    RedisClient redisClient;

    @Autowired
    RcsLabelLimitConfigMapper rcsLabelLimitConfigMapper;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Autowired
    RcsMatchMarketConfigSubMapper rcsMatchMarketConfigSubMapper;
    @Autowired
    RealTimeControlUtils realTimeControlUtils;


    /**
     * 获取 玩法盘口位置 限额
     *
     * @param request
     * @return Response
     */
    @Override
    public Response<MarkerPlaceLimitAmountResVo> getMarketPlaceLimit(Request<MarkerPlaceLimitAmountReqVo> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("获取玩法盘口位置限额开始:{}", JSON.toJSONString(request));
            MarkerPlaceLimitAmountReqVo reqVo = request.getData();
            Long matchId = reqVo.getMatchId();
            Integer playId = reqVo.getPlayId();
            Integer placeNum = reqVo.getPlaceNum();
            String subPlayId = reqVo.getSubPlayId();
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(matchId);
            if (ObjectUtils.isEmpty(standardMatchInfo)) {
                log.info("赛事不存在:{}", matchId);
                throw new RcsServiceException("赛事不存在:");
            }

            MarkerPlaceLimitAmountResVo markerPlaceLimitAmountResVo = new MarkerPlaceLimitAmountResVo();
            //先查询rcs_match_market_config
            LambdaQueryWrapper<RcsMatchMarketConfig> marketConfigWrapper = new LambdaQueryWrapper<>();
            marketConfigWrapper.eq(RcsMatchMarketConfig::getMatchId, matchId);
            marketConfigWrapper.eq(RcsMatchMarketConfig::getPlayId, playId);
            marketConfigWrapper.ge(RcsMatchMarketConfig::getMaxSingleBetAmount, 0);
            marketConfigWrapper.eq(RcsMatchMarketConfig::getMarketIndex, placeNum);
            marketConfigWrapper.select(RcsMatchMarketConfig::getMaxSingleBetAmount, RcsMatchMarketConfig::getMarketIndex, RcsMatchMarketConfig::getPlayId);
            RcsMatchMarketConfig rcsMatchMarketConfig = rcsMatchMarketConfigMapper.selectOne(marketConfigWrapper);
            log.info("::{}::数据库获取赛事盘口配置:{}", matchId, JSON.toJSONString(rcsMatchMarketConfig));
            if (Objects.nonNull(rcsMatchMarketConfig)) {
                //bug-38376
                //markerPlaceLimitAmountResVo.setLimitAmount(rcsMatchMarketConfig.getMaxSingleBetAmount() == null ? new BigDecimal(Long.MAX_VALUE / 1000000) : new BigDecimal(rcsMatchMarketConfig.getMaxSingleBetAmount()).multiply(AMOUNT_UNIT));
                markerPlaceLimitAmountResVo.setLimitAmount(rcsMatchMarketConfig.getMaxSingleBetAmount() == null ? null : new BigDecimal(rcsMatchMarketConfig.getMaxSingleBetAmount()).multiply(AMOUNT_UNIT));
            } else {
                //先查操盘窗口 rcs_match_market_config_sub
                RcsMatchMarketConfigSub rcsMatchMarketConfigSub = rcsMatchMarketConfigSubMapper.selectOne(getQuerySub(matchId, playId, placeNum, subPlayId));
                if (Objects.isNull(rcsMatchMarketConfigSub)) {
                    rcsMatchMarketConfigSub = rcsMatchMarketConfigSubMapper.selectOne(getQuerySub(matchId, playId, placeNum, "-1"));
                }
                //markerPlaceLimitAmountResVo.setLimitAmount(rcsMatchMarketConfigSub == null || rcsMatchMarketConfigSub.getMaxSingleBetAmount() == null ? new BigDecimal(Long.MAX_VALUE / 1000000) : new BigDecimal(rcsMatchMarketConfigSub.getMaxSingleBetAmount()).multiply(AMOUNT_UNIT));
                markerPlaceLimitAmountResVo.setLimitAmount(rcsMatchMarketConfigSub == null || rcsMatchMarketConfigSub.getMaxSingleBetAmount() == null ? null : new BigDecimal(rcsMatchMarketConfigSub.getMaxSingleBetAmount()).multiply(AMOUNT_UNIT));
            }
            log.info("获取玩法盘口位置限额返回:{}", JSON.toJSONString(markerPlaceLimitAmountResVo));
            return Response.success(markerPlaceLimitAmountResVo);
        } catch (Exception e) {
            log.error("获取玩法盘口位置限额异常:{},{}", e.getMessage(), e);
            return Response.error(-1, "查询限额失败:" + e.getMessage());
        }
    }

    private LambdaQueryWrapper<RcsMatchMarketConfigSub> getQuerySub(long matchId, Integer playId, Integer placeNum, String subPlayId) {
        LambdaQueryWrapper<RcsMatchMarketConfigSub> rcsMatchMarketConfigSubLambdaQueryWrapper = new LambdaQueryWrapper<>();
        rcsMatchMarketConfigSubLambdaQueryWrapper.eq(RcsMatchMarketConfigSub::getMatchId, matchId);
        rcsMatchMarketConfigSubLambdaQueryWrapper.eq(RcsMatchMarketConfigSub::getPlayId, playId);
        rcsMatchMarketConfigSubLambdaQueryWrapper.eq(RcsMatchMarketConfigSub::getMarketIndex, placeNum);
        rcsMatchMarketConfigSubLambdaQueryWrapper.eq(RcsMatchMarketConfigSub::getSubPlayId, subPlayId);
        rcsMatchMarketConfigSubLambdaQueryWrapper.ge(RcsMatchMarketConfigSub::getMaxSingleBetAmount, 0);
        rcsMatchMarketConfigSubLambdaQueryWrapper.select(RcsMatchMarketConfigSub::getMaxSingleBetAmount);
        return rcsMatchMarketConfigSubLambdaQueryWrapper;
    }

    /**
     * 获取 商户限额 配置
     * 表:rcs_quota_business_limit
     *
     * @return
     */
    @Override
    public Response<RcsQuotaBusinessLimitResVo> getRcsQuotaBusinessLimit(String busId) {
        try {
            MDC.put("X-B3-TraceId", UUID.randomUUID().toString().replace("-", ""));
            log.info("获取商户限额开始:{}", busId);
            String key = String.format(RedisKey.MERCHANT_LIMIT_KEY, busId);
            Object businessStr = RcsLocalCacheUtils.timedCache.get(key,false);
            if (Objects.nonNull(businessStr)) {
                RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = JSON.parseObject(String.valueOf(businessStr), RcsQuotaBusinessLimitResVo.class);
                this.processAmountData(rcsQuotaBusinessLimitResVo,true);
                log.info("获取商户限额本地缓存返回:" + JSONObject.toJSONString(rcsQuotaBusinessLimitResVo));
                return Response.success(rcsQuotaBusinessLimitResVo);
            }
            RcsQuotaBusinessLimit RcsQuotaBusinessLimit = limitService.geRcsQuotaBusinessLimit(busId);
            RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = CopyUtils.clone(RcsQuotaBusinessLimit, RcsQuotaBusinessLimitResVo.class);
            //金额单位 统一转换  元转分
            this.processAmountData(rcsQuotaBusinessLimitResVo,false);
            JSONObject json = new JSONObject();
            json.put("key", key);
            json.put("value", rcsQuotaBusinessLimitResVo);
            producerSendMessageUtils.sendMessage("rcs_order_limit_cache_update", "", key, json);
            log.info("获取商户限额返回:{}", JSONObject.toJSONString(rcsQuotaBusinessLimitResVo));
            return Response.success(rcsQuotaBusinessLimitResVo);
        } catch (Exception e) {
            log.info("获取商户限额异常:{},{}", e.getMessage(), e);
            return Response.error(-1, "查询商户限额失败:" + e.getMessage());
        }
    }

    private void processAmountData(RcsQuotaBusinessLimitResVo vo,boolean isRedis) {
        if (vo.getBusinessSingleDayLimit() != null) {
            vo.setBusinessSingleDayLimit(isRedis ? vo.getBusinessSingleDayLimit() : vo.getBusinessSingleDayLimit() * AMOUNT_UNIT.longValue());
        }
        if (vo.getUserSingleStrayLimit() != null) {
            vo.setUserSingleStrayLimit(isRedis ?  vo.getUserSingleStrayLimit() : vo.getUserSingleStrayLimit() * AMOUNT_UNIT.longValue());
        }
        if (vo.getBusinessSingleDaySeriesLimit() == null) {
            vo.setBusinessSingleDaySeriesLimit(vo.getBusinessSingleDayLimit() == null ? null : ((isRedis ? vo.getBusinessSingleDayLimit() : vo.getBusinessSingleDayLimit() * AMOUNT_UNIT.longValue()) / 2));
        } else {
            vo.setBusinessSingleDaySeriesLimit(isRedis ? vo.getBusinessSingleDaySeriesLimit() :  vo.getBusinessSingleDaySeriesLimit() * AMOUNT_UNIT.longValue());
        }
    }

    /**
     * 根据赛事查询各维度限额数据
     *
     * @param request
     * @return
     */
    @Override
    public Response<MatchLimitDataVo> getMatchLimitData(Request<MatchLimitDataReqVo> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("获取限额开始:{}", JSONObject.toJSONString(request));
            MatchLimitDataVo data = limitService.getMatchLimitData(request.getData());
            log.info("获取限额结果:请求{} 返回:{}", JSONObject.toJSONString(request), JSONObject.toJSONString(data));
            return Response.success(data);
        } catch (Exception e) {
            log.info("获取赛事多维度限额异常:{},{}", e.getMessage(), e);
            return Response.error(-1, e.getMessage());
        }
    }

    /**
     * 获取用户投注限额 上限 参考值
     *
     * @return
     */
    @Override
    public Response<UserLimitReferenceResVo> getUserLimitReference(Request<Long> request) {
        UserLimitReferenceResVo resVo = new UserLimitReferenceResVo();

        try {
            LambdaQueryWrapper<RcsQuotaBusinessLimit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsQuotaBusinessLimit::getBusinessId, String.valueOf(request.getData()));
            RcsQuotaBusinessLimit businessLimit = businessLimitMapper.selectOne(wrapper);
            BigDecimal userRatio = new BigDecimal("1");
            if (businessLimit != null) {
                userRatio = businessLimit.getUserQuotaRatio();
            }
            List<UserLimitReferenceResVo.UserReferenceLimitVo> list = new ArrayList<>();
            /**单注赔付限额 单场赔付限额*/

            //足球 篮球
            List<UserReferenceLimitVo> referenceList = limitMapper.referenceLimit();
            for (UserReferenceLimitVo vo : referenceList) {
                UserLimitReferenceResVo.UserReferenceLimitVo apiVo = resVo.new UserReferenceLimitVo();
                apiVo.setSportId(vo.getSportId());
                apiVo.setUserSingleLimit(new BigDecimal(vo.getUserSingleLimit()).multiply(userRatio).longValue());
                apiVo.setUserMatchLimit(new BigDecimal(vo.getUserMatchLimit()).multiply(userRatio).longValue());
                list.add(apiVo);
            }

            //其他
            UserLimitReferenceResVo.UserReferenceLimitVo apiVo = resVo.new UserReferenceLimitVo();
            apiVo.setUserSingleLimit(new BigDecimal(limitMapper.referenceUserSingleLimit().getUserSingleLimit()).multiply(userRatio).longValue());
            apiVo.setUserMatchLimit(new BigDecimal(limitMapper.referenceUserMatchLimit().getUserMatchLimit()).multiply(userRatio).longValue());
            apiVo.setSportId(-1L);
            list.add(apiVo);
            resVo.setList(list);

            resVo.setUserQuotaCrossLimit(new BigDecimal(limitMapper.referenceCross().getUserQuotaCrossLimit()).multiply(userRatio).longValue());
        } catch (Exception e) {
            log.info("取用户投注限额 上限 参考值异常:{},{}", e.getMessage(), e);
            e.printStackTrace();
        }
        return Response.success(resVo);
    }

    /**
     * 获取用户标签
     *
     * @param request
     * @return Response
     */
    @Override
    public Response<Integer> getUserTag(Request<Long> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("获取用户标签开始:{}", JSONObject.toJSONString(request));
            Integer tagId = limitService.getUserTag(request.getData());
            log.info("获取用户标签返回:{}", tagId);
            return Response.success(tagId);
        } catch (Exception e) {
            log.error("获取用户标签异常:{},{}", e.getMessage(), e);
            return Response.error(-1, "获取用户标签失败:" + e.getMessage());
        }
    }

    @Override
    public Response<Boolean> queryRcsUserConfig(String userId) {
        List<RcsUserConfig> rcsUserConfigs = rcsUserConfigNewService.getByUserId(Long.parseLong(userId));
        String userLabelKey = String.format(USER_LABEL_CONFIG, userId);
        if (CollectionUtils.isNotEmpty(rcsUserConfigs)) {
            Map<String, com.panda.sport.rcs.pojo.RcsUserConfig> map = rcsUserConfigs.stream().collect(Collectors.toMap(e -> e.getSportId().toString(), e -> e));
            for (String key : map.keySet()) {
                redisClient.hSet(String.format(userLabelKey, userId), key, JSONObject.toJSONString(map.get(key)));
                redisClient.expireKey(String.format(userLabelKey, userId), 60 * 60 * 24 * 30);
            }
        }
        return Response.success(true);
    }

    @Override
    public Response<List<RcsUserSpecialBetLimitConfigDTO>> queryUserSpecialBetLimitConfig(Request<RcsUserSpecialBetLimitConfigDTO> request) {
        RcsUserSpecialBetLimitConfigVo configVo = new RcsUserSpecialBetLimitConfigVo();
        BeanUtils.copyProperties(request.getData(), configVo);
        String linkId = "userConfigNew" + configVo.getUserId();
        //查询rcs_user_config_new表，获取用户真实特殊限额类型;
        Integer type = limitMapper.getUserConfigNewByUserId(configVo.getUserId());
        log.info("::{}::用户真实特殊限额类型：{}", linkId, type);
        List<RcsUserSpecialBetLimitConfigDTO> resultList = new ArrayList<>();
        if (type == null || type == 0) {
            type = 1;
            RcsUserSpecialBetLimitConfigDTO dto = new RcsUserSpecialBetLimitConfigDTO();
            dto.setSpecialBettingLimitType(type);
            resultList.add(dto);
            return Response.success(resultList);
        }
        configVo.setSpecialBettingLimitType(type);
        List<RcsUserSpecialBetLimitConfigVo> list = limitMapper.queryRcsUserSpecialBetLimitConfig(configVo);
        if (list != null && list.size() > 0) {
            for (RcsUserSpecialBetLimitConfigVo vo : list) {
                RcsUserSpecialBetLimitConfigDTO dto = new RcsUserSpecialBetLimitConfigDTO();
                BeanUtils.copyProperties(vo, dto);
                dto.setSpecialBettingLimitType(type);
                resultList.add(dto);
            }
        }
        return Response.success(resultList);

    }

    @Override
    public Response<String> queryPlayInfoById(Integer sportId, Integer playId) {
        return Response.success(limitMapper.queryPlayInfoById(sportId, playId));
    }

    @Override
    public Response<String> queryOrderLimitKeyValue() {
        String msg = Objects.nonNull(RcsLocalCacheUtils.timedCache.get("rcs_order_limit_key")) ? String.valueOf(RcsLocalCacheUtils.timedCache.get("rcs_order_limit_key")) : null;
        Map<String, String> tempMap = new HashMap<>();
        if (StringUtils.isNotBlank(msg)) {
            Map<String, String> map = JSON.parseObject(msg, Map.class);
            String limitVal = realTimeControlUtils.getLimitLinkUpVal();
            tempMap = JSON.parseObject(limitVal, Map.class);
            for (String key : map.keySet()) {
                tempMap.put(key, map.get(key));
            }
        }
        return Response.success(JSON.toJSONString(tempMap));
    }

    @Override
    public Response<String> getTagPercentage(Request<Long> request) {
        try {
            MDC.put("X-B3-TraceId", request.getGlobalId());
            log.info("获取标签限额开始:{}", JSONObject.toJSONString(request));

            RcsLabelLimitConfig rcsLabelLimitConfig = rcsLabelLimitConfigMapper.selectOne(new LambdaQueryWrapper<RcsLabelLimitConfig>()
                    .eq(RcsLabelLimitConfig::getTagId, request.getData())
                    .eq(RcsLabelLimitConfig::getSpecialBettingLimit, 1)
                    .last("limit 1"));
            log.info("获取标签限额符合条件数据:{}", JSONObject.toJSONString(rcsLabelLimitConfig));
            if (rcsLabelLimitConfig !=null && rcsLabelLimitConfig.getLimitPercentage() != null && 1 == rcsLabelLimitConfig.getSpecialBettingLimit()) {
                return Response.success(String.valueOf(rcsLabelLimitConfig.getLimitPercentage().doubleValue() / 100));
            } else {
                //获取的时候如果为空 默认为1
                return Response.success("1");
            }
        } catch (Exception e) {
            log.error("获取标签限额异常:{}", e.getMessage(), e);
            return Response.error(-1, "获取标签限额失败:" + e.getMessage());
        }
    }
}

