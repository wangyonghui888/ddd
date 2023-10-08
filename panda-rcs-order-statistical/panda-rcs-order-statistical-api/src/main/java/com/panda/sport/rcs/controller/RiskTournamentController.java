package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.RiskTournamentServiceImpl;
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
 * @date 2022-03-26
 */
@Api(tags = "报表系统-风险联赛池-1646")
@RestController
@RequestMapping("/riskTournament")
public class RiskTournamentController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(UserPortraitController.class);

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    RiskTournamentServiceImpl riskTournamentService;

    @Autowired
    RedisService redisService;

    @ApiOperation(value = "增加危险联赛")
    @RequestMapping(value = "/addRiskTournament", method = {RequestMethod.POST})
    public Result<String> addRiskTournament(@RequestBody @Valid TyRiskAddRiskTournamentReqVo vo, HttpServletRequest request) {
        log.info("start 增加危险联赛 getTyRiskTournamentList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/addRiskTournament"), JSON.toJSONString(vo), appId);
            vo.setIp(IPUtil.getRequestIp(request));
            riskTournamentService.insertTournamentLog(vo);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("增加危险联赛{}" + e);
            return Result.fail("增加危险联赛异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "删除危险联赛")
    @RequestMapping(value = "/delRiskTournamentList", method = {RequestMethod.POST})
    public Result<String> delRiskTournamentList(@RequestBody @Valid TyRiskDelRiskTournamentReqVo vo, HttpServletRequest request) {
        log.info("start 删除危险联赛 delRiskTournamentList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/delRiskTournamentList"), JSON.toJSONString(vo), appId);
            redisService.delete("rcs:danger:tournament:" + vo.getId());
            TyRiskDelRiskTournamentReqVoMessage tyRiskDelRiskTournamentReqVoMessage = new TyRiskDelRiskTournamentReqVoMessage();
            tyRiskDelRiskTournamentReqVoMessage.setId(vo.getId());
            tyRiskDelRiskTournamentReqVoMessage.setTournamentId(vo.getId());
            tyRiskDelRiskTournamentReqVoMessage.setType("del");
            producerSendMessageUtils.sendMessage("Danger_Tournament_Data", "del", String.valueOf(vo.getId()), JSON.toJSONString(tyRiskDelRiskTournamentReqVoMessage));
            vo.setIp(IPUtil.getRequestIp(request));
            riskTournamentService.deleteTournamentLog(vo);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("删除危险联赛{}" + e);
            return Result.fail("删除危险联赛异常");
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


    @ApiOperation(value = "查询非危险联赛列表")
    @RequestMapping(value = "/queryDisRiskTournamentList", method = {RequestMethod.POST})
    public Result<String> queryDisRiskTournamentList(@RequestBody @Valid TyRiskQueryTournamentListReqVo vo) {
        log.info("start 查询非危险联赛列表 queryDisRiskTournamentList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/queryDisRiskTournamentList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("查询非危险联赛列表{}" + e);
            return Result.fail("查询非危险联赛列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险等级更改")
    @RequestMapping(value = "/updateRiskLevelById", method = {RequestMethod.POST})
    public Result<String> updateRiskLevelById(@RequestBody @Valid TyRiskUpdateLevelTournamentReqVo vo,HttpServletRequest request) {
        log.info("start 危险等级更改 updateRiskLevelById:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/updateRiskLevelById"), JSON.toJSONString(vo), appId);
            vo.setIp(IPUtil.getRequestIp(request));
            riskTournamentService.updateTournamentLog(vo);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险等级更改{}" + e);
            return Result.fail("危险等级更改异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险联赛有效状态更改")
    @RequestMapping(value = "/updateStatusById", method = {RequestMethod.POST})
    public Result<String> updateStatusById(@RequestBody @Valid TyRiskUpdateStatusLevelTournamentReqVo vo) {
        log.info("start 危险联赛有效状态更改 updateStatusById:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskManagement/updateStatusById"), JSON.toJSONString(vo), appId);
            if (vo.getStatus()==0){
                TyRiskDelRiskTournamentReqVoMessage tyRiskDelRiskTournamentReqVoMessage = new TyRiskDelRiskTournamentReqVoMessage();
                tyRiskDelRiskTournamentReqVoMessage.setId(vo.getId());
                tyRiskDelRiskTournamentReqVoMessage.setTournamentId(vo.getId());
                tyRiskDelRiskTournamentReqVoMessage.setType("del");
                producerSendMessageUtils.sendMessage("Danger_Tournament_Data", "del", String.valueOf(vo.getId()), JSON.toJSONString(tyRiskDelRiskTournamentReqVoMessage));
            }
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险联赛有效状态更改{}" + e);
            return Result.fail("危险联赛有效状态更改异常");
        }
        return Result.succes(data);
    }


}
