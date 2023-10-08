package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.mgr.mq.bean.RcsMerchantsHideRangeConfigDto;
import com.panda.sport.rcs.mgr.paid.annotion.BusinessLog;
import com.panda.sport.rcs.mgr.wrapper.RcsMerchantsHideRangeConfigService;
import com.panda.sport.rcs.vo.HttpResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商户+球种配置后台藏单金额配置
 * @author bobi
 */
@RestController
@RequestMapping(value = "/riskHideRangeConfig")
@Slf4j
public class RcsMerchantsHideRangeConfigController {

    @Autowired
    private RcsMerchantsHideRangeConfigService rcsMerchantsHideRangeConfigService;




    @ApiOperation("编辑开关")
    @PostMapping(value = "/editHideStatusList")
    @BusinessLog()
    public HttpResponse editHideStatusList(@RequestBody  RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig) {
        log.info("::editHideStatusList::输入参数{}", JSON.toJSONString(rcsMerchantsHideRangeConfig));
        try {
            rcsMerchantsHideRangeConfigService.editHideStatusList(rcsMerchantsHideRangeConfig);
        }catch (RuntimeException e){
            return HttpResponse.failToMsg(e.getMessage());
        }
        return HttpResponse.success();
    }
    @ApiOperation("编辑金额")
    @PostMapping(value = "/editHideMoneyList")
    @BusinessLog()
    public HttpResponse editHideMoneyList(@RequestBody RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig) {
        log.info("::editHideMoneyList::输入参数{}", JSON.toJSONString(rcsMerchantsHideRangeConfig));
        try {
            rcsMerchantsHideRangeConfigService.editHideMoneyList(rcsMerchantsHideRangeConfig);
        }catch (RuntimeException e){
            return HttpResponse.failToMsg(e.getMessage());
        }
        return HttpResponse.success();
    }

}
