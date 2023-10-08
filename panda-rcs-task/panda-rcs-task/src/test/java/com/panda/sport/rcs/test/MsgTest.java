package com.panda.sport.rcs.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.panda.sport.rcs.task.RcsTaskApplication;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class MsgTest {

	
    @org.junit.Test
    public void test(){
    	/*String msg = "{\"linkId\":\"ac12b2f62019121817301283430f0f80\",\"data\":{\"id\":1206861793828323330,\"standardTournamentId\":null,\"standardMatchInfoId\":23010,\"marketCategoryId\":1,\"marketType\":1,\"oddsValue\":null,\"oddsName\":\"胜平负\",\"orderType\":null,\"oddsMetric\":0,\"addition1\":null,\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"dataSourceCode\":\"SR\",\"status\":0,\"scopeId\":\"\",\"thirdMarketSourceId\":\"20024670_1_\",\"remark\":null,\"modifyTime\":1576661398671,\"i18nNames\":null,\"marketOddsList\":[{\"id\":1206861793853489154,\"marketId\":1206861793828323330,\"active\":1,\"settlementResultText\":null,\"settlementResult\":null,\"betSettlementCertainty\":null,\"oddsType\":\"1\",\"addition1\":\"13666\",\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"name\":null,\"nameExpressionValue\":null,\"oddsValue\":189000,\"paOddsValue\":191368,\"oddsFieldsTemplateId\":47,\"thirdTemplateSourceId\":\"SR:1:1\",\"originalOddsValue\":200936,\"targetSide\":null,\"orderOdds\":1,\"dataSourceCode\":\"SR\",\"thirdOddsFieldSourceId\":\"20024670_1__1\",\"remark\":null,\"modifyTime\":1576661398675,\"i18nNames\":null},{\"id\":1206861793866072066,\"marketId\":1206861793828323330,\"active\":1,\"settlementResultText\":null,\"settlementResult\":null,\"betSettlementCertainty\":null,\"oddsType\":\"X\",\"addition1\":\"0\",\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"name\":null,\"nameExpressionValue\":null,\"oddsValue\":315000,\"paOddsValue\":328966,\"oddsFieldsTemplateId\":48,\"thirdTemplateSourceId\":\"SR:1:2\",\"originalOddsValue\":345414,\"targetSide\":null,\"orderOdds\":2,\"dataSourceCode\":\"SR\",\"thirdOddsFieldSourceId\":\"20024670_1__2\",\"remark\":null,\"modifyTime\":1576661398680,\"i18nNames\":null},{\"id\":1206861793887043586,\"marketId\":1206861793828323330,\"active\":1,\"settlementResultText\":null,\"settlementResult\":null,\"betSettlementCertainty\":null,\"oddsType\":\"2\",\"addition1\":\"13682\",\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"name\":null,\"nameExpressionValue\":null,\"oddsValue\":430000,\"paOddsValue\":447506,\"oddsFieldsTemplateId\":49,\"thirdTemplateSourceId\":\"SR:1:3\",\"originalOddsValue\":469881,\"targetSide\":null,\"orderOdds\":3,\"dataSourceCode\":\"SR\",\"thirdOddsFieldSourceId\":\"20024670_1__3\",\"remark\":null,\"modifyTime\":1576661398687,\"i18nNames\":null}],\"extraInfo\":\"{}\"}}";
		DataRealTimeMessageBean<StandardMarketMessage> msgBean = JSONObject.parseObject(msg,new TypeReference<DataRealTimeMessageBean<StandardMarketMessage>>() {});
		matchMarketOddsChangeConsumer.handleMs(msgBean, null);*/
    }
    
}
