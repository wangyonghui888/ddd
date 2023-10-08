package com.panda.rcs.cleanup.job;

import com.mongodb.client.result.DeleteResult;
import com.panda.rcs.cleanup.utils.DataUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

@Slf4j
@Component
@JobHandler(value = "orderExtCleanupMongoDataJob")
public class OrderExtCleanupMongoDataJob extends IJobHandler {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private JedisCluster jedisCluster;

    // 設置保留時間
    private String keepHoursKey = "rcs:cleanup:orderExt:keepHours";

    private Integer limit = 100000;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Long starTime = System.currentTimeMillis();
        String keepHours = jedisCluster.get(keepHoursKey);
        int hours = Integer.parseInt(null == keepHours ? "24" : keepHours);
        Query query =
            new Query(Criteria.where("createTime").lt(DataUtils.minusTimestampByHours(hours)))
                .limit(limit);
        DeleteResult result = mongoTemplate.remove(query, "t_order_detail_ext");
        log.info("::接拒单表数据清理::，本次清理数据->{}", result.getDeletedCount());

        log.info("::接拒单表数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
        XxlJobLogger.log("::接拒单表数据清理::createTime<{}本次清理数据->{},耗时{}",
                DataUtils.transferLongToDateStrings(DataUtils.minusTimestampByHours(hours)),result.getDeletedCount(),System.currentTimeMillis() - starTime);
        return ReturnT.SUCCESS;
    }

}
