//package com.panda.sport.rcs.mts.sportradar.mq.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
//import com.panda.sport.rcs.mts.sportradar.order.BasicTest;
//import com.panda.sport.rcs.mts.sportradar.wrapper.MtsCommonService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
///**
// * 订单发送到MTS
// */
//@Component
//@Slf4j
//public class MtsTestConsumer extends ConsumerAdapter<Map<String, Object>> {
//
//    @Autowired
//    MtsCommonService mtsCommonService;
//
//    public MtsTestConsumer() {
//        super("queue_mts_test", "");
//    }
//
//    @Override
//    public Boolean handleMs(Map<String, Object> dataMap, Map<String, String> paramsMap) {
//        log.info("mts测试MQ收到:" + JSONObject.toJSONString(dataMap));
//        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(dataMap));
//        String matchId = json.getString("matchId");
//        int marketType = json.getInteger("marketType");
//        String sprotId = json.getString("sprotId");
//        int playId = json.getInteger("playId");
//        String marketId = json.getString("marketId");
//        String marketValue = json.getString("marketValue");
//        int odds = json.getInteger("odds");
//        int money = json.getInteger("money");
//        log.info("mts测试MQ开始:");
//        BasicTest.test(matchId, marketType, sprotId, playId, marketId, marketValue, odds, money);
//        return true;
//    }
//
//    public static void main(String[] args) {
//        Map<String, Object> dataMap = JSONObject.parseObject("{\n" +
//                "      \"matchId\":\"sr:season:68176\",\n" +
//                "          \"sprotId\":\"sr:sport:2\",\n" +
//                "          \"playId\":534,\n" +
//                "          \"marketId\":\"pre:outcometext:6885023\",\n" +
//                "          \"money\":10000,\n" +
//                "          \"odds\":22400,\n" +
//                "          \"marketValue\":\"variant=pre:markettext:79226\",\n" +
//                "          \"marketType\":3\n" +
//                "  }", Map.class);
//
//
//        log.info("mts测试MQ收到:" + JSONObject.toJSONString(dataMap));
//        JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(dataMap));
//        String matchId = json.getString("matchId");
//        int marketType = json.getInteger("marketType");
//        String sprotId = json.getString("sprotId");
//        int playId = json.getInteger("playId");
//        String marketId = json.getString("marketId");
//        String marketValue = json.getString("marketValue");
//        int odds = json.getInteger("odds");
//        int money = json.getInteger("money");
//        log.info("mts测试MQ开始:");
//        BasicTest.test(matchId, marketType, sprotId, playId, marketId, marketValue, odds, money);
//    }
//
//}
