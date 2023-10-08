package com.panda.sport.rcs.mgr.operation.rollback;

import com.panda.sport.rcs.mq.utils.Consumer;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.operation.rollback
 * @Description :  回滚
 * @Date: 2020-02-28 10:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class OrderRollbackAdapter {
    private List<OrderRollback> orderRollbackList;

    public OrderRollbackAdapter(List<OrderRollback> orderRollbacks){
        List<OrderRollback> list = new ArrayList<>();
        this.orderRollbackList = orderRollbacks;


    }


}
