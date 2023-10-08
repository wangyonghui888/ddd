package com.panda.sport.rcs;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.OrderPaidApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.sdk.core.Sdk;
import com.panda.sport.sdk.listeners.OrderStatusServer;

public class VirtualDemo {

//    private static Sdk rcsSdk;
//    private static OrderStatusServer orderStatusServer;

//    public static void main(String[] args) {
//        queryMaxBetMoneyBySelect();
//    }
//        public static void queryMaxBetMoneyBySelect() {
//            OrderPaidApiService orderPaidApi = rcsSdk.getOrderPaidApi(orderStatusServer);
//            String jsonStr = "{\"globalId\":\"0aeb5bdde45c4730873ce1dd6fee3292\",\"data\":{\"orderNo\":\"5061259786158220\",\"uid\":506100576579300020,\"acceptOdds\":2," +
//                    "\"orderStatus\":0,\"handleStatus\":0,\"productCount\":1,\"seriesType\":1,\"productAmountTotal\":1000,\"orderAmountTotal\":1000,\"deviceType\":2,\"ip\":\"172.26.177.220\"," +
//                    "\"tenantId\":2,\"tenantName\":\"tm002 密码QuFO~IZEkw!R\",\"createTime\":1687086595369,\"userFlag\":\"\",\"userTagLevel\":230,\"username\":\"\",\"infoStatus\":0," +
//                    "\"modifyTime\":1687086595369,\"currencyCode\":\"CNY\",\"ipArea\":\"局域网,局域网,\",\"vipLevel\":0,\"validateResult\":1,\"items\":[{\"betNo\":\"506125978617325\"," +
//                    "\"orderNo\":\"5061259786158220\",\"uid\":506100576579300020,\"sportId\":1001,\"sportName\":\"VR足球\",\"playId\":20002,\"playName\":\"全场大小\"," +
//                    "\"matchId\":334882632584679425,\"matchName\":\"Spain League 2023 - OS32\",\"betTime\":1687086595398,\"marketType\":\"EU\",\"marketValue\":\"2/2.5\"," +
//                    "\"matchInfo\":\"Đua Xe Ngựa VR v rayo_vallecano\",\"betAmount\":1000,\"handleStatus\":0,\"marketId\":334882644752355329,\"oddsValue\":1.81,\"oddFinally\":\"1.81\"," +
//                    "\"maxWinAmount\":810.0,\"scoreBenchmark\":\"\",\"playOptionsId\":334882644752355330,\"playOptionsName\":\"小 2/2.5\",\"playOptions\":\"Under\",\"matchProcessId\":0," +
//                    "\"createTime\":1687086595398,\"createUser\":\"系统\",\"modifyUser\":\"系统\",\"modifyTime\":1687086595398,\"tournamentId\":324677364116377602,\"validateResult\":0," +
//                    "\"dateExpect\":\"2023-06-18\",\"orderStatus\":0,\"turnamentLevel\":0,\"otherScore\":\"\",\"dataSourceCode\":\"GR\",\"handledBetAmout\":10,\"handleAfterOddsValue\":0.0," +
//                    "\"paidAmount\":0.0,\"paidAmount1\":0.0,\"handleAfterOddsValue1\":1.8E-5,\"betAmount1\":10}],\"fpId\":\"a13596378f9135ebe307f9794b207eb91d\",\"orderGroup\":\"common\"}}";
////            Request<OrderBean> requestParam = new Request<>();
////            requestParam.setGlobalId("0aeb5bdde45c4730873ce1dd6fee3292");
//
//            Request<OrderBean> request = JSONObject.parseObject(jsonStr,Request.class);
//
//            Response response = orderPaidApi.saveOrderAndValidateMaxPaid(request);
//            System.out.println(response);
//        }
}
