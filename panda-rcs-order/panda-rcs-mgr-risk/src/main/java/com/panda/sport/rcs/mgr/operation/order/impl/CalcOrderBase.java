package com.panda.sport.rcs.mgr.operation.order.impl;

import com.panda.sport.rcs.mgr.operation.order.CalcOrder;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.operation.order.impl
 * @Description :  mq order处理基类
 * 实现自定义排序
 * 升序
 * @Date: 2019-12-28 16:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public abstract class CalcOrderBase  implements Comparable<CalcOrder> {
    @Override
    public int compareTo(CalcOrder calcOrder) {
        Order order1 = this.getClass().getAnnotation(Order.class);
        Order order2 = AnnotationUtils.findAnnotation(calcOrder.getClass(), Order.class);


        if(order1 == null || order2==null) return 0;
        if (order1.value() < order2.value()) {
            return -1;
        } else {
            return 1;
        }
    }
}