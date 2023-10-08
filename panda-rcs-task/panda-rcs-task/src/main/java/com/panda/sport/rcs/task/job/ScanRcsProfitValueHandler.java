package com.panda.sport.rcs.task.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.task.job
 * @Description :  批量期望值缓存
 * @Date: 2019-12-07 11:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@JobHandler(value="scanRcsProfitValueHandler")
@Component
@Slf4j
public class ScanRcsProfitValueHandler extends IJobHandler {

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        return null;
    }
}
