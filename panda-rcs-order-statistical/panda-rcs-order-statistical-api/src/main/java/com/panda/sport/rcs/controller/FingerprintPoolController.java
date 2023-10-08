package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.ExportUtils;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.FingerprintReqVO;
import com.panda.sport.rcs.common.vo.api.request.FingerprintUserListReqVO;
import com.panda.sport.rcs.common.vo.api.response.FingerprintResVo;
import com.panda.sport.rcs.common.vo.api.response.FingerprintUserLisRestVO;
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
import java.util.List;

/**
 * 指纹池管理
 *
 * @author derre
 * @date 2022-04-10
 */
@Api(tags = "指纹池及数据统计-1691")
@RestController
@RequestMapping("/fingerprintPool")
public class FingerprintPoolController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;
    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(FingerprintPoolController.class);

    private static final String finger_print_columns[] = {"指纹ID", "设备", "危险等级", "关联用户总数", "成功投注金额", "平台盈利", "平台盈利率", "平台胜率", "最后下注时间"};
    private static final String finger_print_attributes[] = {"fingerprintId", "device", "riskLevel", "userCount", "betAmount", "netAmount", "netAmountRate", "winAmountRate", "maxBetTime"};

    private static final String finger_print_user_columns[] = {"用户id", "用户名", "商户名", "用户投注特征标签", "成功投注金额", "平台盈利", "平台盈利率", "平台胜率", "关联天数"};
    private static final String finger_print_user_attributes[] = {"userId", "username", "merchantCode", "userLabel", "betAmount", "netAmount", "netAmountRate", "winAmountRate", "betDays"};


    @ApiOperation(value = "指纹池管理列表")
    @RequestMapping(value = "/fingerprintList", method = {RequestMethod.POST})
    public Result<String> fingerprintList(@RequestBody @Valid FingerprintReqVO vo) {
        log.info("start 指纹池管理列表 fingerprintList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/fingerprintPool/fingerprintList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("指纹池管理列表{}" + e);
            return Result.fail("指纹池管理列表异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "指纹池管理列表---导出(外部接口调用)")
    @RequestMapping(value = "/fingerprintListOut", method = {RequestMethod.GET})
    public void fingerprintListOut(HttpServletResponse response, FingerprintReqVO vo) {
        log.info("start 指纹池管理列表导出 fingerprintListOut:" + JSON.toJSONString(vo));
        try {
            String result = HttpUtil.post(urlPrefix.concat("/fingerprintPool/fingerprintListOut"), JSON.toJSONString(vo), appId);
            log.info("data:" + result);
            List<FingerprintResVo> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));
                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, FingerprintResVo.class);
                }
            }
            String fileName = ExcelUtils.generatorFileName("指纹池管理列表");
            ExportUtils<FingerprintResVo> exportUtils = new ExportUtils();
            exportUtils.export(response, fileName, "指纹池管理列表", finger_print_columns, finger_print_attributes, list);
        } catch (Exception e) {
            log.error("指纹池管理列表导出{}" + e);
        }
    }


    @ApiOperation(value = "通过指纹id获取关联用户列表")
    @RequestMapping(value = "/fingerprintUserList", method = {RequestMethod.POST})
    public Result<String> fingerprintUserList(@RequestBody @Valid FingerprintUserListReqVO vo) {
        log.info("start 通过指纹id获取关联用户列表 fingerprintUserList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/fingerprintPool/fingerprintUserList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("通过指纹id获取关联用户列表{}" + e);
            return Result.fail("通过指纹id获取关联用户列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "通过指纹id获取关联用户列表---导出(外部接口调用)")
    @RequestMapping(value = "/fingerprintUserListOut", method = {RequestMethod.GET})
    public void fingerprintUserListOut(HttpServletResponse response, FingerprintUserListReqVO vo) {
        log.info("start 通过指纹id获取关联用户列表 fingerprintUserListOut:" + JSON.toJSONString(vo));
        try {
            String result = HttpUtil.post(urlPrefix.concat("/fingerprintPool/fingerprintUserListOut"), JSON.toJSONString(vo), appId);
            log.info("data:" + result);
            List<FingerprintUserLisRestVO> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));
                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, FingerprintUserLisRestVO.class);
                }
            }
            String fileName = ExcelUtils.generatorFileName("指纹id获取关联用户列表");
            ExportUtils<FingerprintUserLisRestVO> exportUtils = new ExportUtils();
            exportUtils.export(response, fileName, "指纹id获取关联用户列表", finger_print_user_columns, finger_print_user_attributes, list);
        } catch (Exception e) {
            log.error("通过指纹id获取关联用户列表{}" + e);
        }
    }


}
