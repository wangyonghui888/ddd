package com.panda.sport.rcs.mgr.aspect;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.mapper.RcsLockMapper;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.pojo.TOrder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.aspect
 * @Description :  RcsLock 切面
 * @Date: 2020-04-01 15:20
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Aspect
@Slf4j
@Component
public class RcsLockAspect {
    @Autowired
    private RcsLockMapper lockMapper;

    @Autowired
    private TOrderMapper orderMapper;


    @Before("@annotation(RcsLockable)")
    public void doBefore(JoinPoint point) {
    }

    /**
     * 锁
     * @param point
     * @return
     */
    private Boolean lock(JoinPoint point){
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method targetMethod = AopUtils.getMostSpecificMethod(methodSignature.getMethod(), point.getTarget().getClass());
        String targetName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] arguments = point.getArgs();

        RcsLockable rcsLockable = targetMethod.getAnnotation(RcsLockable.class);

        for (Object obj : arguments) {
            if (obj.getClass().getName().equals(OrderBean.class.getName())) {
                OrderBean orderBean = (OrderBean) obj;
                String orderNo = orderBean.getOrderNo();
                if(orderBean.getSeriesType() == null){
                    QueryWrapper<TOrder> queryWrapper  = new QueryWrapper<>();
                    queryWrapper.lambda().eq(TOrder::getOrderNo,orderNo);
                    queryWrapper.lambda().select(TOrder::getSeriesType);
                    TOrder order = orderMapper.selectOne(queryWrapper);
                    orderBean.setSeriesType(order.getSeriesType());
                }

                if ((rcsLockable.seriesType()==RcsLockSeriesTypeEnum.Single && orderBean.getSeriesType().equals(RcsLockSeriesTypeEnum.Single.getCode())) ||
                        (rcsLockable.seriesType() == RcsLockSeriesTypeEnum.Duplex && orderBean.getSeriesType().equals(RcsLockSeriesTypeEnum.Duplex.getCode())) ||
                        rcsLockable.seriesType() == RcsLockSeriesTypeEnum.All) {
                    String lock = String.format("%s_%s", rcsLockable.key(), orderNo);
                    if (lockMapper.saveLock(lock) <= 0) {
                        log.error("::{}::单号以处理，不再处理当前锁:{}",orderNo,rcsLockable.key());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 环绕处理
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(RcsLockable)")
    public Object Around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if(lock(proceedingJoinPoint)) {
            Object obj= proceedingJoinPoint.proceed();
            return obj;
        }else{
            return null;
        }
    }
}
