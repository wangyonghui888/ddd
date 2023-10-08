package com.panda.sport.rcs.credit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSeriesLimit;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSinglePlayLimit;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.mapper.RcsOperateMerchantsSetMapper;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSeriesLimitService;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSinglePlayLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : TODO
 * @Author : Paca
 * @Date : 2021-06-10 19:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "/credit/job")
public class JobController {

    @Autowired
    private RcsOperateMerchantsSetMapper rcsOperateMerchantsSetMapper;
    @Autowired
    private RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;

    @Autowired
    private RcsCreditSeriesLimitService rcsCreditSeriesLimitService;
    @Autowired
    private RcsCreditSinglePlayLimitService rcsCreditSinglePlayLimitService;

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/initUserCommonLimit")
    public Response<Boolean> initUserCommonLimit(@RequestParam("merchantId") Long merchantId) {
//        long merchantId = 1402279098864242688L;
        // 全部标记为0
        RcsOperateMerchantsSet entity = new RcsOperateMerchantsSet();
        entity.setValidStatus(0);
        LambdaUpdateWrapper<RcsOperateMerchantsSet> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(RcsOperateMerchantsSet::getCreditParentId, merchantId);
        int updateRow = rcsOperateMerchantsSetMapper.update(entity, updateWrapper);
        log.info("全部标记为0的行数：" + updateRow);

        updateRow = 0;
        Long userId = -1L;
        // 查询用户通用限额默认值
        List<RcsCreditSeriesLimit> defaultSeriesLimitList = rcsCreditSeriesLimitService.querySeriesLimit(0L, "0", userId);
        // 查询用户通用限额默认值
        List<RcsCreditSinglePlayLimit> defaultSinglePlayLimitList = rcsCreditSinglePlayLimitService.querySinglePlayLimit(0L, "0", userId);
        while (true) {
            // 每次查询100个代理
            LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RcsOperateMerchantsSet::getCreditParentId, merchantId)
                    .eq(RcsOperateMerchantsSet::getValidStatus, 0)
                    .last("LIMIT 100");
            List<RcsOperateMerchantsSet> creditList = rcsOperateMerchantsSetMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(creditList)) {
                break;
            }
            List<String> creditIdList = creditList.stream().map(RcsOperateMerchantsSet::getMerchantsId).collect(Collectors.toList());
            creditIdList.forEach(creditId -> {
                if (CollectionUtils.isNotEmpty(defaultSeriesLimitList)) {
                    defaultSeriesLimitList.forEach(config -> {
                        config.setMerchantId(merchantId);
                        config.setCreditId(creditId);
                        config.setCreateTime(null);
                        config.setUpdateTime(null);
                    });
                    // 设置代理用户通用限额
                    rcsCreditSeriesLimitService.batchInsertOrUpdate(defaultSeriesLimitList);
                    // 缓存
                    Map<String, String> hashMap = defaultSeriesLimitList.stream().collect(Collectors.toMap(config -> String.valueOf(config.getSeriesType()), config -> config.getValue().toPlainString()));
                    String key = CreditRedisKey.Limit.getUserSeriesKey(merchantId, creditId, userId);
                    redisUtils.hmset(key, hashMap);
                    log.info("用户串关通用限额缓存：key={},hashMap={}", key, hashMap);
                }

                if (CollectionUtils.isNotEmpty(defaultSinglePlayLimitList)) {
                    defaultSinglePlayLimitList.forEach(config -> {
                        config.setMerchantId(merchantId);
                        config.setCreditId(creditId);
                        config.setCreateTime(null);
                        config.setUpdateTime(null);
                    });
                    // 设置代理用户通用限额
                    rcsCreditSinglePlayLimitService.batchInsertOrUpdate(defaultSinglePlayLimitList);
                    // 缓存
                    Map<Integer, List<RcsCreditSinglePlayLimit>> mapBySportId = defaultSinglePlayLimitList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getSportId));
                    mapBySportId.forEach((sportId, playClassifyList) -> {
                        Map<Integer, List<RcsCreditSinglePlayLimit>> mapByPlayClassify = playClassifyList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getPlayClassify));
                        mapByPlayClassify.forEach((playClassify, betStageList) -> {
                            Map<String, List<RcsCreditSinglePlayLimit>> mapByBetStage = betStageList.stream().collect(Collectors.groupingBy(RcsCreditSinglePlayLimit::getBetStage));
                            mapByBetStage.forEach((betStage, list) -> {
                                Map<String, String> hashMap = list.stream().collect(Collectors.toMap(config -> String.valueOf(config.getTournamentLevel()), config -> CommonUtils.toString(config.getValue())));
                                String key = CreditRedisKey.Limit.getUserSinglePlayKey(merchantId, creditId, userId, sportId, playClassify, betStage);
                                redisUtils.hmset(key, hashMap);
                                log.info("用户玩法通用限额缓存：key={},hashMap={}", key, hashMap);
                            });
                        });
                    });
                }
            });

            // 已完成的标记为1
            entity = new RcsOperateMerchantsSet();
            entity.setValidStatus(1);
            updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(RcsOperateMerchantsSet::getCreditParentId, merchantId)
                    .in(RcsOperateMerchantsSet::getMerchantsId, creditIdList);
            int row = rcsOperateMerchantsSetMapper.update(entity, updateWrapper);
            log.info("已完成的标记为1的行数：" + row);
            updateRow += row;
        }
        log.info("标记为1的总行数：" + updateRow);

        return Response.success(true);
    }

    @GetMapping("/initMerchantLimit")
    public Response<Boolean> initMerchantLimit(@RequestParam("merchantId") Long merchantId, @RequestParam("limit") Long limit) {
        // 全部标记为0
        RcsOperateMerchantsSet entity = new RcsOperateMerchantsSet();
        entity.setValidStatus(0);
        LambdaUpdateWrapper<RcsOperateMerchantsSet> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(RcsOperateMerchantsSet::getCreditParentId, merchantId);
        int updateRow = rcsOperateMerchantsSetMapper.update(entity, updateWrapper);
        log.info("全部标记为0的行数：" + updateRow);

        updateRow = 0;
        int updateRow2 = 0;
        BigDecimal ratio = new BigDecimal("0.1");
        while (true) {
            // 每次查询100个代理
            LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RcsOperateMerchantsSet::getCreditParentId, merchantId)
                    .eq(RcsOperateMerchantsSet::getValidStatus, 0)
                    .last("LIMIT 100");
            List<RcsOperateMerchantsSet> creditList = rcsOperateMerchantsSetMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(creditList)) {
                break;
            }
            List<String> creditIdList = creditList.stream().map(RcsOperateMerchantsSet::getMerchantsId).collect(Collectors.toList());
            // 将100个代理单日限额设置成1000万
            RcsQuotaBusinessLimit businessLimit = new RcsQuotaBusinessLimit();
            businessLimit.setBusinessSingleDayLimitProportion(ratio);
            businessLimit.setBusinessSingleDayLimit(limit);
            LambdaUpdateWrapper<RcsQuotaBusinessLimit> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
            lambdaUpdateWrapper.in(RcsQuotaBusinessLimit::getBusinessId, creditIdList);
            int result = rcsQuotaBusinessLimitMapper.update(businessLimit, lambdaUpdateWrapper);
            updateRow2 += result;

            // 删除缓存
            String[] fields = creditIdList.toArray(new String[0]);
            for (String field : fields) {
                redisUtils.del(CreditRedisKey.BUSINESS_LIMIT_KEY  + field);
            }

            // 已完成的标记为1
            entity = new RcsOperateMerchantsSet();
            entity.setValidStatus(1);
            updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(RcsOperateMerchantsSet::getCreditParentId, merchantId)
                    .in(RcsOperateMerchantsSet::getMerchantsId, creditIdList);
            int row = rcsOperateMerchantsSetMapper.update(entity, updateWrapper);
            log.info("已完成的标记为1的行数：{},{}", row, result);
            updateRow += row;
        }
        log.info("标记为1的总行数：{},{}", updateRow, updateRow2);

        return Response.success(true);
    }
}
