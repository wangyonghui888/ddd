package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.MatchMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobHandler(value = "marketOddsCleanJob")
public class MarketOddsCleanJob extends IJobHandler {

    @Autowired
    private MatchMapper matchMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        return null;
    }
}
