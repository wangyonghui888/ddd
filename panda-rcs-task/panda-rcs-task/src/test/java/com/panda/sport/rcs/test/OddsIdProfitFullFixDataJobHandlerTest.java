//package com.panda.sport.rcs.test;
//
//import com.panda.sport.rcs.task.RcsTaskApplication;
//import com.panda.sport.rcs.task.job.operation.OddsIdProfitFullFixDataJobHandler;
//import com.panda.sport.rcs.task.job.orderMode.UpdateOrderStatusHandlerOld;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author :  toney
// * @Project Name :  rcs-parent
// * @Package Name :  com.panda.sport.rcs.test
// * @Description :  TODO
// * @Date: 2020-01-27 16:46
// * @ModificationHistory Who    When    What
// * --------  ---------  --------------------------
// */
//@SpringBootTest(classes=RcsTaskApplication.class)
//@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
//public class OddsIdProfitFullFixDataJobHandlerTest {
//    @Autowired
//    private OddsIdProfitFullFixDataJobHandler oddsIdProfitFullFixDataJobHandler;
//
//    @Autowired
//    private UpdateOrderStatusHandlerOld manualOrderRefusalOrderHandler;
//
//    @Test
//    public void test() throws  Exception{
//        oddsIdProfitFullFixDataJobHandler.execute("298382");
//        //manualOrderRefusalOrderHandler.execute("");
//    }
//}
