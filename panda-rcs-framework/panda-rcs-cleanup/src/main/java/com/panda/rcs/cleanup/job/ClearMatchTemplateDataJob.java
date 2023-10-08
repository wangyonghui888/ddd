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
 * @Date: 2022-05-27 21:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@JobHandler(value = "clearMatchTemplateDataJob")
public class ClearMatchTemplateDataJob extends IJobHandler {


    @Autowired
    private MatchService matchService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Long startTime = System.currentTimeMillis();
//        matchService.cleanTemplateData();
        log.info("清理模板数据耗时={}毫秒", System.currentTimeMillis() - startTime);
        return ReturnT.SUCCESS;
    }

}
