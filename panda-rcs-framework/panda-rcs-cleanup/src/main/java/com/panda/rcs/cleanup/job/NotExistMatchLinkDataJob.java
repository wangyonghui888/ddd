package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.service.MatchService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobHandler(value = "notExistMatchLinkDataJob")
public class NotExistMatchLinkDataJob extends IJobHandler {

    @Autowired
    private MatchService matchService;


    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Long starTime = System.currentTimeMillis();
        matchService.cleanupNotExistMatchLinkData();
        log.info("::不存在赛事关联数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
        return ReturnT.SUCCESS;
    }

}
