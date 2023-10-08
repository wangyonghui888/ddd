package com.panda.sport.rcs.task.job;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteResult;
import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import com.panda.sport.rcs.pojo.TOrderDetailExtDO;
import com.panda.sport.rcs.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.utils.TOrderDetailExtUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@JobHandler(value = "migrateOrderExtHandler")
@Component
@Slf4j
public class MigrateOrderExtHandler extends IJobHandler {

    @Autowired
    private TOrderDetailExtUtils tOrderDetailExtUtils;
    @Autowired
    private TOrderDetailExtMapper tOrderDetailExtMapper;
    @Autowired
    private TOrderDetailExtRepository tOrderDetailExtRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    private Date queryLastTime = null;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
    	String linkId = "migrateOrderExtHandler";
        try {
        	log.info("::{}::-开始执行定时搬移t_order_detail_ext", linkId);

            if (tOrderDetailExtUtils.isSaveToMongo()) {
                List<TOrderDetailExt> extList = tOrderDetailExtMapper.queryUnHandleOrder(queryLastTime);

                if (extList.size() > 0) {
                    BulkWriteResult result;
                    BulkOperations operation = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "t_order_detail_ext");
                    extList.forEach(ext -> {
                        operation.insert(new TOrderDetailExtDO(ext));
                        queryLastTime = ext.getCrtTime();
                    });
                    try {
                        result = operation.execute();
                    } catch (DuplicateKeyException e) {
                        result = ((MongoBulkWriteException) e.getCause()).getWriteResult();
                        log.info("::{}::-转移t_order_detail_ext至Mongo錯誤:{}", linkId , e.getMessage(), e);
                    }
                    log.info("::{}::-待轉移總比數 {} documents: inserted={}, modified={}, upserted={}", linkId,
                            extList.size(), result.getInsertedCount(), result.getModifiedCount(), result.getUpserts().size());
                }
            }
            log.info("::{}::-結束执行定时搬移t_order_detail_ext", linkId);
        } catch (Exception e) {
        	log.info("::{}::-轉移t_order_detail_ext至Mongo錯誤:{}", linkId , e.getMessage(), e);
        }
        return SUCCESS;
    }
}
