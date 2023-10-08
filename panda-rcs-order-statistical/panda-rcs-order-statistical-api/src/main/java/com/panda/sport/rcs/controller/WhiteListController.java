package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.entity.AddWhitenReqVo;
import com.panda.sport.rcs.common.entity.QueryWhiteVo;
import com.panda.sport.rcs.common.utils.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "白名单-1715")
@RestController
@RequestMapping("/whitelist")
public class WhiteListController {

    Logger log = LoggerFactory.getLogger(WhiteListController.class);

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlRiskPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    @ApiOperation(value = "白名单列表")
    @RequestMapping(value = "/queryWhitelistList", method = {RequestMethod.POST})
    public Result<String> queryWhitelistList(@RequestBody @Valid QueryWhiteVo vo) {
        log.info("start 新增白名单 queryWhitelistList:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/whitelist/queryWhitelistList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("白名单列表" + e);
            return Result.fail("白名单列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "新增白名单")
    @RequestMapping(value = "/addWhitelist", method = {RequestMethod.POST})
    public Result<String> addWhitelist(@RequestBody @Valid AddWhitenReqVo vo) {
        log.info("start 新增白名单 addWhitelist:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/whitelist/addWhitelist"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("新增白名单" + e);
            return Result.fail("新增白名单异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "移除白名单")
    @RequestMapping(value = "/deleteWhitelist", method = {RequestMethod.POST})
    public Result<String> deleteWhitelist(@RequestBody @Valid AddWhitenReqVo vo) {
        log.info("start 移除白名单 deleteWhitelist:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/whitelist/deleteWhitelist"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("移除白名单" + e);
            return Result.fail("移除白名单异常");
        }
        return Result.succes(data);
    }
}
