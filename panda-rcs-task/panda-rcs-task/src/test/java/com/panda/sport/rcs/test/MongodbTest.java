package com.panda.sport.rcs.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.pojo.odd.StandardMatchMarketMessage;
import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.mq.bean.DataRealTimeMessageBean;
import com.panda.sport.rcs.task.mq.impl.match.MatchMarketOddsChangeV2Consumer;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class MongodbTest {

	@Autowired
	MatchMarketOddsChangeV2Consumer consumer;
	
    @org.junit.Test
    public void test(){
    	String test = "{\"linkId\": \"ac12b2f620201008144456121c96ff43\",\"data\": {\"standardTournamentId\": null,\"standardMatchInfoId\": 1411439,\"dataSourceCode\": \"SR\",\"modifyTime\": 1602139498850,\"displayCorner\": null,\"displayPenalty\": null,\"displayMarketCount\": null,\"status\": 0,\"sportId\": 1,\"marketList\": [{\"id\": 1314094343791226882,\"marketCategoryId\": 18,\"marketType\": 0,\"tradeType\": 0,\"oddsValue\": null,\"oddsName\": \"上半场 - 合计\",\"orderType\": \"\",\"oddsMetric\": null,\"addition1\": \"3.5\",\"addition2\": \"\",\"addition3\": \"\",\"addition4\": \"\",\"addition5\": \"\",\"dataSourceCode\": \"SR\",\"thirdMarketSourceStatus\": 2,\"status\": 2,\"scopeId\": \"1\",\"thirdMarketSourceId\": \"23893937_68_3.5\",\"remark\": null,\"modifyTime\": 1602139496871,\"i18nNames\": null,\"marketOddsList\": null,\"extraInfo\": \"{\\\"total\\\":\\\"3.5\\\"}\",\"sendData\": null,\"placeNum\": 2}, {\"id\": 1314093527164825602,\"marketCategoryId\": 18,\"marketType\": 0,\"tradeType\": 0,\"oddsValue\": null,\"oddsName\": \"上半场 - 合计\",\"orderType\": \"\",\"oddsMetric\": null,\"addition1\": \"2.5\",\"addition2\": \"\",\"addition3\": \"\",\"addition4\": \"\",\"addition5\": \"\",\"dataSourceCode\": \"SR\",\"thirdMarketSourceStatus\": 0,\"status\": 0,\"scopeId\": \"1\",\"thirdMarketSourceId\": \"23893937_68_2.5\",\"remark\": null,\"modifyTime\": 1602139497818,\"i18nNames\": null,\"marketOddsList\": [{\"id\": 1314093944564789249,\"marketId\": 1314093527164825602,\"active\": 1,\"settlementResultText\": \"\",\"settlementResult\": \"\",\"betSettlementCertainty\": null,\"oddsType\": \"Under\",\"addition1\": \"\",\"addition2\": \"\",\"addition3\": \"\",\"addition4\": \"\",\"addition5\": \"\",\"name\": \"under 2.5\",\"nameExpressionValue\": null,\"margin\": null,\"oddsValue\": 230000,\"paOddsValue\": 0,\"oddsFieldsTemplateId\": 95,\"thirdTemplateSourceId\": \"SR:68:13\",\"originalOddsValue\": 260185,\"targetSide\": null,\"orderOdds\": 1,\"dataSourceCode\": \"SR\",\"thirdOddsFieldSourceId\": \"23893937_68_2.5_13\",\"remark\": null,\"modifyTime\": 1602139497822,\"i18nNames\": null,\"extraInfo\": \"68And13\"}, {\"id\": 1314093944606732291,\"marketId\": 1314093527164825602,\"active\": 1,\"settlementResultText\": \"\",\"settlementResult\": \"\",\"betSettlementCertainty\": null,\"oddsType\": \"Over\",\"addition1\": \"\",\"addition2\": \"\",\"addition3\": \"\",\"addition4\": \"\",\"addition5\": \"\",\"name\": \"over 2.5\",\"nameExpressionValue\": null,\"margin\": null,\"oddsValue\": 150000,\"paOddsValue\": 0,\"oddsFieldsTemplateId\": 96,\"thirdTemplateSourceId\": \"SR:68:12\",\"originalOddsValue\": 162428,\"targetSide\": null,\"orderOdds\": 2,\"dataSourceCode\": \"SR\",\"thirdOddsFieldSourceId\": \"23893937_68_2.5_12\",\"remark\": null,\"modifyTime\": 1602139497826,\"i18nNames\": null,\"extraInfo\": \"68And12\"}],\"extraInfo\": \"{\\\"total\\\":\\\"2.5\\\"}\",\"sendData\": null,\"placeNum\": 1}, {\"id\": 1314093520185503746,\"marketCategoryId\": 18,\"marketType\": 0,\"tradeType\": 0,\"oddsValue\": null,\"oddsName\": \"上半场 - 合计\",\"orderType\": \"\",\"oddsMetric\": null,\"addition1\": \"1.5\",\"addition2\": \"\",\"addition3\": \"\",\"addition4\": \"\",\"addition5\": \"\",\"dataSourceCode\": \"SR\",\"thirdMarketSourceStatus\": 2,\"status\": 2,\"scopeId\": \"1\",\"thirdMarketSourceId\": \"23893937_68_1.5\",\"remark\": null,\"modifyTime\": 1602139480735,\"i18nNames\": null,\"marketOddsList\": null,\"extraInfo\": \"{\\\"total\\\":\\\"1.5\\\"}\",\"sendData\": null,\"placeNum\": 3}, {\"id\": 1314093874071252994,\"marketCategoryId\": 18,\"marketType\": 0,\"tradeType\": 0,\"oddsValue\": null,\"oddsName\": \"上半场 - 合计\",\"orderType\": \"\",\"oddsMetric\": null,\"addition1\": \"0.5\",\"addition2\": \"\",\"addition3\": \"\",\"addition4\": \"\",\"addition5\": \"\",\"dataSourceCode\": \"SR\",\"thirdMarketSourceStatus\": 2,\"status\": 2,\"scopeId\": \"1\",\"thirdMarketSourceId\": \"23893937_68_0.5\",\"remark\": null,\"modifyTime\": 1602139381717,\"i18nNames\": null,\"marketOddsList\": null,\"extraInfo\": \"{\\\"total\\\":\\\"0.5\\\"}\",\"sendData\": null,\"placeNum\": 4}]},\"dataSourceTime\": 1602139496119,\"dataSourceCode\": null}";
    	DataRealTimeMessageBean<StandardMatchMarketMessage> standardMatchMarketMessage = JSONObject.parseObject(test,new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {});
    	//consumer.onMessage(standardMatchMarketMessage);
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
