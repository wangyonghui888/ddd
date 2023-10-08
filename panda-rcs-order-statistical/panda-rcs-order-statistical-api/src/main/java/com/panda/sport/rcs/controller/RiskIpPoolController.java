package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.ExportUtils;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.TyUserIpRiskSummaryResp;
import com.panda.sport.rcs.common.vo.api.response.TyUserIpSummaryResp;
import com.panda.sport.rcs.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author derre
 * @date 2022-03-20
 */
@Api(tags = "报表系统-危险IP池管理-1665")
@RestController
@RequestMapping("/riskIpPool")
public class RiskIpPoolController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(UserPortraitController.class);

    @Autowired
    RedisService redisService;

    private static final String user_ip_summary_columns[] = {"用户id", "用户名称", "商户号", "用户投注特征标签", "投注金额", "盈利金额", "盈利率", "胜率", "关联天数"};
    private static final String user_ip_summary_attributes[] = {"userId", "userName", "merchantCode", "userLabel", "betAmount", "netAmount", "netAmountRate", "winAmountRate", "betDays"};

    private static final String user_ip_risk_summary_columns[] = {"IP", "地区", "ip标签", "关联用户总数", "成功投注金额", "平台盈利", "平台盈利率", "平台胜率", "最后下注时间"};
    private static final String user_ip_risk_summary_attributes[] = {"ip", "area", "ipLabel", "userCount", "betAmount", "netAmount", "netAmountRate", "winAmountRate", "maxBetTime"};


    @ApiOperation(value = "通过ip获取危险ip池用户列表")
    @RequestMapping(value = "/getListByIp", method = {RequestMethod.POST})
    public Result<String> getListByIp(@RequestBody @Valid RiskIpPoolListIpReqVo vo) {
        log.info("start 通过ip获取危险ip池用户列表 getListByIp:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIpPool/getListByIp"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("通过ip获取危险ip池用户列表{}" + e);
            return Result.fail("通过ip获取危险ip池用户列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "通过ip获取危险ip池用户列表导出(外部调用)")
    @RequestMapping(value = "/getListByIpOutPutList", method = {RequestMethod.GET})
    public void getListByIpOutPutList(HttpServletResponse response, RiskIpPoolIpOutputReqVo vo) {
        log.info("start 通过ip获取危险ip池用户列表导出(外部调用) getListByIpOutPutList:" + JSON.toJSONString(vo));
        try {
            List<TyUserIpSummaryResp> list = new ArrayList<>();
            String result = HttpUtil.post(urlPrefix.concat("/riskIpPool/getListByIpOutPutList"), JSON.toJSONString(vo), appId);
            log.info("data:" + result);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));
                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, TyUserIpSummaryResp.class);
                }
                String fileName = ExcelUtils.generatorFileName("通过ip获取危险ip池用户列表");
                ExportUtils<TyUserIpSummaryResp> exportUtils = new ExportUtils();
                exportUtils.export(response, fileName, "通过ip获取危险ip池用户列表", user_ip_summary_columns, user_ip_summary_attributes, list);
            }
        } catch (Exception e) {
            log.error("通过ip获取危险ip池用户列表导出(外部调用){}" + e);
        }
    }

    @ApiOperation(value = "获取新增ip地址列表")
    @RequestMapping(value = "/getUserIpList", method = {RequestMethod.POST})
    public Result<String> getUserIpList(@RequestBody @Valid RiskIpPoolUserIpListReqVo vo) {
        log.info("start 获取新增ip地址列表 getUserIpList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIpPool/getUserIpList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("获取新增ip地址列表{}" + e);
            return Result.fail("获取新增ip地址列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "分页获取危险ip池列表")
    @RequestMapping(value = "/getUserIpRiskList", method = {RequestMethod.POST})
    public Result<String> getUserIpRiskList(@RequestBody @Valid RiskIpPoolUserIpRiskListReqVo vo) {
        log.info("start 分页获取危险ip池列表 getUserIpRiskList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIpPool/getUserIpRiskList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("分页获取危险ip池列表{}" + e);
            return Result.fail("分页获取危险ip池列表异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "获取危险ip池列表导出(外部接口调用)")
    @RequestMapping(value = "/getUserIpRiskOutPutList", method = {RequestMethod.GET})
    public void getUserIpRiskOutPutList(HttpServletResponse response, RiskIpPoolUserIpRiskOutPutListReqVo vo) {
        log.info("start 获取危险ip池列表导出 getUserIpRiskOutPutList:" + JSON.toJSONString(vo));
        try {
            List<TyUserIpRiskSummaryResp> list = new ArrayList<>();
            String result = HttpUtil.post(urlPrefix.concat("/riskIpPool/getUserIpRiskOutPutList"), JSON.toJSONString(vo), appId);
            log.info("data:" + result);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));
                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, TyUserIpRiskSummaryResp.class);
                }
                String fileName = ExcelUtils.generatorFileName("危险ip池列表");
                ExportUtils<TyUserIpRiskSummaryResp> exportUtils = new ExportUtils();
                exportUtils.export(response, fileName, "危险ip池列表", user_ip_risk_summary_columns, user_ip_risk_summary_attributes, list);
            }
        } catch (Exception e) {
            log.error("获取危险ip池列表导出{}" + e);
        }
    }

    @ApiOperation(value = "解除危险ip")
    @RequestMapping(value = "/removeRiskIpLabelById", method = {RequestMethod.POST})
    public Result<String> removeRiskIpLabelById(@RequestBody @Valid RiskIpPoolRemoveIpReqVo vo) {
        log.info("start 解除危险ip removeRiskIpLabelById:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIpPool/removeRiskIpLabelById"), JSON.toJSONString(vo), appId);
            redisService.delete("rcs:danger:ip:" + vo.getId());
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("解除危险ip{}" + e);
            return Result.fail("解除危险ip异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "修改投注ip的标签")
    @RequestMapping(value = "/updateBetIp", method = {RequestMethod.POST})
    public Result<String> updateBetIp(@RequestBody @Valid RiskIpPoolUpdateBetIpReqVo vo) {
        log.info("start 修改投注ip的标签 updateBetIp:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIpPool/updateBetIp"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("修改投注ip的标签{}" + e);
            return Result.fail("修改投注ip的标签异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "修改危险ip备注信息")
    @RequestMapping(value = "/updateRiskIpRemarkById", method = {RequestMethod.POST})
    public Result<String> updateRiskIpRemarkById(@RequestBody @Valid RiskIpPoolUpdateRiskIpReqVo vo) {
        log.info("start 修改危险ip备注信息 updateRiskIpRemarkById:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIpPool/updateRiskIpRemarkById"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("修改危险ip备注信息{}" + e);
            return Result.fail("修改危险ip备注信息异常");
        }
        return Result.succes(data);
    }


}
