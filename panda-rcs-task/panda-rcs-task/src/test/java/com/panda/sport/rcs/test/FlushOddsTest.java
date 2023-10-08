package com.panda.sport.rcs.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.mq.bean.DataRealTimeMessageBean;
import com.panda.sport.rcs.task.wrapper.impl.MarketViewServiceImpl;
import com.panda.sport.rcs.task.wrapper.impl.StandardSportMarketOddsServiceImpl;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class FlushOddsTest {

	@Autowired
	MongoTemplate mongotemplate;
	
	@Autowired
	MarketViewServiceImpl marketViewServiceImpl;
	
	@Autowired
	StandardSportMarketMapper standardSportMarketMapper;
	
	@Autowired
	StandardMatchInfoMapper standardMatchInfoMapper;
	
	@Autowired
	ProducerSendMessageUtils producerSendMessageUtils;
	
	@Autowired
	StandardSportMarketOddsServiceImpl 	standardSportMarketOddsServiceImpl;
	
    @org.junit.Test
    public void test(){
		Long[] ids = {275892l,275893l,275894l};
		for(Long id : ids) {
			StandardMatchInfo matchInfo = standardMatchInfoMapper.selectById(id);
			StandardMatchMarketMessage standardMatchMarketMessage = new StandardMatchMarketMessage();
			standardMatchMarketMessage.setStandardTournamentId(matchInfo.getStandardTournamentId());
			standardMatchMarketMessage.setStandardMatchInfoId(id);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("standard_match_info_id", id);
//    		map.put("market_category_id", 1);
			List<StandardSportMarket> list =  standardSportMarketMapper.selectByMap(map);
			List<StandardMarketMessage> marketList = new ArrayList<>();
			for(StandardSportMarket marketBean : list) {
				StandardMarketMessage messageBean = BeanCopyUtils.copyProperties(marketBean, StandardMarketMessage.class);
				QueryWrapper<StandardSportMarketOdds> queryWrapper = new QueryWrapper<StandardSportMarketOdds>();
				queryWrapper.lambda().eq(StandardSportMarketOdds::getMarketId, marketBean.getId());
				List<StandardSportMarketOdds> oddslist = standardSportMarketOddsServiceImpl.list(queryWrapper);
				List<StandardMarketOddsMessage> oddmsgList = new ArrayList();
				for(StandardSportMarketOdds odd : oddslist) {
					StandardMarketOddsMessage oddMessageBean = BeanCopyUtils.copyProperties(odd, StandardMarketOddsMessage.class);
					oddMessageBean.setPaOddsValue(oddMessageBean.getOddsValue());
					oddMessageBean.setOddsFieldsTemplateId(odd.getOddsFieldsTempletId());
					oddmsgList.add(oddMessageBean);
				}
				messageBean.setMarketOddsList(oddmsgList);
				marketList.add(messageBean);
			}
			standardMatchMarketMessage.setMarketList(marketList);
			DataRealTimeMessageBean<StandardMatchMarketMessage> msgBean = new DataRealTimeMessageBean<StandardMatchMarketMessage>();
			msgBean.setLinkId(UUID.randomUUID().toString().replace("-", ""));
			msgBean.setData(standardMatchMarketMessage);
			producerSendMessageUtils.sendMessage("FLUSH_ODDS_TOPIC", msgBean);
			System.out.println(JSONObject.toJSONString(msgBean));
		}
    }
    
    public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("matchId", "50313");
		map.put("playId", "5");
		map.put("marketId", "1199612701056172034");
		map.put("oddsId", "1199612701148446722");
		System.out.println(JSONObject.toJSONString(map));
	}

}
