package com.panda.sport.rcs.mgr.aspect;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.sport.rcs.constants.CommonConstants;
import com.panda.sport.rcs.mapper.RcsMerchantsHideRangeConfigMapper;
import com.panda.sport.rcs.mapper.RcsOmitConfigMapper;
import com.panda.sport.rcs.mgr.mq.bean.RcsMerchantsHideRangeConfigDto;
import com.panda.sport.rcs.mgr.mq.bean.RcsQuotaBusinessLimitLogBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMerchantsHideRangeConfig;
import com.panda.sport.rcs.pojo.RcsOmitConfig;
import com.panda.sport.rcs.pojo.vo.RcsOmitConfigBatchUpdateVo;
import com.panda.sport.rcs.utils.OperateLogUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BusinessLog 风控日誌AOP
 * @author Z9-jing
 */
@Slf4j
@Aspect
@Component
public class BusinessLogAspect {
    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(20, 40, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000), new ThreadPoolExecutor.CallerRunsPolicy());
    private final ProducerSendMessageUtils sendMessage;
    private final Integer code=200;
    @Autowired
    private RcsOmitConfigMapper rcsOmitConfigMapper;


    @Autowired
    private RcsMerchantsHideRangeConfigMapper rcsMerchantsHideRangeConfigMapper;
    @Autowired
    public BusinessLogAspect(ProducerSendMessageUtils sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Around("@annotation(com.panda.sport.rcs.mgr.paid.annotion.BusinessLog)")
    public Object operLog(ProceedingJoinPoint joinPoint) throws Throwable {
            RcsQuotaBusinessLimitLogBean logBean = beforeProcess(joinPoint);
            Object resultObj = joinPoint.proceed();
            afterProcess(joinPoint, resultObj, logBean);
            return resultObj;

    }
    private RcsQuotaBusinessLimitLogBean beforeProcess(ProceedingJoinPoint joinPoint) {
        RcsQuotaBusinessLimitLogBean logBean=new RcsQuotaBusinessLimitLogBean();
        try {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        logBean.setMethod(method.getName());
        logBean.setUserId(OperateLogUtils.getUserId()) ;
        logBean.setIp(OperateLogUtils.getIpAddr());
        logBean=getBeforeString(joinPoint,method.getName(),logBean);
        }catch (Exception e){

        }
        return  logBean;
    }

    /**
     * 修改前的历史数据
     * @param joinPoint
     * @param methodName
     * @return
     */
    private RcsQuotaBusinessLimitLogBean getBeforeString(ProceedingJoinPoint joinPoint, String methodName,RcsQuotaBusinessLimitLogBean logBean){
        switch (methodName) {
            case "editHideMoneyList":
            case "editHideStatusList":
                return editHideMoneyListName(joinPoint,logBean);
            case "batchUpdateConfig":
                return batchUpdateConfig(joinPoint,logBean);


        }
        return logBean;
    }

    private RcsQuotaBusinessLimitLogBean batchUpdateConfig(ProceedingJoinPoint joinPoint, RcsQuotaBusinessLimitLogBean logBean){
        RcsOmitConfigBatchUpdateVo reqVo=
                new ObjectMapper().convertValue(joinPoint.getArgs()[0],RcsOmitConfigBatchUpdateVo.class);
        QueryWrapper<RcsOmitConfig> queryWrapper = new QueryWrapper();
        if(reqVo.getType()==2){
            Long merchantIds = 999999999999L;
            RcsOmitConfig rcsOmitConfig = rcsOmitConfigMapper.selectByMerchantId(merchantIds);
            logBean.setBeforeString(JSONObject.toJSONString(Arrays.asList(rcsOmitConfig)));
            return logBean;
        }
        queryWrapper.lambda().in(RcsOmitConfig::getMerchantsId,reqVo.getMerchantIds());
        List<RcsOmitConfig> list = rcsOmitConfigMapper.selectList(queryWrapper);
        logBean.setBeforeString(JSONObject.toJSONString(list));
        return logBean;
    }

    private RcsQuotaBusinessLimitLogBean editHideMoneyListName(ProceedingJoinPoint joinPoint,RcsQuotaBusinessLimitLogBean logBean){
        RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig=
                new ObjectMapper().convertValue(joinPoint.getArgs()[0],RcsMerchantsHideRangeConfigDto.class);
        QueryWrapper<RcsMerchantsHideRangeConfig> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().in(RcsMerchantsHideRangeConfig::getId,rcsMerchantsHideRangeConfig.getIds());
        List<RcsMerchantsHideRangeConfig> list = rcsMerchantsHideRangeConfigMapper.selectList(queryWrapper);
        logBean.setHideMoney(rcsMerchantsHideRangeConfig.getHideMoney());
        logBean.setStatus(rcsMerchantsHideRangeConfig.getStatus());
        logBean.setBeforeString(JSONObject.toJSONString(list));
        return logBean;
    }



    private void afterProcess(ProceedingJoinPoint joinPoint, Object resultObj,RcsQuotaBusinessLimitLogBean logBean) {
        try {
            HttpResponse result = (HttpResponse) resultObj;
            if (code == result.getCode()) {
                pool.execute(() -> {
                    try {
                        logBean.setAfterString(joinPoint.getArgs());
                        sendMessage.sendMessage(CommonConstants.RCS_BUSINESS_LOG_SAVE, null, "", JSONObject.toJSONString(Arrays.asList(logBean)));
                    } catch (Exception e) {
                        log.info("::{}::风控日誌-策略流程异常{}::{}", "", e.getMessage());
                    } finally {
                        log.info("风控日誌-流程結束");
                    }
                });
            }
        } catch (Exception e) {
            log.error("::{}::{}", "", e.getMessage(), e);
        }
    }




}
