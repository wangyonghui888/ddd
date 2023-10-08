package com.panda.sport.rcs.console.mq.impl;


import java.util.HashMap;
import java.util.Map;

import com.panda.sport.rcs.console.common.utils.DateUtils;
import com.panda.sport.rcs.console.service.LogBeanService;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.log.interceptors.LogBean;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class LogHttpConsumer extends ConsumerAdapter<LogBean> {

    @Autowired
    private LogBeanService logBeanService;

    @Autowired
    private RedisClient redisClient;

    public static final String RCSCACHE_LOG_HEADER = "rcs:log:header:%s";
    
    private Map<String, Long> timeMap = new HashMap<String, Long>();

    public LogHttpConsumer() {
        super("RCS_HTTP_LOG", "");
    }

    @Override
    public Boolean handleMs(LogBean bean, Map<String, String> paramsMap) {
        log.info("收到http请求消息jsonData：{}, paramsMap：{}", JSONObject.toJSONString(bean), JSONObject.toJSONString(paramsMap));
        //{"code":"/standardMatchInfo/getCurrentAllMatch","exeTime":597,"name":"MTS应急开关获取赛事接口","requestVal":"{\"sportId\":1,\"pageSize\":50,\"currentPage\":1}","returnVal":"{\"code\":200,\"msg\":\"成功\",\"data\":{\"records\":[],\"total\":0,\"size\":50,\"current\":1,\"orders\":[],\"searchCount\":true,\"pages\":0},\"success\":true}","title":"{\"currentPage\":\"当前查询页\",\"pageSize\":\"数据数量\",\"sportId\":\"体育类型\"}","url":"/standardMatchInfo/getCurrentAllMatch","values":"{\"currentPage\":1,\"pageSize\":50,\"sportId\":1}"}
        try {
            bean.setCreateTime(DateUtils.getCurrentDate());
            logBeanService.insert(bean);
            
            if(timeMap.get(bean.getCode()) == null  || timeMap.get(bean.getCode()) - System.currentTimeMillis() > 1000 * 60 * 60 ) {
            	String key = String.format(RCSCACHE_LOG_HEADER, bean.getCode());
            	timeMap.put(bean.getCode(), System.currentTimeMillis());
            	redisClient.setExpiry(key, bean.getTitle(), 1000*60*60*24*7L);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }

        return true;
    }
}
