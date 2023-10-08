package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.ExportUtils;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.danger.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * @author derre
 * @date 2022-03-19
 */
@Api(tags = "报表系统-系统监控系统-1663")
@RestController
@RequestMapping("/tyHazardMonitoring")
public class HazardMonitoringController {

    @Value("${user.portrait.http.url.prefix}")
    String urlPrefix;
    @Value("${user.portrait.risk.http.url.prefix}")
    String urlRiskPrefix;
    @Value("${user.portrait.http.appId}")
    String appId;

    private static final String user_play_columns[] = {"序号", "商户编码", "用户ID", "用户名", "用户投注标签", "联赛名称",
        "开赛时间", "赛事id", "赛事信息", "投注时间", "投注金额", "盘口类型", "赛事类型", "注单赔率", "订单单号", "玩法名称",
        "投注项名称", "危险类型", "危险说明", "IP", "地区", "设备名称", "设备ID"};
    private static final String user_play_attributes[] = {"seq", "merchantCode", "userId", "userName", "userTagName",
        "tournamentName", "beginTime", "matchId", "matchInfo", "createTime", "betAmount", "marketType", "matchType",
        "oddsValue", "orderNo", "playName", "playOption", "riskType", "riskDesc", "ip", "area", "deviceType", "deviceId"};


    Logger log = LoggerFactory.getLogger(UserPortraitController.class);

    @ApiOperation(value = "查询体育所有币种信息")
    @RequestMapping(value = "/getTyCurrencyList", method = {RequestMethod.POST})
    public Result<String> getTyCurrencyList() {
        log.info("start 查询体育所有币种信息 getTyCurrencyList");
        String data;
        try {
            HashMap<String, String> header = new HashMap<>();
            header.put("appId", appId);
            data =HttpUtil.post(urlRiskPrefix.concat("/tyCurrency/getTyCurrencyList"),"1",appId);
            log.info("data:" + JSON.toJSONString(data));
        } catch (Exception e) {
            log.error("查询体育所有币种信息{}" + e);
            return Result.fail("查询体育所有币种信息异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "危险单关监控")
    @RequestMapping(value = "/abnormalSingleOrderList", method = {RequestMethod.POST})
    public Result<String> abnormalSingleOrderList(@RequestBody @Valid AbnormalSingleOrderListReqVo vo) {
        log.info("start 危险单关监控 abnormalComboDateList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/danger/abnormalSingleOrderList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险单关监控{}" + e);
            return Result.fail("危险单关监控异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险单关监控导出")
    @RequestMapping(value = "/outAbnormalSingleOrderListExport", method = {RequestMethod.GET})
    public void abnormalSingleOrderListExport(HttpServletResponse response, AbnormalSingleOrderListReqVo vo) {
        log.info("start 危险单关监控导出 abnormalSingleOrderListExport:" + JSON.toJSONString(vo));
        try {
            List<RiskOrderExportVORiskOrderExportVO> list = new ArrayList<>();
            String result = HttpUtil.post(urlRiskPrefix.concat("/danger/outAbnormalSingleOrderListExport"), JSON.toJSONString(vo), appId);
            log.info("data:" + result);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));
                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, RiskOrderExportVORiskOrderExportVO.class);
                    // 給予報表流水號 (seq)
                    if (!CollectionUtils.isEmpty(list)) {
                        int i = 1;
                        for (RiskOrderExportVORiskOrderExportVO rpt : list) {
                            rpt.setSeq(i++);
                        }
                    }
//                    if (!CollectionUtils.isEmpty(list)) {
//                        list.forEach(obj -> {
//                            obj.setDeviceType(StringUtils.isBlank(obj.getDeviceType()) ? null : DeviceTypeEnum.get(Integer.valueOf(obj.getDeviceType())).getName());
//                            obj.setMatchType(StringUtils.isBlank(obj.getMatchType()) ? null : DeviceTypeEnum.get(Integer.valueOf(obj.getMatchType())).getName());
//                        });
//                    }
                }
                String fileName = ExcelUtils.generatorFileName("危险单关监控");
                ExportUtils<RiskOrderExportVORiskOrderExportVO> exportUtils = new ExportUtils();
                exportUtils.export(response, fileName, "危险单关监控", user_play_columns, user_play_attributes, list);
            }
        } catch (Exception e) {
            log.error("危险单关监控导出{}" + e);
        }
    }

    @ApiOperation(value = "危险串关监控")
    @RequestMapping(value = "/abnormalComboDateList", method = {RequestMethod.POST})
    public Result<String> abnormalComboDateList(@RequestBody @Valid AbnormalComboDateListReqVo vo) {
        log.info("start 危险串关监控 abnormalComboDateList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/danger/abnormalComboDateList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险串关监控{}" + e);
            return Result.fail("危险串关监控异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险串关监控-用户列表")
    @RequestMapping(value = "/abnormalComboDetailList", method = {RequestMethod.POST})
    public Result<String> abnormalComboDetailList(@RequestBody @Valid AbnormalComboDetailListReqVo vo) {
        log.info("start 危险串关监控-用户列表 abnormalComboDetailList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/danger/abnormalComboDetailList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险串关监控-用户列表{}" + e);
            return Result.fail("危险串关监控-用户列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险用户监控列表")
    @RequestMapping(value = "/abnormalMatchUserList", method = {RequestMethod.POST})
    public Result<String> abnormalMatchUserList(@RequestBody @Valid AbnormalMatchUserListReqVo vo) {
        log.info("start 危险用户监控列表 abnormalComboDetailList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/danger/abnormalMatchUserList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险用户监控列表{}" + e);
            return Result.fail("危险用户监控列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "串关类型列表")
    @RequestMapping(value = "/getAllTyComboSeriesTypeList", method = {RequestMethod.GET})
    public Result<String> getAllTyComboSeriesTypeList() {
        log.info("start 串关类型列表 abnormalMatchUserList");
        String data;
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("appId", appId);
            data = HttpUtil.get(urlPrefix.concat("/danger/getAllTyComboSeriesTypeList"), headerMap);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("串关类型列表{}" + e);
            return Result.fail("串关类型列表异常");
        }
        return Result.succes(data);
    }

    @Deprecated
    @ApiOperation(value = "危险赛事列表")
    @RequestMapping(value = "/matchList", method = {RequestMethod.POST})
    public Result<String> matchList(@RequestBody @Valid MatchListReqVo vo) {
        log.info("start 危险赛事列表 matchList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/danger/matchList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险赛事列表{}" + e);
            return Result.fail("危险赛事列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险赛事-用户投注列表")
    @RequestMapping(value = "/matchUserBetList", method = {RequestMethod.POST})
    public Result<String> matchUserBetList(@RequestBody @Valid MatchUserBetListReqVo vo) {
        log.info("start 危险赛事-用户投注列表 matchUserBetList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/danger/matchUserBetList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险赛事-用户投注列表{}" + e);
            return Result.fail("危险赛事-用户投注列表异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险单关监控备注修改")
    @RequestMapping(value = "/abnormalSingleUpdate", method = {RequestMethod.POST})
    public Result<String> abnormalSingleUpdate(@RequestBody @Valid UpdateRiskOrderRemarkReqVo vo) {
        log.info("start 危险单关监控备注修改 matchUserBetList:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlPrefix.concat("/danger/abnormalSingleUpdate"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        } catch (Exception e) {
            log.error("危险单关监控备注修改{}" + e);
            return Result.fail("危险单关监控备注修改异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险串关赛事监控")
    @RequestMapping(value = "/comboMatches", method = { RequestMethod.POST } )
    public Result<PageResult> comboMatches(@RequestBody @Valid MatchListReqVo vo ) {

        log.info(String.format( "危险串关赛事监控 comboMatches %s --start", JSON.toJSON( vo ) ) );

        //empty check
        if( StringUtils.isBlank( vo.getStartTime() ) || StringUtils.isBlank( vo.getEndTime() ) ){
            return Result.fail(" params, startTime and endTime, are required ");
        }

        //date format validation
        try{
            DateUtils.parseDate( vo.getStartTime(), "yyyy-MM-dd HH:mm" );
            DateUtils.parseDate( vo.getEndTime(), "yyyy-MM-dd HH:mm" );
        }catch ( Exception e ){
            return Result.fail(" params, startTime and endTime, must be formatted yyyy-MM-dd HH:mm ");
        }

        String responseRawData = null;
        try{
            responseRawData = HttpUtil.post(urlRiskPrefix.concat("/danger/matchList"), JSON.toJSONString(vo), appId);
        }catch (Exception e){
            log.error("大数据接口请求异常", e );
            return Result.fail("大数据接口请求异常, 请在试一次");
        }

        if( StringUtils.isBlank( responseRawData ) ){
            return Result.fail("大数据接口请求异常, 请在试一次");
        }

        PageResult pageResult = JSON.parseObject( responseRawData, PageResult.class );

        if( pageResult.getCode() != 200 ){
            log.error( "大数据接口请求异常, response from bigData api: %s", responseRawData );
            return Result.fail("大数据接口请求异常, 请在试一次");
        }

        return Result.succes( pageResult );
    }



    @ApiOperation(value = "危险串关赛事监控-导出")
    @RequestMapping(value = "/comboMatches/export", method = { RequestMethod.GET })
    public void comboMatchesExport(
        MatchListReqVo vo,
        HttpServletResponse resp
    ) {
        log.info(String.format( "危险串关赛事监控-导出 comboMatchesExport %s --start", JSON.toJSON( vo ) ) );

        String responseRawData = null;

        vo.setPage(1);
        vo.setPageSize( Integer.MAX_VALUE );

        try{
            responseRawData = HttpUtil.post(urlRiskPrefix.concat("/danger/matchList"), JSON.toJSONString(vo), appId);
        }catch (IOException e){
            log.error("大数据接口请求异常", e );
            return;
        }

        PageResult pageResult = JSON.parseObject( responseRawData, PageResult.class );
        if( pageResult==null ){
            log.error("资料转换异常 from string to json object, raw data string is:" + responseRawData );
            return;
        }

        final String[] dangerMatchColumns = {
            "赛种", "联赛名称", "赛事名称", "开赛时间", "玩法名称",
            "投注项/盘口值", "投注阶段", "投注笔数", "投注金额"
        };
        final String[] dangerMatchAttributes = {
            "sportName","tournamentName","matchInfo","matchBeginTime","playName"
            ,"playOption", "matchType", "orderCount", "betAmount"
        };

        try{
            ExportUtils exportUtils = new ExportUtils();
            String sheetName = "危险串关赛事监控列表";
            String excelFileName = ExcelUtils.generatorFileName( sheetName );
            exportUtils.export(
                resp,
                excelFileName,
                sheetName,
                dangerMatchColumns,
                dangerMatchAttributes,
                pageResult.getData()
            );
        }catch (Exception e){
            log.error("危险串关赛事监控列表导出异常");
        }

    }
}
