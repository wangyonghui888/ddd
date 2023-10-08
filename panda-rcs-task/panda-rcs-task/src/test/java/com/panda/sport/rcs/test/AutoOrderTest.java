/*
package com.panda.sport.rcs.test;

import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.job.orderMode.AutoOrderJobHandler;
import com.panda.sport.rcs.task.job.orderMode.AutoOrderJobHandlerOld;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

*/
/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.test
 * @Description :  TODO
 * @Date: 2020-04-03 13:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 *//*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class AutoOrderTest {
    private int i = 10;
    private Object object = new Object();

    @Autowired
    private AutoOrderJobHandler autoOrderJobHandler;

    @Test
    public void test() throws Exception {
        //autoOrderJobHandler.execute(null);
        MyThread thread = new MyThread();
        thread.start();

        MyThread thread1 = new MyThread();
        thread1.start();


        MyThread thread2 = new MyThread();
        thread2.start();
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            i++;
            System.out.println("i:" + i);
            try {
                System.out.println("线程" + Thread.currentThread().getName() + "进入睡眠状态");
                //Thread.currentThread().sleep(10000);
                autoOrderJobHandler.getOrderAcceptConfigs(363317L, 961L);
            } catch (Exception e) {
                // TODO: handle exception
            }
            System.out.println("线程" + Thread.currentThread().getName() + "睡眠结束");
            i++;
            System.out.println("i:" + i);
        }
    }
}*/
