package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author derre
 * @date 2022-03-26
 */
@Api(tags = "报表系统-风控公共查询")
@RestController
@RequestMapping("/riskCommon")
public class RiskCommonController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;
    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(UserPortraitController.class);

    @ApiOperation(value = "地区下拉查询")
    @RequestMapping(value = "/queryRegionList", method = {RequestMethod.POST})
    public Result<String> queryRegionList() {
        log.info("start 地区下拉查询 queryRegionList");
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/common/queryRegionList"),"1",appId);
            log.info("data:" + JSON.toJSONString(data));
        } catch (Exception e) {
            log.error("地区下拉查询{}" + e);
            return Result.fail("地区下拉查询异常");
        }
        return Result.succes(data);
    }


}
