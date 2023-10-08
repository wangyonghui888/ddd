import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.trade.Bootstrap;
import com.panda.sport.rcs.trade.wrapper.SportMatchViewService;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@Slf4j
public class MatchInfoQueryTest {

    @Resource
    private SportMatchViewService sportMatchViewService;
    @Test
    public void test(){
        MatchMarketLiveBean matchMarketLiveBean = sportMatchViewService.queryByMatchId(3399292L, "");
        System.out.println(matchMarketLiveBean);
    }
}
