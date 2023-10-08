package com.panda.sport.rcs.trade.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.SpecEventConfigEnum;
import com.panda.sport.rcs.mapper.RcsSpecEventConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTemplateEventInfoConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import com.panda.sport.rcs.pojo.dto.SpecEventChangeDTO;
import com.panda.sport.rcs.pojo.dto.SpecEventConfigDTO;
import com.panda.sport.rcs.pojo.param.AutoOpenMarketStatusParam;
import com.panda.sport.rcs.pojo.param.RcsSpecEventConfigParam;
import com.panda.sport.rcs.pojo.param.UpdateSpecEventStatusParam;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTemplateEventInfoConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.trade.enums.PeriodEnum;
import com.panda.sport.rcs.trade.service.RcsSpecEventConfigService;
import com.panda.sport.rcs.trade.service.TradeStatusService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.MarketStatusUpdateVO;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AO特殊事件配置实现类
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/10 15:39
 */
@Service
@Slf4j
public class RcsSpecEventConfigServiceImpl implements RcsSpecEventConfigService {
    
    @Resource
    private RcsSpecEventConfigMapper rcsSpecEventConfigMapper;
    
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    protected ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    RcsTemplateEventInfoConfigMapper rcsTemplateEventInfoConfigMapper;

    @Autowired
    private TradeStatusService tradeStatusService;

    @Autowired
    private RcsTournamentTemplateMapper rcsTournamentTemplateMapper;

    /**
     * 根据ID修改
     *
     * @param rcsSpecEventConfigParam
     * @return
     */
    @Override
    public HttpResponse<Integer> updateSpecEventConfigById(RcsSpecEventConfigParam rcsSpecEventConfigParam) {
        try {
            Integer type = rcsSpecEventConfigParam.getType();
            Long typeVal = rcsSpecEventConfigParam.getTypeVal();
            SpecEventConfigDTO specEventConfigDTO = new SpecEventConfigDTO();
            specEventConfigDTO.setType(type);
            specEventConfigDTO.setTypeVal(typeVal);
            specEventConfigDTO.setActive(1);
            //查询特殊事件active是否有开
            Integer eventSwitch = rcsSpecEventConfigParam.getEventSwitch();
            List<RcsSpecEventConfig> specEventConfigList = rcsSpecEventConfigMapper.querySpecEventConfigList(specEventConfigDTO);
            int record = rcsSpecEventConfigMapper.updateSpecEventConfigById(rcsSpecEventConfigParam);
            if(null != eventSwitch && eventSwitch==0){
                UpdateSpecEventStatusParam updateSpecEventStatusParam = new UpdateSpecEventStatusParam();
                updateSpecEventStatusParam.setType(type);
                updateSpecEventStatusParam.setTypeVal(typeVal);
                updateSpecEventStatusParam.setMatchIdSwitch(eventSwitch.longValue());
                updateSpecEventStatusParam.setId(rcsSpecEventConfigParam.getId());
                rcsSpecEventConfigMapper.updateSpecEventStatusByMatchId(updateSpecEventStatusParam);
                exitSpecEvent(typeVal);
            }
            //todo 配置修改同步融合
            RcsSpecEventConfig rcsSpecEventConfig = new RcsSpecEventConfig();
            rcsSpecEventConfig.setType(3);
            rcsSpecEventConfig.setId(rcsSpecEventConfigParam.getId());
            rcsSpecEventConfig.setTypeVal(typeVal);
            pushRcsSpecEventConfig(rcsSpecEventConfig);
            //特殊事件状态同步推送
            pushMatchSpecEventStatus(typeVal);
            //事件关闭并且active=1
            if(!CollectionUtils.isEmpty(specEventConfigList) && eventSwitch==0){
                closeTrade(typeVal);
            }
            return HttpResponse.success(record);
        } catch (Exception e) {
            log.error("::{}-{}::修改AO事件配置异常", rcsSpecEventConfigParam.getTypeVal(), rcsSpecEventConfigParam.getType());
            return HttpResponse.error(HttpResponse.FAIL, "修改AO事件配置异常");
        }
    }
    
    /**
     * 根据赛事ID和时间编码修改激活时间、激活参数、激活次数
     *
     * @param rcsSpecEventConfig
     * @return
     */
    @Override
    public HttpResponse<Integer> updateActiveByMatchId(RcsSpecEventConfig rcsSpecEventConfig) {
        try {
            int record = rcsSpecEventConfigMapper.updateActiveByMatchId(rcsSpecEventConfig);
            //todo 配置修改同步融合
            rcsSpecEventConfig.setType(3);
            pushRcsSpecEventConfig(rcsSpecEventConfig);
            return HttpResponse.success(record);
        } catch (Exception e) {
            log.error("::{}-{}::激活事件配置异常", rcsSpecEventConfig.getTypeVal(), rcsSpecEventConfig.getEventCode());
            return HttpResponse.error(HttpResponse.FAIL, "激活事件配置异常");
        }
    }

    /**
     * 根据赛事ID和事件编码修改赔率
     *
     * @param rcsSpecEventConfig
     * @return
     */
    @Override
    public HttpResponse<Integer> updateSpecEventConfigProbByMatchId(RcsSpecEventConfig rcsSpecEventConfig) {
        try {
            int record = rcsSpecEventConfigMapper.updateSpecEventConfigProbByMatchId(rcsSpecEventConfig);
            //todo 配置修改同步融合
            rcsSpecEventConfig.setType(3);
            pushRcsSpecEventConfig(rcsSpecEventConfig);
            return HttpResponse.success(record);
        } catch (Exception e) {
            log.error("::{}-{}::激活事件配置异常", rcsSpecEventConfig.getTypeVal(), rcsSpecEventConfig.getEventCode());
            return HttpResponse.error(HttpResponse.FAIL, "激活事件配置异常");
        }
    }
    
    /**
     * 根据赛事ID查询AO事件配置
     *
     * @param rcsSpecEventConfig
     * @return
     */
    @Override
    public List<RcsSpecEventConfig> querySpecEventConfigList(RcsSpecEventConfig rcsSpecEventConfig) {
        SpecEventConfigDTO specEventConfigDTO = new SpecEventConfigDTO();
        specEventConfigDTO.setType(rcsSpecEventConfig.getType());
        specEventConfigDTO.setTypeVal(rcsSpecEventConfig.getTypeVal());
        List<RcsSpecEventConfig> specEventConfigList = rcsSpecEventConfigMapper.querySpecEventConfigList(specEventConfigDTO);
        if (CollectionUtils.isEmpty(specEventConfigList)) {
            specEventConfigList = new ArrayList<>();
            for (SpecEventConfigEnum eventConfigEnum : SpecEventConfigEnum.values()) {
                RcsSpecEventConfig specEventConfig = RcsSpecEventConfig.builder().type(specEventConfigDTO.getType()).typeVal(specEventConfigDTO.getTypeVal())
                        .eventName(eventConfigEnum.getEventName()).eventCode(eventConfigEnum.getEventCode()).build();
                specEventConfig.setEventSwitch(0);
                specEventConfig.setOneSideSwitch(0);
                specEventConfig.setAwayGoalProb(0f);
                specEventConfig.setHomeGoalProb(0f);
                specEventConfigList.add(specEventConfig);
            }
            if(rcsSpecEventConfig.getType()==3){//赛事初始化查询对应联赛的特殊事件数据
                RcsTournamentTemplate rcsTournamentTemplate = rcsTournamentTemplateMapper.selectOne(new QueryWrapper<RcsTournamentTemplate>().lambda()
                        .eq(RcsTournamentTemplate::getType, 3).eq(RcsTournamentTemplate::getTypeVal, rcsSpecEventConfig.getTypeVal())
                        .eq(RcsTournamentTemplate::getMatchType,0).last(" limit 1"));
                if(null == rcsTournamentTemplate){//代表赛事还是不是滚球状态
                    return new ArrayList<>();
                }
                RcsTournamentTemplate tournamentTemplate = rcsTournamentTemplateMapper.selectById(rcsTournamentTemplate.getCopyTemplateId());
                if(null != tournamentTemplate){
                    specEventConfigDTO.setType(2);
                    specEventConfigDTO.setTypeVal(tournamentTemplate.getTypeVal());
                    List<RcsSpecEventConfig> rcsSpecEventConfigs = rcsSpecEventConfigMapper.querySpecEventConfigList(specEventConfigDTO);
                    for (RcsSpecEventConfig rcsSpec : rcsSpecEventConfigs) {
                        rcsSpec.setId(null);
                        rcsSpec.setType(rcsSpecEventConfig.getType());
                        rcsSpec.setTypeVal(rcsSpecEventConfig.getTypeVal());
                    }
                    if(!CollectionUtils.isEmpty(rcsSpecEventConfigs)){
                        specEventConfigList = rcsSpecEventConfigs;
                    }
                    specEventConfigDTO.setType(rcsSpecEventConfig.getType());
                    specEventConfigDTO.setTypeVal(rcsSpecEventConfig.getTypeVal());
                }
            }
            rcsSpecEventConfigMapper.initSpecEventConfig(specEventConfigList);
            specEventConfigList = rcsSpecEventConfigMapper.querySpecEventConfigList(specEventConfigDTO);
        } else {
            List<Integer> switchList = specEventConfigList.stream().map(RcsSpecEventConfig::getEventSwitch).collect(Collectors.toList());
            String specEventStatusKey = String.format(RedisKey.SPECIAL_EVENT_STATUS_KEY, rcsSpecEventConfig.getType(), rcsSpecEventConfig.getTypeVal());
            if (switchList.contains(1)) {
                redisUtils.setex(specEventStatusKey, 1L, 4 * 60 * 60, TimeUnit.MINUTES);
            } else {
                redisUtils.setex(specEventStatusKey, 0L, 4 * 60 * 60, TimeUnit.MINUTES);
            }
        }
        return specEventConfigList;
    }
    
    /**
     * 批量插入赛事级别、联赛级别、等级级别AO事件配置信息
     *
     * @param srcObj
     * @param targetObj
     * @return
     */
    @Override
    public HttpResponse<Integer> batchInsert(SpecEventConfigDTO srcObj, SpecEventConfigDTO targetObj) {
        try {
            //根据type、typeVal查找模板
            List<RcsSpecEventConfig> eventConfigList = rcsSpecEventConfigMapper.querySpecEventConfigList(srcObj);
            if (CollectionUtils.isEmpty(eventConfigList)) {
                eventConfigList = new ArrayList<>();
                for (SpecEventConfigEnum eventConfigEnum : SpecEventConfigEnum.values()) {
                    RcsSpecEventConfig rcsSpecEventConfig = RcsSpecEventConfig.builder().type(targetObj.getType()).typeVal(targetObj.getTypeVal())
                            .eventName(eventConfigEnum.getEventName()).eventCode(eventConfigEnum.getEventCode()).build();
                    rcsSpecEventConfig.setEventSwitch(0);
                    rcsSpecEventConfig.setOneSideSwitch(0);
                    rcsSpecEventConfig.setAwayGoalProb(0f);
                    rcsSpecEventConfig.setHomeGoalProb(0f);
                    eventConfigList.add(rcsSpecEventConfig);
                }
                rcsSpecEventConfigMapper.initSpecEventConfig(eventConfigList);
            } else {
                for (RcsSpecEventConfig specEventConfig : eventConfigList) {
                    specEventConfig.setType(targetObj.getType());
                    specEventConfig.setTypeVal(targetObj.getTypeVal());
                }
                rcsSpecEventConfigMapper.batchInsert(eventConfigList);
            }
            return HttpResponse.success();
        } catch (Exception e) {
            log.error("::{}-{}::批量插入AO事件配置信息", targetObj.getTypeVal(), targetObj.getType());
            return HttpResponse.error(HttpResponse.FAIL, "查询AO事件配置异常");
        }
    }
    
    /**
     * 赛事级事件开关状态修改
     *
     * @param specEventStatusParam
     * @return
     */
    @Override
    public HttpResponse<Boolean> updateSpecEventStatus(UpdateSpecEventStatusParam specEventStatusParam) {
        
        try {
            Long typeVal = specEventStatusParam.getTypeVal();
            Integer type = specEventStatusParam.getType();
            SpecEventConfigDTO specEventConfigDTO = new SpecEventConfigDTO();
            specEventConfigDTO.setType(type);
            specEventConfigDTO.setTypeVal(typeVal);
            specEventConfigDTO.setActive(1);
            //查询特殊事件active是否有开
            List<RcsSpecEventConfig> specEventConfigList = rcsSpecEventConfigMapper.querySpecEventConfigList(specEventConfigDTO);
            rcsSpecEventConfigMapper.updateSpecEventStatusByMatchId(specEventStatusParam);
            String specEventStatusKey = String.format(RedisKey.SPECIAL_EVENT_STATUS_KEY, specEventStatusParam.getType(), typeVal);
            redisUtils.setex(specEventStatusKey, specEventStatusParam.getMatchIdSwitch().longValue(), 4 * 60 * 60, TimeUnit.SECONDS);
            //推送融合
            RcsSpecEventConfig rcsSpecEventConfig = new RcsSpecEventConfig();
            rcsSpecEventConfig.setType(type);
            rcsSpecEventConfig.setTypeVal(typeVal);
            pushRcsSpecEventConfig(rcsSpecEventConfig);
            Long matchIdSwitch = specEventStatusParam.getMatchIdSwitch();
            //特殊事件状态同步推送
            if(matchIdSwitch==0 && type==3){
                exitSpecEvent(typeVal);
            }
            if(null != matchIdSwitch && matchIdSwitch!=0){
                return HttpResponse.success();
            }
            if(1==type || 2 == type || CollectionUtils.isEmpty(specEventConfigList)){
                return HttpResponse.success();
            }

            //active=0 关闭状态进行锁盘
            closeTrade(typeVal);
            return HttpResponse.success();
        } catch (Exception e) {
            log.error(":::{}-{}:赛事级事件开关状态修改异常:", specEventStatusParam.getTypeVal(), specEventStatusParam.getType(), e);
            return HttpResponse.error(HttpResponse.FAIL, "赛事级事件开关状态修改异常");
        }
    }

    public void exitSpecEvent(Long matchId){
        String linkId = CommonUtils.getLinkId("exit_match_spec_event");
        SpecEventChangeDTO specEventChangeDTO = new SpecEventChangeDTO();
        Request request = new Request();
        specEventChangeDTO.setMatchId(matchId);
        request.setData(specEventChangeDTO);
        request.setGlobalId(linkId);
        String tag = "RCS_MATCH_SPEC_EVENT_EXIT_" + matchId;
        log.info("::{}::发送mq退出赛事特殊事件:Message:{}", linkId, JSONObject.toJSON(request));
        producerSendMessageUtils.sendMessage("RCS_MATCH_SPEC_EVENT_EXIT", tag, linkId, JSONObject.toJSON(request));
    }

    /**
     * 赛事收盘
     * */
    public void closeTrade(Long typeVal){
        MarketStatusUpdateVO vo = new MarketStatusUpdateVO();
        vo.setMatchId(typeVal);
        vo.setMarketStatus(13);
        vo.setSportId(1l);
        vo.setTradeLevel(1);
        String s = tradeStatusService.updateTradeStatus(vo);
        log.info("{}::赛事级特殊事件赛事状态::赛事状态linkdId::{}",typeVal,s);
    }

    public void pushRcsSpecEventConfig(RcsSpecEventConfig rcsSpecEventConfig ){
        //查询事件配置
        List<RcsSpecEventConfig> rcsSpecEventConfigs = querySpecEventConfigList(rcsSpecEventConfig);
        String linkId = CommonUtils.getLinkId()+"_updateSpecEventStatus";
        log.info("::{}::赛事级事件开关状态修改发送消息队列：" + JSON.toJSONString(rcsSpecEventConfigs),linkId);
        producerSendMessageUtils.sendMessage("RCS_DATA_AO_SPECIAL_EVENT_CONFIG",null,linkId ,rcsSpecEventConfigs);
    }

    @Override
    public List<String> qryRcsTemplateEventInfoConfig(){
        //判断系统级别的特殊事件是否开启 只有进球类玩法才有特殊事件
        List<RcsTemplateEventInfoConfig> eventInfoConfigs = rcsTemplateEventInfoConfigMapper.selectList(
                new LambdaQueryWrapper<RcsTemplateEventInfoConfig>()
                        .eq(RcsTemplateEventInfoConfig::getCategorySetId, 131)
                        .eq(RcsTemplateEventInfoConfig::getRejectType, 1)
                        .eq(RcsTemplateEventInfoConfig::getEventType,"special"));
        Map<String, String> stringMap = Arrays.stream(SpecEventConfigEnum.values()).collect(Collectors.toMap(SpecEventConfigEnum::getEventCode, SpecEventConfigEnum::getEventCode));
        List<String> eventInfoConfigList = new ArrayList<>();
        eventInfoConfigs.forEach(rcsTemplateEventInfoConfig -> {
            String eventCode = rcsTemplateEventInfoConfig.getEventCode();
            if(!StringUtils.isEmpty(stringMap.get(eventCode))){
                eventInfoConfigList.add(eventCode);
            }
        });
        return eventInfoConfigList;
    }

    @Override
    public void pushMatchSpecEventStatus(Long matchId){
        /*
        5-13 改为在特殊事件处理进行推送active状态  TOrderDetailExtServiceImplV3
        Map<String,Object> map = new HashMap<>();
        map.put("matchId",matchId);
        AutoOpenMarketStatusParam autoOpenMarketStatusParam = new AutoOpenMarketStatusParam();
        autoOpenMarketStatusParam.setSwitchType(1);
        autoOpenMarketStatusParam.setTypeVal(matchId);
        autoOpenMarketStatusParam.setType(3);
        HttpResponse autoOpenMarketStatus = getAutoOpenMarketStatus(autoOpenMarketStatusParam);
        map.put("aoAutoOpen",autoOpenMarketStatus.getData());
        map.put("spceChoose",qryRcsTemplateEventInfoConfig());
        //事件级
        if(null != matchId){
            RcsSpecEventConfig rcsSpecEventConfig = new RcsSpecEventConfig();
            rcsSpecEventConfig.setType(3);
            rcsSpecEventConfig.setTypeVal(matchId);
            List<RcsSpecEventConfig> specEventConfigList = querySpecEventConfigList(rcsSpecEventConfig);
            specEventConfigList.forEach(rcsSpecEventConfig1 -> {
                map.put(rcsSpecEventConfig1.getEventCode(),rcsSpecEventConfig1.getEventSwitch());
            });
        }
        String linkId = CommonUtils.getLinkId()+"_specEventStatus";
        log.info("::{}::特殊事件开关状态修改发送消息队列：" + JSON.toJSONString(map),linkId);
        producerSendMessageUtils.sendMessage("RCS_SPECIAL_EVENT_STATUS_SYNC",null,linkId,map);*/
    }

    /**
     * 查询自动玩法开盘开关状态
     *
     * @param autoOpenMarketParam
     * @return
     */
    @Override
    public HttpResponse getAutoOpenMarketStatus(AutoOpenMarketStatusParam autoOpenMarketParam) {
        if (autoOpenMarketParam.getSwitchType() == 1) {
        } else if (autoOpenMarketParam.getSwitchType() == 0) {
            autoOpenMarketParam.setType(0);
            autoOpenMarketParam.setTypeVal(100L);
        } else {
            return HttpResponse.error(HttpResponse.FAIL, "不支持的开关类型");
        }
        String authSwitchKey = String.format(RedisKey.AO_PLAY_AUTO_OPEN, autoOpenMarketParam.getType(), autoOpenMarketParam.getTypeVal());
        String authSwitchStatus = redisUtils.get(authSwitchKey);
        if (StringUtils.isEmpty(authSwitchStatus)) {
            authSwitchStatus = loadAutoOpenMarketStatus(autoOpenMarketParam);
            
        }
        Integer resultStatus = Integer.valueOf(authSwitchStatus);
        return HttpResponse.success(resultStatus);
    }

    /**
     * 修改自动开盘开关状态
     * @param autoOpenMarketParam
     * @return
     */
    @Override
    public HttpResponse<Boolean> updateAutoOpenMarketStatus(AutoOpenMarketStatusParam autoOpenMarketParam) {
        try {
            if (autoOpenMarketParam.getSwitchType() == 1) {
            } else if (autoOpenMarketParam.getSwitchType() == 0) {
                autoOpenMarketParam.setType(0);
                autoOpenMarketParam.setTypeVal(100L);
            } else {
                return HttpResponse.error(HttpResponse.FAIL, "不支持的开关类型");
            }
            Long matchId = autoOpenMarketParam.getTypeVal();
            rcsSpecEventConfigMapper.updateAutoOpenMarketStatus(autoOpenMarketParam.getTypeVal() + "-" + autoOpenMarketParam.getType(), autoOpenMarketParam.getSwitchStatus());
            String authSwitchKey = String.format(RedisKey.AO_PLAY_AUTO_OPEN, autoOpenMarketParam.getType(), autoOpenMarketParam.getTypeVal());
            redisUtils.setex(authSwitchKey, autoOpenMarketParam.getSwitchStatus(), 4 * 60 * 60, TimeUnit.SECONDS);
            //特殊事件状态同步推送
            pushMatchSpecEventStatus(autoOpenMarketParam.getTypeVal());
            Integer switchStatus = autoOpenMarketParam.getSwitchStatus();
            if(null != switchStatus && switchStatus == 0){
                return HttpResponse.success();
            }
            log.info("{}::AO确认自动开盘::赛事ID::{}",matchId,matchId);
            //进行AO玩法开盘
            String dataKey = String.format(RedisKey.AO_PLAY_AUTO_OPEN_DATA, autoOpenMarketParam.getTypeVal());
            String data = redisUtils.get(dataKey);
            if(StringUtils.isEmpty(data)){
                log.info("{}::AO确认自动开盘暂无数据::赛事ID::{}",matchId,matchId);
                return HttpResponse.success();
            }
            JSONObject jsonObject = JSON.parseObject(data);
            log.info("{}::AO确认自动开盘::赛事玩法数据::{}",matchId,data);
            List<Long> aoPlayIds = JSON.parseArray(jsonObject.getString("aoPlayIds"), Long.class);
            List<Long> otherPlayIds = JSON.parseArray(jsonObject.getString("otherPlayIds"), Long.class);
            //ao玩法开盘
            MarketStatusUpdateVO vo = new MarketStatusUpdateVO();
            vo.setMatchId(autoOpenMarketParam.getTypeVal());
            vo.setMarketStatus(0);
            vo.setSportId(1l);
            vo.setTradeLevel(9);
            vo.setCategorySetId(PeriodEnum.FULL_TIME_1.getCategorySetId());
            vo.setPlaySetCode(PeriodEnum.FULL_TIME_1.getPlaySetCode());
            String matchLinkId = tradeStatusService.updateTradeStatus(vo);
            log.info("{}::AO确认自动开盘::赛事开盘ID::{}::开盘linkId::{}",matchId,data,matchLinkId);
            if(!CollectionUtils.isEmpty(aoPlayIds)){
                vo.setTradeLevel(5);
                vo.setCategoryIdList(aoPlayIds);
                String s = tradeStatusService.updateTradeStatus(vo);
                log.info("{}::AO确认自动开盘::赛事ao玩法ID::{}::开盘linkId::{}",matchId,aoPlayIds,s);
            }

            if(!CollectionUtils.isEmpty(otherPlayIds)){
                vo.setMarketStatus(2);
                vo.setCategoryIdList(otherPlayIds);
                String s1 = tradeStatusService.updateTradeStatus(vo);
                log.info("{}::AO确认自动开盘::赛事其他玩法ID::{}::关盘linkId::{}",matchId,otherPlayIds,s1);
            }
            redisUtils.del(dataKey);
            return HttpResponse.success();
        } catch (Exception e) {
            log.error(":::{}-{}:修改自动开盘开关状态:", autoOpenMarketParam.getTypeVal(), autoOpenMarketParam.getType(), e);
            return HttpResponse.error(HttpResponse.FAIL, "修改自动开盘开关状态异常");
        }
    }

    /**
     * 查询自动开盘开关状态-带初始化
     *
     * @param autoOpenMarketParam
     * @return
     */
    private String loadAutoOpenMarketStatus(AutoOpenMarketStatusParam autoOpenMarketParam) {
        String authSwitchStatus = rcsSpecEventConfigMapper.getAutoOpenMarketStatus(autoOpenMarketParam.getTypeVal() + "-" + autoOpenMarketParam.getType());
        if (StringUtils.isEmpty(authSwitchStatus)) {
            rcsSpecEventConfigMapper.initAutoOpenMarketStatus(autoOpenMarketParam.getTypeVal() + "-" + autoOpenMarketParam.getType(), 0);
            authSwitchStatus = "0";
        }
        String authSwitchKey = String.format(RedisKey.AO_PLAY_AUTO_OPEN, autoOpenMarketParam.getType(), autoOpenMarketParam.getTypeVal());
        redisUtils.setex(authSwitchKey, authSwitchStatus, 4 * 60 * 60, TimeUnit.SECONDS);
        return authSwitchStatus;
    }

    /**
     * 赛事是否特殊事件激活中
     * @param matchId
     * @return
     */
    public boolean isMatchSpecEvent(Long matchId){
        LambdaQueryWrapper<RcsSpecEventConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsSpecEventConfig::getType,3);
        wrapper.eq(RcsSpecEventConfig::getTypeVal,matchId);
        List<RcsSpecEventConfig> rcsSpecEventConfigs = rcsSpecEventConfigMapper.selectList(wrapper);
        if(CollUtil.isEmpty(rcsSpecEventConfigs)){
            return false;
        }
        List<RcsSpecEventConfig> active = rcsSpecEventConfigs.stream()
                .filter(o->o.getAwayActive() == 1 || o.getHomeActive() == 1)
                .collect(Collectors.toList());
        if(CollUtil.isEmpty(active)){
            return false;
        }
        return true;
    }
}
