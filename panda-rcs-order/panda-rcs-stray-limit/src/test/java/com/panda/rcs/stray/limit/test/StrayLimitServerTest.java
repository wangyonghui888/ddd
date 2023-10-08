package com.panda.rcs.stray.limit.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.stray.StrayLimitServer;
import com.panda.sport.data.rcs.api.OrderLimitNewVersionApi;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StrayLimitServer.class)
@Slf4j
public class StrayLimitServerTest {


    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    ApplicationContext ctx;

    @Value("${spring.profiles.active:Prod}")
    private String env;

    @Test
    public void test() {
        List<String> list = new ArrayList<>();
        String str = "[AlertType]：MerchantOverDailyLimit商户单日限额告警\n" +
                "[Env]：" + env + "\n" +
                "[Alert Time]：" + DateUtils.transferLongToDateStrings(System.currentTimeMillis()) + "\n" +
                "[Merchant ID]：123456\n" +
                "[Merchant Name]：beulah\n" +
                "[MerchantDailyLimit]：" + new DecimalFormat("#,###").format(new BigDecimal("100000")) + "\n" +
                "[MerchantDailyLimitUsed]：" + new DecimalFormat("#,###").format(new BigDecimal("85000")) + "\n" +
                "[MerchantDailyLimitUsed%]：85%";
        list.add(str);
        JSONObject param = new JSONObject(new LinkedHashMap<>());
        param.put("data", list);
        //需求编号
        param.put("dataSourceCode", "2088");
        //linkId
        String job = "ScoreAlertJob:123456";
        param.put("linkId", job);
        //推送融合，进行mango/telegram预警
        producerSendMessageUtils.sendMessage("PA_COMMON_WARN_INFO", "", job, param);
    }

    @Resource
    OrderLimitNewVersionApi api;

    /**
     * debug 串关限额逻辑
     */
    @Test
    public void testStrayOrder(){

        String str = "{\n" +
                "\t\t\"deviceType\": 2,\n" +
                "\t\t\"handleStatus\": 0,\n" +
                "\t\t\"infoStatus\": 0,\n" +
                "\t\t\"items\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"betAmount\": 0,\n" +
                "\t\t\t\t\"betAmount1\": 0,\n" +
                "\t\t\t\t\"dataSourceCode\": \"AO\",\n" +
                "\t\t\t\t\"dateExpect\": \"2023-03-08\",\n" +
                "\t\t\t\t\"handleAfterOddsValue\": 1.92,\n" +
                "\t\t\t\t\"handleAfterOddsValue1\": 1.92,\n" +
                "\t\t\t\t\"handleStatus\": 0,\n" +
                "\t\t\t\t\"handledBetAmout\": 0,\n" +
                "\t\t\t\t\"marketId\": 145504511530745423,\n" +
                "\t\t\t\t\"marketValue\": \"0\",\n" +
                "\t\t\t\t\"marketValueNew\": \"0\",\n" +
                "\t\t\t\t\"matchId\": 3407138,\n" +
                "\t\t\t\t\"matchProcessId\": 0,\n" +
                "\t\t\t\t\"matchType\": 1,\n" +
                "\t\t\t\t\"oddFinally\": \"1.92\",\n" +
                "\t\t\t\t\"oddsValue\": 192000.0,\n" +
                "\t\t\t\t\"orderStatus\": 0,\n" +
                "\t\t\t\t\"otherOddsValue\": 184000.0,\n" +
                "\t\t\t\t\"paidAmount\": 0.0,\n" +
                "\t\t\t\t\"paidAmount1\": 0.0,\n" +
                "\t\t\t\t\"placeNum\": 1,\n" +
                "\t\t\t\t\"platform\": \"PA\",\n" +
                "\t\t\t\t\"playId\": 4,\n" +
                "\t\t\t\t\"playOptions\": \"1\",\n" +
                "\t\t\t\t\"playOptionsId\": 146936313460322003,\n" +
                "\t\t\t\t\"scoreBenchmark\": \"0:0\",\n" +
                "\t\t\t\t\"sportId\": 1,\n" +
                "\t\t\t\t\"subPlayId\": \"4\",\n" +
                "\t\t\t\t\"tournamentId\": 822361,\n" +
                "\t\t\t\t\"tradeType\": 0,\n" +
                "\t\t\t\t\"turnamentLevel\": 1,\n" +
                "\t\t\t\t\"uid\": 501580967842800001,\n" +
                "\t\t\t\t\"validateResult\": 0\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"betAmount\": 0,\n" +
                "\t\t\t\t\"betAmount1\": 0,\n" +
                "\t\t\t\t\"dataSourceCode\": \"AO\",\n" +
                "\t\t\t\t\"dateExpect\": \"2023-03-08\",\n" +
                "\t\t\t\t\"handleAfterOddsValue\": 1.67,\n" +
                "\t\t\t\t\"handleAfterOddsValue1\": 1.67,\n" +
                "\t\t\t\t\"handleStatus\": 0,\n" +
                "\t\t\t\t\"handledBetAmout\": 0,\n" +
                "\t\t\t\t\"marketId\": 143576051883838161,\n" +
                "\t\t\t\t\"marketValue\": \"-0/0.5\",\n" +
                "\t\t\t\t\"marketValueNew\": \"-0/0.5\",\n" +
                "\t\t\t\t\"matchId\": 3406879,\n" +
                "\t\t\t\t\"matchProcessId\": 0,\n" +
                "\t\t\t\t\"matchType\": 1,\n" +
                "\t\t\t\t\"oddFinally\": \"1.67\",\n" +
                "\t\t\t\t\"oddsValue\": 167000.0,\n" +
                "\t\t\t\t\"orderStatus\": 0,\n" +
                "\t\t\t\t\"otherOddsValue\": 220000.0,\n" +
                "\t\t\t\t\"paidAmount\": 0.0,\n" +
                "\t\t\t\t\"paidAmount1\": 0.0,\n" +
                "\t\t\t\t\"placeNum\": 1,\n" +
                "\t\t\t\t\"platform\": \"PA\",\n" +
                "\t\t\t\t\"playId\": 4,\n" +
                "\t\t\t\t\"playOptions\": \"1\",\n" +
                "\t\t\t\t\"playOptionsId\": 141122080614354645,\n" +
                "\t\t\t\t\"scoreBenchmark\": \"0:0\",\n" +
                "\t\t\t\t\"sportId\": 1,\n" +
                "\t\t\t\t\"subPlayId\": \"4\",\n" +
                "\t\t\t\t\"tournamentId\": 822779,\n" +
                "\t\t\t\t\"tradeType\": 0,\n" +
                "\t\t\t\t\"turnamentLevel\": 4,\n" +
                "\t\t\t\t\"uid\": 501580967842800001,\n" +
                "\t\t\t\t\"validateResult\": 0\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"betAmount\": 0,\n" +
                "\t\t\t\t\"betAmount1\": 0,\n" +
                "\t\t\t\t\"dataSourceCode\": \"PA\",\n" +
                "\t\t\t\t\"dateExpect\": \"2023-04-03\",\n" +
                "\t\t\t\t\"handleAfterOddsValue\": 2.58,\n" +
                "\t\t\t\t\"handleAfterOddsValue1\": 2.58,\n" +
                "\t\t\t\t\"handleStatus\": 0,\n" +
                "\t\t\t\t\"handledBetAmout\": 0,\n" +
                "\t\t\t\t\"marketId\": 142055640740303392,\n" +
                "\t\t\t\t\"matchId\": 3400204,\n" +
                "\t\t\t\t\"matchProcessId\": 0,\n" +
                "\t\t\t\t\"matchType\": 1,\n" +
                "\t\t\t\t\"oddFinally\": \"2.58\",\n" +
                "\t\t\t\t\"oddsValue\": 258000.0,\n" +
                "\t\t\t\t\"orderStatus\": 0,\n" +
                "\t\t\t\t\"paidAmount\": 0.0,\n" +
                "\t\t\t\t\"paidAmount1\": 0.0,\n" +
                "\t\t\t\t\"placeNum\": 1,\n" +
                "\t\t\t\t\"platform\": \"PA\",\n" +
                "\t\t\t\t\"playId\": 1,\n" +
                "\t\t\t\t\"playOptions\": \"1\",\n" +
                "\t\t\t\t\"playOptionsId\": 145492304075833244,\n" +
                "\t\t\t\t\"scoreBenchmark\": \"0:0\",\n" +
                "\t\t\t\t\"sportId\": 1,\n" +
                "\t\t\t\t\"subPlayId\": \"1\",\n" +
                "\t\t\t\t\"tournamentId\": 835439,\n" +
                "\t\t\t\t\"tradeType\": 1,\n" +
                "\t\t\t\t\"turnamentLevel\": 1,\n" +
                "\t\t\t\t\"uid\": 501580967842800001,\n" +
                "\t\t\t\t\"validateResult\": 0\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"orderStatus\": 0,\n" +
                "\t\t\"seriesType\": 3004,\n" +
                "\t\t\"tenantId\": 2,\n" +
                "\t\t\"uid\": 501580967842800001,\n" +
                "\t\t\"userTagLevel\": 0,\n" +
                "\t\t\"username\": \"111111_tydVifer8rEq\",\n" +
                "\t\t\"validateResult\": 2\n" +
                "\t}";

        OrderBean orderBean = JSON.parseObject(str, OrderBean.class);
        Request<OrderBean> request = new Request<>();
        request.setData(orderBean);
        request.setGlobalId("d0e1782a1b8242e38c5c17760bf733a0");

        api.queryMaxBetAmountByOrder(request);


    }

}
