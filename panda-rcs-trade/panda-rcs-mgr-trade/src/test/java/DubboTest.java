import com.panda.sport.data.rcs.api.trade.RedisApiService;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.trade.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@Slf4j
public class DubboTest {

    @Reference(check = false, lazy = true, retries = 3, timeout = 3000)
    private RedisApiService redisApiService;

	@Test
	public void test() {
        String key = RedisKey.getRelevanceTypeKey(3405907L, 40L);
        Map<String, String> map =  redisApiService.hgetAll(key).getData();
        for(String str : map.keySet()){
            log.info("=====>"+ str);
        }
    }
}
