package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.BusinessLimitLogTypeEnum;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantHighRiskLimit;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantHighRiskRespVo;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLowLimit;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSingleLimit;
import com.panda.rcs.stray.limit.service.IRcsMerchantHighRiskLimitService;
import com.panda.rcs.stray.limit.service.IRcsMerchantHighRiskLimitWebService;
import com.panda.rcs.stray.limit.service.IRcsMerchantLowLimitService;
import com.panda.rcs.stray.limit.service.IRcsMerchantSingleLimitService;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.rcs.stray.limit.wrapper.CommonService;
import com.panda.sport.rcs.enums.SeriesTypeEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitLogMapper;
import com.panda.sport.rcs.mapper.RcsSysUserMapper;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.pojo.RcsSysUser;
import com.panda.sport.rcs.utils.TradeUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RcsMerchantHighRiskLimitWebServiceImpl implements IRcsMerchantHighRiskLimitWebService {

    @Autowired
    private IRcsMerchantHighRiskLimitService rcsMerchantHighRiskLimitService;

    @Autowired
    private IRcsMerchantSingleLimitService rcsMerchantSingleLimitService;

    @Autowired
    private IRcsMerchantLowLimitService rcsMerchantLowLimitService;

    @Autowired
    private RcsSysUserMapper rcsSysUserMapper;
    @Autowired
    private RcsQuotaBusinessLimitLogMapper rcsQuotaBusinessLimitLogMapper;
    @Autowired
    private CommonService commonService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public RcsMerchantHighRiskRespVo queryData(Integer sportId) {
        RcsMerchantHighRiskRespVo rcsMerchantHighRiskRespVo = new RcsMerchantHighRiskRespVo();
        List<RcsMerchantHighRiskLimit> rcsMerchantHighRiskLimits = rcsMerchantHighRiskLimitService.queryHighRiskLimit(sportId);
        rcsMerchantHighRiskRespVo.setRcsMerchantHighRiskLimits(rcsMerchantHighRiskLimits);
        return rcsMerchantHighRiskRespVo;
    }


    @Override
    public List<RcsMerchantLowLimit> queryByLowData() {
        return rcsMerchantLowLimitService.queryAll();
    }

    @Override
    @Transactional
    public void updateByLowData(List<RcsMerchantLowLimit> rcsMerchantLowLimitList) {
        if (!ObjectUtils.isEmpty(rcsMerchantLowLimitList)) {
            //记录日志
            addsMerchantLowLimitLog(rcsMerchantLowLimitList);
            boolean resultType =  rcsMerchantLowLimitService.updateBatchById(rcsMerchantLowLimitList);
            if (resultType) {
                rcsMerchantLowLimitList.forEach(s -> {
                    String key = String.format(SeriesRedisKeyEnums.MERCHANT_LOW_LIMIT_AMOUNT, s.getStrayType());
                    redisUtils.setex(key, JSON.toJSONString(s), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
                    commonService.initJsonObject(key,  JSON.toJSONString(s));
                });
            }

        }
    }

    private void addsMerchantLowLimitLog(List<RcsMerchantLowLimit> newList) {
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(7);
        String paramNamePrefix = "低风险注单可投金额-";
        for (RcsMerchantLowLimit newData : newList) {
            String paramName = paramNamePrefix;
            RcsMerchantLowLimit oldData = rcsMerchantLowLimitService.getById(newData.getId());
            if (oldData != null) {
                if (newData.getMinAmount().compareTo(oldData.getMinAmount()) != 0) {
                    String seriesName = com.panda.rcs.stray.limit.enums.SeriesTypeEnum.getNameByType(newData.getStrayType());
                    paramName = paramName + seriesName;
                    String afterVal = newData.getMinAmount().longValue() + "";
                    String beforeVal = oldData.getMinAmount().longValue() + "";
                    //insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
            }
        }
    }

    @Override
    public List<RcsMerchantSingleLimit> queryBySingleLimitData(Integer sportId) {
        return rcsMerchantSingleLimitService.querySingleLimitByList(sportId);
    }

    @Override
    @Transactional
    public void updateBySingleLimitData(List<RcsMerchantSingleLimit> rcsMerchantSingleLimits) {
        if (!CollectionUtils.isEmpty(rcsMerchantSingleLimits)) {
            //记录日志
            addSingleLimitLog(rcsMerchantSingleLimits);
            boolean resultType =  rcsMerchantSingleLimitService.updateBatchById(rcsMerchantSingleLimits);
            if (resultType) {
                rcsMerchantSingleLimits.forEach(s -> {
                    String key=String.format(SeriesRedisKeyEnums.MERCHANT_HIGH_SINGLE_LIMIT,s.getStrayType(),s.getSportId());
                    redisUtils.setex(key, s.getHighRiskConfig(), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
                    commonService.initJsonObject(key, s.getHighRiskConfig());
                });
            }
        }
    }

    private void addSingleLimitLog(List<RcsMerchantSingleLimit> newList) {
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(7);
        String paramNamePrefix = "高风险赔率(x)区间-";
        for (RcsMerchantSingleLimit newData : newList) {
            String paramName = paramNamePrefix;
            RcsMerchantSingleLimit oldData = rcsMerchantSingleLimitService.getById(newData.getId());
            if (oldData != null) {
                if (!newData.getHighRiskConfig().equals(oldData.getHighRiskConfig())) {
                    String seriesName = com.panda.rcs.stray.limit.enums.SeriesTypeEnum.getNameByType(newData.getStrayType());
                    paramName = paramName + seriesName;
                    JSONArray newHighRiskConfig = JSONArray.parseArray(newData.getHighRiskConfig());
                    JSONArray oldHighRiskConfig = JSONArray.parseArray(oldData.getHighRiskConfig());
                    String afterVal = newHighRiskConfig.getJSONObject(0).getString("min") + " <=x<= " + newHighRiskConfig.getJSONObject(0).getString("max");
                    String beforeVal = oldHighRiskConfig.getJSONObject(0).getString("min") + " <=x<= " + oldHighRiskConfig.getJSONObject(0).getString("max");
                    //insertBusinessLimitLog(paramName,operateType,beforeVal,afterVal);
                    insertBusinessLimitLogIP(paramName,operateType,beforeVal,afterVal,newData.getIp());
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateData(RcsMerchantHighRiskRespVo rcsMerchantHighRiskRespVo) {
        if (!CollectionUtils.isEmpty(rcsMerchantHighRiskRespVo.getRcsMerchantHighRiskLimits())) {
            //记录日志
            //日志添加IP
            for(RcsMerchantHighRiskLimit  logLimit :rcsMerchantHighRiskRespVo.getRcsMerchantHighRiskLimits()){
                logLimit.setIp(rcsMerchantHighRiskRespVo.getIp());
            }
            addMerchantHighRiskLog(rcsMerchantHighRiskRespVo.getRcsMerchantHighRiskLimits());
            boolean resultType = rcsMerchantHighRiskLimitService.updateBatchById(rcsMerchantHighRiskRespVo.getRcsMerchantHighRiskLimits());
            if (resultType) {
                long sportId = rcsMerchantHighRiskRespVo.getRcsMerchantHighRiskLimits().get(0).getSportId();
                //从数据库查询最新数据
                List<RcsMerchantHighRiskLimit> rcsMerchantHighRiskLimits =rcsMerchantHighRiskLimitService.list(new LambdaQueryWrapper<RcsMerchantHighRiskLimit>()
                        .eq(RcsMerchantHighRiskLimit::getSportId, sportId));
                rcsMerchantHighRiskLimits.forEach(s -> {
                    String formatKey = String.format(SeriesRedisKeyEnums.HIGH_RISK_KEY, s.getSeriesType(), s.getSportId(), s.getTournamentLevel());
                    redisUtils.setex(formatKey, JSON.toJSONString(s), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
                    commonService.initJsonObject(formatKey, JSON.toJSONString(s));
                });
            }
        }

    }

    private void addMerchantHighRiskLog(List<RcsMerchantHighRiskLimit> newList) {
        long sportId = newList.get(0).getSportId();
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(7);
        String paramNamePrefix = "单注赔付限额-";
        String sportName;
        if (sportId == -1) {
            sportName = "其他";
        } else {
            sportName = SportIdEnum.getNameById(sportId);
        }
        paramNamePrefix = paramNamePrefix + sportName + "-";
        for (RcsMerchantHighRiskLimit newData : newList) {
            String paramName = paramNamePrefix;
            RcsMerchantHighRiskLimit oldData = rcsMerchantHighRiskLimitService.getById(newData.getId());
            if (oldData != null) {
                if (newData.getSeriesAmount().compareTo(oldData.getSeriesAmount()) != 0) {
                    String seriesName = SeriesTypeEnum.getValue(oldData.getSeriesType());
                    paramName = paramName + seriesName + "-";
                    String level = "无";
                    if (oldData.getTournamentLevel() > 0) {
                        level = oldData.getTournamentLevel() + "级";
                    }
                    paramName = paramName + level;
                    String afterVal = newData.getSeriesAmount().longValue() + "";
                    String beforeVal = oldData.getSeriesAmount().longValue() + "";
                    //insertBusinessLimitLog(paramName,operateType,beforeVal,afterVal);
                    insertBusinessLimitLogIP(paramName,operateType,beforeVal,afterVal,newData.getIp());
                }
            }
        }

    }

    @Override
    public void insertBusinessLimitLog(String paramName, String operateType, String beforeVal, String afterVal) {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory("商户风控管理");
        limitLoglog.setObjectId("-");
        limitLoglog.setObjectName("-");
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(operateType);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        RcsSysUser user = null;
        try {
            user = rcsSysUserMapper.selectById(TradeUserUtils.getUserId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("当前用户不存在");
        }
        limitLoglog.setUserId(user.getId().toString());
        limitLoglog.setUserName(user.getUserCode());
        rcsQuotaBusinessLimitLogMapper.insert(limitLoglog);
    }

    @Override
    public void insertBusinessLimitLogIP(String paramName, String operateType, String beforeVal, String afterVal, String ip) {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory("商户风控管理");
        limitLoglog.setObjectId("-");
        limitLoglog.setObjectName("-");
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(operateType);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setIp(ip);
        RcsSysUser user = null;
        try {
            user = rcsSysUserMapper.selectById(TradeUserUtils.getUserId());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException("当前用户不存在");
        }
        limitLoglog.setUserId(user.getId().toString());
        limitLoglog.setUserName(user.getUserCode());
        rcsQuotaBusinessLimitLogMapper.insert(limitLoglog);
    }
}
