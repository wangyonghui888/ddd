package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsOperateMerchantsSetMapper;
import com.panda.sport.rcs.mgr.paid.annotion.BusinessLog;
import com.panda.sport.rcs.pojo.RcsOMerchantsIDCode;
import com.panda.sport.rcs.pojo.RcsOmitConfig;
import com.panda.sport.rcs.pojo.vo.RcsOmitConfigBatchUpdateVo;
import com.panda.sport.rcs.pojo.vo.RcsOmitConfigPageQueryVo;
import com.panda.sport.rcs.pojo.vo.RcsPageQueryVo;
import com.panda.sport.rcs.pojo.vo.RcsSwitchUpdateVo;
import com.panda.sport.rcs.service.RcsOmitConfigService;
import com.panda.sport.rcs.service.RcsSwitchService;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author :  tim
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :  数据商动态漏单管理
 * @Date: 2023-08-04 14:56
 * @ModificationHistory Who    When    What
 */
@Slf4j
@RestController
@RequestMapping(value = "/risk/rcsOmitConfig")
public class RcsOmitConfigController {

    @Autowired
    private RcsOmitConfigService rcsOmitConfigService;

    @Autowired
    private RcsSwitchService rcsSwitchService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsOperateMerchantsSetMapper rcsOperateMerchantsSetMapper;


    /**
     * @return
     * @Description //前端获取数据商动态漏单 管理页面数据
     * @Param []
     * @Author tim
     * @Date 2023-08-04
     **/
    @RequestMapping(value = "/pageList", method = RequestMethod.GET)
    public HttpResponse<RcsPageQueryVo> pageList(RcsOmitConfigPageQueryVo query) {
        try {
            log.info("商户漏单 列表：请求参数："+ JSON.toJSONString(query));
            return rcsOmitConfigService.listPage(query.getCurrentPage(), query.getPageSize(),
                    query.getMerchantsId(), query.getMerchantsCode());
        } catch (Exception e) {
            log.error("RcsOmitConfigController:: pageList ERROR{}", e.getMessage(), e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * 商户漏单 获取默认设置
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/getDefaultConfig",method = RequestMethod.GET)
    public HttpResponse<RcsOmitConfig> getDefaultConfig() {
        try {
            log.info("商户漏单 获取默认设置：：");
            return rcsOmitConfigService.getDefaultConfig();
        } catch (Exception e) {
            log.error("RcsOmitConfigController::getDefaultConfig 商户漏单 获取默认设置接口异常{}", e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * 商户漏单 批量配置
     *
     * @param reqVo
     * @return
     */
    @RequestMapping(value = "/batchUpdateConfig",method = RequestMethod.POST)
    @BusinessLog
    public HttpResponse<?> batchUpdateConfig(@RequestBody RcsOmitConfigBatchUpdateVo reqVo) {
        try {
            log.info("商户漏单 批量配置：请求参数："+ JSON.toJSONString(reqVo));
            HttpResponse<?> validationResult = validation(reqVo);
            if(validationResult != null){
                return validationResult;
            }
            if(reqVo.getType() == 1){
                //批量配置
                return rcsOmitConfigService.batchUpdateConfig(reqVo);
            }else if(reqVo.getType() == 3){
                //例外配置
                return rcsOmitConfigService.exceptUpdateConfig(reqVo);
            } else{
                //默认配置
                return rcsOmitConfigService.defaultUpdateConfig(reqVo);
            }


        } catch (Exception e) {
            log.error("RcsOmitConfigController::batchUpdate 商户漏单批量配置异常{}", e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }


    /**
     * 商户漏单 总开关接口
     *
     * @param reqVo
     * @return
     */
    @RequestMapping(value = "/editSwitch",method = RequestMethod.POST)
    @BusinessLog
    public HttpResponse<?> editSwitch(@RequestBody @Validated RcsSwitchUpdateVo reqVo) {
        try {
            log.info("商户漏单 总开关接口：请求参数："+ JSON.toJSONString(reqVo));
            return rcsSwitchService.editSwitch(reqVo.getStatus());
        } catch (Exception e) {
            log.error("RcsOmitConfigController::editSwitch 商户漏单 总开关接口异常{}", e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }


    /**
     * @return
     * @Description 验证参数
     * @Param [RcsOmitConfigBatchUpdateVo]
     * @Author tim
     * @Date 2023-08-04
     **/
    public HttpResponse<?> validation(RcsOmitConfigBatchUpdateVo reqVo) {
        if (reqVo.getType() == null) {
            log.warn(":::: type不能为空");
            return HttpResponse.failToMsg("type不能为空");
        }
        if (!Stream.of(1,2,3).anyMatch(x -> x.equals(reqVo.getType()))) {
            log.warn(":::: type值错误");
            return HttpResponse.failToMsg("type值错误");
        }

        if(Objects.nonNull(reqVo.getMaxMoney()) && Objects.nonNull(reqVo.getMinMoney())){
            if (reqVo.getMaxMoney() <= reqVo.getMinMoney()) {
                log.warn(":::: 投注额区间错误 ");
                return HttpResponse.failToMsg("投注额区间错误");
            }
        }

        if(Objects.nonNull(reqVo.getVolumePercentage())){
            if (reqVo.getVolumePercentage().compareTo(BigDecimal.valueOf(100)) > 0
                    || reqVo.getVolumePercentage().compareTo(BigDecimal.ZERO) < 0) {
                log.warn(":::: 漏单比例无效 必须0-100之间有效整数或小数");
                return HttpResponse.failToMsg("漏单比例无效 必须0-100之间有效整数或小数");
            }
        }

        if(!CollectionUtils.isEmpty(reqVo.getMerchantIds())){
            List<Long> allMerchantIds = rcsOperateMerchantsSetMapper.getAllMerchantIdAndCode()
                    .stream().map(RcsOMerchantsIDCode::getMerchantsId).collect(toList());
            List<Long> collect = reqVo.getMerchantIds().stream()
                    .map(item -> Long.valueOf(item))
                    .filter(item -> !allMerchantIds.contains(item)).collect(toList());

            collect.removeIf(n -> n.equals(999999999999L));

            if(!CollectionUtils.isEmpty(collect)){
                log.warn(":::: 商户ID错误:{}", JSON.toJSONString(collect));
                return HttpResponse.failToMsg("商户ID错误:" + JSON.toJSONString(collect));
            }
        }


        return null;
    }


}
