package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.constants.CommonConstants;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.SwitchEnum;
import com.panda.sport.rcs.mapper.RcsHideRangeConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsHideRangeConfig;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.pojo.dto.RcsHideRangeConfigDTO;
import com.panda.sport.rcs.service.IRcsHideRangeConfigService;
import com.panda.sport.rcs.utils.TradeUserUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RcsHideRangeConfigServiceImpl extends ServiceImpl<RcsHideRangeConfigMapper, RcsHideRangeConfig> implements IRcsHideRangeConfigService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;


    private final String logCode = "10040";

    /**
     * 保存藏单参数配置
     * @param configs
     */
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveHideList(List<RcsHideRangeConfigDTO> configs) {
        List<RcsHideRangeConfig> saveConfigs = new ArrayList<>();
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }
        boolean valueTag1 = configs.stream().anyMatch(x -> x.getHideStatus().compareTo(SwitchEnum.OPEN.getId()) == 0 && x.getHideAmount() == null);
        if(valueTag1){
            throw new RuntimeException("保存失败,最大藏单金额字段为必填");
        }
        boolean valueTag2 = configs.stream().anyMatch(x -> x.getHideAmount() != null && x.getHideAmount().compareTo(BigDecimal.ZERO) < 0);
        if(valueTag2){
            throw new RuntimeException("保存失败,最大藏单金额字段必须输入>=0");
        }

        String redisMatchHideRangeListKey = RedisKey.REDIS_MATCH_HIDE_RANGE_LIST_KEY;
        Date date = new Date();
        //判断
        List<RcsHideRangeConfig> list = list();
        if (list.size() > 0) {
            Map<Integer, RcsHideRangeConfigDTO> configMap = configs.stream().collect(Collectors.toMap(RcsHideRangeConfigDTO::getSportId, y -> y));
            for (RcsHideRangeConfig rcsHideRangeConfig : list) {
                RcsHideRangeConfigDTO rcsHideRangeConfigDTO = configMap.get(rcsHideRangeConfig.getSportId());
                if (rcsHideRangeConfigDTO != null) {
                    rcsHideRangeConfig.setHideStatus(rcsHideRangeConfigDTO.getHideStatus());
                    rcsHideRangeConfig.setHideAmount(rcsHideRangeConfigDTO.getHideAmount());
                    rcsHideRangeConfig.setUpdateTime(date);
                }
            }
            updateBatchById(list);
            //判断是否有新增
            List<Integer> sportIds = list.stream().map(RcsHideRangeConfig::getSportId).collect(Collectors.toList());
            List<RcsHideRangeConfigDTO> insertConfigs = configs.stream().filter(x -> !sportIds.contains(x.getSportId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(insertConfigs)) {
                for (RcsHideRangeConfigDTO rcsHideRangeConfigDTO : insertConfigs) {
                    RcsHideRangeConfig rcsHideRangeConfig = new RcsHideRangeConfig();
                    BeanUtils.copyProperties(rcsHideRangeConfigDTO, rcsHideRangeConfig);
                    rcsHideRangeConfig.setCreateTime(date);
                    rcsHideRangeConfig.setUpdateTime(date);
                    saveConfigs.add(rcsHideRangeConfig);
                }
                saveBatch(saveConfigs);
            }

        } else {
            for (RcsHideRangeConfigDTO rcsHideRangeConfigDTO : configs) {
                RcsHideRangeConfig rcsHideRangeConfig = new RcsHideRangeConfig();
                BeanUtils.copyProperties(rcsHideRangeConfigDTO, rcsHideRangeConfig);
                rcsHideRangeConfig.setCreateTime(date);
                rcsHideRangeConfig.setUpdateTime(date);
                saveConfigs.add(rcsHideRangeConfig);
            }
            saveBatch(saveConfigs);
        }
        //操作日志
        List<RcsQuotaBusinessLimitLog> logs = getLogList(getResultList(), configs);
        String jsonString = JSONArray.toJSONString(logs);
        log.info("配置变更记录:{}",jsonString);
        if(logs.size() >0) {
            producerSendMessageUtils.sendMessage(CommonConstants.RCS_BUSINESS_LOG_SAVE, null, logCode, jsonString);
        }
        redisClient.set(redisMatchHideRangeListKey, JSONObject.toJSONString(configs));
    }

    /**
     * 获取结果集
     * @return
     */
    private List<RcsHideRangeConfigDTO> getResultList(){
        List<RcsHideRangeConfigDTO> result = new ArrayList<>();
        String redisMatchHideRangeListKey = RedisKey.REDIS_MATCH_HIDE_RANGE_LIST_KEY;
        String json = redisClient.get(redisMatchHideRangeListKey);
        if (StringUtils.isNotBlank(json)) {
            result = JSONObject.parseObject(json, new TypeReference<List<RcsHideRangeConfigDTO>>() {
            });
        } else {
            LambdaQueryWrapper<RcsHideRangeConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByAsc(RcsHideRangeConfig::getSportId);
            List<RcsHideRangeConfig> list = list(wrapper);

            if (CollectionUtils.isNotEmpty(list)) {
                for (RcsHideRangeConfig rcsHideRangeConfig : list) {
                    RcsHideRangeConfigDTO rcsHideRangeConfigDTO = new RcsHideRangeConfigDTO();
                    BeanUtils.copyProperties(rcsHideRangeConfig, rcsHideRangeConfigDTO);
                    result.add(rcsHideRangeConfigDTO);
                }
                redisClient.set(redisMatchHideRangeListKey, JSONObject.toJSONString(result));
            }
        }
        return result;
    }

    /**
     * 获取藏单参数配置
     * @return
     */
    @Override
    public List<RcsHideRangeConfigDTO> getHideList() {

        //配置默认配置项
        List<RcsHideRangeConfigDTO> defaultConfigs = Arrays.asList(
                new RcsHideRangeConfigDTO(SportIdEnum.FOOTBALL.getId().intValue(),SwitchEnum.CLOSE.getId(),null),
                new RcsHideRangeConfigDTO(SportIdEnum.BASKETBALL.getId().intValue(),SwitchEnum.CLOSE.getId(),null),
                new RcsHideRangeConfigDTO(SportIdEnum.TENNIS.getId().intValue(),SwitchEnum.CLOSE.getId(),null),
                new RcsHideRangeConfigDTO(SportIdEnum.BASEBALL.getId().intValue(),SwitchEnum.CLOSE.getId(),null),
                new RcsHideRangeConfigDTO(SportIdEnum.VOLLEYBALL.getId().intValue(),SwitchEnum.CLOSE.getId(),null),
                new RcsHideRangeConfigDTO(SportIdEnum.PING_PONG.getId().intValue(),SwitchEnum.CLOSE.getId(),null),
                new RcsHideRangeConfigDTO(SportIdEnum.SNOOKER.getId().intValue(),SwitchEnum.CLOSE.getId(),null),
                new RcsHideRangeConfigDTO(SportIdEnum.ICE_HOCKEY.getId().intValue(),SwitchEnum.CLOSE.getId(),null),
                new RcsHideRangeConfigDTO(SportIdEnum.BADMINTON.getId().intValue(),SwitchEnum.CLOSE.getId(),null)
                );

        List<RcsHideRangeConfigDTO> result = getResultList();
        if(result.size() >0){
            Map<Integer, RcsHideRangeConfigDTO> sportMap = result.stream().collect(Collectors.toMap(x -> x.getSportId(), y -> y));
            for (RcsHideRangeConfigDTO defaultConfig : defaultConfigs) {
                RcsHideRangeConfigDTO rcsHideRangeConfigDTO = sportMap.get(defaultConfig.getSportId());
                if(rcsHideRangeConfigDTO != null) {
                    defaultConfig.setHideStatus(rcsHideRangeConfigDTO.getHideStatus());
                    defaultConfig.setHideAmount(rcsHideRangeConfigDTO.getHideAmount());
                }
            }
        }

        return defaultConfigs;
    }


    /**
     * 分赛种组装日志集合
     * @param oldConfig 旧配置数据
     * @param newConfigs 新配置数据
     * @return
     * @throws Exception
     */
    private List<RcsQuotaBusinessLimitLog> getLogList(List<RcsHideRangeConfigDTO> oldConfig, List<RcsHideRangeConfigDTO> newConfigs) throws Exception {
        List<RcsQuotaBusinessLimitLog> logs = new ArrayList<>();
        Map<Integer, RcsHideRangeConfigDTO> oldConfigMap = new HashMap<>();
        if(oldConfig.size() >0) {
            oldConfigMap = oldConfig.stream().collect(Collectors.toMap(RcsHideRangeConfigDTO::getSportId, y -> y));
        }
        if (newConfigs.size() > 0) {
            for (RcsHideRangeConfigDTO configDTO : newConfigs) {
                Integer sportId = configDTO.getSportId();
                RcsHideRangeConfigDTO oldConfigDTO = oldConfigMap.get(sportId);
                String oldHideStatus = "";
                String oldHideAmount = "";
                if (oldConfigDTO != null) {
                    oldHideStatus = oldConfigDTO.getHideStatus().toString();
                    if(oldConfigDTO.getHideAmount() != null) {
                        oldHideAmount = oldConfigDTO.getHideAmount().toString();
                    }
                }

                //变更的状态(非变更不记录)
                if (!oldHideStatus.equals(configDTO.getHideStatus().toString())) {
                    if(StringUtils.isNotBlank(oldHideStatus)){
                        oldHideStatus = SwitchEnum.getDescById(Integer.parseInt(oldHideStatus));
                    }
                    logs.add(getLogObj(SportIdEnum.getNameById(Long.valueOf(sportId)), "开关", oldHideStatus, SwitchEnum.getDescById(configDTO.getHideStatus())));
                }

                String hideAmount = "";
                if(configDTO.getHideAmount() != null){
                    hideAmount = configDTO.getHideAmount().toString();
                }
                //变更的阀值(非变更不记录)
                if (!oldHideAmount.equals(hideAmount)) {
                    logs.add(getLogObj(SportIdEnum.getNameById(Long.valueOf(sportId)), "最大藏单金额", oldHideAmount, hideAmount));
                }
            }
        }

        return logs;
    }

    /**
     * 组装日志对象
     * @param operateType
     * @param paramName
     * @param beforeVal
     * @param afterVal
     * @return
     * @throws Exception
     */
    private RcsQuotaBusinessLimitLog getLogObj(String operateType, String paramName, String beforeVal, String afterVal) throws Exception {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory("动态风控设置-通用设置");
        limitLoglog.setObjectId("-");
        limitLoglog.setObjectName("投注货量-金额区间配置");
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType("-");
        limitLoglog.setParamName(operateType+"-"+paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(String.valueOf(TradeUserUtils.getUserId()));

        return limitLoglog;
    }
}

