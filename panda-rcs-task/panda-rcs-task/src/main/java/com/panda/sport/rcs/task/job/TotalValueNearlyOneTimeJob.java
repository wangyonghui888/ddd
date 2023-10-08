package com.panda.sport.rcs.task.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.pojo.statistics.RcsTotalValueNearlyOneTime;
import com.panda.sport.rcs.task.wrapper.statistics.RcsTotalValueNearlyOneTimeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xxl.job.core.biz.model.ReturnT.SUCCESS;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.job
 * @Description :  TODO
 * @Date: 2019-12-30 21:02
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@JobHandler(value = "totalValueNearlyOneTimeJobHandler")
@Component
@Slf4j
public class TotalValueNearlyOneTimeJob extends IJobHandler {
    @Autowired
    private RcsTotalValueNearlyOneTimeService rcsTotalValueNearlyOneTimeService;


    @Override
    public ReturnT<String> execute(String s) throws Exception {
        QueryWrapper<RcsTotalValueNearlyOneTime> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("update_time",System.currentTimeMillis()- 60*60*1000);
        rcsTotalValueNearlyOneTimeService.remove(queryWrapper);
        return SUCCESS;
    }
}
