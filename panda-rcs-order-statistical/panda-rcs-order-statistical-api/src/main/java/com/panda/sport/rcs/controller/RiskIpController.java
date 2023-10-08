package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.ExportUtils;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.BetIpResVO;
import com.panda.sport.rcs.common.vo.api.response.TyUserIpSummaryResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author derre
 * @date 2022-03-29
 */
@Api(tags = "报表系统-投注ip管理-1653")
@RestController
@RequestMapping("/riskIp")
public class RiskIpController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;
    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(UserPortraitController.class);

    private static final String bet_ip_out_put_columns[] = {"IP地址", "地区", "ip标签", "关联用户数", "成功投注金额", "平台盈利", "平台盈利率", "平台胜率", "最后下注时间"};
    private static final String bet_ip_out_put_attributes[] = {"ip", "area", "ipLabel", "userCount", "betAmount", "netAmount", "netAmountRate", "winAmountRate", "maxBetTime"};

    private static final String ip_out_put_columns[] = {"用户ID", "用户名", "所属商户", "投注特征标签", "成功投注金额", "平台盈利", "平台盈利率", "平台胜率", "关联天数"};
    private static final String ip_out_put_attributes[] = {"userId", "username", "merchantCode", "userLabel", "betAmount", "netAmount", "netAmountRate", "winAmountRate", "betDays"};


    @ApiOperation(value = "新增ip标签")
    @RequestMapping(value = "/addIpLabel", method = {RequestMethod.POST})
    public Result<String> addIpLabel(@RequestBody @Valid AddIpLabelReqVO vo) {
        log.info("start 新增ip标签 addIpLabel:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIp/addIpLabel"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("新增ip标签{}" + e);
            return Result.fail("新增ip标签异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "投注ip池")
    @RequestMapping(value = "/betIpList", method = {RequestMethod.POST})
    public Result<String> betIpList(@RequestBody @Valid BetIpReqVo vo) {
        log.info("start 投注ip池 betIpList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIp/betIpList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("投注ip池{}" + e);
            return Result.fail("投注ip池异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "删除ip标签")
    @RequestMapping(value = "/deleteIpLabel", method = {RequestMethod.POST})
    public Result<String> deleteIpLabel(@RequestBody @Valid AddIpLabelReqVO vo) {
        log.info("start 删除ip标签 deleteIpLabel:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIp/deleteIpLabel"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("删除ip标签{}" + e);
            return Result.fail("删除ip标签异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "获取投注ip池列表导出(外部接口调用)")
    @RequestMapping(value = "/getBetIpOutPutList", method = {RequestMethod.GET})
    public void getBetIpOutPutList(HttpServletResponse response, BetIpReqVo vo) {
        log.info("start 获取投注ip池列表导出 getBetIpOutPutList:" + JSON.toJSONString(vo));
        try {
            String result = HttpUtil.post(urlPrefix.concat("/riskIp/getBetIpOutPutList"), JSON.toJSONString(vo), appId);
            log.info("data:" + result);
            List<BetIpResVO> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));
                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, BetIpResVO.class);
                }
            }
            String fileName = ExcelUtils.generatorFileName("投注ip池列表");
            ExportUtils<BetIpResVO> exportUtils = new ExportUtils();
            exportUtils.export(response, fileName, "投注ip池列表", bet_ip_out_put_columns, bet_ip_out_put_attributes, list);
        } catch (Exception e) {
            log.error("获取投注ip池列表导出{}" + e);
        }
    }

    @ApiOperation(value = "ip标签列表")
    @RequestMapping(value = "/getIpList", method = {RequestMethod.POST})
    public Result<String> getIpList() {
        log.info("start ip标签列表 getIpList");
        String data;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("appId", appId);
            data = HttpUtil.post(urlPrefix.concat("/riskIp/getIpList"),"1",appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("ip标签列表{}" + e);
            return Result.fail("ip标签列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "通过ip获取投注IP管理用户列表")
    @RequestMapping(value = "/getListByIp", method = {RequestMethod.POST})
    public Result<String> getListByIp(@RequestBody @Valid BetIpUserReqVo vo) {
        log.info("start 通过ip获取投注IP管理用户列表 getListByIp:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIp/getListByIp"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("通过ip获取投注IP管理用户列表{}" + e);
            return Result.fail("通过ip获取投注IP管理用户列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "通过ip获取投注IP管理用户列表导出(外部接口调用)")
    @RequestMapping(value = "/getListByIpOutPutList", method = {RequestMethod.GET})
    public void getListByIpOutPutList(HttpServletResponse response, TyUserIpSummaryOutPutReqVo vo) {
        log.info("start 通过ip获取投注IP管理用户列表导出 getListByIpOutPutList:" + JSON.toJSONString(vo));
        try {
            String result = HttpUtil.post(urlPrefix.concat("/riskIp/getListByIpOutPutList"), JSON.toJSONString(vo), appId);
            log.info("data:" + result);
            List<TyUserIpSummaryResp> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));
                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, TyUserIpSummaryResp.class);
                }
            }
            String fileName = ExcelUtils.generatorFileName("获取投注IP管理用户列表");
            ExportUtils<TyUserIpSummaryResp> exportUtils = new ExportUtils();
            exportUtils.export(response, fileName, "获取投注IP管理用户列表", ip_out_put_columns, ip_out_put_attributes, list);
        } catch (Exception e) {
            log.error("通过ip获取投注IP管理用户列表导出{}" + e);
        }
    }

    @ApiOperation(value = "修改投注ip的标签")
    @RequestMapping(value = "/updateBetIp", method = {RequestMethod.POST})
    public Result<String> updateBetIp(@RequestBody @Valid UpdateBetIpReqVo vo) {
        log.info("start 修改投注ip的标签 updateBetIp:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIp/updateBetIp"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("修改投注ip的标签{}" + e);
            return Result.fail("修改投注ip的标签异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "编辑ip标签")
    @RequestMapping(value = "/updateIpLabel", method = {RequestMethod.POST})
    public Result<String> updateIpLabel(@RequestBody @Valid AddIpLabelReqVO vo) {
        log.info("start 编辑ip标签 updateIpLabel:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskIp/updateIpLabel"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("编辑ip标签{}" + e);
            return Result.fail("编辑ip标签异常");
        }
        return Result.succes(data);
    }

}
