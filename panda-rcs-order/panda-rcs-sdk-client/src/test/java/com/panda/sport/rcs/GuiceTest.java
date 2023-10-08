package com.panda.sport.rcs;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.sdk.module.SdkInjectModule;
import com.panda.sport.sdk.scan.IocHandle;
import com.panda.sport.sdk.service.impl.LuaPaidService;
import com.panda.sport.sdk.util.GuiceContext;

public class GuiceTest {
	
	public static void main(String[] args) throws ClassNotFoundException {
		Injector injector = Guice.createInjector(new SdkInjectModule());
		GuiceContext.setInjector(injector);
		injector.getInstance(IocHandle.class);
		LuaPaidService luaPaidService = injector.getInstance(LuaPaidService.class);
		String str = "{\"busId\":\"2\",\"currentMaxPaid\":0,\"currentScore\":\"0:0\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-05-31\",\"handicap\":\"0\",\"isScroll\":\"0\",\"itemBean\":{\"betAmount\":0,\"betAmount1\":0,\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-05-31\",\"handleAfterOddsValue\":3.9,\"handleAfterOddsValue1\":3.9,\"handleStatus\":0,\"handledBetAmout\":0,\"marketId\":1266803809055457282,\"matchId\":18647,\"matchProcessId\":0,\"matchType\":1,\"oddFinally\":\"3.90\",\"oddsValue\":390000.0,\"orderStatus\":0,\"paidAmount\":0.0,\"paidAmount1\":0.0,\"platform\":\"PA\",\"playId\":1,\"playOptions\":\"1\",\"playOptionsId\":1266803809080623106,\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"tournamentId\":24,\"tradeType\":0,\"turnamentLevel\":5,\"uid\":172208755198160896,\"validateResult\":0},\"marketId\":\"1266803809055457282\",\"matchId\":\"18647\",\"odds\":\"3.9\",\"orderMoney\":0,\"playId\":\"1\",\"playType\":\"3\",\"selectId\":\"1266803809080623106\",\"seriesType\":1,\"sportId\":\"1\",\"tournamentId\":24,\"tournamentLevel\":5,\"userId\":\"172208755198160896\"}";
		ExtendBean bean = JSONObject.parseObject(str,ExtendBean.class);
		Long result = luaPaidService.getUserSelectsMaxBetAmountV3(bean,null,null);
		System.out.println(result);
	}

}
