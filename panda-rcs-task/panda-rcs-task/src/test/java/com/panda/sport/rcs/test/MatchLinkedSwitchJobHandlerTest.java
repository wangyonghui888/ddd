package com.panda.sport.rcs.test;


import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.job.match.MatchLinkedSwitchJobHandler;
import com.panda.sport.rcs.task.job.tourTemplate.BasketballSyncTemplateJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * L/A+模式默认切换
 *
 * @author wealth
 * @ClassName: MatchLinkedSwitchJobHandler
 * @Description: TODO
 * @date 2022年6月20日 下午15:06:18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class MatchLinkedSwitchJobHandlerTest {

    @Autowired
    private MatchLinkedSwitchJobHandler matchLinkedSwitchJobHandler;

    @Autowired
    private BasketballSyncTemplateJob basketballSyncTemplateJob;

    @Test
    public void test() throws Exception{
        //matchLinkedSwitchJobHandler.execute(null);

        basketballSyncTemplateJob.execute(null);
    }
}
