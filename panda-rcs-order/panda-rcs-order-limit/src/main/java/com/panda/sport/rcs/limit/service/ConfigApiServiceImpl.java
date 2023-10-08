package com.panda.sport.rcs.limit.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.config.ConfigApiService;
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
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.vo.UserReferenceLimitVo;
import com.panda.sport.rcs.service.IRcsTournamentTemplateService;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.limit.constants.LimitConstants.AMOUNT_UNIT;

/**
 * @Description 获取mts-1配置
 * @Param
 * @Author lithan
 * @Date 2023-02-15 11:08:31
 * @return
 **/
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class ConfigApiServiceImpl implements ConfigApiService {

    @Autowired
    IRcsTournamentTemplateService rcsTournamentTemplateService;

    @Override
    public Response<String> getMts1Status(Request<Mts1StatusReqVo> request) {
        try {
            Response response = new Response();
            Mts1StatusReqVo vo = request.getData();
            Integer matchType = vo.getMatchType() == 2 ? 0 : 1;
            //先从缓存读取
            String key = String.format(com.panda.sport.rcs.constants.RedisKey.REDIS_MTS_CONTACT_CONFIG_KEY, vo.getMatchId(), matchType);
            Object redisConfig = RcsLocalCacheUtils.timedCache.get(key);
            if (Objects.nonNull(redisConfig)) {
                response.setData(redisConfig);
                return response;
            }
            LambdaQueryWrapper<RcsTournamentTemplate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RcsTournamentTemplate::getTypeVal, vo.getMatchId());
            queryWrapper.eq(RcsTournamentTemplate::getMatchType, vo.getMatchType());
            List<RcsTournamentTemplate> list = rcsTournamentTemplateService.list(queryWrapper);
            if (ObjectUtils.isEmpty(list)) {
                return null;
            }
            String config = list.get(0).getMtsConfigValue();
            RcsLocalCacheUtils.timedCache.put(key, config, 60 * 1000);
            response.setData(config);
            return response;
        } catch (Exception e) {
            log.error("获取mts-1配置异常:{},{}", e.getMessage(), e);
            return Response.error(-1, "获取mts-1配置失败:" + e.getMessage());
        }
    }
}

