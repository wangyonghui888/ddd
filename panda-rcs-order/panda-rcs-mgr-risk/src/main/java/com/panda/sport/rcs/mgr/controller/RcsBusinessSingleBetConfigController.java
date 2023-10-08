package com.panda.sport.rcs.mgr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessSingleBetConfigService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBusinessSingleBetConfig;
import com.panda.sport.rcs.vo.BusinessSingleBetAndPlayVo;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.mgr.mq.impl.BasicConfigProvider.msgConfTag;
import static com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp.BUS_SINGLE_BET_CONFIG_KEY;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  TODO
 * @Date: 2019-11-25 22:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "businessSingleBet")
public class RcsBusinessSingleBetConfigController {

    @Autowired
    RcsBusinessSingleBetConfigService businessSingleBetConfigService;
    @Autowired
    private RcsBusinessConfigMapper rcsBusinessConfigMapper;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @RequestMapping(value = "getList")
    public HttpResponse<BusinessSingleBetAndPlayVo> getList(@RequestBody RcsBusinessSingleBetConfig config) {
        if (config.getSportId() == null) {
            return HttpResponse.fail("sportId不能为空");
        }
        if (config.getMatchType() == null) {
            return HttpResponse.fail("matchType不能为空");
        }
        if (config.getTimePeriod() == null) {
            return HttpResponse.fail("timePeriod不能为空");
        }
        if (config.getBusinessId() == null) {
            return HttpResponse.fail("businessId不能为空");
        }
        
        QueryWrapper<RcsBusinessSingleBetConfig> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsBusinessSingleBetConfig::getBusinessId, config.getBusinessId());
        wrapper.lambda().eq(RcsBusinessSingleBetConfig::getSportId, config.getSportId());
        wrapper.lambda().eq(RcsBusinessSingleBetConfig::getMatchType, config.getMatchType());
        wrapper.lambda().eq(RcsBusinessSingleBetConfig::getTimePeriod, config.getTimePeriod());
        //增加排序支持
        wrapper.orderByDesc("order_number");
        if (config.getTournamentLevel() != null) {
            wrapper.lambda().eq(RcsBusinessSingleBetConfig::getTournamentLevel, config.getTournamentLevel());
        }
        List<RcsBusinessSingleBetConfig> list = businessSingleBetConfigService.list(wrapper);
        if(list == null || list.size() <= 0 ) {
        	businessSingleBetConfigService.initBusinessSingleBetConfig(config);
        }
        
        BusinessSingleBetAndPlayVo businessSingleBetAndPlayVo = businessSingleBetConfigService.selectBusinessSingleBetConfigView(config);
        return HttpResponse.success(businessSingleBetAndPlayVo);
    }

    @RequestMapping(value = "update")
    public HttpResponse updateRcsBusinessSingleBetConfig(@RequestBody List<RcsBusinessSingleBetConfig> configs) {
        boolean rerult = false;
        try {
            rerult = businessSingleBetConfigService.updateBusinessSingleBetConfig(configs);

            List<RcsBusinessSingleBetConfig> singleBetList = rcsBusinessConfigMapper.queryBusSingleBetConfigList();
            Long busId = -1L;
            for (RcsBusinessSingleBetConfig m : singleBetList) {
                if (m.getId().equals(configs.get(0).getId())) {
                    busId = m.getBusinessId();
                    break;
                }
            }


            Map<Long, List<RcsBusinessSingleBetConfig>> result = singleBetList.stream().collect(Collectors.groupingBy(RcsBusinessSingleBetConfig::getBusinessId));
            if (result != null && result.size() > 0) {
                for (Long busid : result.keySet()) {
                    if (busId.equals(busid)) {
                        sendMessage.sendMessage(msgConfTag, BUS_SINGLE_BET_CONFIG_KEY, String.valueOf(busId), result.get(busId));
                    }
                }
            }

        } catch (Exception e) {
            log.error("::updateRcsBusinessSingleBetConfig:: 更新失败 {}",e.getMessage());
            return HttpResponse.fail(rerult);
        }

        return HttpResponse.success(rerult);
    }

}
