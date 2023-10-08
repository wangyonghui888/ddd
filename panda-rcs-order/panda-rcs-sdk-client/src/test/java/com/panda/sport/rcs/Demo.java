//package com.panda.sport.rcs;
//
//import com.alibaba.fastjson.JSONObject;
//import com.panda.sport.data.rcs.api.OrderPaidApiService;
//import com.panda.sport.data.rcs.api.Request;
//import com.panda.sport.data.rcs.api.Response;
//import com.panda.sport.sdk.core.Sdk;
//import com.panda.sport.data.rcs.dto.OrderBean;
//import com.panda.sport.data.rcs.dto.OrderItem;
//import com.panda.sport.data.rcs.dto.SettleItem;
//import com.panda.sport.sdk.listeners.OrderStatusHandler;
//import com.panda.sport.sdk.listeners.OrderStatusServer;
//import com.panda.sport.sdk.service.impl.OrderPaidApiImpl;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//
//public class Demo {
//
//    private static Sdk rcsSdk;
//    private static OrderStatusServer orderStatusServer;
//
//    public static void main(String[] args) {
//
//        rcsSdk = Sdk.init();
//
//        orderStatusServer = new OrderStatusHandler();
//        
//        //查询未登录
//        //queryInitMaxBetMoneyBySelect();
//
//        //查询已登录
//        //queryMaxBetMoneyBySelect();
//
//        //下单
//        saveOrderAndValidateMaxPaid();
//
//        //订单结算
////        updateOrderAfterRefund();
//
//        //拒单通知
//        /*rejectOrder();*/
//    }
//
//
//    /*
//     * @Description 查询未登录最大最小限额
//     * @Param
//     * @return
//     **/
//    public static void queryInitMaxBetMoneyBySelect() {
//        OrderPaidApiService orderPaidApi =  rcsSdk.getOrderPaidApi(orderStatusServer);
//
//        Request<OrderBean> requestParam = new Request<>();
//
//        OrderBean orderBean = new OrderBean();
//        orderBean.setDeviceType(2);//设备类型
//        orderBean.setSeriesType(1);//串关类型(1：单关(默认)  )
//        orderBean.setSportId(1);//运动种类编号  1足球  2 篮球
//        orderBean.setTenantId(1L);//商户id
//
//        List<OrderItem> list = new ArrayList<>();
//        OrderItem orderItem = new OrderItem();
//        orderItem.setSportId(1);//运动种类编号  1足球  2 篮球
//        orderItem.setMatchType(1);//类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
//        orderItem.setPlayId(1);//玩法ID
//        orderItem.setOddsValue(194000.0);//注单赔率   扩大了10万倍
//        list.add(orderItem);
//        orderBean.setItems(list);
//
//        requestParam.setData(orderBean);
//
//        Response response = orderPaidApi.queryInitMaxBetMoneyBySelect(requestParam);
//
//        System.out.println("result:" + JSONObject.toJSONString(response));
//    }
//
//
//    /*
//     * @Description 查询已登录最大最小限额
//     * @Param
//     * @return
//     **/
//    public static void queryMaxBetMoneyBySelect() {
//        OrderPaidApiService orderPaidApi =  rcsSdk.getOrderPaidApi(orderStatusServer);
//
//        Request<OrderBean> requestParam = new Request<>();
//
//        OrderBean orderBean = new OrderBean();
//        orderBean.setDeviceType(2);//设备类型
//        orderBean.setSeriesType(1);//串关类型(1：单关(默认)  )
//        orderBean.setSportId(1);//运动种类编号  1足球  2 篮球
//        orderBean.setTenantId(1L);//商户id
//        orderBean.setUid(1L);//用户IDid
//
//        List<OrderItem> list = new ArrayList<>();
//        OrderItem orderItem = new OrderItem();
//        orderItem.setSportId(1);//运动种类编号  1足球  2 篮球
//        orderItem.setUid(1L);
//        orderItem.setMarketId(1212438265764782083L);//盘口id
//        orderItem.setOddsValue(203452.0);//注单赔率   扩大了10万倍
//        orderItem.setPlayId(2);//玩法ID
//        orderItem.setMatchId(84867L);//赛事id
//        orderItem.setPlatform("Panda");//操盘平台
//        orderItem.setMatchProcessId(0L);//赛事阶段id
//        orderItem.setMatchType(1);//类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
//        orderItem.setPlayOptionsId(1212438265777364993L);//投注类型ID
//        orderItem.setTurnamentLevel(1);  //新增字段  联赛分级. 1: 一级联赛; 2:二级联赛; 3: 三级联赛; 以此类推; 0: 未分级
//        orderItem.setTournamentId(816L);//联赛id
//        orderItem.setDataSourceCode("SR");//数据源
//        Date date = new Date(1579359600000L);//standardMatchInfo.getBeginTime() 比赛开始时间
//        date = org.apache.commons.lang3.time.DateUtils.addHours(date, -12);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        orderItem.setDateExpect(sdf.format(date));//开始时间
//
//        list.add(orderItem);
//        orderBean.setItems(list);
//
//        requestParam.setData(orderBean);
//
//        Response response = orderPaidApi.queryMaxBetMoneyBySelect(requestParam);
//
//        System.out.println("result:" + JSONObject.toJSONString(response));
//    }
//
//    /*
//     * @Description 下单
//     * @Param
//     * @return
//     **/
//    public static void saveOrderAndValidateMaxPaid() {
//        OrderPaidApiService orderPaidApi =  rcsSdk.getOrderPaidApi(orderStatusServer);
//
//        Request<OrderBean> requestParam = new Request<>();
//
//        OrderBean orderBean = new OrderBean();
//
//        orderBean.setSportId(1);//运动种类编号  1足球  2 篮球
//        orderBean.setTenantId(1L);//商户id
//        orderBean.setUid(1L);//用户IDid
//        orderBean.setSeriesType(1);
//        orderBean.setOrderNo("18990884798464");
//        orderBean.setCreateTime(new Date().getTime());
//        orderBean.setCreateUser("系统");
//        orderBean.setCurrencyCode("CNY");
//        orderBean.setDeviceType(2);
//        orderBean.setIp("172.18.178.251");
//        orderBean.setModifyTime(new Date().getTime());
//        orderBean.setModifyUser("系统");
//        orderBean.setOrderAmountTotal(10000L);
//        orderBean.setOrderNo("18990884798464");
//        orderBean.setOrderStatus(0);
//        orderBean.setProductAmountTotal(10000L);
//        orderBean.setProductCount(1L);
//        orderBean.setRemark("");
//        orderBean.setCurrencyCode("CYN");
//        orderBean.setIpArea("局域网");
//
//
//        List<OrderItem> list = new ArrayList<>();
//        OrderItem item = new OrderItem();
//        item.setBetAmount(10000L);
//        item.setBetNo("28990872215552");
//        item.setBetTime(1578901586791L);
//        item.setCreateTime(new Date().getTime());
//        item.setCreateUser("系统");
//        item.setIsValid(1);
//        item.setMarketId(1212438265764782083L);
//        item.setMarketType("EU");
//        item.setMarketValue("2.5/3");
//        item.setMatchId(84867L);
//        item.setPlatform("panda");
//        item.setMatchInfo("沃特福德 VS 托特纳姆");
//        item.setMatchName("超级联赛");
//        item.setMatchProcessId(0L);
//        item.setMaxWinAmount(10300.0);
//        item.setModifyTime(1578901586791L);
//        item.setModifyUser("系统");
//        item.setOddFinally("2.03");
//        item.setOddsValue(203452.0);
//        item.setOrderNo("18990884798464");
//        item.setPlayId(2);
//        item.setPlayName("大小盘");
//        item.setPlayOptions("OVer");
//        item.setPlayOptionsId(1212438265777364993L);
//        item.setPlayOptionsName("大 2.5/3");
//        item.setPlayOptionsRange("1");
//        item.setScoreBenchmark("0:0");
//        item.setSportId(1);
//        item.setSportName("足球");
//        item.setUid(1L);
//        item.setMatchType(1);
//        item.setDataSourceCode("SR");
//
//        item.setTurnamentLevel(1);  //新增字段  联赛分级. 1: 一级联赛; 2:二级联赛; 3: 三级联赛; 以此类推; 0: 未分级
//        item.setTournamentId(816L);//联赛id
//        item.setDataSourceCode("SR");//数据源
//        Date date = new Date(1579359600000L);//standardMatchInfo.getBeginTime() 比赛开始时间
//        date = org.apache.commons.lang3.time.DateUtils.addHours(date, -12);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        item.setDateExpect(sdf.format(date));//开始时间
//
//        list.add(item);
//        orderBean.setItems(list);
//
//        requestParam.setData(orderBean);
//
//        Response response = orderPaidApi.saveOrderAndValidateMaxPaid(requestParam);
//
//        System.out.println("result:" + JSONObject.toJSONString(response));
//    }
//
//
//    /*
//     * @Description 订单结算
//     * @Param
//     * @return
//     **/
//    public static void updateOrderAfterRefund() {
//        String json= "{\n" +
//                "    \"sourceResult\":null,\n" +
//                "    \"id\":null,\n" +
//                "    \"uid\":1,\n" +
//                "    \"orderNo\":\"18990884798464\",\n" +
//                "    \"settleAmount\":30000L,\n" +
//                "    \"payoutStatus\":0,\n" +
//                "    \"settleType\":1,\n" +
//                "    \"settleTime\":1579062737962,\n" +
//                "    \"oddFinally\":\"2.03\",\n" +
//                "    \"oddsValue\":203452.0,\n" +
//                "    \"createUser\":\"系统\",\n" +
//                "    \"createTime\":1579062737962,\n" +
//                "    \"modifyUser\":\"系统\",\n" +
//                "    \"modifyTime\":1579062737962,\n" +
//                "    \"delFlag\":0,\n" +
//                "    \"remark\":\"投注结算\",\n" +
//                "    \"betAmount\":10000L,\n" +
//                "    \"settleScore\":null,\n" +
//                "    \"outCome\":4,\n" +
//                "    \"orderStatus\":1,\n" +
//                "    \"orderDetails\":[\n" +
//                "        {\n" +
//                "            \"createUser\":\"系统\",\n" +
//                "            \"createTime\":1578835102977,\n" +
//                "            \"modifyUser\":\"系统\",\n" +
//                "            \"modifyTime\":1578835102977,\n" +
//                "            \"id\":168997,\n" +
//                "            \"betNo\":\"28990872215552\",\n" +
//                "            \"orderNo\":\"18990884798464\",\n" +
//                "            \"uid\":1,\n" +
//                "            \"sportId\":1,\n" +
//                "            \"sportName\":null,\n" +
//                "            \"playId\":2,\n" +
//                "            \"playName\":\"全场赛果\",\n" +
//                "            \"matchId\":84867,\n" +
//                "            \"matchType\":0,\n" +
//                "            \"oddFinally\":\"1.59\",\n" +
//                "            \"acceptBetOdds\":1,\n" +
//                "            \"betTime\":1578835102971,\n" +
//                "            \"marketType\":\"EU\",\n" +
//                "            \"marketValue\":\"2.5/3\",\n" +
//                "            \"matchInfo\":\"Go Ahead Eagles vs. Almere City FC\",\n" +
//                "            \"betAmount\":10000,\n" +
//                "            \"oddsValue\":203452.0,\n" +
//                "            \"maxWinAmount\":295,\n" +
//                "            \"betStatus\":null,\n" +
//                "            \"scoreBenchmark\":\"-\",\n" +
//                "            \"playOptionsId\":1212438265777364993,\n" +
//                "            \"playOptions\":\"OVer\",\n" +
//                "            \"playOptionsRange\":\"1\",\n" +
//                "            \"remark\":\"用户下注\",\n" +
//                "            \"delFlag\":null,\n" +
//                "            \"matchProcessId\":0,\n" +
//                "            \"tournamentId\":816,\n" +
//                "            \"marketId\":1212438265764782083,\n" +
//                "            \"marketTypeFinally\":\"EU\",\n" +
//                "            \"result\":null,\n" +
//                "            \"matchName\":\"测试\",\n" +
//                "            \"betResult\":3,\n" +
//                "            \"addition\":null,\n" +
//                "            \"playOptionName\":null,\n" +
//                "            \"riskChannel\":1,\n" +
//
//                "            \"playType\":1,\n" +
//                "            \"tournamentLevel\":1,\n" +
//                "            \"dateExpect\":\"2020-01-18\",\n" +
//
//                "            \"betAllResult\":null\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}";
//
//        OrderPaidApiService orderPaidApi =  rcsSdk.getOrderPaidApi(orderStatusServer);
//        Request<SettleItem> requestParam = new Request<>();
//        SettleItem settleItem = JSONObject.parseObject(json,SettleItem.class);
//        requestParam.setData(settleItem);
//
//        Response response = orderPaidApi.updateOrderAfterRefund(requestParam);
//
//        System.out.println("result:" + JSONObject.toJSONString(response));
//    }
//
//    /*
//     * @Description 业务拒单，通知风控
//     * @Param
//     * @return
//     **/
//    public static void rejectOrder(){
//        OrderPaidApiImpl orderPaidApi = rcsSdk.getOrderPaidApi(new OrderStatusHandler());
//        Request<OrderBean> requestParam = new Request<>();
//
//        OrderBean orderBean = new OrderBean();
//        orderBean.setOrderNo("18990884798464");
//        orderBean.setOrderStatus(2);//已取消
//
//        List<OrderItem> list = new ArrayList<>();
//        OrderItem item = new OrderItem();
//        item.setBetNo("28990872215552");
//        item.setOrderNo("18990884798464");
//        item.setOddsValue(100000D);
//        item.setValidateResult(2);
//        list.add(item);
//        orderBean.setItems(list);
//        requestParam.setData(orderBean);
//
//        Response response = orderPaidApi.rejectOrder(requestParam);
//
//        System.out.println("result:" + JSONObject.toJSONString(response));
//    }
//}
