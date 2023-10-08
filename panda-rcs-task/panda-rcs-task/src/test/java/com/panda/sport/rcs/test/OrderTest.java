package com.panda.sport.rcs.test;

import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.vo.OrderDetailStatisticVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.test
 * @Description :  TODO
 * @Date: 2019-12-24 17:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class OrderTest {
    @Autowired
    private TOrderDetailMapper tOrderDetailMapper;

    @Test
    public void getx() {
        Set<String> betNoSet = new LinkedHashSet<>();
        betNoSet.add("20026315481088");
        betNoSet.add("20041351790592");
        List<OrderDetailStatisticVo> orderDetailStatisticVos = tOrderDetailMapper.selectOrderDetailStatisticVoList(betNoSet);
        System.out.println(orderDetailStatisticVos);
    }

}
