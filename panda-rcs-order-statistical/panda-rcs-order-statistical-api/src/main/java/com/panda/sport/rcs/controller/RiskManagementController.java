package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.RiskTeamServiceImpl;
import com.panda.sport.rcs.utils.IPUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author derre
 * @date 2022-03-20
 */
@Api(tags = "报表系统-危险球队池-1664")
@RestController
@RequestMapping("/riskManagement")
public class RiskManagementController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(UserPortraitController.class);

    @Autowired
    RedisService redisService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    RiskTeamServiceImpl riskTeamService;

    @ApiOperation(value = "新增危险球队")
    @RequestMapping(value = "/addRiskTeam", method = {RequestMethod.POST})
    public Result<String> addRiskTeam(@RequestBody @Valid RiskTeamSaveReqVo vo, HttpServletRequest request) {
        log.info("start 新增危险球队 addRiskTeam:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/addRiskTeam"), JSON.toJSONString(vo), appId);
            vo.setIp(IPUtil.getRequestIp(request));
            riskTeamService.insertTeamLog(vo);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("新增危险球队{}" + e);
            return Result.fail("新增危险球队异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "删除危险球队")
    @RequestMapping(value = "/deleteRiskTeam", method = {RequestMethod.POST})
    public Result<String> deleteRiskTeam(@RequestBody @Valid RiskTeamDelReqVo vo, HttpServletRequest request) {
        log.info("start 删除危险球队 deleteRiskTeam:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/deleteRiskTeam"), JSON.toJSONString(vo), appId);
            redisService.delete("rcs:danger:team:" + vo.getTeamId());
            RiskTeamDelReqVoMessage riskTeamDelReqVoMessage = new RiskTeamDelReqVoMessage();
            riskTeamDelReqVoMessage.setTeamId(vo.getTeamId());
            riskTeamDelReqVoMessage.setType("del");
            producerSendMessageUtils.sendMessage("Danger_Team_Data", "del", String.valueOf(vo.getTeamId()), JSON.toJSONString(riskTeamDelReqVoMessage));
            vo.setIp(IPUtil.getRequestIp(request));
            riskTeamService.deleteTeamLog(vo);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("删除危险球队{}" + e);
            return Result.fail("删除危险球队异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "球队列表")
    @RequestMapping(value = "/getTeamList", method = {RequestMethod.POST})
    public Result<String> getTeamList(@RequestBody @Valid RiskManagementTeamListReqVo vo) {
        log.info("start 球队列表 getTeamList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/getTeamList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("球队列表{}" + e);
            return Result.fail("球队列表异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "危险球队信息危险等级修改")
    @RequestMapping(value = "/updateRiskTeamLevel", method = {RequestMethod.POST})
    public Result<String> updateRiskTeamLevel(@RequestBody @Valid RiskTeamUpdateReqVo vo, HttpServletRequest request) {
        log.info("start 危险球队信息危险等级修改 updateRiskTeamLevel:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/updateRiskTeamLevel"), JSON.toJSONString(vo), appId);
            vo.setIp(IPUtil.getRequestIp(request));
            riskTeamService.updateTeamLog(vo);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险球队信息危险等级修改{}" + e);
            return Result.fail("危险球队信息危险等级修改异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "危险球队信息状态修改")
    @RequestMapping(value = "/updateRiskTeamStatus", method = {RequestMethod.POST})
    public Result<String> updateRiskTeamStatus(@RequestBody @Valid RiskTeamUpdateReqVo vo) {
        log.info("start 危险球队信息状态修改 updateRiskTeamStatus:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/updateRiskTeamStatus"), JSON.toJSONString(vo), appId);
            RiskTeamDelReqVoMessage riskTeamDelReqVoMessage = new RiskTeamDelReqVoMessage();
            riskTeamDelReqVoMessage.setTeamId(vo.getTeamId());
            String  type = vo.getStatus()==0 ?"del":"sync";
            riskTeamDelReqVoMessage.setType(type);
            producerSendMessageUtils.sendMessage("Danger_Team_Data", type, String.valueOf(vo.getTeamId()), JSON.toJSONString(riskTeamDelReqVoMessage));
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险球队信息状态修改{}" + e);
            return Result.fail("危险球队信息状态修改异常");
        }
        return Result.succes(data);
    }



    @ApiOperation(value = "危险球队列表")
    @RequestMapping(value = "/getRiskTeamList", method = {RequestMethod.POST})
    public Result<String> getRiskTeamList(@RequestBody @Valid RiskTeamListReqVo vo) {
        log.info("start 危险球队列表 getRiskTeamList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/getRiskTeamList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险球队列表{}" + e);
            return Result.fail("危险球队列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险联赛列表")
    @RequestMapping(value = "/getTyRiskTournamentList", method = {RequestMethod.POST})
    public Result<String> getTyRiskTournamentList(@RequestBody @Valid TyRiskTournamentListReqVo vo) {
        log.info("start 危险联赛列表 getTyRiskTournamentList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/getTyRiskTournamentList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险联赛列表{}" + e);
            return Result.fail("危险联赛列表异常");
        }
        return Result.succes(data);
    }


}
