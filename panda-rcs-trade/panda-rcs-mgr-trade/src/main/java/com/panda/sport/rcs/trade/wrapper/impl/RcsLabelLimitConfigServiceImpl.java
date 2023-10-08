package com.panda.sport.rcs.trade.wrapper.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.RedisCacheSyncBean;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.mapper.RcsSysUserMapper;
import com.panda.sport.rcs.mapper.TUserLevelMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.mapper.RcsSysUserMapper;
import com.panda.sport.rcs.mapper.TUserLevelMapper;
import com.panda.sport.rcs.pojo.RcsLabelLimitConfig;
import com.panda.sport.rcs.pojo.RcsLabelSportVolumePercentage;
import com.panda.sport.rcs.trade.service.TradeVerificationService;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.pojo.TUserLevel;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.vo.BusinessLogVo;
import com.panda.sport.rcs.trade.wrapper.IRcsLabelSportVolumePercentageService;
import com.panda.sport.rcs.trade.wrapper.RcsLabelLimitConfigService;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.RcsLabelLimitConfigVo;
import com.panda.sport.rcs.vo.RcsLabelSportVolumePercentageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-02-04 12:28
 **/
@Service
public class RcsLabelLimitConfigServiceImpl extends ServiceImpl<RcsLabelLimitConfigMapper, RcsLabelLimitConfig> implements RcsLabelLimitConfigService {
    @Autowired
    private RcsLabelLimitConfigMapper rcsLabelLimitConfigMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    IRcsLabelSportVolumePercentageService rcsLabelSportVolumePercentageService;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private TradeVerificationService tradeVerificationService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private TUserLevelMapper tUserLevelMapper;

    @Autowired
    RcsSysUserMapper rcsSysUserMapper;

    private final static String RCS_FEATURE_LABEL_CONFIG_TOP = "rcs_feature_label_config";

    @Transactional
    @Override
    public HttpResponse updateRcsLabelLimitConfigVo(List<RcsLabelLimitConfigVo> rcsLabelLimitConfigVoList, Integer tradeId,String ip) {
        List<RcsLabelLimitConfig> rcsLabelLimitConfigList = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        List<RcsLabelSportVolumePercentage> oldVolumePercentages=new ArrayList<>();
        List<RcsLabelSportVolumePercentage> newVolumePercentages=new ArrayList<>();
        try{
        for (RcsLabelLimitConfigVo rcsLabelLimitConfigVo : rcsLabelLimitConfigVoList) {
            //提前结算extraMargin 校验
            checkExtraMargin(rcsLabelLimitConfigVo);
            idList.add(rcsLabelLimitConfigVo.getTagId());
            List<Integer> sportIdList = rcsLabelLimitConfigVo.getSportIdList();
            BigDecimal volumePercentage = rcsLabelLimitConfigVo.getVolumePercentage();
            BigDecimal limitPercentage = rcsLabelLimitConfigVo.getLimitPercentage();

            RedisCacheSyncBean bean = RedisCacheSyncBean.build("rcs:label:order:delay:config:",
                    String.format("rcs:label:order:delay:config:%s",
                            rcsLabelLimitConfigVo.getTagId()));
            producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_SYNC", bean);

            if (!CollectionUtils.isEmpty(sportIdList)) {
                for (Integer sportId : sportIdList) {
                    RcsLabelLimitConfig rcsLabelLimitConfig = new RcsLabelLimitConfig();
                    BeanCopyUtils.copyProperties(rcsLabelLimitConfigVo, rcsLabelLimitConfig);
                    rcsLabelLimitConfig.setUpdateUserId(tradeId);
                    rcsLabelLimitConfig.setId(null);
                    rcsLabelLimitConfig.setSportId(sportId);
                    rcsLabelLimitConfig.setVolumePercentage(volumePercentage.divide(new BigDecimal(100)));
                    rcsLabelLimitConfigList.add(rcsLabelLimitConfig);

                    RedisCacheSyncBean beanSport = RedisCacheSyncBean.build("rcs:label:order:delay:config:",
                            String.format("rcs:label:order:delay:config:%s", rcsLabelLimitConfigVo.getTagId()),
                            String.valueOf(sportId), JSONObject.toJSONString(rcsLabelLimitConfig),
                            30*24*60*60L);
                    producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_SYNC", beanSport);

                    //发送到接距服务做刷新
                    String key=String.format("rcs:label:sportId:%s:order:delay:config:%s", sportId,  rcsLabelLimitConfigVo.getTagId());
                    JSONObject json = new JSONObject();
                    json.put("key", key);
                    json.put("value", rcsLabelLimitConfig);
                    producerSendMessageUtils.sendMessage("rcs_order_reject_cache_update", "", key, json);

                }
            } else {
                RcsLabelLimitConfig rcsLabelLimitConfig = new RcsLabelLimitConfig();
                BeanCopyUtils.copyProperties(rcsLabelLimitConfigVo, rcsLabelLimitConfig);
                rcsLabelLimitConfig.setUpdateUserId(tradeId);
                rcsLabelLimitConfig.setId(null);
                rcsLabelLimitConfig.setSportId(null);
                rcsLabelLimitConfig.setVolumePercentage(volumePercentage.divide(new BigDecimal(100)));
                rcsLabelLimitConfigList.add(rcsLabelLimitConfig);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tagId",rcsLabelLimitConfigVo.getTagId());
            jsonObject.put("type", LimitDataTypeEnum.TAG_LIMIT.getType());
            if (limitPercentage != null && 1 == rcsLabelLimitConfigVo.getSpecialBettingLimit()) {
                double v = limitPercentage.doubleValue();
                jsonObject.put("value",String.valueOf(v / 100));
            }else{
                //默认为1
                jsonObject.put("value","1");
            }
            producerSendMessageUtils.sendMessage("rcs_limit_cache_clear_sdk", jsonObject);

            // 标签赛种货量入库
            List<RcsLabelSportVolumePercentageVo> volumePercentageVoList = rcsLabelLimitConfigVo.getSportVolumePercentageList();
            if (volumePercentageVoList == null) {
                volumePercentageVoList = new ArrayList<>();
            }
            //删除元配置
            LambdaQueryWrapper<RcsLabelSportVolumePercentage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsLabelSportVolumePercentage::getTagId, rcsLabelLimitConfigVo.getTagId());
            List<RcsLabelSportVolumePercentage> list = rcsLabelSportVolumePercentageService.list(wrapper);
            if (!CollectionUtils.isEmpty(list)) {
                rcsLabelSportVolumePercentageService.remove(wrapper);
                String userTagLevelKeyOld = "risk:user:tag:level:" + rcsLabelLimitConfigVo.getTagId();
                //删除缓存
                list.forEach(e -> {
                    RedisCacheSyncBean beanLevel = RedisCacheSyncBean.build("risk:user:tag:level",userTagLevelKeyOld + e.getSportId());
                    producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_SYNC", beanLevel);
                });
            }

            //插入新配置
            List<RcsLabelSportVolumePercentage> volumePercentageList = new ArrayList<>();
            for (RcsLabelSportVolumePercentageVo volumePercentageVo : volumePercentageVoList) {
                RcsLabelSportVolumePercentage rcsLabelSportVolumePercentage = new RcsLabelSportVolumePercentage();
                BeanUtils.copyProperties(volumePercentageVo, rcsLabelSportVolumePercentage);
                volumePercentageList.add(rcsLabelSportVolumePercentage);
                //缓存标签赛种货量百分比
                String userTagLevelKey = String.format("risk:user:tag:level:%s", volumePercentageVo.getTagId() + "" + volumePercentageVo.getSportId());

                RedisCacheSyncBean beanLevel = RedisCacheSyncBean.build("risk:user:tag:level",userTagLevelKey,JSONObject.toJSONString(rcsLabelSportVolumePercentage),30*24*60*60L);
                producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_SYNC", beanLevel);
            }
            rcsLabelSportVolumePercentageService.saveBatch(volumePercentageList);
        }
        QueryWrapper<TUserLevel> queryWrapper = new QueryWrapper();
        List<TUserLevel> tUserLevels = tUserLevelMapper.selectList(queryWrapper);
        List<RcsLabelLimitConfig> oldLabelLimitConfigs =  rcsLabelLimitConfigMapper.getRcsLabelLimitConfigs();
        if (!CollectionUtils.isEmpty(idList)) {
            rcsLabelLimitConfigMapper.removeRcsLabelLimitConfigs(idList);
        }
        if (!CollectionUtils.isEmpty(rcsLabelLimitConfigList)) {
            saveBatch(rcsLabelLimitConfigList);
        }
        BusinessLogVo businessLogVo=new BusinessLogVo();
        businessLogVo.setOldLabelLimitConfigs(oldLabelLimitConfigs);
        businessLogVo.setNewsLabelLimitConfigs(rcsLabelLimitConfigVoList);
        businessLogVo.setOldLabelSportVolumePercentages(oldVolumePercentages);
        businessLogVo.setUserId(tradeId.toString());
        businessLogVo.setTUserLevels(tUserLevels);
        //businessLogVo.setIp(rcsLabelLimitConfigVoList.get(0).getIp());
        List<RcsQuotaBusinessLimitLog> listFuture = taskExecutor.submit(new BusinessLogServiceImpl(businessLogVo)).get();
        if(Objects.nonNull(listFuture)){
            for(RcsQuotaBusinessLimitLog ipList : listFuture){
                ipList.setIp(ip);
            }
            String arrString = JSONArray.toJSONString(listFuture);
            producerSendMessageUtils.sendMessage(CommonUtil.RCS_BUSINESS_LOG_SAVE,null,CommonUtil.logCode,arrString);
        }

        producerSendMessageUtils.sendMessage(RCS_FEATURE_LABEL_CONFIG_TOP, RCS_FEATURE_LABEL_CONFIG_TOP, tradeVerificationService.getRequestId(), rcsLabelLimitConfigVoList);
        return HttpResponse.success();
        }catch (Exception ex){
            log.error("操作失败",ex);
            return HttpResponse.failToMsg("操作失败");
        }
    }

    public Integer getMachValue(Integer matchLength, String config) {
        String[] str = config.split(",");
        Integer result = 0;
        if (str != null && str.length > 0) {
            for (int i = 0; i < str.length; i++) {
                String[] split = str[i].split(":");
                if (split.length == 2 && split[0].equals(matchLength.toString())) {
                    result = Integer.valueOf(split[1]);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @return void
     * @Description //校验rcsLabelLimitConfigVo
     * 只能输入1位小数，且小数位只能为0或者5，且输入数值绝对值不能大于4。
     * @Param [rcsLabelLimitConfigVo]
     * @Author sean
     * @Date 2022/4/8
     **/
    private void checkExtraMargin(RcsLabelLimitConfigVo rcsLabelLimitConfigVo) {
        BigDecimal extraMargin = rcsLabelLimitConfigVo.getExtraMargin();
        if (ObjectUtils.isEmpty(extraMargin)) {
            return;
        }
        if (extraMargin.abs().compareTo(BigDecimal.valueOf(20)) >= 1) {
            throw new RcsServiceException("输入数值绝对值不能大于20");
        }
        if (extraMargin.divideAndRemainder(new BigDecimal("0.5"))[1].compareTo(BigDecimal.ZERO) > 0) {
            throw new RcsServiceException("只能输入1位小数，且小数位只能为0或者5");
        }
    }
}
