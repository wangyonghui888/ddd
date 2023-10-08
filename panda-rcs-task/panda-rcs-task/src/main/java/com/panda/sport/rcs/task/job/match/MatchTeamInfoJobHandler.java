package com.panda.sport.rcs.task.job.match;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.task.wrapper.RcsLanguageInternationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mongo.MatchTeamVo;
import com.panda.sport.rcs.task.wrapper.MongoService;
import com.panda.sport.rcs.task.wrapper.StandardSportTeamService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

import lombok.extern.slf4j.Slf4j;

import static com.panda.sport.rcs.constants.RedisKey.RCS_TASK_LANGUAGE_CACHE;
import static com.panda.sport.rcs.constants.RedisKey.RCS_TASK_TEAMINFO_CACHE;

@JobHandler(value = "matchTeamInfoJobHandler")
@Component
@Slf4j
public class MatchTeamInfoJobHandler extends IJobHandler {
	
    @Autowired
    private RedisClient redisClient;
	
	private String JOB_SCAN_TIME = "rcs:task:MatchTeamInfoJobHandler:time";

	private static final String PREFIX_TEAM_NAME = "rcs:ws:team:name:%s";

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    
    @Autowired
    private RcsLanguageInternationService languageService;
    
    @Autowired
    StandardSportTeamService standardSportTeamService;
    
    @Autowired
    MongoService mongoService;
	
	@Override
	public ReturnT<String> execute(String param) throws Exception {
		Long endTime = new Date().getTime();
		try {
			String cacheTimeStr = redisClient.get(JOB_SCAN_TIME);
			//如果是第一次启动，往前推五分钟数据查询
			if(StringUtils.isBlank(cacheTimeStr)) cacheTimeStr = String.valueOf(System.currentTimeMillis() - 1000 * 60 * 5);
			
			Long cacheTime = Long.parseLong(cacheTimeStr);
			
			Map<String, Object> queryParmas = new HashMap<String, Object>();
			queryParmas.put("startTime", DateUtils.parseDate(cacheTime, DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
			queryParmas.put("endTime", DateUtils.parseDate(endTime, DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
			List<Map<String, Object>> list = standardMatchInfoMapper.queryUpdateInfoByTime(queryParmas);
			
			if(list == null || list.size() <= 0 ) return ReturnT.SUCCESS;
			
			Map<String, List<Map<String, Object>>> result = list.stream().collect(Collectors.groupingBy(map -> String.valueOf(map.get("id"))));
			for(String matchId : result.keySet() ) {
				MatchMarketLiveBean marketLiveBean = new MatchMarketLiveBean();
				marketLiveBean.setMatchId(Long.parseLong(matchId));
				boolean isUpdateTeam = false;
				for(Map<String, Object> map : result.get(matchId)) {
					String type = String.valueOf(map.get("type"));
					String nameCode = String.valueOf(map.get("name_code"));
					String nameCodeRedisKey = RCS_TASK_LANGUAGE_CACHE+nameCode;
					if(map.get("name_code")!=null){
						if("1".equals(type)) {//联赛
							marketLiveBean.setTournamentNames(languageService.getCachedNamesByCode(Long.parseLong(nameCode)));
						}else if("2".equals(type)) {//球队
							isUpdateTeam = true;
						}
						redisClient.delete(nameCodeRedisKey);
					}else {
						log.error("MatchTeamInfoJobHandler队伍名称为空:{}", JSONObject.toJSONString(result.get(matchId)));
					}

				}
				if(isUpdateTeam) {
					redisClient.delete(RCS_TASK_TEAMINFO_CACHE+matchId);
					List<MatchTeamVo> teamList = standardSportTeamService.queryTeamList(Long.parseLong(matchId));
					marketLiveBean.setTeamList(teamList);
					//ws sean使用
					redisClient.delete(String.format(PREFIX_TEAM_NAME, matchId));
				}
				
				Map map1 = new HashMap<>();
                map1.put("matchId", marketLiveBean.getMatchId());
				String oddsLiveTradeSealKey = String.format("rcs:match:oddsLive:trade:seal:%s",marketLiveBean.getMatchId());
				String oddsLiveTradeSeal = redisClient.get(oddsLiveTradeSealKey);
				if(StringUtils.isNotBlank(oddsLiveTradeSeal)){
					redisClient.delete(oddsLiveTradeSealKey);
					log.info("::{}::MatchTeamInfoJobHandler更新赛事数据:获取切滚球盘封盘状态", marketLiveBean.getMatchId());
					marketLiveBean.setOperateMatchStatus(Integer.valueOf(oddsLiveTradeSeal));
				}
                mongoService.upsert(map1, "match_market_live", marketLiveBean);
			}
			
			
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			redisClient.set(JOB_SCAN_TIME, endTime);
		}
		
		return ReturnT.SUCCESS;
	}

}
