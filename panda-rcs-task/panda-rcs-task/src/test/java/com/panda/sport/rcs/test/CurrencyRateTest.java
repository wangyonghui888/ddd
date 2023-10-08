/*
package com.panda.sport.rcs.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mapper.RcsMarketOddsConfigMapper;
import com.panda.sport.rcs.mapper.RcsOrderSummaryMapper;
import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.job.CurrencyRateJobHandler;
import com.panda.sport.rcs.task.job.orderSummary.OrderSummaryJobHandler;
import com.panda.sport.rcs.task.mq.impl.RcsMarketOddsConfigConsumer;
import com.panda.sport.rcs.task.mq.orderSummary.OrderSummaryConsumer;
import com.panda.sport.rcs.vo.operation.RealTimeVolumeBean;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class CurrencyRateTest {

    @Autowired
    CurrencyRateJobHandler currencyRateJobHandler;
    @Autowired
    OrderSummaryJobHandler orderSummaryJobHandler;
    @Autowired
    private RcsOrderSummaryMapper rcsOrderSummaryMapper;
    @Autowired
    private OrderSummaryConsumer orderSummaryConsumer;
    @Autowired
    private RcsMarketOddsConfigConsumer rcsMarketOddsConfigConsumer;
    @Autowired
    private RcsMarketOddsConfigMapper rcsMarketOddsConfigMapper;
    @org.junit.Test
    public void test() throws Exception {
        String s="[{\"betOrderNum\":3,\"marketIndex\":1,\"matchId\":1946065,\"matchMarketId\":140333100370447339,\"matchType\":\"2\",\"paidAmount\":1206,\"playId\":38,\"playOptionsId\":142232539968701493,\"profitValue\":-606,\"sportId\":2,\"standardTournamentId\":834,\"sumMoney\":600},{\"betOrderNum\":0,\"marketIndex\":1,\"matchId\":1946065,\"matchMarketId\":140333100370447339,\"matchType\":\"2\",\"paidAmount\":0,\"playId\":38,\"playOptionsId\":144904199266590055,\"profitValue\":600,\"sportId\":2,\"standardTournamentId\":834,\"sumMoney\":0}]";
        List<RealTimeVolumeBean> realTimeVolumeBeans = JSON.parseArray(s, RealTimeVolumeBean.class);
        //rcsMarketOddsConfigConsumer.handleMs(realTimeVolumeBeans,null);
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
*/
