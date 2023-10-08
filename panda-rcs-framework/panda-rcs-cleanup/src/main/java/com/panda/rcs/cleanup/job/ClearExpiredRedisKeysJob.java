package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.TempRedisKesMapper;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 过期redis数据清理
 */
@Slf4j
@Component
@JobHandler(value = "clearExpiredRedisKeysJob")
public class ClearExpiredRedisKeysJob extends IJobHandler {

    @Autowired
    private TempRedisKesMapper tempRedisKesMapper;

    @Autowired
    private RedisClient redisClient;

    static final List<String> dateLists = new ArrayList<String>(){{
        this.add("01");
        this.add("02");
        this.add("03");
        this.add("04");
        this.add("05");
        this.add("06");
        this.add("07");
        this.add("08");
        this.add("09");
        this.add("10");
        this.add("11");
        this.add("12");
        this.add("13");
        this.add("14");
        this.add("15");
        this.add("16");
        this.add("17");
        this.add("18");
        this.add("19");
        this.add("20");
        this.add("21");
        this.add("22");
        this.add("23");
        this.add("24");
        this.add("25");
        this.add("26");
        this.add("27");
        this.add("28");
        this.add("29");
        this.add("30");
        this.add("31");
    }};

    @Override
    public ReturnT<String> execute(String saveDate) throws Exception {
        Long startTime = System.currentTimeMillis();
        log.info("请求参数 = {}", saveDate);
        for (String date : dateLists){
            String selectDate = saveDate + "-" + date;
            List<String> redisKeysLists = tempRedisKesMapper.getRedisKeysLists(selectDate);
            if(redisKeysLists != null && redisKeysLists.size() > 0){
                log.info("清理日期={}，一共清理条数={}", selectDate, redisKeysLists.size());
                for (String key : redisKeysLists){
                    log.info("清理Redis-Key={}", key);
                    redisClient.delete(key);
                    Thread.sleep(100);
                }
            }
            Thread.sleep(20000);
        }
        log.info("清理{}日数据耗时={}毫秒", saveDate, System.currentTimeMillis() - startTime);
        return ReturnT.SUCCESS;
    }
}
