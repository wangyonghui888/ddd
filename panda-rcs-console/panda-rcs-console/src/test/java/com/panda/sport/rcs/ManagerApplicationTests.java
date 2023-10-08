//package com.panda.sport.rcs;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.TypeReference;
//import com.panda.sport.rcs.console.ManagerApplication;
//import com.panda.sport.rcs.console.mq.impl.MarketOddsFlowingPushImpl;
//import com.panda.sport.rcs.console.mq.impl.MarketSellFlowingPushImpl;
//import com.panda.sport.rcs.console.mq.impl.MatchStatusFlowingPushApiImpl;
//import com.panda.sport.rcs.console.pojo.RcsStandardSportMarketSellFlowing;
//import com.panda.sport.rcs.console.pojo.Request;
//import com.panda.sport.rcs.console.pojo.StandardMatchMarketMessage;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {ManagerApplication.class})
//public class ManagerApplicationTests {
//
//    @Autowired
//    MarketOddsFlowingPushImpl marketOddsFlowingPushImpl;
//
//    @Autowired
//    MatchStatusFlowingPushApiImpl matchStatusFlowingPushApiImpl;
//    @Autowired
//    MarketSellFlowingPushImpl marketSellFlowingPushImpl;
//    @Test
//    public void contextLoads() {
//		String  password = "1,9";
//		String[] split = password.split(",");
//		for (String s:split){
//			System.out.println(s);
//		}
//
//
//
//        try {
//            StringBuilder sb = new StringBuilder();
//            sb.append("{\"linkId\":\"P2s0YGfi5h6MfIUPAmf2swLh64uehC1f\",\"data\":{\"id\":null,\"linkId\":\"P2s0YGfi5h6MfIUPAmf2swLh64uehC1f\",\"oId\":null,\"matchInfoId\":null,\"matchManageId\":null,\"sportId\":null,\"liveOddBusiness\":null,\"tournamentId\":null,\"tournamentNameCn\":null,\"tournamentNameEn\":null,\"teamHomeId\":null,\"teamHomeNameCn\":null,\"teamHomeNameEn\":null,\"teamAwayId\":null,\"teamAwayNameCn\":null,\"teamAwayNameEn\":null,\"preMatchTime\":null,\"liveOddTime\":1583078400000,\"beginTime\":1583078400000,\"preTraderId\":null,\"preTrader\":null,\"preTraderStatus\":null,\"liveTraderId\":null,\"liveTrader\":null,\"liveTraderStatus\":null,\"auditorId\":null,\"auditor\":null,\"auditorStatus\":null,\"neutralGround\":null,\"businessEvent\":null,\"preMatchSellStatus\":null,\"liveMatchSellStatus\":null,\"video\":null,\"animation\":null,\"status\":null,\"marketCount\":null,\"cornerShow\":null,\"cardShow\":null,\"createTime\":1582646830060,\"modifyTime\":1582646829503,\"preRiskManagerCode\":null,\"liveRiskManagerCode\":null,\"preMatchDataProviderCode\":null,\"liveMatchDataProviderCode\":null,\"platform\":null,\"tournamentLevel\":null,\"roundType\":0,\"preTraderDepartmentId\":null,\"liveTraderDepartmentId\":null,\"auditorDepartmentId\":null,\"videoId\":null,\"animationId\":null,\"insertTime\":null}}");
//            sb.append("");
//            String str = sb.toString();
//            Request<RcsStandardSportMarketSellFlowing> request = JSONObject.parseObject(str, new TypeReference<Request<RcsStandardSportMarketSellFlowing>>() {
//            });
//            marketSellFlowingPushImpl.handleMs(request, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
