package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.service.IRcsUserConfigNewService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * user-config 优化配置
 *
 * @description:
 * @author: magic
 * @create: 2022-07-18 15:15
 **/
@Slf4j
@Component
@JobHandler(value = "rcsUserConfigDataConvertJob")
public class RcsUserConfigDataConvertJob extends IJobHandler {

    @Autowired
    IRcsUserConfigNewService rcsUserConfigNewService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("rcsUserConfigDataConvertJob:清洗任务开始");
        rcsUserConfigNewService.convertOldDataTask();
        return ReturnT.SUCCESS;
    }

}
