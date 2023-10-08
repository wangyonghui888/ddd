package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.BusinessLimitLogTypeEnum;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLimitCompensation;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSeriesConfig;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSeriesRespVo;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSportLimit;
import com.panda.rcs.stray.limit.service.*;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.rcs.stray.limit.wrapper.CommonService;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.pojo.RcsQuotaUserDailyQuota;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RcsMerchantSeriesServiceImpl implements RcsMerchantSeriesService {


    @Autowired
    private IRcsMerchantSeriesConfigService rcsMerchantSeriesConfigService;

    @Autowired
    private IRcsMerchantSportLimitService rcsMerchantSportLimitService;

    @Autowired
    private IRcsMerchantLimitCompensationService rcsMerchantLimitCompensationService;

    @Autowired
    private IRcsMerchantHighRiskLimitWebService iRcsMerchantHighRiskLimitWebService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public RcsMerchantSeriesRespVo queryData() {
        RcsMerchantSeriesRespVo rcsMerchantSeriesRespVo = new RcsMerchantSeriesRespVo();
        rcsMerchantSeriesRespVo.setRcsMerchantSeriesConfig(rcsMerchantSeriesConfigService.queryRedisCache());
        rcsMerchantSeriesRespVo.setRcsMerchantSportLimitList(rcsMerchantSportLimitService.queryAll());
        rcsMerchantSeriesRespVo.setRcsMerchantLimitCompensationList(rcsMerchantLimitCompensationService.queryAll());
        return rcsMerchantSeriesRespVo;
    }

    @Override
    @Transactional
    public void updateData(RcsMerchantSeriesRespVo rcsMerchantSeriesRespVo) {
        if (!ObjectUtils.isEmpty(rcsMerchantSeriesRespVo.getRcsMerchantSeriesConfig())) {
            //记录日志
            RcsMerchantSeriesConfig rcsMerchantSeriesConfigIp = rcsMerchantSeriesRespVo.getRcsMerchantSeriesConfig();
            rcsMerchantSeriesConfigIp.setIp(rcsMerchantSeriesRespVo.getIp());
            addMerchantSeriesConfigLog(rcsMerchantSeriesConfigIp);
            boolean resultType =  rcsMerchantSeriesConfigService.updateById(rcsMerchantSeriesRespVo.getRcsMerchantSeriesConfig());
            if(resultType){
                String key = SeriesRedisKeyEnums.SERIES_PAYOUT_AND_PAICAI_TOTAL_AMOUNT;
                redisUtils.setex(key, JSON.toJSONString(rcsMerchantSeriesRespVo.getRcsMerchantSeriesConfig()), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
                commonService.initJsonObject(key,JSON.toJSONString(rcsMerchantSeriesRespVo.getRcsMerchantSeriesConfig()));
            }
        }
        if (!ObjectUtils.isEmpty(rcsMerchantSeriesRespVo.getRcsMerchantSportLimit())) {
            //记录日志
            RcsMerchantSportLimit rcsMerchantSportLimitIp = rcsMerchantSeriesRespVo.getRcsMerchantSportLimit();
            rcsMerchantSportLimitIp.setIp(rcsMerchantSeriesRespVo.getIp());
            addMerchantSportLimitLog(rcsMerchantSportLimitIp);
            boolean resultType =   rcsMerchantSportLimitService.updateById(rcsMerchantSeriesRespVo.getRcsMerchantSportLimit());
            if(resultType){
                RcsMerchantSportLimit rcsMerchantSportLimit=rcsMerchantSportLimitService.getById(rcsMerchantSeriesRespVo.getRcsMerchantSportLimit().getId());
                String key = String.format(SeriesRedisKeyEnums.MERCHANT_SPORT_LIMIT_TOTAL, rcsMerchantSportLimit.getSportId());
                redisUtils.setex(key, JSON.toJSONString(rcsMerchantSportLimit), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
                commonService.initJsonObject(key,JSON.toJSONString(rcsMerchantSportLimit));
            }
        }
        if (!ObjectUtils.isEmpty(rcsMerchantSeriesRespVo.getRcsMerchantLimitCompensation())) {
            //记录日志
            RcsMerchantLimitCompensation rcsMerchantLimitCompensationIp = rcsMerchantSeriesRespVo.getRcsMerchantLimitCompensation();
            rcsMerchantLimitCompensationIp.setIp(rcsMerchantSeriesRespVo.getIp());
            addMerchantLimitCompensationLog(rcsMerchantLimitCompensationIp);
            boolean resultType =   rcsMerchantLimitCompensationService.updateById(rcsMerchantSeriesRespVo.getRcsMerchantLimitCompensation());
            if(resultType){
                RcsMerchantLimitCompensation rcsMerchantLimitCompensation=rcsMerchantLimitCompensationService.getById(rcsMerchantSeriesRespVo.getRcsMerchantLimitCompensation().getId());
                String key = String.format(SeriesRedisKeyEnums.MERCHANT_LIMIT_COMPENSATION_KEY, rcsMerchantLimitCompensation.getSeriesType());
                redisUtils.setex(key, JSON.toJSONString(rcsMerchantLimitCompensation), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
                commonService.initJsonObject(key,JSON.toJSONString(rcsMerchantLimitCompensation));
            }
        }
    }

    private void addMerchantLimitCompensationLog(RcsMerchantLimitCompensation newData){
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(8);
        RcsMerchantLimitCompensation oldData = rcsMerchantLimitCompensationService.getById(newData.getId());
        if (oldData != null) {
            if (newData.getSeriesLimitAmount().compareTo(oldData.getSeriesLimitAmount()) != 0) {
                String paramName = "单日串关类型赔付限额-";
                paramName = paramName + getSeriesName(oldData.getSeriesType());
                String afterVal = newData.getSeriesLimitAmount().longValue()+"";
                String beforeVal = oldData.getSeriesLimitAmount().longValue()+"";
                iRcsMerchantHighRiskLimitWebService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
            }
        }
    }

    private String getSeriesName(int type){
        String seriesName;
        switch (type){
            case 2:
                seriesName = "2串1";
                break;
            case 3:
                seriesName = "3串N";
                break;
            case 4:
                seriesName = "4串N";
                break;
            case 5:
                seriesName = "5串N";
                break;
            case 6:
                seriesName = "6串N";
                break;
            case 7:
                seriesName = "7串N";
                break;
            case 8:
                seriesName = "8串N";
                break;
            case 9:
                seriesName = "9串N";
                break;
            case 10:
                seriesName = "10串N";
                break;
            default:
                seriesName = "";
        }
        return seriesName;
    }

    private void addMerchantSportLimitLog(RcsMerchantSportLimit newData){
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(8);
        RcsMerchantSportLimit oldData = rcsMerchantSportLimitService.getById(newData.getId());
        if (oldData != null) {
            if (newData.getStrayLimitAmount().compareTo(oldData.getStrayLimitAmount()) != 0) {
                String paramName = "单日串关赛种赔付限额-";
                long sportId = oldData.getSportId();
                String sportName;
                if (sportId == -1){
                    sportName = "其他";
                }else{
                    sportName = SportIdEnum.getNameById(sportId);
                }
                paramName = paramName + sportName;
                String afterVal = newData.getStrayLimitAmount().longValue()+"";
                String beforeVal = oldData.getStrayLimitAmount().longValue()+"";
                iRcsMerchantHighRiskLimitWebService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
            }
        }
    }

    private void addMerchantSeriesConfigLog(RcsMerchantSeriesConfig newData){
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(8);
        RcsMerchantSeriesConfig oldData = rcsMerchantSeriesConfigService.getById(newData.getId());
        if (oldData != null) {
            if (newData.getSeriesPayoutTotalAmount().compareTo(oldData.getSeriesPayoutTotalAmount()) != 0) {
                String paramName = "单日串关赔付总限额";
                String afterVal = newData.getSeriesPayoutTotalAmount().longValue()+"";
                String beforeVal = oldData.getSeriesPayoutTotalAmount().longValue()+"";
                iRcsMerchantHighRiskLimitWebService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
            }
        }
    }
}
