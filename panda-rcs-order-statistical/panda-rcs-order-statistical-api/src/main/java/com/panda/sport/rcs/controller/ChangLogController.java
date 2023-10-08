package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.entity.QueryListByExternalVo;
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

@Api(tags = "玩家组日志-1714")
@RestController
@RequestMapping("/changLog")
public class ChangLogController {

    Logger log = LoggerFactory.getLogger(ChangLogController.class);

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlRiskPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    @ApiOperation(value = "玩家组日志列表")
    @RequestMapping(value = "/queryList", method = {RequestMethod.POST})
    public Result<String> queryList(@RequestBody @Valid QueryListByExternalVo vo) {
        log.info("start 玩家组日志列表 queryList:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/changLog/queryList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("玩家组日志列表",  e);
            return Result.fail("玩家组日志列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "玩家组日志列表对外提供")
    @RequestMapping(value = "/queryListByExternal", method = {RequestMethod.POST})
    public Result<String> queryListByExternal(@RequestBody @Valid QueryListByExternalVo vo) {
        log.info("start 玩家组日志列表对外提供 queryListByExternal:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/changLog/queryListByExternal"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("玩家组日志列表对外提供",  e);
            return Result.fail("玩家组日志列表对外提供异常");
        }
        return Result.succes(data);
    }

}
