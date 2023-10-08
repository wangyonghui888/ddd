package com.panda.sport.rcs.trade.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.trade.enums.MatchTypeEnum;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.vo.SystemPreSwitchVo;
import com.panda.sport.rcs.trade.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsMatchTemplateModifyService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplateService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置controller
 */
@RestController
@RequestMapping(value = "systemConfig")
@Slf4j
public class SystemConfigController {


    @Autowired
    public RedisClient redisClient;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Resource
    private IRcsTournamentTemplateService rcsTournamentTemplateService;
    @Resource
    private IRcsMatchTemplateModifyService rcsMatchTemplateModifyService;


    /**
     * 查询系统级别提前结算开关配置
     * @return
     */
    @RequestMapping("queryConfig")
    public HttpResponse queryConfig(){

        return HttpResponse.success(getConfig());
    }

    private SystemPreSwitchVo getConfig(){
        SystemPreSwitchVo vo = null;
        if(!redisClient.exist(RcsConstant.SYSTEM_PRE_STATUS_CACHE_KEY)){
            //默认都是打开的
            vo = new SystemPreSwitchVo(SystemPreSwitchVo.OPEN, SystemPreSwitchVo.OPEN);
            return vo;
        }
        String json = redisClient.get(RcsConstant.SYSTEM_PRE_STATUS_CACHE_KEY);
        vo = JSONUtil.toBean(json, SystemPreSwitchVo.class);
        return vo;
    }

    /**
     * 更新系统级别开关
     * 缓存无过期时间
     * @param dto
     * @return
     */
    @PostMapping("updateConfig")
    public HttpResponse updateConfig(@RequestBody SystemPreSwitchVo dto){
        if (ObjectUtil.isNull(dto) || ObjectUtil.isNull(dto.getAO()) || ObjectUtil.isNull(dto.getSR())){
            return HttpResponse.failToMsg("入参不能为空");
        }
        String linkId = CommonUtils.mdcPut();
        HttpResponse httpResponse =  HttpResponse.success(dto);
        httpResponse.setLinkId(linkId);
        SystemPreSwitchVo oldConfig = getConfig();
        redisClient.set(RcsConstant.SYSTEM_PRE_STATUS_CACHE_KEY, dto);

        //比较变化，将对应的赛事提前结算下发业务
        LambdaQueryWrapper<StandardMatchInfo> wrapper = new LambdaQueryWrapper<>();
        // 3=结束 4=关闭
        wrapper.notIn(StandardMatchInfo::getMatchStatus, Arrays.asList(3, 4));
        wrapper.eq(StandardMatchInfo::getSportId, SportIdEnum.FOOTBALL.getId());
        List<StandardMatchInfo> matchInfos = standardMatchInfoService.list(wrapper);
        if(CollUtil.isEmpty(matchInfos)){
            log.info("{}::提前结算系统开关::当前无可用赛事", linkId);
            return httpResponse;
        }

        //滚球的
        List<Long> liveMatchIds = matchInfos.stream()
                .filter(item -> RcsConstant.isLive(item.getMatchStatus()))
                .map(StandardMatchInfo::getId)
                .collect(Collectors.toList());
        //早盘的
        List<Long> preMatchIds = matchInfos.stream()
                .filter(item -> !RcsConstant.isLive(item.getMatchStatus()))
                .map(StandardMatchInfo::getId)
                .collect(Collectors.toList());

        //查询这些赛事的模板信息
        List<RcsTournamentTemplate> templateList = new ArrayList<>();
        if(CollUtil.isNotEmpty(liveMatchIds)) {
            templateList.addAll(rcsTournamentTemplateService.getByMatchIds(liveMatchIds, MatchTypeEnum.LIVE));
        }
        if(CollUtil.isNotEmpty(preMatchIds)) {
            templateList.addAll(rcsTournamentTemplateService.getByMatchIds(preMatchIds, MatchTypeEnum.EARLY));
        }
        if(CollUtil.isEmpty(templateList)){
            log.error("{}::提前结算系统开关::赛事无模板::pre:{}::live:{}", linkId, preMatchIds, liveMatchIds);
            return httpResponse;
        }
        List<RcsTournamentTemplate> changeStatusMatchTemplates = new ArrayList<>();
        if(dto.getAO() != oldConfig.getAO()){
            List<RcsTournamentTemplate> aoDataSourceTemplates = templateList.stream()
                    .filter(item -> "AO".equals(CommonUtil.getDataSourceCode(item.getEarlySettStr())))
                    .collect(Collectors.toList());
            changeStatusMatchTemplates.addAll(aoDataSourceTemplates);
        }
        if(dto.getSR() != oldConfig.getSR()){
            List<RcsTournamentTemplate> srDataSourceTemplates = templateList.stream()
                    .filter(item -> "SR".equals(CommonUtil.getDataSourceCode(item.getEarlySettStr())))
                    .collect(Collectors.toList());
            changeStatusMatchTemplates.addAll(srDataSourceTemplates);
        }
        if(CollUtil.isEmpty(changeStatusMatchTemplates)){
            log.error("{}::提前结算系统开关::无相关数据源赛事", linkId);
            return httpResponse;
        }
        changeStatusMatchTemplates.stream().forEach(item -> rcsMatchTemplateModifyService.sendMatchPreStatus(item, linkId));

        return httpResponse;
    }

}
