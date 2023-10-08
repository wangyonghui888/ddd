package com.panda.sport.rcs.task.mq.impl;

import static com.panda.sport.rcs.common.MqConstants.MARKET_WATER_CONFIG_TOPIC;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mongo.MatchMarketVo;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.task.config.RedissonManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 调整自动水差状态改变更新mongodb
 *
 * @author enzo
 */
@Component
@Slf4j
public class MarketWaterConfigUpdateConsumer extends ConsumerAdapter<List<RcsMatchMarketConfig>> {

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private RedissonManager redissonManager;

    public MarketWaterConfigUpdateConsumer() {
        super(MARKET_WATER_CONFIG_TOPIC, "rocketmq.market.change.config");
    }

    @Override
    public Boolean handleMs(List<RcsMatchMarketConfig> rcsMatchMarketConfigs, Map<String, String> paramsMap) {
        Long matchId = null;
        String lock = "";
        log.info("MarketWaterConfigUpdateConsumer接收参数:{}", JsonFormatUtils.toJson(rcsMatchMarketConfigs));
        try {
            //根据玩法ID分组
            Map<Long, List<RcsMatchMarketConfig>> collect = rcsMatchMarketConfigs.stream().filter(filter -> null != filter.getPlayId()).collect(Collectors.groupingBy(RcsMatchMarketConfig::getPlayId));
            if (collect.size() > 0) {
                for (Map.Entry<Long, List<RcsMatchMarketConfig>> mapGroup : collect.entrySet()) {
                    List<RcsMatchMarketConfig> matchMarketConfigList = mapGroup.getValue();
                    for (RcsMatchMarketConfig matchMarketConfig : matchMarketConfigList) {
                        matchId = matchMarketConfig.getMatchId();
                        Long marketId = matchMarketConfig.getMarketId();
                        lock = String.format("MONGODB_MARKET_%s_%s", matchId, matchMarketConfig.getPlayId());
                        try {
                        	redissonManager.lock(lock);
                            Query query = new Query();
                            query.addCriteria(Criteria.where("matchId").is(String.valueOf(matchId)).and("id").is(matchMarketConfig.getPlayId()));
                            MarketCategory category = mongotemplate.findOne(query, MarketCategory.class);
                            List<MatchMarketVo> matchMarketVoList = category.getMatchMarketVoList();
                            matchMarketVoList.stream().forEach(model -> {
                            	if (String.valueOf(marketId).equals(String.valueOf(model.getId()))) {
                                    //盘口水差 差值
                                    String water = isBlank(matchMarketConfig.getHomeAutoChangeRate()) ? (isBlank(matchMarketConfig.getAwayAutoChangeRate()) ? null : matchMarketConfig.getAwayAutoChangeRate()) : matchMarketConfig.getHomeAutoChangeRate() ;
                                    
                                    if(water == null ) {
                                    	model.setChangeRateStatus(0);
                                    	model.setWaterValue(null);
                                    }else {
                                    	model.setChangeRateStatus(1);
                                    	model.setWaterValue(NumberUtils.getBigDecimal(water).multiply(NumberUtils.getBigDecimal(100)));
                                    }
                                }

                            });
                            System.out.println(JSONObject.toJSONString(matchMarketVoList));
                            Update update = new Update();
                            update.set("matchMarketVoList", matchMarketVoList);
                            mongotemplate.updateFirst(query, update, MarketCategory.class);
                        }catch (Exception e) {
                        	log.error(e.getMessage(),e);
                        }finally {
                        	redissonManager.unlock(lock);
						}
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
        	redissonManager.unlock(lock);
            /*去除更新赛事表里的玩法list
            if (matchId != null) {
                MatchMarketLiveUpdateBean updateBean = new MatchMarketLiveUpdateBean(matchId);
                producerSendMessageUtils.sendMessage(MatchMarketLiveUpdateConsumer.MATCH_MARKET_LIVE_UPDATE_TOPIC, updateBean);
            }*/
        }
        return true;
    }
    
    private static Boolean isBlank(String water) {
    	if(StringUtils.isBlank(water) || "null".equals(water) ) {
    		return true;
    	}
    	
    	if(NumberUtils.isNumber(water) && Double.valueOf(water).equals(Double.parseDouble("0")) ) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static void main(String[] args) {
    	String bean = "{\"awayAutoChangeRate\":\"0.00\",\"homeAutoChangeRate\":\"0.02\",\"marketId\":1294625950412873731,\"matchId\":128720,\"playId\":4,\"tieAutoChangeRate\":\"null\"}";
    	
    	RcsMatchMarketConfig matchMarketConfig = JSONObject.parseObject(bean,RcsMatchMarketConfig.class);
    	String water = isBlank(matchMarketConfig.getHomeAutoChangeRate()) ? (isBlank(matchMarketConfig.getAwayAutoChangeRate()) ? null : matchMarketConfig.getAwayAutoChangeRate()) : matchMarketConfig.getHomeAutoChangeRate() ;
    	
    	System.out.println(water);
	}
}
