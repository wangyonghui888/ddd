import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.trade.Bootstrap;
import com.panda.sport.rcs.trade.util.RedissonManager;
import com.panda.sport.rcs.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.JedisCluster;
@SpringBootTest(classes = Bootstrap.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@Slf4j
public class RedissonManagerConnTest {

    /**
     * 应用于redission 测试
     */
    private String REDIS_CLIENT_TRADE_TEST_KEY="redis_client_trade_test_key";
    /**
     * 应用于redisson 缓存测试
     */
    private String REDIS_CLIENT_TRADE_TEST_CACHE="redis_client_trade_test_cache";
    /**
     * 应用于 jedis 测试
     */
    private String JEDIES_TRADE_TEST_KEY="jedis_trade_test_key";
    /**
     * 应用于 jedis缓存测试
     */
    private String JEDIES_TRADE_TEST_CACHE="jedis_trade_test_key";

    private Long EXPIRED_TIMES=60*5*1000L;
    @Autowired
    private RedissonManager redissonManager;

    private Redisson redisson;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private JedisCluster jedisCluster;
    @Before
    public void connTest() {
         redisson = redissonManager.getRedisson();
        assert redisson != null;

    }

    @Test
    public void lockTest() {
      /*  boolean b = redissonManager.lockA(redisson, "RCS:TEST", 300);
        System.out.println("redisson conn success");
        redissonManager.unlockA(redisson, "RCS:TEST");
        redisClient.setExpiry(REDIS_CLIENT_TRADE_TEST_KEY,new Date().getTime(),EXPIRED_TIMES);*/
       /* redissionLockTest();
        redissionCacheTest();
        jedisLockTest();*/
        String linkId = CommonUtils.mdcPut();
        MDC.put("X-B3-TraceId", "tradeId");
        log.info("::{}::linkId:{},traceId:{}",linkId,linkId,"tradeId");


    }

    /**
     * redissonq分布式锁测试
     */
    private void redissionLockTest(){
        if (redissonManager.isLockA(redisson,REDIS_CLIENT_TRADE_TEST_KEY)) {
            redissonManager.unlockA(redisson,REDIS_CLIENT_TRADE_TEST_KEY);
        }
        boolean lock =redissonManager.lockA(redisson, REDIS_CLIENT_TRADE_TEST_KEY, 300);
        Assert.assertEquals(lock,true);
        boolean isLock= redissonManager.isLockA(redisson,REDIS_CLIENT_TRADE_TEST_KEY);
        Assert.assertEquals(isLock,true);
        redissonManager.unlockA(redisson,REDIS_CLIENT_TRADE_TEST_KEY);
        isLock=redissonManager.isLockA(redisson,REDIS_CLIENT_TRADE_TEST_KEY);
        Assert.assertEquals(isLock,false);
    }

    /**
     * redis缓存设置测试
     */
    private void redissionCacheTest(){
        redisClient.setExpiry(REDIS_CLIENT_TRADE_TEST_CACHE,"1",300L);
        String value= redisClient.get(REDIS_CLIENT_TRADE_TEST_CACHE);
        Assert.assertEquals(value,"1");
    }

    /**
     * jedis 分布式锁测试
     */
    private void jedisLockTest(){
        try {
            boolean getLock= jedisCluster.setnx(JEDIES_TRADE_TEST_KEY,"1").equals(1L);
            if (!getLock) {
                jedisCluster.del(JEDIES_TRADE_TEST_KEY);
            }
            Assert.assertEquals(getLock,true);
            getLock=jedisCluster.setnx(JEDIES_TRADE_TEST_KEY,"1").equals(1L);
            Assert.assertEquals(getLock,false);
            jedisCluster.del(JEDIES_TRADE_TEST_KEY);
            getLock=jedisCluster.setnx(JEDIES_TRADE_TEST_KEY,"1").equals(1L);
            Assert.assertEquals(getLock,true);
        } finally {
            jedisCluster.del(JEDIES_TRADE_TEST_KEY);
        }

    }


}
