package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.console.dao.LogBeanMapper;
import com.panda.sport.rcs.console.pojo.LogRecord;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.LogBeanService;
import com.panda.sport.rcs.log.interceptors.LogBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class LogBeanServiceImpl implements LogBeanService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LogBeanMapper logBeanMapper;

    @Override
    public int insert(LogBean logBean) {
        return logBeanMapper.insert(logBean);
    }

    @Override
    public PageDataResult getLogRecords(LogRecord logBean, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(logBean.getRequestKey())) {
            logBean.setRequestKey(logBean.getRequestKey().trim());
        }
        if (StringUtils.isNotBlank(logBean.getRequestValue())) {
            logBean.setRequestVal(logBean.getRequestValue().trim());
        }
        if (StringUtils.isNotBlank(logBean.getUrl())) {
            logBean.setUrl(logBean.getUrl().trim());
        }
        if (StringUtils.isNotBlank(logBean.getUserId())) {
            logBean.setUserId(logBean.getUserId().trim());
        }
        if (StringUtils.isNotBlank(logBean.getIp())) {
            logBean.setIp(logBean.getIp().trim());
        }
        if (StringUtils.isBlank(logBean.getStartTime())) {
            logBean.setStartTime(DateUtils.transferLongToDateStrings(System.currentTimeMillis()-12*60*60*1000));
        }

        List<LogBean> orderList = logBeanMapper.selectLogRecords(logBean);
        if (orderList.size() > 0) {
            PageInfo<LogBean> pageInfo = new PageInfo<>(orderList);
            pageDataResult.setList(orderList);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }


}
