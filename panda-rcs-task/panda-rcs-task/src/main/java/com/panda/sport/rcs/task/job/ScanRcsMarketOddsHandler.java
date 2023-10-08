package com.panda.sport.rcs.task.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsMarketOddsConfigMapper;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo.MatchMarketOddsFieldVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 废弃不在使用
 * @author Administrator
 *
 */
@JobHandler(value="scanRcsMarketOddsHandler")
@Component
@Slf4j
public class ScanRcsMarketOddsHandler extends IJobHandler {
	
	@Autowired
	private RedisClient redisClient;
	
	@Autowired
	private RcsMarketOddsConfigMapper rcsMarketOddsConfigMapper;
	
    @Autowired
    MongoTemplate mongotemplate;
    
    @Autowired
    private MatchServiceImpl matchServiceImpl;
	
	private String SCAN_RCS_MARKET_ODDS_CACHE_KEY = "rcs:task:scan:SCAN_RCS_MARKET_ODDS_CACHE_KEY";
	
	@Override
	public ReturnT<String> execute(String param) throws Exception {
//		Long endTime = System.currentTimeMillis();
//		String startTime = redisClient.get(SCAN_RCS_MARKET_ODDS_CACHE_KEY);
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		if(startTime != null ) map.put("START_TIME", DateUtils.parseDate(Long.parseLong(startTime), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
//		map.put("END_TIME", DateUtils.parseDate(endTime, DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
//		List<RcsMarketOddsConfig> list = rcsMarketOddsConfigMapper.queryListByTime(map);
//		if(list == null)  list = new ArrayList<RcsMarketOddsConfig>();
//		
//		for(RcsMarketOddsConfig config : list) {
//			updateMarketOddsData(config);
//		}
//		
//		redisClient.set(SCAN_RCS_MARKET_ODDS_CACHE_KEY, String.valueOf(endTime));
		
		return SUCCESS;
	}
	
//	private void updateMarketOddsData(RcsMarketOddsConfig config) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("matchId").is(String.valueOf(config.getMatchId())).and("id").is(config.getMarketCategoryId()));
//        MarketCategory category = mongotemplate.findOne(query, MarketCategory.class);
//        if (category != null) {
//        	MatchMarketLiveOddsVo.MatchMarketVo vo = category.getMatchMarketVoList().get(0);
//        	if(vo == null || vo.getOddsFieldsList() == null ) return;
//        	for(MatchMarketOddsFieldVo oddField : vo.getOddsFieldsList()) {
//        		if(!String.valueOf(oddField.getId()).equals(String.valueOf(config.getMarketOddsId())))  continue;
//
//    			oddField.setBetAmount(NumberUtils.getBigDecimal(config.getBetAmount()));
//    			oddField.setProfitValue(NumberUtils.getBigDecimal(config.getProfitValue()));
//    			oddField.setBetNum(NumberUtils.getBigDecimal(config.getBetOrderNum()));
//    			break;
//        	}
//        	
//        	matchServiceImpl.updateMongodbOdds(String.valueOf(config.getMatchId()), config.getMarketCategoryId().longValue(), config.getMatchMarketId(), category);
//        }
//	}
}
