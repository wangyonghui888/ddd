package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.mq.bean.RcsMerchantsHideRangeConfigDto;
import com.panda.sport.rcs.mgr.paid.annotion.BusinessLog;
import com.panda.sport.rcs.mgr.utils.IPUtil;
import com.panda.sport.rcs.mgr.wrapper.IMerchantsSinglePercentageService;
import com.panda.sport.rcs.mgr.wrapper.RcsMerchantsHideRangeConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaBusinessLimitService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MerchantsSinglePercentage;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitLogReqVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitReqVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitVo;
import com.panda.sport.rcs.service.IRcsTournamentTemplateService;
import com.panda.sport.rcs.vo.HttpResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author :  kimi
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :  商户限额管理
 * @Date: 2020-09-04 14:56
 * @ModificationHistory Who    When    What
 */
@Slf4j
@RestController
@RequestMapping(value = "/rcsQuotaBusinessLimit")
public class RcsQuotaBusinessLimitController {

    @Autowired
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;

    @Autowired
    private ProducerSendMessageUtils sendMessage;
    @Autowired
    private RedisClient redisClient;

    @Autowired
    IMerchantsSinglePercentageService merchantsSinglePercentageService;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    IRcsTournamentTemplateService rcsTournamentTemplateService;
    @Autowired
    RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;

    @Autowired
    private RcsMerchantsHideRangeConfigService rcsMerchantsHideRangeConfigService;


    /**
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List < com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit>>
     * @Description //前端获取商户限额管理页面数据
     * @Param []
     * @Author kimi
     * @Date 2020/9/6
     **/
    @Deprecated
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public HttpResponse<RcsQuotaBusinessLimitVo> getList(Integer current, Integer size) {
        try {
            if (current == null) {
                current = 1;
            }
            if (size == null) {
                size = 10;
            }
            return rcsQuotaBusinessLimitService.getList(current, size);
        } catch (Exception e) {
            log.error("::rcsQuotaBusinessLimit:: getList ERROR{}", e.getMessage(), e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * 限额配置列表
     *
     * @param reqVo
     * @return
     */
    @PostMapping("/limitConfigList")
    public HttpResponse<RcsQuotaBusinessLimitVo> limitConfigList(@RequestBody RcsQuotaBusinessLimitReqVo reqVo) {
        try {
            log.info("限额配置列表：请求参数："+JSON.toJSONString(reqVo));
            return rcsQuotaBusinessLimitService.limitConfigList(reqVo);
        } catch (Exception e) {
            log.error("limitConfigList 限额配置列表异常{}", e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * 商户通用设置日志列表
     *
     * @param reqVo
     * @return
     */
    @PostMapping("/limitConfigLogList")
    public HttpResponse<RcsQuotaBusinessLimitVo> limitConfigLogList(@RequestBody RcsQuotaBusinessLimitLogReqVo reqVo) {
        try {
            return rcsQuotaBusinessLimitService.limitConfigLogList(reqVo);
        } catch (Exception e) {
            log.error("::limitConfigLogList:: 商户通用设置日志列表异常{}", e.getMessage(), e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    @RequestMapping(value = "/getBusinessLRiskStatus", method = RequestMethod.GET)
    public HttpResponse<RcsQuotaBusinessLimit> getgetBusinessLRiskStatusList(Long userId) {
        try {
            return rcsQuotaBusinessLimitService.getgetBusinessLRiskStatusList(userId);
        } catch (Exception e) {
            log.error("::getBusinessLRiskStatus:: ERROR{}", e.getMessage(), e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List < com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit>>
     * @Description //前端获取商户限额管理页面数据
     * @Param []
     * @Author kimi
     * @Date 2020/9/6
     **/
    @RequestMapping(value = "/getSubList", method = RequestMethod.GET)
    public HttpResponse<RcsQuotaBusinessLimitVo> getSubList(Integer current, Integer size, Long businessId, String agentId, String agentName) {
        try {
            if (businessId == null) {
                throw new RuntimeException("参数businessId 为空！");
            }
            if (current == null) {
                current = 1;
            }
            if (size == null) {
                size = 10;
            }
            log.info("::{}::获取商户{}下的代理：{}--{}", "getSubList" + businessId, agentId, agentName);
            String[] agentIds = null;
            String[] agentNames = null;
            if (StringUtils.isNotEmpty(agentId)) {
                agentIds = agentId.split(",");
            }
            if (StringUtils.isNotEmpty(agentName)) {
                agentNames = agentName.split(",");
            }

            HttpResponse<RcsQuotaBusinessLimitVo> response = rcsQuotaBusinessLimitService.getSubList(current, size, businessId, agentIds, agentNames);
            if (response == null) {
                response = new HttpResponse<>();
            }
            return response;
        } catch (Exception e) {
            log.error("::getSubList:: ERROR{}", e.getMessage(), e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List < com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit>>
     * @Description 更新商户限额管理数据
     * @Param [rcsQuotaBusinessLimit]
     * @Author kimi
     * @Date 2020/9/6
     **/
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public HttpResponse<RcsQuotaBusinessLimit> update(@RequestBody RcsQuotaBusinessLimit rcsQuotaBusinessLimit, HttpServletRequest request) {
        try {
            log.info("::{}::更新商户限额管理数据 入参：{}", rcsQuotaBusinessLimit.getBusinessId(), JSON.toJSONString(rcsQuotaBusinessLimit));
            HttpResponse<RcsQuotaBusinessLimit> validation = validation(rcsQuotaBusinessLimit);
            if (validation != null) {
                return validation;
            }
            if (null == rcsQuotaBusinessLimit.getGamingBetPercent()) {
                rcsQuotaBusinessLimit.setGamingBetPercent(new BigDecimal(100));
            }
            //按商户id缓存这个电竞货量比
            redisClient.set(String.format("rcs:gaming:volume:percentage:%s", rcsQuotaBusinessLimit.getBusinessId()), new BigDecimal(100).subtract(rcsQuotaBusinessLimit.getGamingBetPercent()));
            //redisClient.hSet(Constants.RCS_QUOTA_BUSINESS_LIMIT, rcsQuotaBusinessLimit.getBusinessId(), JSON.toJSONString(rcsQuotaBusinessLimit));
            // 比例百分比
            BigDecimal businessSingleDayLimitProportion = rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion().divide(Constants.BASE);
            BigDecimal businessSingleDaySeriesLimitProportion = rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimitProportion().divide(Constants.BASE);
            rcsQuotaBusinessLimit.setBusinessSingleDayLimitProportion(businessSingleDayLimitProportion);
            rcsQuotaBusinessLimit.setBusinessSingleDaySeriesLimitProportion(businessSingleDaySeriesLimitProportion);

            BigDecimal businessSingleDayGameProportion = rcsQuotaBusinessLimit.getBusinessSingleDayGameProportion().divide(Constants.BASE);
            rcsQuotaBusinessLimit.setBusinessSingleDayGameProportion(businessSingleDayGameProportion);
            //商户货量比例
            String businessBetPercentKey = RcsConstant.RCS_TRADE_BUSINESS_BET_PERCENT;
            if (rcsQuotaBusinessLimit.getBusinessBetPercent() != null) {
                BigDecimal businessBetPercent = rcsQuotaBusinessLimit.getBusinessBetPercent().divide(Constants.BASE);
                rcsQuotaBusinessLimit.setBusinessBetPercent(businessBetPercent);
                redisClient.set(businessBetPercentKey + rcsQuotaBusinessLimit.getBusinessId(), businessBetPercent.toString());
            } else {
                redisClient.delete(businessBetPercentKey + rcsQuotaBusinessLimit.getBusinessId());
            }
            //用户串关限额比例
            BigDecimal userStrayQuotaRatio = rcsQuotaBusinessLimit.getUserStrayQuotaRatio().divide(Constants.BASE);
            rcsQuotaBusinessLimit.setUserQuotaRatio(rcsQuotaBusinessLimit.getUserQuotaRatio().divide(Constants.BASE));
            rcsQuotaBusinessLimit.setUserQuotaBetRatio(rcsQuotaBusinessLimit.getUserQuotaBetRatio().divide(Constants.BASE));
            rcsQuotaBusinessLimit.setUserStrayQuotaRatio(userStrayQuotaRatio);
            rcsQuotaBusinessLimit.setBusinessSingleDayLimit(Constants.BUSINESS_SINGLE_DAY_LIMIT.multiply(rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion()).longValue());
            // 串关限额：限额百分比 * 基数一亿
            rcsQuotaBusinessLimit.setBusinessSingleDaySeriesLimit(Constants.BUSINESS_SINGLE_DAY_LIMIT.multiply(rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimitProportion()).longValue());
            BigDecimal championBusinessProportion = rcsQuotaBusinessLimit.getChampionBusinessProportion().divide(Constants.BASE);
            rcsQuotaBusinessLimit.setChampionBusinessProportion(championBusinessProportion);
            BigDecimal championUserProportion = rcsQuotaBusinessLimit.getChampionUserProportion().divide(Constants.BASE);
            rcsQuotaBusinessLimit.setChampionUserProportion(championUserProportion);
            //需求优化 bug-37387 开放 信用商户限额比例设置
            String key = String.format(Constants.RCS_TRADE_CREDIT_BUSINESS_BET_PERCENT, rcsQuotaBusinessLimit.getFatherId());
            if (rcsQuotaBusinessLimit.getCreditBetRatio() != null && rcsQuotaBusinessLimit.getCreditName() != null) {
                redisClient.hSet(key, rcsQuotaBusinessLimit.getCreditName(), rcsQuotaBusinessLimit.getCreditBetRatio().toString());
            }
            //查一下数据库老数据，用来做日志对比
            RcsQuotaBusinessLimit dbRcsQuotaBusinessLimit = rcsQuotaBusinessLimitService.getById(rcsQuotaBusinessLimit.getId());

            boolean updateFlag = rcsQuotaBusinessLimitService.updateById(rcsQuotaBusinessLimit);

            //如果前端设置为null则存入数据库也为null(updateById达不到该效果)
            if (null == rcsQuotaBusinessLimit.getBusinessBetPercent() || rcsQuotaBusinessLimit.getBusinessBetPercent().equals("")) {
                LambdaUpdateWrapper<RcsQuotaBusinessLimit> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(RcsQuotaBusinessLimit::getBusinessBetPercent, null);
                lambdaUpdateWrapper.eq(RcsQuotaBusinessLimit::getId, rcsQuotaBusinessLimit.getId());
                updateFlag = rcsQuotaBusinessLimitService.update(lambdaUpdateWrapper);
            }
            //日志中添加操作IP
            rcsQuotaBusinessLimit.setIp(IPUtil.getRequestIp(request));
            //数据修改日志记录
            //new Thread(()->{
            rcsQuotaBusinessLimitService.addRcsQuotaBusinessLimitLog(rcsQuotaBusinessLimit, dbRcsQuotaBusinessLimit);
            //}).start();
            RcsQuotaBusinessLimit rcsQuotaBusinessLimitNew = rcsQuotaBusinessLimitService.getById(rcsQuotaBusinessLimit.getId());
            //添加操作IP
            rcsQuotaBusinessLimitNew.setIp(IPUtil.getRequestIp(request));

            //dev-2088
            //若是与原来的值不一样，则重置预警次数
            if (dbRcsQuotaBusinessLimit != null && rcsQuotaBusinessLimitNew != null) {
                Long oldSingleDayLimitProportion = dbRcsQuotaBusinessLimit.getBusinessSingleDayLimit();
                Long oldSingleDaySeriesLimitProportion = dbRcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimit();
                Long newSingleDayLimitProportion = rcsQuotaBusinessLimitNew.getBusinessSingleDayLimit();
                Long newSingleDaySeriesLimitProportion = rcsQuotaBusinessLimitNew.getBusinessSingleDaySeriesLimit();
                String dateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
                String MERCHANT_DAILY_ALERT_KEY = "rcs:sdk:over:num:daily:%s:warnType:%s:merchantsId:%s";
                if (oldSingleDayLimitProportion != null && newSingleDayLimitProportion != null && oldSingleDayLimitProportion.compareTo(newSingleDayLimitProportion) != 0) {
                    log.info("::{}::更新商户限额-单关单日限额比例，old:{},new:{}", rcsQuotaBusinessLimit.getBusinessId(), oldSingleDayLimitProportion, newSingleDayLimitProportion);
                    String alertKey80 = String.format(MERCHANT_DAILY_ALERT_KEY, dateExpect, 0, rcsQuotaBusinessLimitNew.getBusinessId()) + ":80";
                    String alertKey100 = String.format(MERCHANT_DAILY_ALERT_KEY, dateExpect, 0, rcsQuotaBusinessLimitNew.getBusinessId()) + ":100";
                    //重置单关商户单日预警次数
                    redisClient.delete(alertKey80);
                    redisClient.delete(alertKey100);
                }
                if (oldSingleDaySeriesLimitProportion != null && newSingleDaySeriesLimitProportion != null && oldSingleDaySeriesLimitProportion.compareTo(newSingleDaySeriesLimitProportion) != 0) {
                    log.info("::{}::更新商户限额-串关单日限额比例，old:{},new:{}", rcsQuotaBusinessLimit.getBusinessId(), oldSingleDayLimitProportion, newSingleDayLimitProportion);
                    String alertKey80 = String.format(MERCHANT_DAILY_ALERT_KEY, dateExpect, 1, rcsQuotaBusinessLimitNew.getBusinessId()) + ":80";
                    String alertKey100 = String.format(MERCHANT_DAILY_ALERT_KEY, dateExpect, 1, rcsQuotaBusinessLimitNew.getBusinessId()) + ":100";
                    //重置串关商户单日预警次数
                    redisClient.delete(alertKey80);
                    redisClient.delete(alertKey100);
                }
            }

            //转换商户限额为分
            rcsQuotaBusinessLimitNew.setBusinessSingleDayLimit(rcsQuotaBusinessLimitNew.getBusinessSingleDayLimit() * 100);
            rcsQuotaBusinessLimitNew.setUserSingleStrayLimit(Objects.nonNull(rcsQuotaBusinessLimitNew.getUserSingleStrayLimit()) ? rcsQuotaBusinessLimitNew.getUserSingleStrayLimit() * 100 : null);
            if (rcsQuotaBusinessLimitNew.getBusinessSingleDaySeriesLimit() == null) {
                rcsQuotaBusinessLimitNew.setBusinessSingleDaySeriesLimit(rcsQuotaBusinessLimitNew.getBusinessSingleDayLimit() / 2);
            } else {
                rcsQuotaBusinessLimitNew.setBusinessSingleDaySeriesLimit(rcsQuotaBusinessLimitNew.getBusinessSingleDaySeriesLimit() * 100);
            }
            String businessKey = String.format(Constants.MERCHANT_LIMIT_KEY, rcsQuotaBusinessLimitNew.getBusinessId());
            redisClient.setExpiry(businessKey, rcsQuotaBusinessLimitNew, 30 * 24 * 60 * 60L);
            //商户风控开关 设置缓存
            // redisClient.hSet("rcs:riskstatus:merchants:data", rcsQuotaBusinessLimit.getBusinessName(), rcsQuotaBusinessLimit.getRiskStatus() + "");
            //投注货量动态风控开关 设置缓存
            redisClient.set(RcsConstant.RCS_RISK_BET_VOLUME_SWITCH + rcsQuotaBusinessLimit.getBusinessId(), rcsQuotaBusinessLimit.getBetVolumeStatus());

//            LimitCacheClearVo limitCacheClearVo = new LimitCacheClearVo();
//            limitCacheClearVo.setDataType(DataTypeEnum.MERCHANTS.getType());
//            limitCacheClearVo.setBusinessId(rcsQuotaBusinessLimit.getBusinessId());
//            String tag = String.valueOf(limitCacheClearVo.getDataType());
//            sendMessage.sendMessage("RCS_LIMIT_CACHE_CLEAR_TOPIC", tag, rcsQuotaBusinessLimit.getBusinessId(), limitCacheClearVo);

            //修改商户限额的时候，把最新的 商户行情开关状态发给业务那边
            sendMessage.sendMessage("RCS_BUSINESS_TAG_MARKET_STATUS_TOPIC", rcsQuotaBusinessLimit);

            //1782 应业务方代码耦合问题，对于改需求的配置重新发送新的topic
            sendMessage.sendMessage("rcs_merchant_tag_market_level_status", rcsQuotaBusinessLimit);

            redisClient.hSet(Constants.RCS_QUOTA_BUSINESS_LIMIT, rcsQuotaBusinessLimit.getBusinessId(), JSON.toJSONString(rcsQuotaBusinessLimit));
            String redisKey = String.format(RedisKey.MERCHANT_LIMIT_KEY, rcsQuotaBusinessLimitNew.getBusinessId());
            JSONObject json = new JSONObject();
            json.put("key", redisKey);
            json.put("value", rcsQuotaBusinessLimitNew);
            sendMessage.sendMessage("rcs_order_limit_cache_update", "", redisKey, json);
            return HttpResponse.success(rcsQuotaBusinessLimit);
        } catch (Exception e) {
            log.error("::{}::更新商户限额管理数据 ERROR{}", rcsQuotaBusinessLimit.getBusinessId(), e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * @return java.lang.Boolean
     * @Description 验证参数
     * @Param [rcsQuotaBusinessLimit]
     * @Author kimi
     * @Date 2020/10/3
     **/
    public HttpResponse<RcsQuotaBusinessLimit> validation(RcsQuotaBusinessLimit rcsQuotaBusinessLimit) {
        if (rcsQuotaBusinessLimit.getId() == null) {
            log.warn("::{}:: id不能为空", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("id不能为空");

        }
        if (rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion() == null) {
            log.warn("::{}:: 商户单日限额比例不能为空", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("商户单日限额比例不能为空");
        }
        BigDecimal businessSingleDayLimitProportion = rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion().divide(Constants.BASE);
        if (Constants.MIN_PROPORTION.compareTo(businessSingleDayLimitProportion) > 0 || Constants.MAX_PROPORTION.compareTo(businessSingleDayLimitProportion) < 0) {
            log.warn("::{}::商户单场限额比例超出范围范围是0.0001-10", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("商户单场限额比例超出范围,自定义输入范围0.01-1000");
        }
        if (rcsQuotaBusinessLimit.getBusinessSingleDayGameProportion() == null) {
            log.warn("::{}::商户单场限额比例不能为空", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("商户单场限额比例不能为空");
        }
        if (rcsQuotaBusinessLimit.getTagMarketLevelIdPc() == null) {
            log.warn("::{}:: PC赔率等级不能为空", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("PC赔率等级不能为空");
        }
        if (rcsQuotaBusinessLimit.getTagMarketLevelId() == null) {
            log.warn("::{}:: 其他设备等级不能为空", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("其他设备等级不能为空");
        }
        if (rcsQuotaBusinessLimit.getUserQuotaRatio() == null) {
            log.warn("::{}::用户限额比例不能为空", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("用户限额比例不能为空");
        }
        BigDecimal userQuotaRatio = rcsQuotaBusinessLimit.getUserQuotaRatio().divide(Constants.BASE);
        if (Constants.MIN_PROPORTION.compareTo(userQuotaRatio) > 0 || Constants.MAX_PROPORTION.compareTo(userQuotaRatio) < 0) {
            log.warn("::{}::用户限额比例超出范围范围是0.0001-10", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("用户限额比例超出范围,自定义输入范围0.01-1000");
        }
        BigDecimal creditBetRatio = rcsQuotaBusinessLimit.getCreditBetRatio();
        if (creditBetRatio != null) {
            creditBetRatio = creditBetRatio.divide(Constants.BASE);
            rcsQuotaBusinessLimit.setCreditBetRatio(creditBetRatio);
            if (creditBetRatio.compareTo(BigDecimal.ZERO) <= 0 || creditBetRatio.compareTo(BigDecimal.ONE) > 0) {
                log.warn("::{}::信用单注限额比例超出范围0-100%", rcsQuotaBusinessLimit.getBusinessId());
                return HttpResponse.failToMsg("信用单注限额比例超出范围0-100%");
            }
        }
        //需求：赛种默认为空，如果延时数值不为空、点击保存时需检查，赛种如果未选择，则不允许保存
        if (org.springframework.util.StringUtils.isEmpty(rcsQuotaBusinessLimit.getDelay()) && !CollectionUtils.isEmpty(rcsQuotaBusinessLimit.getSportIdList())) {
            log.warn("::{}::延迟时间不能为空", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("延迟时间不能为空");
        }
        if (!org.springframework.util.StringUtils.isEmpty(rcsQuotaBusinessLimit.getDelay()) && CollectionUtils.isEmpty(rcsQuotaBusinessLimit.getSportIdList())) {
            log.warn("::{}::请选择赛种", rcsQuotaBusinessLimit.getBusinessId());
            return HttpResponse.failToMsg("请选择赛种");
        }
        rcsQuotaBusinessLimit.setSportIds(StringUtils.join(rcsQuotaBusinessLimit.getSportIdList(), ","));
        return null;
    }

    /**
     * 获取渠道商code列表
     *
     * @return
     */
    @GetMapping("/queryParentName")
    public HttpResponse<String> queryParentName() {
        try {
            return HttpResponse.success(rcsQuotaBusinessLimitService.queryParentName());
        } catch (Exception e) {
            log.error("::queryParentName:: 获取渠道商code列表异常{}", e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * 需求优化 bug-37387 开放 信用商户通用货量百分比设置
     *
     * @param rate
     * @return
     */
    @RequestMapping(value = "/updateRate", method = RequestMethod.POST)
    public HttpResponse<Long> updateRate(Long rate) {
        try {
            log.info("::updateRate::更新商户通用货量百分比入参：{}", rate);
            BigDecimal percent = new BigDecimal(rate).divide(Constants.BASE);
            redisClient.set(Constants.RCS_TRADE_CREDIT_BUSINESS_VOLUME_PERCENT, new BigDecimal(rate).divide(Constants.BASE));
            return HttpResponse.success(percent);
        } catch (Exception e) {
            log.error("::updateRate:: ERROR：{}", e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * 获取商户通用货量百分比设置
     *
     * @return
     */
    @RequestMapping(value = "/queryRate", method = RequestMethod.GET)
    public HttpResponse<Long> queryRate() {
        try {
            return HttpResponse.success(redisClient.get(Constants.RCS_TRADE_CREDIT_BUSINESS_VOLUME_PERCENT));
        } catch (Exception e) {
            log.error("::queryRate:: ERROR：{}", e.getMessage());
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * 刷新预警信息
     *
     * @param businessId
     * @param businessSingleDayGameProportion
     */
    public void refreshMerchantsSinglePercentageWaring(String businessId, BigDecimal businessSingleDayGameProportion) {
        LambdaQueryWrapper<MerchantsSinglePercentage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantsSinglePercentage::getMerchantsId, businessId);
        queryWrapper.eq(MerchantsSinglePercentage::getStatus, 1);
        queryWrapper.eq(MerchantsSinglePercentage::getMatchId, 3398503);
        List<MerchantsSinglePercentage> merchantsSinglePercentages = merchantsSinglePercentageService.list(queryWrapper);
        if (CollectionUtils.isEmpty(merchantsSinglePercentages)) {
            log.warn("::{}::更新商户单场限额监控预警，记录未查到", businessId);
            return;
        }
        //String matchInfoKey = "rcs:redis:standard:match:%s";
        for (MerchantsSinglePercentage e : merchantsSinglePercentages) {
            //获取赛事开赛时间
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(e.getMatchId());
            if (ObjectUtils.isEmpty(standardMatchInfo) || standardMatchInfo.getBeginTime() == null) {
                log.warn("::{}::更新商户单场限额监控预警，赛事：{}信息未找到", businessId, e.getMatchId());
                continue;
            }
            String betDateExpect = DateUtils.getDateExpect(standardMatchInfo.getBeginTime());
            //商户赛事单场限额 已使用多少
            StringBuffer sb = new StringBuffer();
            sb.append("RCS:RISK:").append(betDateExpect).append(":").append(e.getMerchantsId()).append(":")
                    .append(e.getSportId()).append(":").append(e.getMatchId()).append(":").append(e.getMatchType().intValue() == 2 ? "1" : "0").append(":V2")
                    .append("_{").append(e.getMerchantsId()).append("_").append(e.getMatchId()).append("}");
            String singleMatchInf = redisClient.hGet(sb.toString(), "MAX_MATCH_PAID");
            BigDecimal use = BigDecimal.ZERO;
            if (StringUtils.isNotBlank(singleMatchInf)) {
                use = new BigDecimal(singleMatchInf).setScale(2, BigDecimal.ROUND_DOWN);
            }
            log.info("::{}::更新商户单场限额监控预警，获取商户对应赛事已用限额:{}，赛种Id：{},赛事Id：{}", businessId, use, e.getSportId(), e.getMatchId());
            //读取最新的配置
            LambdaQueryWrapper<RcsTournamentTemplate> templateWrapper = new LambdaQueryWrapper<>();
            templateWrapper.eq(RcsTournamentTemplate::getSportId, e.getSportId());
            templateWrapper.eq(RcsTournamentTemplate::getType, 3);
            templateWrapper.eq(RcsTournamentTemplate::getTypeVal, e.getMatchId());
            templateWrapper.eq(RcsTournamentTemplate::getMatchType, e.getMatchType());
            RcsTournamentTemplate rcsTournamentTemplate = rcsTournamentTemplateService.getOne(templateWrapper);
            if (rcsTournamentTemplate == null) {
                log.warn("::{}::更新商户单场限额监控预警，商户模板信息未找到,赛种Id:{},模板类型：{}，赛事Id:{},赛事类型：{}", businessId, e.getSportId(), 3, e.getMatchId(), e.getMatchType());
                continue;
            }
            Long businessMatchPayVal = rcsTournamentTemplate.getBusinesMatchPayVal();
            log.info("::{}::更新商户单场限额监控预警，商户最新限额配置:{}，赛种Id：{},赛事Id：{},赛事类型：{}", businessId, businessMatchPayVal, e.getSportId(), e.getMatchId(), e.getMatchType());
            //计算实际限额值
            Long limit = businessSingleDayGameProportion.multiply(new BigDecimal(10000000)).longValue();
            e.setMatchLimit(limit);
            BigDecimal rate = use.divide(new BigDecimal(e.getMatchLimit()), 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);
            e.setPercentage(rate);
            log.info("::{}::更新商户单场限额监控预警，实际限额：{}，已用限额比例:{}", businessId, limit, rate);
            merchantsSinglePercentageService.updateById(e);
            //批量
        }
    }





    @ApiOperation("编辑开关")
    @PostMapping(value = "/editHideStatusList")
   @BusinessLog()
    public HttpResponse editHideStatusList(@RequestBody RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig) {
        log.info("::editHideStatusList::输入参数{}", JSON.toJSONString(rcsMerchantsHideRangeConfig));
        try {
            rcsMerchantsHideRangeConfigService.editHideStatusList(rcsMerchantsHideRangeConfig);
        }catch (RuntimeException e){
            return HttpResponse.failToMsg(e.getMessage());
        }
        return HttpResponse.success();
    }
    @ApiOperation("编辑金额")
    @PostMapping(value = "/editHideMoneyList")
    @BusinessLog()
    public HttpResponse editHideMoneyList(@RequestBody RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig) {
        log.info("::editHideMoneyList::输入参数{}", JSON.toJSONString(rcsMerchantsHideRangeConfig));
        try {
            rcsMerchantsHideRangeConfigService.editHideMoneyList(rcsMerchantsHideRangeConfig);
        }catch (RuntimeException e){
            return HttpResponse.failToMsg(e.getMessage());
        }
        return HttpResponse.success();
    }

}
