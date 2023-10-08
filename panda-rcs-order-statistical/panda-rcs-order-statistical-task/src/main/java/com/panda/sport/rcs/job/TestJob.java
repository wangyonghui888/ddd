package com.panda.sport.rcs.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@JobHandler(value = "lithanTestJob")
@Component
public class TestJob extends IJobHandler {

    Logger log = LoggerFactory.getLogger(TestJob.class);

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("您好..lithan ");
        return null;
    }
}
