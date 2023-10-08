//package com.panda.sport.rcs;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import org.junit.runner.RunWith;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.alibaba.fastjson.JSONObject;
//import com.google.inject.Guice;
//import com.google.inject.Injector;
//import com.panda.rcs.sdk.SdkServer;
//import com.panda.rcs.sdk.env.GuicePropertySource;
//import com.panda.sport.data.rcs.dto.ExtendBean;
//import com.panda.sport.sdk.bean.MatrixBean;
//import com.panda.sport.sdk.core.Sdk;
//import com.panda.sport.sdk.module.SdkInjectModule;
//import com.panda.sport.sdk.scan.IocHandle;
//import com.panda.sport.sdk.service.impl.LuaPaidService;
//import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
//import com.panda.sport.sdk.util.GuiceContext;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SdkServer.class)
//public class LuaPaidV2Test {
//	
//	public static void main(String[] args) throws ClassNotFoundException {
//		Properties properties = SdkServer.getNacosConfig();
//        
//        Map<String, String> map = new HashMap<String, String>((Map) properties);
//        String port = map.get("server.port");
//        System.setProperty("server.port", port);//设置服务端口
//        Map<String, Map<String, String>> paramMap= new HashMap<>(1);
//        paramMap.put("sdk-properties",map);
//        Sdk.initProperties(paramMap);
//        SdkServer.initDubboService();
//        
//        SpringApplication application = new SpringApplication(SdkServer.class);
//       	application.addInitializers(new GuicePropertySource());
//       	application.run(args);
//       	
//		LuaPaidService luaPaidService = GuiceContext.getInstance(LuaPaidService.class);
//		String str = "{\"busId\":\"2\",\"currentMaxPaid\":0,\"currentScore\":\"0:0\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-05-31\",\"handicap\":\"0\",\"isScroll\":\"0\",\"itemBean\":{\"betAmount\":0,\"betAmount1\":0,\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-05-31\",\"handleAfterOddsValue\":3.1,\"handleAfterOddsValue1\":3.1,\"handleStatus\":0,\"handledBetAmout\":0,\"marketId\":1266721701174886401,\"matchId\":319848,\"matchProcessId\":0,\"matchType\":1,\"oddFinally\":\"3.10\",\"oddsValue\":310000.0,\"orderStatus\":0,\"paidAmount\":0.0,\"paidAmount1\":0.0,\"platform\":\"PA\",\"playId\":1,\"playOptions\":\"X\",\"playOptionsId\":1266721701208440835,\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"tournamentId\":9568,\"tradeType\":0,\"turnamentLevel\":6,\"uid\":141744528432836608,\"validateResult\":0},\"marketId\":\"1266721701174886401\",\"matchId\":\"319848\",\"odds\":\"3.1\",\"orderMoney\":0,\"playId\":\"1\",\"playType\":\"3\",\"selectId\":\"1266721701208440835\",\"seriesType\":1,\"sportId\":\"1\",\"tournamentId\":9568,\"tournamentLevel\":6,\"userId\":\"141744528432836608\"}";
////		String str = "{\"busId\":\"2\",\"currentMaxPaid\":240,\"currentScore\":\"0:0\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-05-31\",\"handicap\":\"0\",\"isScroll\":\"0\",\"itemBean\":{\"betAmount\":100,\"betAmount1\":1,\"betNo\":\"27126230884352\",\"betTime\":1590912670239,\"createTime\":1590912670239,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-05-31\",\"handleAfterOddsValue\":3.4,\"handleAfterOddsValue1\":3.4,\"handleStatus\":0,\"handledBetAmout\":1,\"marketId\":1266721701174886401,\"marketType\":\"EU\",\"matchId\":319848,\"matchInfo\":\"魯赫布雷斯特 VS 比斯特迪纳摩\",\"matchName\":\"白俄斯超级联赛\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":240.0,\"modifyTime\":1590912670239,\"modifyUser\":\"系统\",\"oddFinally\":\"3.40\",\"oddsValue\":340000.0,\"orderNo\":\"17126235078656\",\"orderStatus\":0,\"paidAmount\":340.0,\"paidAmount1\":3.4,\"platform\":\"PA\",\"playId\":1,\"playName\":\"全场赛果\",\"playOptions\":\"1\",\"playOptionsId\":1266721701195857923,\"playOptionsName\":\"主胜\",\"recVal\":\"[[100,100,100,100,100,100,100,100,100,100,100,100,100],[-240,100,100,100,100,100,100,100,100,100,100,100,100],[-240,-240,100,100,100,100,100,100,100,100,100,100,100],[-240,-240,-240,100,100,100,100,100,100,100,100,100,100],[-240,-240,-240,-240,100,100,100,100,100,100,100,100,100],[-240,-240,-240,-240,-240,100,100,100,100,100,100,100,100],[-240,-240,-240,-240,-240,-240,100,100,100,100,100,100,100],[-240,-240,-240,-240,-240,-240,-240,100,100,100,100,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,100,100,100,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,-240,100,100,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,100,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,100]]\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"足球\",\"tournamentId\":9568,\"tradeType\":0,\"turnamentLevel\":6,\"uid\":141744528432836608,\"validateResult\":0},\"itemId\":\"27126230884352\",\"marketId\":\"1266721701174886401\",\"matchId\":\"319848\",\"odds\":\"3.4\",\"orderId\":\"17126235078656\",\"orderMoney\":100,\"playId\":\"1\",\"playType\":\"3\",\"recType\":0,\"recVal\":\"[[100,100,100,100,100,100,100,100,100,100,100,100,100],[-240,100,100,100,100,100,100,100,100,100,100,100,100],[-240,-240,100,100,100,100,100,100,100,100,100,100,100],[-240,-240,-240,100,100,100,100,100,100,100,100,100,100],[-240,-240,-240,-240,100,100,100,100,100,100,100,100,100],[-240,-240,-240,-240,-240,100,100,100,100,100,100,100,100],[-240,-240,-240,-240,-240,-240,100,100,100,100,100,100,100],[-240,-240,-240,-240,-240,-240,-240,100,100,100,100,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,100,100,100,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,-240,100,100,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,100,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,100,100],[-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,-240,100]]\",\"selectId\":\"1266721701195857923\",\"seriesType\":1,\"sportId\":\"1\",\"tournamentId\":9568,\"tournamentLevel\":6,\"userId\":\"141744528432836608\"}";
//		ExtendBean bean = JSONObject.parseObject(str,ExtendBean.class);
//		bean.setBusId("1");
//		
////		bean.setOrderMoney(10000l);
////		bean.getItemBean().setBetAmount(10000l);
////		Map<String, Object> result = luaPaidService.saveOrder(bean, "B,B,B,B,B,B,D,B,B,B,B,B,B,X,B,B,B,B,B,D,X,B,B,B,B,B,X,X,B,B,B,B,D,X,X,B,B,B,B,X,X,X,B,B,B,D,X,X,X,B,B,B,X,X,X,X,B,B,D,X,X,X,X,B,B,X,X,X,X,X,B,D,X,X,X,X,X,B,X,X,X,X,X,X,J");
//		
//		
//		bean.setOrderMoney(100l);//虚拟出来一个值，以1为单位
//    	MatrixBean matrixBean = GuiceContext.getInstance(MatrixAdapter.class).process(bean.getSportId(), bean.getPlayId(), bean);
//        /*** 修改到redis时,默认值为 空字符串,因此此处给空字符串. 该变量其余地方没有使用 ***/
//        if(0 == matrixBean.getRecType()) {
//        	bean.setRecVal(JSONObject.toJSONString(matrixBean.getMatrixValueArray()));
//        }
//        bean.setRecType(matrixBean.getRecType());
//        bean.setOrderMoney(null);//还原当前值
//		Long result = luaPaidService.getUserSelectsMaxBetAmountV2(bean);
//		
//		System.out.println(result);
//	}
//
//}