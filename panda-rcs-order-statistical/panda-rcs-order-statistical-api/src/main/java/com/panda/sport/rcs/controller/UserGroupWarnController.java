package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.entity.QueryUserGroupWarnVo;
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

@Api(tags = "玩家组预警-1714")
@RestController
@RequestMapping("/tyUserGroupWarn")
public class UserGroupWarnController {

    Logger log = LoggerFactory.getLogger(UserGroupWarnController.class);

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlRiskPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    @ApiOperation(value = "玩家组预警用户列表")
    @RequestMapping(value = "/queryUserGroupWarnList", method = {RequestMethod.POST})
    public Result<String> queryUserGroupWarnList(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 玩家组预警用户列表 queryUserGroupWarnList:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserGroupWarn/queryUserGroupWarnList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("玩家组预警用户列表",  e);
            return Result.fail("玩家组预警用户列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "玩家组预警查看日志")
    @RequestMapping(value = "/queryUserGroupWarnLogs", method = {RequestMethod.POST})
    public Result<String> queryUserGroupWarnLogs(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 玩家组预警查看日志 queryUserGroupWarnLogs:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserGroupWarn/queryUserGroupWarnLogs"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("玩家组预警查看日志" + e);
            return Result.fail("玩家组预警查看日志异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险注单详情")
    @RequestMapping(value = "/queryRiskOrderDetail", method = {RequestMethod.POST})
    public Result<String> queryRiskOrderDetail(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 危险注单详情 queryRiskOrderDetail:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserGroupWarn/queryRiskOrderDetail"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("危险注单详情" + e);
            return Result.fail("危险注单详情异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "处理按钮获取玩家组")
    @RequestMapping(value = "/dealButtonUserGroupQuery", method = {RequestMethod.POST})
    public Result<String> dealButtonUserGroupQuery(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 处理按钮获取玩家组 dealButtonUserGroupQuery:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserGroupWarn/dealButtonUserGroupQuery"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("处理按钮获取玩家组" + e);
            return Result.fail("处理按钮获取玩家组异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "未处理条数")
    @RequestMapping(value = "/notUserGroupQueryCount", method = {RequestMethod.POST})
    public Result<String> notUserGroupQueryCount(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 未处理条数 notUserGroupQueryCount:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserGroupWarn/notUserGroupQueryCount"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("未处理条数" + e);
            return Result.fail("未处理条数异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "待分组用户列表")
    @RequestMapping(value = "/dealButtonIsNotGroupList", method = {RequestMethod.POST})
    public Result<String> dealButtonIsNotGroupList(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 待分组用户列表 dealButtonIsNotGroupList:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserGroupWarn/dealButtonIsNotGroupList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("待分组用户列表" + e);
            return Result.fail("待分组用户列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "获取已有玩家组列表")
    @RequestMapping(value = "/queryUserGroupExistsList", method = {RequestMethod.POST})
    public Result<String> queryUserGroupExistsList(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 获取已有玩家组列表 queryUserGroupExistsList:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserGroupWarn/queryUserGroupExistsList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("获取已有玩家组列表" + e);
            return Result.fail("获取已有玩家组列表异常");
        }
        return Result.succes(data);
    }

}
