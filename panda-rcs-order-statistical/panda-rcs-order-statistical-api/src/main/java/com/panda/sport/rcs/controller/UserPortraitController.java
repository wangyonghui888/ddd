package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.entity.DictionaryVO;
import com.panda.sport.rcs.common.entity.QueryUserGroupWarnVo;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.ExportUtils;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.UserPlayExportDataResVo;
import com.panda.sport.rcs.common.vo.api.response.UserTournamentExportDataResVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报表系统接口
 * </p>
 *
 */
@Api(tags = "用户画像V2.3-增加设备类型和胜率-1713")
@RestController
@RequestMapping("/tyUserPortrait")
public class UserPortraitController {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlRiskPrefix;
    @Value("${user.portrait.http.appId}")
    String appId;

    Logger log = LoggerFactory.getLogger(UserPortraitController.class);


    private static final String user_tournament_columns[] = {"用户id","用户名称","赛种ID","赛种名称","联赛ID","联赛名称","投注日期","成功投注场次",
            "成功投注笔数","成功投注金额","用户盈利笔数","用户盈利金额","有效投注金额"};
    private static final String user_tournament_attributes[] = {"userId", "userName", "sportId", "sportNameZs", "tournamentId", "tournamentNameZs", "dateId",
            "matchCount", "orderCount", "betAmount", "userWinOrderCount", "netAmount", "validBetAmount"};

    private static final String user_play_columns[] = {"用户ID","用户名","赛种ID","赛种名称","玩法ID","玩法名称","投注日期","成功投注笔数","成功投注金额",
            "用户盈利笔数","用户盈利金额","有效投注金额"};
    private static final String user_play_attributes[] = {"userId", "userName", "sportId", "sportNameZs", "playId", "playNameZs", "dateId",
            "orderCount", "betAmount", "winOrderCount", "netAmount", "validBetAmount"};



    @ApiOperation(value = "体育联赛投注统计导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "sportIds", value = "赛种ids", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名 此用户名是 商户编码_用户名 形式", paramType = "query")

    })
    @RequestMapping(value = "/exportTyUserTournament", method = {RequestMethod.GET})
    public void exportTyUserTournament(HttpServletResponse response,
                                                 @RequestParam(value="endTime", required = false) String endTime,
                                                 @RequestParam(value="sportIds", required = false) List<String> sportIds,
                                                 @RequestParam(value="startTime", required = false) String startTime,
                                                 @RequestParam(value="userId") String userId,
                                                 @RequestParam(value="userName", required = false) String userName) {
        log.info("start 体育联赛投注统计导出 endTime:{},sportIds:{},startTime:{},userId:{},userName:{}", endTime, sportIds, startTime, userId, userName);


        String result;
        Map<String,String> map = new HashMap<>();
        map.put("userId",userId);
        try {

            if (StringUtils.isNotEmpty(endTime)) {
                map.put("endTime",endTime);
            }
            if (StringUtils.isNotEmpty(startTime)) {
                map.put("startTime",startTime);
            }
            if (null != sportIds && sportIds.size() > 0) {
                map.put("sportIds",sportIds.toString());
            }
            if (StringUtils.isNotEmpty(userName)) {
                map.put("userName",userName);
            }

            List<UserTournamentExportDataResVo> list = new ArrayList<>();
            //获取三方数据
            result = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/tyUserTournamentExportData"), map, true, appId);
            log.info("result:" + result);

            if (StringUtils.isNotEmpty(result)) {

                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));

                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, UserTournamentExportDataResVo.class);
                    if (list.size() > 0) {

                        //用户盈利取反
                        for (UserTournamentExportDataResVo vo : list) {
                            Double d = vo.getNetAmount();
                            vo.setNetAmount(0 - d);
                        }
                    }
                }
            }

            String fileName = ExcelUtils.generatorFileName("体育联赛投注统计");
            ExportUtils<UserTournamentExportDataResVo> exportUtils = new ExportUtils();
            exportUtils.export(response,fileName,"体育联赛投注统计", user_tournament_columns, user_tournament_attributes, list);

        }catch (Exception e){
            log.error("体育联赛投注统计导出异常{}" + e);
        }
    }


    @ApiOperation(value = "体育玩家玩法导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "playId", value = "玩法id", paramType = "query"),
            @ApiImplicitParam(name = "sportIds", value = "赛种id", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户名", paramType = "query")

    })
    @RequestMapping(value = "/exportUserPlay", method = {RequestMethod.GET})
    public void exportUserPlay(HttpServletResponse response,
                                         @RequestParam(value="endTime",required = false) String endTime,
                                         @RequestParam(value="playId", required = false) String playId,
                                         @RequestParam(value="sportIds",required = false) List<String> sportIds,
                                         @RequestParam(value="startTime",required = false) String startTime,
                                         @RequestParam(value="userId") String userId,
                                         @RequestParam(value="userName",required = false) String userName) {

        log.info("start 体育玩家玩法导出 endTime:{},playId:{},sportIds:{},startTime:{},userId:{},userName:{}", endTime, playId, sportIds, startTime, userId, userName);

        String result;
        Map<String,String> map = new HashMap<>();
        map.put("userId",userId);
        try {

            if (StringUtils.isNotEmpty(endTime)) {
                map.put("endTime",endTime);
            }
            if (StringUtils.isNotEmpty(startTime)) {
                map.put("startTime",startTime);
            }
            if (StringUtils.isNotEmpty(playId)) {
                map.put("playId",playId);
            }
            if (null != sportIds && sportIds.size() > 0) {
                map.put("sportIds",sportIds.toString());
            }
            if (StringUtils.isNotEmpty(userName)) {
                map.put("userName",userName);
            }

            List<UserPlayExportDataResVo> list = new ArrayList<>();
            //获取三方数据
            result = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/tyUserPlayExportData"), map, true, appId);
            log.info("result:" + result);
            if (StringUtils.isNotEmpty(result)) {

                JSONObject json = JSONObject.parseObject(result);
                String code = String.valueOf(json.get("code"));
                String data = String.valueOf(json.get("data"));

                if ("200".equals(code) && StringUtils.isNotEmpty(data)) {
                    list = JSONArray.parseArray(data, UserPlayExportDataResVo.class);
                    if (list.size() > 0) {

                        //用户盈利取反
                        for (UserPlayExportDataResVo vo : list) {
                            Double d = vo.getNetAmount();
                            vo.setNetAmount(0 - d);
                        }
                    }
                }
            }

            String fileName = ExcelUtils.generatorFileName("体育玩家玩法");
            ExportUtils<UserPlayExportDataResVo> exportUtils = new ExportUtils();
            exportUtils.export( response, fileName,"体育玩家玩法", user_play_columns, user_play_attributes, list);

        }catch (Exception e){
            log.error("体育玩家玩法导出异常{}" + e);
        }
    }

    @ApiOperation(value = "体育用户日期投注")
    @RequestMapping(value = "/tyDateBetInfo", method = {RequestMethod.POST})
    public Result<String> tyDateBetInfo(@RequestBody @Valid DateBetInfoReqVo vo) {
        String linkId = "tyDateBetInfo" + vo.getUserId();
        log.info("::{}::start体育用户日期投注 DateBetInfoReqVo:{}",linkId,JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/tyDateBetInfo"), JSON.toJSONString(vo), appId);
            log.info("::{}::data:{}",linkId,data);
        }catch (Exception e){
            log.error("::{}::体育用户日期投注:{}",linkId,e);
            return Result.fail("体育用户日期投注异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "体育用户赛种投注")
    @RequestMapping(value = "/tySportBetInfo", method = {RequestMethod.POST})
    public Result<String> tySportBetInfo(@RequestBody @Valid SportBetInfoReqVo vo) {
        String linkId = "tySportBetInfo" + vo.getUserId();
        log.info("::{}::start体育用户赛种投注 SportBetInfoReqVo:{}",linkId,JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/tySportBetInfo"), JSON.toJSONString(vo), appId);
            log.info("::{}::data:{}",linkId,data);
        }catch (Exception e){
            log.error("::{}::体育用户赛种投注:{}",linkId,e);
            return Result.fail("体育用户赛种投注异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "体育用户访问记录")
    @RequestMapping(value = "/userAccessLogs", method = {RequestMethod.POST})
    public Result<String> userAccessLogs(@RequestBody @Valid UserAccessLogsReqVo vo) {
        String linkId = "userAccessLogs" + vo.getUserId();
        log.info("::{}::start体育用户访问记录 UserAccessLogsReqVo:{}",linkId,JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/userAccessLogs"), JSON.toJSONString(vo), appId);
            log.info("::{}::data:{}",linkId,data);
        }catch (Exception e){
            log.error("::{}::体育用户访问记录:{}",linkId,e);
            return Result.fail("体育用户访问记录异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "体育用户列表")
    @RequestMapping(value = "/userList", method = {RequestMethod.POST})
    public Result<String> userList(@RequestBody @Valid PortraitUserListReqVo vo) {

        log.info("start 体育用户列表 PortraitUserListReqVo:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/userList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("体育用户列表{}" + e);
            return Result.fail("体育用户列表异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "获取用户信息")
    @RequestMapping(value = "/userMessageInfo", method = {RequestMethod.POST})
    public Result<String> userMessageInfo(@RequestBody @Valid UserMessageInfoReqVo vo) {
        String linkId = "userMessageInfo"+ vo.getUserId();
        log.info("::{}::start获取用户信息 UserMessageInfoReqVo:{}",linkId,JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/userMessageInfo"), JSON.toJSONString(vo), appId);
            log.info("::{}::data:{}",linkId,data);
        }catch (Exception e){
            log.error("::{}::获取用户信息:{}",linkId,e);
            return Result.fail("获取用户信息异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "体育玩家玩法统计")
    @RequestMapping(value = "/userPlay", method = {RequestMethod.POST})
    public Result<String> userPlay(@RequestBody @Valid UserPlayReqVo vo) {
        String linkId = "userPlay" + vo.getUserId();
        log.info("::{}::start体育玩家玩法统计 UserPlayReqVo:{}",linkId,JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/userPlay"), JSON.toJSONString(vo), appId);
            log.info("::{}::data:{}",linkId,data);
        }catch (Exception e){
            log.error("::{}::体育玩家玩法统计:{}",linkId,e);
            return Result.fail("体育玩家玩法统计异常");
        }
        return Result.succes(data);
    }


    @ApiOperation(value = "体育联赛投注统计")
    @RequestMapping(value = "/userTournament", method = {RequestMethod.POST})
    public Result<String> userTournament(@RequestBody @Valid UserTournamentReqVo vo) {
        String linkId = "userTournament" + vo.getUserId();
        log.info("::{}::start体育联赛投注统计 UserTournamentReqVo:{}",linkId,JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/userTournament"), JSON.toJSONString(vo), appId);
            log.info("::{}::data:{}",linkId,data);
        }catch (Exception e){
            log.error("::{}::体育联赛投注统计:{}",linkId,e);
            return Result.fail("体育联赛投注统计异常");
        }
        return Result.succes(data);
    }


    /**
     * 危险类型列表
     * @return
     */
    @GetMapping(value = "/userDangerTypeList")
    public Result<DictionaryVO> userDangerTypeList() {
        String linkId = "userDangerTypeList";
        DictionaryVO vo = new DictionaryVO();
        vo.setParentName("riskType");
        Result<DictionaryVO> resp = null;
        try {
            String data = HttpUtil.post(urlRiskPrefix.concat("/tyDictionary/getDictionaryByParentName"), JSON.toJSONString(vo), appId);
            resp = JSONObject.parseObject(data, Result.class);
            log.info("::{}::危险类型列表data:{}",linkId, data);
        }catch (Exception e){
            log.error("::{}::危险类型列表",linkId,e);
            resp.setMsg("危险类型列表异常");
            return resp;
        }
        return resp;
    }


    @ApiOperation(value = "危险投注统计接口 - 1800")
    @RequestMapping(value = "/userAbnormalBetStatistic", method = {RequestMethod.POST})
    public Result<String> userAbnormalBetStatistic(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 体育联赛投注统计 userAbnormalBetStatistic:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/userAbnormalBetStatistic"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("危险投注统计接口" + e);
            return Result.fail("危险投注统计异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险投注统计详情 - 1800")
    @RequestMapping(value = "/userAbnormalBetStatisticDetail", method = {RequestMethod.POST})
    public Result<String> userAbnormalBetStatisticDetail(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 危险投注统计详情 userAbnormalBetStatisticDetail:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/userAbnormalBetStatisticDetail"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("危险投注统计详情" + e);
            return Result.fail("危险投注统计详情异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "危险投注统计详情导出 - 1800")
    @RequestMapping(value = "/userAbnormalBetStatisticDetailExport", method = {RequestMethod.GET})
    public Result<String> userAbnormalBetStatisticDetailExport(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 危险投注统计详情导出 userAbnormalBetStatisticDetailExport:" + JSON.toJSONString(vo));

        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/userAbnormalBetStatisticDetailExport"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("危险投注统计详情导出" + e);
            return Result.fail("危险投注统计详情导出异常");
        }
        return Result.succes(data);
    }

    @ApiOperation(value = "体育用户访问记录-关联用户 - 1800")
    @RequestMapping(value = "/relativeUserInfo", method = {RequestMethod.POST})
    public Result<String> relativeUserInfo(@RequestBody @Valid QueryUserGroupWarnVo vo) {
        log.info("start 危险投注统计详情导出 relativeUserInfo:" + JSON.toJSONString(vo));
        String data;
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserPortrait/relativeUserInfo"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
        }catch (Exception e){
            log.error("体育用户访问记录-关联用户" + e);
            return Result.fail("体育用户访问记录-关联用户异常");
        }
        return Result.succes(data);
    }

}
