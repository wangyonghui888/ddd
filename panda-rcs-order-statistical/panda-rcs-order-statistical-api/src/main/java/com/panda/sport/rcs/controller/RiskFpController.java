package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.ExportUtils;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.DeleteFpReqVo;
import com.panda.sport.rcs.common.vo.api.request.RiskFpListReqVo;
import com.panda.sport.rcs.common.vo.api.request.UpdateFpLevelReqVo;
import com.panda.sport.rcs.common.vo.api.request.UpdateRemarkReqVo;
import com.panda.sport.rcs.common.vo.api.response.RiskFpListResp;
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
 * @date 2022-04-09
 */
@Api(tags = "危险指纹池管理-1692")
@RestController
@RequestMapping("/riskFp")
public class RiskFpController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(RiskFpController.class);

    @Autowired
    RedisService redisService;

    private static final String columns[] = {"指纹ID", "危险等级","关联用户总数", "成功投注金额", "平台盈利", "平台盈利率", "平台胜率", "最后下注时间", "备注"};
    private static final String attributes[] = {"fingerprintId","riskLevel", "userCount", "betAmount", "netAmount", "netAmountRate", "winAmountRate", "maxBetTime", "remark"};

    @ApiOperation(value = "删除指纹")
    @RequestMapping(value = "/deleteFp", method = {RequestMethod.POST})
    public Result<String> deleteFp(@RequestBody @Valid DeleteFpReqVo vo) {
        log.info("start 删除指纹 deleteFp:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskFp/deleteFp"), JSON.toJSONString(vo), appId);
            redisService.delete("rcs:danger:fp:" + vo.getFingerprintId());
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("删除指纹{}" + e);
            return Result.fail("删除指纹异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "危险指纹池管理列表")
    @RequestMapping(value = "/riskFpList", method = {RequestMethod.POST})
    public Result<String> riskFpList(@RequestBody @Valid RiskFpListReqVo vo) {
        log.info("start 危险指纹池管理列表 riskFpList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskFp/riskFpList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险指纹池管理列表{}" + e);
            return Result.fail("危险指纹池管理列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险指纹池管理列表--导出，外部使用")
    @RequestMapping(value = "/riskFpListOut", method = {RequestMethod.GET})
    public void riskFpListOut(HttpServletResponse response, RiskFpListReqVo vo) {
        log.info("start 危险指纹池管理列表(外部调用) riskFpListOut:" + JSON.toJSONString(vo));
        try {
            List<RiskFpListResp> list = new ArrayList<>();
            String result = HttpUtil.post(urlPrefix.concat("/riskFp/riskFpListOut"), JSON.toJSONString(vo), appId);
            log.info("data:" + result);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));
                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, RiskFpListResp.class);
                }
                String fileName = ExcelUtils.generatorFileName("危险指纹池管理列表");
                ExportUtils<RiskFpListResp> exportUtils = new ExportUtils();
                exportUtils.export(response, fileName, "危险指纹池管理列表", columns, attributes, list);
            }
        } catch (Exception e) {
            log.error("危险指纹池管理列表(外部调用){}" + e);
        }
    }

    @ApiOperation(value = "修改危险指纹备注")
    @RequestMapping(value = "/updateFp", method = {RequestMethod.POST})
    public Result<String> updateFp(@RequestBody @Valid UpdateRemarkReqVo vo) {
        log.info("start 修改危险指纹备注 updateFp:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskFp/updateFp"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("修改危险指纹备注{}" + e);
            return Result.fail("修改危险指纹备注异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "修改指纹危险等级")
    @RequestMapping(value = "/updateRiskLevel", method = {RequestMethod.POST})
    public Result<String> updateRiskLevel(@RequestBody @Valid UpdateFpLevelReqVo vo) {
        log.info("start 修改指纹危险等级 updateRiskLevel:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/riskFp/updateRiskLevel"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("修改指纹危险等级{}" + e);
            return Result.fail("修改指纹危险等级异常");
        }
        return Result.succes(data);
    }

}
