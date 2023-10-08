package com.panda.sport.rcs.task.job.clean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mapper.RcsMonitorMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 清除一周以外的监控数据
 * @author Administrator
 *
 */
@JobHandler(value = "cleanMonitorData")
@Component
@Slf4j
public class CleanMonitorData extends IJobHandler {
	
    @Autowired
    protected  RcsMonitorMapper rcsMonitorMapper;
    
    List<Map<String, String>> paramsMap = new ArrayList<Map<String,String>>();
    
	@Override
	public void init() {
		super.init();
		//监控数据
		addMapParams("rcs_monitor_error_log", "created_time");
		addMapParams("rcs_monitor_garbage_collector", "create_time");
		addMapParams("rcs_monitor_heart_log", "created_time");
		addMapParams("rcs_monitor_memory", "create_time");
		addMapParams("rcs_monitor_mq_info", "crt_time");
		addMapParams("rcs_monitor_service_info", "create_time");
		addMapParams("rcs_monitor_system_info", "create_time");
		addMapParams("rcs_monitor_thread", "create_time");
		//记录数据
		addMapParams("match_event_info_flowing", "insert_time");
		addMapParams("match_statistics_info_detail_flowing", "insert_time");
		addMapParams("match_statistics_info_flowing", "insert_time");
		addMapParams("match_status_flowing", "insert_time");
		addMapParams("rcs_standard_sport_market_sell_flowing", "insert_time");
		addMapParams("standard_sport_market_flowing", "insert_time");
		addMapParams("standard_sport_market_odds_flowing", "insert_time");
	}
	
	private Map<String, String> addMapParams(String table ,String timeName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("table", table);
		map.put("time_field_name", timeName);
		paramsMap.add(map);
		return map;
	}

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		try {
			if((paramsMap == null || paramsMap.size() <= 0 ) && StringUtils.isBlank(param)) {
				return  ReturnT.SUCCESS;
			}
			
			List<Map<String, String>> tempParams = new ArrayList<Map<String,String>>(paramsMap);
			if(!StringUtils.isBlank(param)) {
				try {
					Arrays.asList(param.split(";")).stream().forEach(consumer -> {
						tempParams.add(addMapParams(consumer.split(",")[0], consumer.split(",")[1]));
					});
				}catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
			
			tempParams.stream().forEach(consumer -> {
				log.info("清理数据：{}",JSONObject.toJSONString(consumer));
				rcsMonitorMapper.cleanData(consumer);
				log.info("清理数据：{} , 完成!",JSONObject.toJSONString(consumer));
			});
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		
		return  ReturnT.SUCCESS;
	}

}
