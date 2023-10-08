package com.panda.sport.rcs.mgr.controller;

import static com.panda.sport.rcs.mgr.mq.impl.BasicConfigProvider.msgConfTag;
import static com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp.BUS_CON_PLAY_CONFIG_KEY;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.mgr.utils.CommonUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessRateService;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTOReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessConPlayConfigService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBusinessConPlayConfig;
import com.panda.sport.rcs.vo.HttpResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  串关额度管理
 * @Date: 2019-11-25 22:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "businessConPlayConfig")
public class RcsBusinessConPlayConfigController {
    @Autowired
    RcsBusinessConPlayConfigService businessConPlayConfigService;
    @Autowired
    ProducerSendMessageUtils sendMessage;
    @Autowired
    private RcsBusinessConfigMapper rcsBusinessConfigMapper;

    @RequestMapping(value = "getList")
    public HttpResponse<List<RcsBusinessConPlayConfig>> getList(@RequestParam("businessId") long businessId) {
        log.info("::businessConPlayConfig:: getList 输入参数  {}",businessId);
        List<RcsBusinessConPlayConfig> rcsBusinessConPlayConfigs = businessConPlayConfigService.selectConPlays(businessId);
        for(RcsBusinessConPlayConfig config : rcsBusinessConPlayConfigs) {
        	if("3".equals(String.valueOf(config.getPlayType()))) 
        		config.setPlayValue(config.getPlayValue().divide(new BigDecimal("100"),2, BigDecimal.ROUND_HALF_UP));
        }
        log.info("::businessConPlayConfig:: getList 输出参数  {}",rcsBusinessConPlayConfigs.size());
        return HttpResponse.success(rcsBusinessConPlayConfigs);
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public HttpResponse updateRcsBusinessConPlayConfig(@RequestBody List<RcsBusinessConPlayConfig> configs) {
        boolean rerult = false;
        log.info("::businessConPlayConfig::update 输入参数  {}", JSONArray.toJSONString(configs));
        try {
            rerult = businessConPlayConfigService.updateConPlayConfig(configs);

            List<RcsBusinessConPlayConfig> conList = rcsBusinessConfigMapper.queryBusConPlayConifgList();

            Map<Long, List<RcsBusinessConPlayConfig>> result = conList.stream().filter(m->m.getBusinessId().equals(configs.get(0).getBusinessId())).collect(Collectors.groupingBy(RcsBusinessConPlayConfig::getBusinessId));

            if(result != null && result.size() > 0 ) {
            	for(Long busId : result.keySet()) {
            		sendMessage.sendMessage(msgConfTag,BUS_CON_PLAY_CONFIG_KEY , String.valueOf(busId),result.get(busId));
            	}
            }

        } catch (Exception e) {
            log.error("::updateRcsBusinessConPlayConfig::更新失败{},{}",e.getMessage(),e);
            return HttpResponse.fail(rerult);
        }
        log.info("::businessConPlayConfig:: update 输出参数  {}",rerult);
        return HttpResponse.success(rerult);
    }
}
