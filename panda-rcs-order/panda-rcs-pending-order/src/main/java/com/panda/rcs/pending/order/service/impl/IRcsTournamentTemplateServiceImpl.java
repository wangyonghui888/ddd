package com.panda.rcs.pending.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.pending.order.constants.NumberConstant;
import com.panda.rcs.pending.order.constants.RedisKey;
import com.panda.rcs.pending.order.mapper.RcsMargainRefMapper;
import com.panda.rcs.pending.order.param.TournamentTemplateParam;
import com.panda.rcs.pending.order.pojo.RcsMargainRefVo;
import com.panda.rcs.pending.order.service.IRcsTournamentTemplateService;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.rcs.pending.order.pojo.TournamentTemplateVo;
import com.panda.sport.rcs.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-05-2022/5/2 18:41
 */
@Service
@Slf4j
public class IRcsTournamentTemplateServiceImpl extends ServiceImpl<RcsTournamentTemplateMapper, RcsTournamentTemplate> implements IRcsTournamentTemplateService {

    @Autowired
    RcsTournamentTemplateMapper rcsTournamentTemplateMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    RcsMargainRefMapper rcsMargainRefMapper;


    @Override
    public RcsTournamentTemplate getTournamentTemplate(TournamentTemplateParam param) {
        String templateKey = String.format(RedisKey.OPEN_ALL_PRE_ORDER_KEY, param.getMatchType(), param.getSportId(), param.getTypeVal());
        String templateVal = redisUtils.get(templateKey);
        if (StringUtils.isBlank(templateVal)) {
            QueryWrapper<RcsTournamentTemplate> templateQueryWrapper = new QueryWrapper();
            templateQueryWrapper.lambda()
                    //.eq(RcsTournamentTemplate::getType, param.getType())
                    .eq(RcsTournamentTemplate::getTypeVal, param.getTypeVal())
                    .eq(RcsTournamentTemplate::getMatchType, param.getMatchType())
                    .eq(RcsTournamentTemplate::getSportId, param.getSportId());


            RcsTournamentTemplate tournamentTemplate = rcsTournamentTemplateMapper.selectOne(templateQueryWrapper);
            if (null == tournamentTemplate) {
                return null;
            }
            tournamentTemplate.setUserPendingOrderPayVal(Objects.isNull(tournamentTemplate.getUserPendingOrderPayVal()) ? NumberConstant.LONG_ZERO : tournamentTemplate.getUserPendingOrderPayVal() * NumberConstant.NUM_ONE_HUNDRED);
            tournamentTemplate.setBusinesPendingOrderPayVal(Objects.isNull(tournamentTemplate.getBusinesPendingOrderPayVal()) ? NumberConstant.LONG_ZERO : tournamentTemplate.getBusinesPendingOrderPayVal() * NumberConstant.NUM_ONE_HUNDRED);
            templateVal = JSON.toJSONString(tournamentTemplate);
            redisUtils.set(templateKey, templateVal);
            redisUtils.expire(templateKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
        }
        return JSON.parseObject(templateVal, RcsTournamentTemplate.class);
    }

    @Override
    public List<RcsTournamentTemplate> getTournamentTemplateList() {
        return rcsTournamentTemplateMapper.getTournamentTemplateList();
    }


    @Override
    public TournamentTemplateVo queryPendingOrder(TournamentTemplateParam param) {
        String templateKey = String.format(RedisKey.PENDING_ORDER_KEY, param.getTypeVal(), param.getMatchType());
        String templateVal = redisUtils.get(templateKey);
        if (StringUtils.isBlank(templateVal)) {
            QueryWrapper<RcsTournamentTemplate> templateQueryWrapper = new QueryWrapper();
            templateQueryWrapper.lambda()
                    .eq(RcsTournamentTemplate::getTypeVal, param.getTypeVal())
                    .eq(RcsTournamentTemplate::getMatchType, param.getMatchType());
            RcsTournamentTemplate tournamentTemplate = rcsTournamentTemplateMapper.selectOne(templateQueryWrapper);
            if (null == tournamentTemplate) {
                throw new RcsServiceException(NumberConstant.NUM_MINUS_ONE, "模板配置没有找到");
            }
            tournamentTemplate.setUserPendingOrderPayVal(Objects.isNull(tournamentTemplate.getUserPendingOrderPayVal()) ? NumberConstant.LONG_ZERO : tournamentTemplate.getUserPendingOrderPayVal() * NumberConstant.NUM_ONE_HUNDRED);
            tournamentTemplate.setBusinesPendingOrderPayVal(Objects.isNull(tournamentTemplate.getBusinesPendingOrderPayVal()) ? NumberConstant.LONG_ZERO : tournamentTemplate.getBusinesPendingOrderPayVal() * NumberConstant.NUM_ONE_HUNDRED);
            templateVal = JSON.toJSONString(tournamentTemplate);
            redisUtils.set(templateKey, templateVal);
            redisUtils.expire(templateKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
        }

        TournamentTemplateVo templateVo = JSON.parseObject(templateVal, TournamentTemplateVo.class);
        String refKey = String.format(RedisKey.PENDING_ORDER_MARGIN_REF_KEY, param.getTypeVal(), param.getSportId(), param.getPlayId());
        String refVal = redisUtils.get(refKey);
        if (StringUtils.isBlank(refVal)) {
            RcsMargainRefVo rcsMargainRefVo = rcsMargainRefMapper.currentMargainRef(param);
            if (null == rcsMargainRefVo) {
                throw new RcsServiceException(NumberConstant.NUM_MINUS_ONE, "分时节点模板数据没有找到");
            }
            rcsMargainRefVo.setPendingOrderPayVal(Objects.isNull(rcsMargainRefVo.getPendingOrderPayVal()) ? NumberConstant.LONG_ZERO : rcsMargainRefVo.getPendingOrderPayVal() * NumberConstant.NUM_ONE_HUNDRED);
            rcsMargainRefVo.setCumulativeCompensationPlaying(Objects.isNull(rcsMargainRefVo.getCumulativeCompensationPlaying()) ? NumberConstant.LONG_ZERO : rcsMargainRefVo.getCumulativeCompensationPlaying() * NumberConstant.NUM_ONE_HUNDRED);
            rcsMargainRefVo.setSinglePayLimit(Objects.isNull(rcsMargainRefVo.getSinglePayLimit()) ? NumberConstant.LONG_ZERO : rcsMargainRefVo.getSinglePayLimit() * NumberConstant.NUM_ONE_HUNDRED);
            refVal = JSON.toJSONString(rcsMargainRefVo);
            redisUtils.set(refKey, refVal);
            redisUtils.expire(refKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
        }
        RcsMargainRefVo rcsMargainRefVo = JSON.parseObject(refVal, RcsMargainRefVo.class);
        templateVo.setRcsMargainRefVo(rcsMargainRefVo);
        return templateVo;
    }

    /**
     * 获取预约投注速率,如果失败赋值默认值
     */
    public Integer getOrderRateLimit(Long matchId, Integer matchType) {
        return getLimitRate(matchId, matchType);

    }


    /**
     * 设置预约速率
     *
     * @param matchId   赛事ID
     * @param matchType 早盘类型
     */
    private Integer getLimitRate(Long matchId, Integer matchType) {
        String key = String.format(RedisKey.PENDING_ORDER_LIMIT_KEY, matchId, matchType);
        String val = redisUtils.get(key);
        if (StringUtils.isNotBlank(val)) {
            return Integer.valueOf(val);
        }
        LambdaQueryWrapper<RcsTournamentTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(RcsTournamentTemplate::getPendingOrderRate);
        wrapper.eq(RcsTournamentTemplate::getTypeVal, matchId);
        wrapper.eq(RcsTournamentTemplate::getMatchType, matchType);
        RcsTournamentTemplate rcsTournamentTemplate = rcsTournamentTemplateMapper.selectOne(wrapper);
        if (null == rcsTournamentTemplate) {
            throw new RcsServiceException(NumberConstant.NUM_MINUS_ONE, "预约速率模板配置没有找到");
        }
        redisUtils.set(key, JSON.toJSONString(rcsTournamentTemplate.getPendingOrderRate()));
        redisUtils.expire(key, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
        return rcsTournamentTemplate.getPendingOrderRate();

    }

}
