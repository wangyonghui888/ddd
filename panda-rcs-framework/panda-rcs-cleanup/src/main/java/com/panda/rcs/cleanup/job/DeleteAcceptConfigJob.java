package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.service.MatchService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-framework
 * @Package Name :  com.panda.rcs.cleanup.job
 * @Description :  TODO
 * @Date: 2023-02-06 11:53
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@JobHandler(value = "deleteAcceptConfigJob")
public class DeleteAcceptConfigJob extends IJobHandler {
    @Autowired
    MatchService matchService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        matchService.deleteAcceptConfig();
        return ReturnT.SUCCESS;
    }
}
