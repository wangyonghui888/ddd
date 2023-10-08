package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.pojo.LogRecord;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.log.interceptors.LogBean;

/**
 *
 */
public interface LogBeanService {

    int insert(LogBean logBean);

    /**
     * 分页查询操盘记录
     * @param logBean
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageDataResult getLogRecords(LogRecord logBean, Integer pageNum, Integer pageSize);
}
