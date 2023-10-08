package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.BusinessLimitLogTypeEnum;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantInterval;
import com.panda.rcs.stray.limit.mapper.RcsMerchantIntervalMapper;
import com.panda.rcs.stray.limit.service.IRcsMerchantHighRiskLimitWebService;
import com.panda.rcs.stray.limit.service.RcsMerchantIntervalService;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.rcs.stray.limit.wrapper.CommonService;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RcsMerchantIntervalServiceImpl extends ServiceImpl<RcsMerchantIntervalMapper, RcsMerchantInterval> implements RcsMerchantIntervalService {

    @Autowired
    private RcsMerchantIntervalService rcsMerchantIntervalService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private IRcsMerchantHighRiskLimitWebService iRcsMerchantHighRiskLimitWebService;
    @Autowired
    private CommonService commonService;


    @Override
    public RcsMerchantInterval queryBySportAndStrayType(Integer sportId, Integer strayType) {
        String key=String.format(SeriesRedisKeyEnums.MERCHANT_INTERVAL_MAX_AMOUNT,sportId,strayType);
        String json = RcsLocalCacheUtils.getValue(key,redisUtils::get, NumberConstant.REDIS_TIM_OUT);
        if (StringUtils.isEmpty(json)) {
            RcsMerchantInterval rcsMerchantInterval = baseMapper.selectOne(new LambdaQueryWrapper<RcsMerchantInterval>()
                    .eq(RcsMerchantInterval::getSportId, sportId).eq(RcsMerchantInterval::getStrayType, strayType));
            redisUtils.setex(key,JSONObject.toJSONString(rcsMerchantInterval),NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            return rcsMerchantInterval;
        }
        return JSONObject.parseObject(json, RcsMerchantInterval.class);
    }

    @Override
    public List<RcsMerchantInterval> queryAll(Integer sportId) {
        return baseMapper.selectList(new LambdaQueryWrapper<RcsMerchantInterval>().eq(RcsMerchantInterval::getSportId, sportId).
                orderByAsc(RcsMerchantInterval::getSportId));
    }

    @Override
    @Transactional
    public void updateData(List<RcsMerchantInterval> rcsMerchantIntervalList) {
        if (!CollectionUtils.isEmpty(rcsMerchantIntervalList)) {
            //记录日志
            addMerchantIntervalLog(rcsMerchantIntervalList);
            boolean resultType = rcsMerchantIntervalService.updateBatchById(rcsMerchantIntervalList);
            if (resultType){
                rcsMerchantIntervalList.forEach(s->{
                    String key=String.format(SeriesRedisKeyEnums.MERCHANT_INTERVAL_MAX_AMOUNT,s.getSportId(),s.getStrayType());
                    redisUtils.setex(key, JSON.toJSONString(s),NumberConstant.NUM_FIVES, TimeUnit.DAYS);
                    commonService.initJsonObject(key,JSON.toJSONString(s));
                });
            }
        }
    }

    private void addMerchantIntervalLog(List<RcsMerchantInterval> newList){
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(7);
        String paramNamePrefix = "高风险注单最大投注金额-";
        long sportId = newList.get(0).getSportId();
        String sportName;
        if (sportId == -1){
            sportName = "其他";
        }else{
            sportName = SportIdEnum.getNameById(sportId);
        }
        paramNamePrefix = paramNamePrefix + sportName + "-";
        for (RcsMerchantInterval newData : newList){
            String paramName = paramNamePrefix;
            RcsMerchantInterval oldData = rcsMerchantIntervalService.getById(newData.getId());
            if (oldData != null){
                if (newData.getMaxIntervalAmount().compareTo(oldData.getMaxIntervalAmount()) != 0){
                    String seriesName = com.panda.rcs.stray.limit.enums.SeriesTypeEnum.getNameByType(newData.getStrayType());
                    paramName = paramName + seriesName;
                    String afterVal = newData.getMaxIntervalAmount().longValue()+"";
                    String beforeVal = oldData.getMaxIntervalAmount().longValue()+"";
                    //iRcsMerchantHighRiskLimitWebService.insertBusinessLimitLog(paramName,operateType,beforeVal,afterVal);
                    iRcsMerchantHighRiskLimitWebService.insertBusinessLimitLogIP(paramName,operateType,beforeVal,afterVal,newData.getIp());
                }
            }
        }
    }

}
