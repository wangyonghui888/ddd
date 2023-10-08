package com.panda.sport.rcs.trade.controller;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.annotion.OperateLog;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import com.panda.sport.rcs.pojo.dto.SpecEventChangeDTO;
import com.panda.sport.rcs.pojo.dto.SpecEventConfigDTO;
import com.panda.sport.rcs.pojo.param.RcsSpecEventConfigParam;
import com.panda.sport.rcs.pojo.param.AutoOpenMarketStatusParam;
import com.panda.sport.rcs.pojo.param.UpdateSpecEventStatusParam;
import com.panda.sport.rcs.pojo.resp.RcsSpecEventConfigResp;
import com.panda.sport.rcs.trade.service.RcsSpecEventConfigService;
import com.panda.sport.rcs.trade.service.TradeStatusService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 业务逻辑
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/10 19:29
 */
@Component
@RestController
@RequestMapping(value = "/aoSpecEventConfig")
@Slf4j
public class RcsSpecEventConfigController {

    @Resource
    private RcsSpecEventConfigService rcsSpecEventConfigService;

    @Resource
    private RedisUtils redisUtils;
    @Autowired
    private ProducerSendMessageUtils sendMessage;

    @Autowired
    private TradeStatusService tradeStatusService;

    /**
     * 根据ID修改
     *
     * @param autoOpenMarketParam
     * @return
     */
    @PostMapping("/updateAutoOpenMarketStatus")
    @OperateLog(operateType= OperateLogEnum.OPERATE_AOAUTO_OPEN)
    public HttpResponse<Boolean> updateAutoOpenMarketStatus(@RequestBody AutoOpenMarketStatusParam autoOpenMarketParam) {
        log.info("::{}-{}::修改AO事件配置请求参数：{}", autoOpenMarketParam.getTypeVal(), autoOpenMarketParam.getType(), JsonFormatUtils.toJson(autoOpenMarketParam));
        if (Objects.isNull(autoOpenMarketParam.getSwitchType())) {
            return HttpResponse.error(HttpResponse.FAIL, "开关类型【switchType】不能为空");
        }
        if (autoOpenMarketParam.getSwitchType() == 1) {
            if (Objects.isNull(autoOpenMarketParam.getType())) {
                return HttpResponse.error(HttpResponse.FAIL, "模板类型【type】不能为空");
            }
            if (Objects.isNull(autoOpenMarketParam.getTypeVal())) {
                return HttpResponse.error(HttpResponse.FAIL, "模板类型值【typeVal】不能为空");
            }
        }
        if (Objects.isNull(autoOpenMarketParam.getSwitchStatus())) {
            return HttpResponse.error(HttpResponse.FAIL, "开关参数不能为空");
        }
        return rcsSpecEventConfigService.updateAutoOpenMarketStatus(autoOpenMarketParam);
    }


    /**
     * 根据ID修改
     *
     * @param autoOpenMarketParam
     * @return
     */
    @PostMapping("/getAutoOpenMarketStatus")
    public HttpResponse<Integer> getAutoOpenMarketStatus(@RequestBody AutoOpenMarketStatusParam autoOpenMarketParam) {
        log.info("::{}-{}::修改AO事件配置请求参数：{}", autoOpenMarketParam.getTypeVal(), autoOpenMarketParam.getType(), JsonFormatUtils.toJson(autoOpenMarketParam));
        if (Objects.isNull(autoOpenMarketParam.getSwitchType())) {
            return HttpResponse.error(HttpResponse.FAIL, "开关类型【switchType】不能为空");
        }
        if (autoOpenMarketParam.getSwitchType() == 1) {
            if (Objects.isNull(autoOpenMarketParam.getType())) {
                return HttpResponse.error(HttpResponse.FAIL, "模板类型【type】不能为空");
            }
            if (Objects.isNull(autoOpenMarketParam.getTypeVal())) {
                return HttpResponse.error(HttpResponse.FAIL, "模板类型值【typeVal】不能为空");
            }
        }
        if (Objects.isNull(autoOpenMarketParam.getSwitchStatus())) {
            return HttpResponse.error(HttpResponse.FAIL, "开关参数不能为空");
        }
        return rcsSpecEventConfigService.getAutoOpenMarketStatus(autoOpenMarketParam);
    }


    /**
     * 根据赛事级事件开关
     *
     * @param specEventStatusParam
     * @return
     */
    @PostMapping("/updateSpecEventStatus")
    public HttpResponse<Boolean> updateSpecEventStatus(@RequestBody UpdateSpecEventStatusParam specEventStatusParam) {
        log.info("::{}-{}::修改AO事件配置请求参数：{}", specEventStatusParam.getTypeVal(), specEventStatusParam.getType(), JsonFormatUtils.toJson(specEventStatusParam));
        if (Objects.isNull(specEventStatusParam.getType())) {
            return HttpResponse.error(HttpResponse.FAIL, "模板类型【type】不能为空");
        }
        if (Objects.isNull(specEventStatusParam.getTypeVal())) {
            return HttpResponse.error(HttpResponse.FAIL, "模板类型值【typeVal】不能为空");
        }
        if (Objects.isNull(specEventStatusParam.getMatchIdSwitch())) {
            return HttpResponse.error(HttpResponse.FAIL, "事件开关参数不能为空");
        }
        return rcsSpecEventConfigService.updateSpecEventStatus(specEventStatusParam);
    }


    /**
     * 根据ID修改
     *
     * @param rcsSpecEventConfigParam
     * @return
     */
    @PostMapping("/updateSpecEventConfigById")
    public HttpResponse<Integer> updateSpecEventConfigById(@RequestBody RcsSpecEventConfigParam rcsSpecEventConfigParam) {
        log.info("::{}-{}::修改AO事件配置请求参数：{}", rcsSpecEventConfigParam.getTypeVal(), rcsSpecEventConfigParam.getType(), JsonFormatUtils.toJson(rcsSpecEventConfigParam));
        if (Objects.isNull(rcsSpecEventConfigParam.getType())) {
            return HttpResponse.error(HttpResponse.FAIL, "模板类型【type】不能为空");
        }
        if (Objects.isNull(rcsSpecEventConfigParam.getTypeVal())) {
            return HttpResponse.error(HttpResponse.FAIL, "模板类型值【typeVal】不能为空");
        }
        if (Objects.isNull(rcsSpecEventConfigParam.getId())) {
            return HttpResponse.error(HttpResponse.FAIL, "事件id不能为空");
        }
        return rcsSpecEventConfigService.updateSpecEventConfigById(rcsSpecEventConfigParam);
    }

    /**
     * 查询特殊事件配置列表
     *
     * @param specEventConfigDTO
     * @return
     */
    @PostMapping("/querySpecEventConfigList")
    public HttpResponse<RcsSpecEventConfigResp> querySpecEventConfigList(@RequestBody SpecEventConfigDTO specEventConfigDTO) {
        log.info("::{}-{}::查询AO事件配置列表请求参数：{}", specEventConfigDTO.getTypeVal(), specEventConfigDTO.getType(), JsonFormatUtils.toJson(specEventConfigDTO));
        if (Objects.isNull(specEventConfigDTO.getType())) {
            return HttpResponse.error(HttpResponse.FAIL, "模板类型【type】不能为空");
        }
        if (Objects.isNull(specEventConfigDTO.getTypeVal())) {
            return HttpResponse.error(HttpResponse.FAIL, "模板类型值【typeVal】不能为空");
        }
        try {
            RcsSpecEventConfig rcsSpecEventConfig = new RcsSpecEventConfig();
            rcsSpecEventConfig.setType(specEventConfigDTO.getType());
            rcsSpecEventConfig.setTypeVal(specEventConfigDTO.getTypeVal());
            List<RcsSpecEventConfig> specEventConfigList = rcsSpecEventConfigService.querySpecEventConfigList(rcsSpecEventConfig);
            String specEventStatusKey = String.format(RedisKey.SPECIAL_EVENT_STATUS_KEY, rcsSpecEventConfig.getType(), rcsSpecEventConfig.getTypeVal());
            String switchStr = redisUtils.get(specEventStatusKey);
            if (StringUtil.isEmpty(switchStr)) {
                switchStr = "0";
            }
            RcsSpecEventConfigResp rcsSpecEventConfigResp = new RcsSpecEventConfigResp();
            rcsSpecEventConfigResp.setMatchSpecEventSwitch(Integer.valueOf(switchStr));
            rcsSpecEventConfigResp.setSpecEventList(specEventConfigList);
            return HttpResponse.success(rcsSpecEventConfigResp);
        } catch (Exception e) {
            log.error("::{}-{}::查询AO事件配置异常", specEventConfigDTO.getTypeVal(), specEventConfigDTO.getType(), e);
            return HttpResponse.error(HttpResponse.FAIL, "查询AO事件配置异常");
        }
    }

    /**
     * 操盘手手动确认修改赛事特殊事件
     *
     * @param dto
     * @return
     */
    @PostMapping("changeMatchSpecEvent")
    @OperateLog(operateType= OperateLogEnum.OPERATE_SPECEVENT_SWITCH)
    public HttpResponse changeMatchSpecEvent(@RequestBody SpecEventChangeDTO dto) {
        log.info("操盘手手动确认修改赛事特殊事件请求参数：{}", JsonFormatUtils.toJson(dto));
        if (Objects.isNull(dto.getMatchId()) || dto.getMatchId() <= 0) {
            return HttpResponse.error(HttpResponse.FAIL, "赛事ID不能为null");
        }

        if (Objects.isNull(dto.getEventCode())) {
            return HttpResponse.error(HttpResponse.FAIL, "事件编码[eventCode]不能为null");
        }
        try {
            String linkId = CommonUtils.getLinkId("change_match_spec_event");
            Request request = new Request();
            request.setData(dto);
            request.setGlobalId(linkId);
            String tag = "RCS_MATCH_SPEC_EVENT_CHANGE_" + dto.getMatchId();
            log.info("::{}::发送mq切换赛事特殊事件:Message:{}", linkId, JSONObject.toJSON(request));
            sendMessage.sendMessage("RCS_MATCH_SPEC_EVENT_CHANGE", tag, linkId, JSONObject.toJSON(request));
        } catch (Exception e) {
            log.error("操盘手手动确认修改赛事特殊事件请求参数异常:{}", JsonFormatUtils.toJson(dto), e);
            return HttpResponse.error(HttpResponse.FAIL, "查询AO事件配置异常");
        }
        return HttpResponse.success();
    }


    /**
     * 根据赛事id及事件编码修改
     *
     * @param RcsSpecEventConfig
     * @return
     */
    @PostMapping("/updateActiveByMatchId")
    public HttpResponse<Integer> updateActiveByMatchId(@RequestBody RcsSpecEventConfig RcsSpecEventConfig) {
        log.info("::{}-{}::修改AO事件配置请求参数：{}", RcsSpecEventConfig.getTypeVal(), RcsSpecEventConfig.getEventCode(), JsonFormatUtils.toJson(RcsSpecEventConfig));
        if (Objects.isNull(RcsSpecEventConfig.getTypeVal())) {
            return HttpResponse.error(HttpResponse.FAIL, "赛事ID【typeVal】不能为null");
        }
        if (Objects.isNull(RcsSpecEventConfig.getEventCode())) {
            return HttpResponse.error(HttpResponse.FAIL, "事件编码【eventCode】不能为空");
        }
        return rcsSpecEventConfigService.updateActiveByMatchId(RcsSpecEventConfig);
    }

    /**
     * 根据赛事id及事件编码修改
     *
     * @param RcsSpecEventConfig
     * @return
     */
    @PostMapping("/updateSpecEventProbByMatchId")
    @OperateLog(operateType= OperateLogEnum.OPERATE_SPECEVENT_GOALPROB)
    public HttpResponse<Integer> updateSpecEventProbByMatchId(@RequestBody RcsSpecEventConfig RcsSpecEventConfig) {
        log.info("::{}-{}::修改AO事件配置请求参数：{}", RcsSpecEventConfig.getTypeVal(), RcsSpecEventConfig.getEventCode(), JsonFormatUtils.toJson(RcsSpecEventConfig));
        if (Objects.isNull(RcsSpecEventConfig.getTypeVal())) {
            return HttpResponse.error(HttpResponse.FAIL, "赛事ID【typeVal】不能为null");
        }
        if (Objects.isNull(RcsSpecEventConfig.getEventCode())) {
            return HttpResponse.error(HttpResponse.FAIL, "事件编码【eventCode】不能为空");
        }
        return rcsSpecEventConfigService.updateSpecEventConfigProbByMatchId(RcsSpecEventConfig);
    }



    /**
     * 操盘手手动确认退出赛事特殊事件
     *
     * @param dto
     * @return
     */
    @PostMapping("exitMatchSpecEvent")
    @OperateLog(operateType= OperateLogEnum.OPERATE_SPECEVENT_QUIT)
    public HttpResponse exitMatchSpecEvent(@RequestBody SpecEventChangeDTO dto) {
        log.info("操盘手手动确认退出赛事特殊事件请求参数：{}", JsonFormatUtils.toJson(dto));
        if (Objects.isNull(dto.getMatchId()) || dto.getMatchId() <= 0) {
            return HttpResponse.error(HttpResponse.FAIL, "赛事ID不能为null");
        }

        try {
            String linkId = CommonUtils.getLinkId("exit_match_spec_event");
            Request request = new Request();
            request.setData(dto);
            request.setGlobalId(linkId);
            String tag = "RCS_MATCH_SPEC_EVENT_EXIT_" + dto.getMatchId();
            log.info("::{}::发送mq退出赛事特殊事件:Message:{}", linkId, JSONObject.toJSON(request));
            sendMessage.sendMessage("RCS_MATCH_SPEC_EVENT_EXIT", tag, linkId, JSONObject.toJSON(request));
        } catch (Exception e) {
            log.error("操盘手手动确认修改赛事特殊事件请求参数异常:{}", JsonFormatUtils.toJson(dto), e);
            return HttpResponse.error(HttpResponse.FAIL, "查询AO事件配置异常");
        }
        return HttpResponse.success();
    }
}
