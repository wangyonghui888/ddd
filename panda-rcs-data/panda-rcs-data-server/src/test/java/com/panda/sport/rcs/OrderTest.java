package com.panda.sport.rcs;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.data.RcsDataServerBootstrap;
import com.panda.sport.rcs.data.service.impl.TOrderDetailExtServiceImplV3;
import com.panda.sport.rcs.data.sync.MarketSellConsumer;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import groovy.util.logging.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs
 * @Description :  TODO
 * @Date: 2020-08-15 15:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsDataServerBootstrap.class)
@Slf4j
public class OrderTest {
    @Autowired
   private TOrderDetailExtServiceImplV3 orderDetailExtService;
    @Autowired
   private com.panda.sport.rcs.data.sync.MarketSellConsumer marketSellConsumer;

    @Test
    public void test(){
        StandardMatchInfo matchInfo= orderDetailExtService.getMatchInfo(3L);


        String a ="{\"data\":{\"auditor\":\"\",\"auditorId\":\"\",\"auditorStatus\":\"Not_Set\",\"beginTime\":1699545600000,\"businessEvent\":\"SR\",\"createTime\":1661278682950,\"id\":1266285,\"label\":0,\"liveMatchDataProviderCode\":\"SR\",\"liveMatchSellStatus\":\"Stop_Sold\",\"liveOddBusiness\":1,\"liveOddTime\":1699545600000,\"liveRiskManagerCode\":\"PA\",\"liveTrader\":\"cptest\",\"liveTraderDepartmentId\":\"20\",\"liveTraderId\":\"418\",\"liveTraderStatus\":\"Setted\",\"liveUsedOddsCodes\":\"SR\",\"matchInfoId\":3381909,\"matchManageId\":\"4188347197813381909\",\"matchStatusSourceCode\":\"SR\",\"modifyTime\":1661313349278,\"neutralGround\":1,\"preMatchDataProviderCode\":\"SR\",\"preMatchSellStatus\":\"Stop_Sold\",\"preMatchTime\":1661312821192,\"preRiskManagerCode\":\"PA\",\"preTrader\":\"cptest\",\"preTraderDepartmentId\":\"20\",\"preTraderId\":\"418\",\"preTraderStatus\":\"Setted\",\"preUsedOddsCodes\":\"SR\",\"roundType\":0,\"sellBeginDiffer\":38232761344,\"settlementTime\":0,\"sportId\":1,\"status\":\"End\",\"teamAwayId\":161018,\"teamHomeId\":144680,\"tournamentId\":823323,\"tournamentLevel\":4},\"linkId\":\"noW1Wyh9G0nP4Qz2mBsOuK5NdlxGJenS\"}";
        Request<RcsStandardSportMarketSell> msg = JSONObject.parseObject(a.toString(),new TypeReference<Request<RcsStandardSportMarketSell>>() {});
        marketSellConsumer.handleMs(msg);
    }

    @Test
    public void testBatchUpdateOrderExt(){

        String a ="{\"id\":1680142056267837442,\"sportId\":1,\"canceled\":0,\"dataSourceCode\":\"KO\",\"sourceType\":1,\"eventCode\":\"lost_connection\",\"eventTime\":1689412056159,\"extraInfo\":\"\",\"homeAway\":\"\",\"matchPeriodId\":6,\"matchType\":1,\"playerIdPrefix\":null,\"player1Id\":null,\"player1Name\":null,\"player2Id\":null,\"player2Name\":null,\"secondsFromStart\":300,\"standardMatchId\":3506618,\"standardTeamId\":null,\"t1\":null,\"t2\":null,\"secondNum\":null,\"firstT1\":null,\"firstT2\":null,\"secondT1\":null,\"secondT2\":null,\"firstNum\":null,\"thirdEventId\":\"2092855639\",\"thirdMatchId\":\"1680129533997043714\",\"thirdMatchSourceId\":\"1688798584698\",\"aoThirdMatchSourceId\":\"348211342559100930\",\"thirdTeamId\":null,\"remark\":null,\"periodRemainingSeconds\":null,\"penaltyShootoutRound\":null,\"createTime\":1689412056401,\"modifyTime\":1689412056401,\"addition6\":null,\"addition7\":null,\"addition8\":null,\"addition9\":null,\"addition10\":null,\"addition1\":null,\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"isErrorEndEvent\":0,\"fragmentId\":null,\"fragmentCode\":null,\"fragmentVideo\":null,\"fragmentPic\":null,\"fragmentLength\":null}";
        MatchEventInfo event = JSONObject.parseObject(a,new TypeReference<MatchEventInfo>(){});
        orderDetailExtService.batchUpdateOrderExt(event,"test_linkedId_0000001");
    }
}
