package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.pojo.dto.RcsHideRangeConfigDTO;
import com.panda.sport.rcs.service.IRcsHideRangeConfigService;
import com.panda.sport.rcs.vo.HttpResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 风控通用配置接口
 * @Param
 * @Author Pumelo
 * @Date 2023/04/22
 */
@RestController
@RequestMapping(value = "/risk/commonConfig")
@Slf4j
public class RcsCommonConfigController {

    @Resource
    private IRcsHideRangeConfigService hideRangeConfigService;


    @ApiOperation("保存藏单投注货量-金额区间配置")
    @PostMapping(value = "saveHideList")
    public HttpResponse saveHideList(@RequestBody List<RcsHideRangeConfigDTO> configs) {
        log.info("::saveHideList::输入参数  {}", JSON.toJSONString(configs));
        try {
            hideRangeConfigService.saveHideList(configs);
        }catch (RuntimeException e){
            return HttpResponse.failToMsg(e.getMessage());
        }
        return HttpResponse.success();
    }


    @ApiOperation("获取藏单投注货量-金额区间配置")
    @GetMapping(value = "getHideList")
    public HttpResponse<List<RcsHideRangeConfigDTO>> getHideList() {
        log.info("::getHideList:: 输入参数  {}");
        List<RcsHideRangeConfigDTO> configs = hideRangeConfigService.getHideList();
        log.info("::getHideList:: getList 输出参数  {}","");
        return HttpResponse.success(configs);
    }

}
