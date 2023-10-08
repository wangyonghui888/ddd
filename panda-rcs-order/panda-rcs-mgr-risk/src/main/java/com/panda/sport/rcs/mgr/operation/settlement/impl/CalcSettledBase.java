package com.panda.sport.rcs.mgr.operation.settlement.impl;

import com.panda.sport.rcs.mgr.operation.settlement.CalcSettled;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.operation.settlement.impl
 * @Description :  自定义排序
 * 升序
 * @Date: 2019-12-28 16:11
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class CalcSettledBase  implements Comparable<CalcSettled> {
    @Override
    public int compareTo(CalcSettled calcSettled) {
        Order order1 = this.getClass().getAnnotation(Order.class);
        Order order2 = AnnotationUtils.findAnnotation(calcSettled.getClass(), Order.class);

        if(order1 ==null || order2 == null ){
            return 0;
        }

        if (order1.value() < order2.value()) {
            return -1;
        } else {
            return 1;
        }
    }
}
