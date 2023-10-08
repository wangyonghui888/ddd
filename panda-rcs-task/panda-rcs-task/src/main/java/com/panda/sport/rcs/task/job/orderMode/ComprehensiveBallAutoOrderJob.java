
package com.panda.sport.rcs.task.job.orderMode;

import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import com.panda.sport.rcs.utils.CommonServer;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 综合球种定时任务扫描ext表
 */
//@JobHandler(value = "ComprehensiveBallAutoOrderJob")
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class ComprehensiveBallAutoOrderJob extends IJobHandler {
//
//    private final TOrderDetailExtMapper tOrderDetailExtMapper;
//    private final CommonServer commonServer;
//
//    @Override
//    public ReturnT<String> execute(String s) throws Exception {
//        log.info("综合球种开始执行扫描ext定时任务:{}", s);
//        //查询不包含篮球和足球的所有单关数据
//        List<TOrderDetailExt> list = tOrderDetailExtMapper.selectWaitedOrderListComprehensive(System.currentTimeMillis());
//        commonServer.commonMethod(list);
//        return ReturnT.SUCCESS;
//    }
//}