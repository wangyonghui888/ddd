package com.panda.sport.rcs.task.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 账务时间变更，广播到前端
 * 中午12点定时
 */
@JobHandler(value = "accountTimeChangeJobHandler")
@Component
@Slf4j
public class AccountTimeChangeJobHandler extends IJobHandler {

	@Autowired
	private ProducerSendMessageUtils send;

    @Override
    public ReturnT<String> execute(String s) {
        try {
        	String afterDate = DateUtils.parseDate(new Date().getTime(), DateUtils.YYYYMMDD);
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("change_before", DateUtils.parseDate(DateUtils.addNHour(new Date(), -24).getTime(), DateUtils.YYYYMMDD));
        	map.put("change_after", afterDate);
        	map.put("command", "5001");
        	send.sendMessage(String.format("RCS_WS_BROADCAST,%s", afterDate), map);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return SUCCESS;
    }

    public static void main(String[] args) {
    	String afterDate = DateUtils.parseDate(new Date().getTime(), DateUtils.YYYYMMDD);
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("change_before", DateUtils.parseDate(DateUtils.addNHour(new Date(), -24).getTime(), DateUtils.YYYYMMDD));
    	map.put("change_after", afterDate);
    	map.put("command", "5001");
    	System.out.println(JSONObject.toJSONString(map));
    	
	}

}
