package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.SaveUserGroupReqVo;
import com.panda.sport.rcs.common.vo.api.request.QueryUserGroupReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserGroupIdReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserGroupUserReqVo;
import com.panda.sport.rcs.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "玩家组管理-1690")
@RestController
@RequestMapping("/tyUserGroup")
public class UserGroupController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(UserGroupController.class);

    @Autowired
    RedisService redisService;

    @ApiOperation(value = "新增玩家组")
    @RequestMapping(value = "/addUserGroup", method = {RequestMethod.POST})
    public Result<String> addUserGroup(@RequestBody @Valid SaveUserGroupReqVo vo) {
        log.info("start 新增玩家组 addUserGroup:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/tyUserGroup/addUserGroup"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("新增玩家组{}" + e);
            return Result.fail("新增玩家组异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "给玩家组添加成员")
    @RequestMapping(value = "/addUserGroupItem", method = {RequestMethod.POST})
    public Result<String> addUserGroupItem(@RequestBody @Valid SaveUserGroupReqVo vo) {
        log.info("start 给玩家组添加成员 addUserGroup:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/tyUserGroup/addUserGroupItem"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("给玩家组添加成员" + e);
            return Result.fail("给玩家组添加成员异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "删除玩家组")
    @RequestMapping(value = "/delUserGroup", method = {RequestMethod.POST})
    public Result<String> delUserGroup(@RequestBody @Valid UserGroupIdReqVo vo) {
        log.info("start 删除玩家组 delUserGroup:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/tyUserGroup/delUserGroup"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("删除玩家组{}" + e);
            return Result.fail("删除玩家组异常");
        }
        return Result.succes(data);
    }



    @ApiOperation(value = "根据玩家组id查询玩家列表")
    @RequestMapping(value = "/getUserListByGroupId", method = {RequestMethod.POST})
    public Result<String> getUserListByGroupId(@RequestBody @Valid UserGroupIdReqVo vo) {
        log.info("start 根据玩家组id查询玩家列表 getUserListByGroupId:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/tyUserGroup/getUserListByGroupId"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("根据玩家组id查询玩家列表{}" + e);
            return Result.fail("根据玩家组id查询玩家列表异常");
        }
        return Result.succes(data);
    }



    @ApiOperation(value = "查询玩家组列表")
    @RequestMapping(value = "/queryUserGroup", method = {RequestMethod.POST})
    public Result<String> queryUserGroup(@RequestBody @Valid QueryUserGroupReqVo vo) {
        log.info("start 查询玩家组列表 queryUserGroup:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/tyUserGroup/queryUserGroup"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("查询玩家组列表{}" + e);
            return Result.fail("查询玩家组列表异常");
        }
        return Result.succes(data);
    }



    @ApiOperation(value = "修改玩家组")
    @RequestMapping(value = "/updateUserGroup", method = {RequestMethod.POST})
    public Result<String> updateUserGroup(@RequestBody @Valid SaveUserGroupReqVo vo) {
        log.info("start 修改玩家组 updateUserGroup:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/tyUserGroup/updateUserGroup"), JSON.toJSONString(vo), appId);
            if(vo.getRemoveUserIdList() != null && vo.getRemoveUserIdList().size() > 0){
                vo.getRemoveUserIdList().forEach(id -> {
                    redisService.delete("rcs:danger:player:group:" + id);
                });
            }
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("修改玩家组{}" + e);
            return Result.fail("修改玩家组 异常");
        }
        return Result.succes(data);
    }



    @ApiOperation(value = "用户列表")
    @RequestMapping(value = "/userListByPage", method = {RequestMethod.POST})
    public Result<String> userListByPage(@RequestBody @Valid UserGroupUserReqVo vo) {
        log.info("start 用户列表 userListByPage:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/tyUserGroup/userListByPage"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("用户列表{}" + e);
            return Result.fail("用户列表 异常");
        }
        return Result.succes(data);
    }





}
