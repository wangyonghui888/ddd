package com.panda.sport.rcs;

import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mgr.wrapper.impl.MarketViewServiceImpl;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.vo.operation.RealTimeVolumeBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.mgr.RiskBootstrap;
import com.panda.sport.rcs.mgr.service.impl.SeriesTradeService;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RiskBootstrap.class)
@Slf4j
public class PaidTest {
	@Autowired
	MarketViewServiceImpl marketViewServiceImpl;
	//@Reference(check = false, lazy=true,retries=3,timeout=10000)
/*	@Autowired
    private SeriesTradeService seriesTradeService;*/
	
//    @Test
    public void test(){
//    	String params = "{\"data\":{\"deviceType\":4,\"items\":[{\"handleAfterOddsValue\":2.27,\"handledBetAmout\":0,\"marketId\":1202364426706010114,\"marketValue\":\"大 3\",\"matchId\":40062,\"matchProcessId\":0,\"matchType\":1,\"oddFinally\":\"227018\",\"oddsValue\":227018.0,\"playId\":2,\"playOptions\":\"Over\",\"playOptionsId\":1202364426726981633,\"scoreBenchmark\":\"0:0\",\"sportId\":1,\"uid\":1},{\"handleAfterOddsValue\":9.27,\"handledBetAmout\":0,\"marketId\":1202097747749027841,\"matchId\":23396,\"matchProcessId\":0,\"matchType\":1,\"oddFinally\":\"927605\",\"oddsValue\":927605.0,\"playId\":1,\"playOptions\":\"1\",\"playOptionsId\":1202097747786776577,\"scoreBenchmark\":\"0:0\",\"sportId\":1,\"uid\":1}],\"seriesType\":2001,\"tenantId\":1,\"uid\":1},\"globalId\":\"5431ce6e6845412d88b7bdb6f6fa5f01\"}";
//    	Request<OrderBean> requestParam = JSONObject.parseObject(params,new TypeReference<Request<OrderBean>>() {});
//    	System.out.println(seriesTradeService.queryMaxBetMoneyBySelect(requestParam));
		String a = "{\"matchId\":54476,\"standardTournamentId\":null,\"playId\":1,\"matchMarketId\":1210929038504239105,\"playOptionsId\":1210929038592319489,\"sumMoney\":2100,\"profitValue\":-6910.5,\"betOrderNum\":5}";
		RealTimeVolumeBean realTimeVolumeBean = JsonFormatUtils.fromJson(a, RealTimeVolumeBean.class);
//		marketViewServiceImpl.updateMatchOdds(realTimeVolumeBean);
    }

	@Test
	public void setCache(){
		System.out.println("1");
		RcsLocalCacheUtils.timedCache.put("demo001", "123", 20000);

		System.out.println("3");
		Object str = RcsLocalCacheUtils.timedCache.get("demo001");
		System.out.println("2");
	}

	@Test
	public void getCache(){

	}

}
