package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsOmitConfigMapper;
import com.panda.sport.rcs.mapper.RcsOperateMerchantsSetMapper;
import com.panda.sport.rcs.mapper.RcsSwitchMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.vo.RcsOmitConfigBatchUpdateVo;
import com.panda.sport.rcs.pojo.vo.RcsOmitConfigVo;
import com.panda.sport.rcs.pojo.vo.RcsPageQueryVo;
import com.panda.sport.rcs.service.RcsOmitConfigService;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKey.REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY;

/**
 * @author :  tim
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-04 15:08
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class RcsOmitConfigServiceImpl extends ServiceImpl<RcsOmitConfigMapper, RcsOmitConfig> implements RcsOmitConfigService {

    @Autowired
    private RcsOperateMerchantsSetMapper rcsOperateMerchantsSetMapper;

    @Autowired
    private RcsOmitConfigMapper rcsOmitConfigMapper;

    @Autowired
    private RcsSwitchMapper rcsSwitchMapper;

    @Resource(name = "asyncPoolTaskExecutor")
    private ThreadPoolTaskExecutor asyncPoolTaskExecutor;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;



    @Override
    public HttpResponse<RcsPageQueryVo> listPage(Integer current, Integer size, Long merchantsId, String merchantsCode) {
        IPage<RcsOmitConfig> iPage = new Page(current, size);
        IPage<RcsOmitConfig> page = rcsOmitConfigMapper.listPage(iPage, merchantsId, merchantsCode);
        RcsPageQueryVo rcsPageQueryVo = new RcsPageQueryVo();

        List<RcsOmitConfigVo> records = page.getRecords().stream().map(entity -> {
            RcsOmitConfigVo vo = new RcsOmitConfigVo();
            BeanUtils.copyProperties(entity, vo);
            List<Long> levelIds = Arrays.stream(entity.getLevelId().split(","))
                    .filter(num -> !StringUtils.isEmpty(num) && org.apache.commons.lang3.StringUtils.isNumeric(num))
                    .map(num -> Long.parseLong(num))
                    .collect(Collectors.toList());
            vo.setLevelId(levelIds);
            return vo;
        }).collect(Collectors.toList());

        RcsSwitch loudan = rcsSwitchMapper.selectByCode("LOUDAN");
        rcsPageQueryVo.setRcsSwitch(loudan);

        rcsPageQueryVo.setList(records);
        rcsPageQueryVo.setPages(iPage.getPages());
        rcsPageQueryVo.setCurrent((int) iPage.getCurrent());
        rcsPageQueryVo.setSize((int) iPage.getSize());
        rcsPageQueryVo.setTotal((int) iPage.getTotal());
        return HttpResponse.success(rcsPageQueryVo);
    }

    @Override
    public HttpResponse<RcsOmitConfig> getDefaultConfig() {
        Long merchantIds = 999999999999L;
        RcsOmitConfig rcsOmitConfig = rcsOmitConfigMapper.selectByMerchantId(merchantIds);
        if(Objects.isNull(rcsOmitConfig)){
            log.error("商户漏单 获取默认设置失败  merchants_id:999,999,999,999L 不存在");
            return HttpResponse.failToMsg("商户漏单 获取预设失败, 需先进行默认设置操作");
        }
        RcsOmitConfigVo vo = new RcsOmitConfigVo();
        BeanUtils.copyProperties(rcsOmitConfig, vo);
        List<Long> levelIds = Arrays.stream(rcsOmitConfig.getLevelId().split(","))
                .filter(num -> !StringUtils.isEmpty(num) && org.apache.commons.lang3.StringUtils.isNumeric(num))
                .map(num -> Long.parseLong(num))
                .collect(Collectors.toList());
        vo.setLevelId(levelIds);
        return HttpResponse.success(vo);
    }

    @Override
    public HttpResponse<RcsOmitConfig> batchUpdateConfig(RcsOmitConfigBatchUpdateVo reqVo) {
        try{

            if(CollectionUtils.isEmpty(reqVo.getMerchantIds())){
                log.error("商户漏单 商户号不可为空");
                return HttpResponse.failToMsg("商户号不可为空");
            }

            //批量配置
            log.info("批量配置 商户:{}笔 数据", reqVo.getMerchantIds().size());
            List<RcsOmitConfig> insertList = new ArrayList<>();
            for(String merchantId : reqVo.getMerchantIds()){
                RcsOperateMerchantsSet operateMerchantsSet = rcsOperateMerchantsSetMapper.getOperateMerchantsSet(merchantId);
                RcsOmitConfig config = new RcsOmitConfig();
                config.setMerchantsId(Long.valueOf(operateMerchantsSet.getMerchantsId()));
                config.setMerchantsCode(operateMerchantsSet.getMerchantsCode());
                config.setVolumePercentage(reqVo.getVolumePercentage());
                config.setMinMoney(reqVo.getMinMoney());
                config.setMaxMoney(reqVo.getMaxMoney());
                config.setBqStatus(reqVo.getBqStatus());
                config.setQjStatus(reqVo.getQjStatus());
                config.setRemark("设置数据商动态漏单");
                config.setLevelId(String.join(",", reqVo.getLevelId()));
                config.setIsDefaultSrc(2);

                insertList.add(config);

                //按商户id缓存这个漏单设置
                redisClient.hSet(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY,String.valueOf(operateMerchantsSet.getMerchantsId()), JSON.toJSONString(config));
            }
            redisClient.expireKey(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY, 60 * 60 * 24 * 10);//TTL设置10天
            rcsOmitConfigMapper.batchInsertUpdate(insertList);

            //发送MQ redis更新消息
            List<Long> merchantIds = insertList.stream().map(RcsOmitConfig::getMerchantsId).collect(Collectors.toList());
            sendMessage(merchantIds);

            return HttpResponse.success(true);

        }catch (Exception e){
            log.error("批量配置失败", e);
            return HttpResponse.success(false);
        }
    }

    @Override
    public HttpResponse<RcsOmitConfig> exceptUpdateConfig(RcsOmitConfigBatchUpdateVo reqVo) {
        try{
            if(CollectionUtils.isEmpty(reqVo.getMerchantIds())){
                log.error("商户漏单 商户号不可为空");
                return HttpResponse.failToMsg("商户号不可为空");
            }
            //例外配置
            List<Long> reserveIds = reqVo.getMerchantIds().stream().map(Long::valueOf).collect(Collectors.toList());

            List<RcsOMerchantsIDCode> allMerchantIdAndCode = rcsOperateMerchantsSetMapper.getAllMerchantIdAndCode();
            List<List<RcsOMerchantsIDCode>> parts = Lists.partition(allMerchantIdAndCode, 1000);
            log.info("例外配置 商户:{}笔 数据, 每1000笔分组, 拆分: {}组进行", allMerchantIdAndCode.size(), parts.size());

            for (List<RcsOMerchantsIDCode> part : parts){
                asyncPoolTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //执行方法
                        List<RcsOmitConfig> insertList = new ArrayList<>();
                        for(RcsOMerchantsIDCode merchant : part){
                            if(reserveIds.contains(merchant.getMerchantsId())){
                                continue;
                            }
                            RcsOmitConfig config = new RcsOmitConfig();
                            config.setMerchantsId(merchant.getMerchantsId());
                            config.setMerchantsCode(merchant.getMerchantsCode());
                            config.setVolumePercentage(reqVo.getVolumePercentage());
                            config.setMinMoney(reqVo.getMinMoney());
                            config.setMaxMoney(reqVo.getMaxMoney());
                            config.setBqStatus(reqVo.getBqStatus());
                            config.setQjStatus(reqVo.getQjStatus());
                            config.setRemark("设置数据商动态漏单");
                            config.setLevelId(String.join(",", reqVo.getLevelId()));
                            config.setIsDefaultSrc(2);

                            insertList.add(config);

                            //按商户id缓存这个漏单设置
                            redisClient.hSet(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY,String.valueOf(merchant.getMerchantsId()), JSON.toJSONString(config));
                        }
                        rcsOmitConfigMapper.batchInsertUpdate(insertList);

                        //发送MQ redis更新消息
                        List<Long> merchantIds = part.stream()
                                .map(RcsOMerchantsIDCode::getMerchantsId)
                                .filter(item -> !reserveIds.contains(item))
                                .collect(Collectors.toList());
                        sendMessage(merchantIds);
                    }
                });

            }
            redisClient.expireKey(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY, 60 * 60 * 24 * 10);//TTL设置10天

            return HttpResponse.success(true);

        }catch (Exception e){
            log.error("批量配置失败", e);
            return HttpResponse.success(false);
        }
    }

    @Override
    public HttpResponse<RcsOmitConfig> defaultUpdateConfig(RcsOmitConfigBatchUpdateVo reqVo) {
        try{
            //默认配置
            List<RcsOMerchantsIDCode> allMerchantIdAndCode = rcsOperateMerchantsSetMapper.getAllMerchantIdAndCode();
            List<List<RcsOMerchantsIDCode>> parts = Lists.partition(allMerchantIdAndCode, 1000);
            log.debug("默认配置 商户:{}笔 数据, 每1000笔分组, 拆分: {}组进行", allMerchantIdAndCode.size(), parts.size());

            List<RcsOmitConfig> rcsOmitConfigsStatusOpened = rcsOmitConfigMapper.selectStatusOpened();

            RcsOmitConfig rcsOmitConfig = rcsOmitConfigMapper.selectByMerchantId(999999999999L);
            boolean firstDo = Objects.isNull(rcsOmitConfig)? true : false;


            for (List<RcsOMerchantsIDCode> part : parts){
                asyncPoolTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //执行方法
                        List<RcsOmitConfig> insertList = new ArrayList<>();
                        for(RcsOMerchantsIDCode merchant : part){
                            //默认设置只针对新增商户和未设置的商户
                            if(!firstDo && rcsOmitConfigsStatusOpened.size() > 0){
                                boolean existedConfig = rcsOmitConfigsStatusOpened.stream()
                                        .filter(c -> c.getMerchantsId().equals(merchant.getMerchantsId()))
                                        .findFirst().isPresent();
                                if(existedConfig) {
                                    continue;
                                }
                            }

                            RcsOmitConfig config = new RcsOmitConfig();
                            config.setMerchantsId(merchant.getMerchantsId());
                            config.setMerchantsCode(merchant.getMerchantsCode());
                            config.setVolumePercentage(reqVo.getVolumePercentage());
                            config.setMinMoney(reqVo.getMinMoney());
                            config.setMaxMoney(reqVo.getMaxMoney());
                            config.setBqStatus(reqVo.getBqStatus());
                            config.setQjStatus(reqVo.getQjStatus());
                            config.setRemark("设置数据商动态漏单");
                            config.setLevelId(String.join(",", reqVo.getLevelId()));
                            config.setIsDefaultSrc(2);

                            insertList.add(config);

                            //按商户id缓存这个漏单设置
                            redisClient.hSet(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY,String.valueOf(merchant.getMerchantsId()), JSON.toJSONString(config));
                        }
                        rcsOmitConfigMapper.batchInsertUpdate(insertList);

                        //发送MQ redis更新消息
                        List<Long> merchantIds = part.stream()
                                .map(RcsOMerchantsIDCode::getMerchantsId)
                                .collect(Collectors.toList());
                        sendMessage(merchantIds);

                    }
                });
            }
            redisClient.expireKey(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY, 60 * 60 * 24 * 10);//TTL设置10天

            RcsOmitConfig defaultConfig = RcsOmitConfig.builder()
                    .merchantsId(999999999999L)
                    .merchantsCode("origin")
                    .volumePercentage(reqVo.getVolumePercentage())
                    .minMoney(reqVo.getMinMoney()).maxMoney(reqVo.getMaxMoney())
                    .bqStatus(reqVo.getBqStatus()).qjStatus(reqVo.getQjStatus())
                    .remark("设置数据商动态漏单").levelId(String.join(",", reqVo.getLevelId()))
                    .isDefaultSrc(1)
                    .build();
            rcsOmitConfigMapper.insertUpdate(defaultConfig);

            return HttpResponse.success(true);

        }catch (Exception e){
            log.error("批量配置失败", e);
            return HttpResponse.success(false);
        }
    }

    public void sendMessage(List<Long> merchantIds){
        //redis 缓存更新发送mq通知
        try{
            String key = "RcsMissedOrderConfigStatus";
            String linkId = key + "_" + System.currentTimeMillis();
            RcsMissedOrderConfigStatus obj = new RcsMissedOrderConfigStatus();
            obj.setStatus(1);
            obj.setMerchantIds(merchantIds);//配置更新之商户ID集合
            log.info("::::,发送MQ消息linkId={}, key={}", linkId, key);
            producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_UPDATE_SYNC_INFO", linkId, key, obj);
            log.info("::::RcsMissedOrderConfigStatus 发送MQ消息发送成功");
        }catch (Exception e){
            log.error("mq消息发送失败", e);
        }
    }

}

