/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.order;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.rcs.mts.sportradar.builder.MtsSdkInit;
import com.panda.sport.rcs.mts.sportradar.builder.RcsMtsSdkApi;
import com.panda.sport.rcs.mts.sportradar.builder.TicketBuilderHelper;
import com.panda.sport.rcs.mts.sportradar.constants.Constants;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketAckHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketCancelAckHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketCancelResponseHandler;
import com.panda.sport.rcs.mts.sportradar.listeners.TicketResponseHandler;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.interfaces.*;
import com.sportradar.mts.sdk.app.MtsSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*{
      "matchId":"sr:season:68176",
          "sprotId":"sr:sport:2",
          "playId":534,
          "marketId":"pre:outcometext:6885023",
          "money":10000,
          "odds":22400,
          "marketValue":"variant=pre:markettext:79226",
          "marketType":3
  }*/
public class BasicTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicTest.class);
    private static int isStart = 0;
    static SdkConfiguration config = null;

    static TicketAckSender ticketAckSender = null;
    static TicketCancelSender ticketCancelSender = null;
    static TicketCancelAckSender ticketCancelAckSender = null;
    static MtsSdkApi mtsSdk = null;

    static {
            logger.info("mts测试开始1");
            config = MtsSdk.getConfiguration();
            Constants.setConfig(config);
            mtsSdk = new RcsMtsSdkApi(config);
            logger.info("mts测试2");
            mtsSdk.open();
            logger.info("mts测试3"+JSONObject.toJSONString(mtsSdk));
//            ticketAckSender = mtsSdk.getTicketAckSender(new TicketAckHandler());
//            ticketCancelAckSender = mtsSdk.getTicketCancelAckSender(new TicketCancelAckHandler());
//            ticketCancelSender = mtsSdk.getTicketCancelSender(new TicketCancelResponseHandler(ticketCancelAckSender, mtsSdk.getBuilderFactory()));
            logger.info("mts测试4");
    }


    public static void test(String matchId, int marketType, String sprotId, int playId, String marketId, String marketValue, int odds, int money) {
        //查额度
        try {
//            logger.info("mts测试5");
//            TicketResponseHandler ticketResponseHandler = new TicketResponseHandler(ticketCancelSender, ticketAckSender, mtsSdk.getBuilderFactory());
//            TicketSender ticketSender = mtsSdk.getTicketSender(ticketResponseHandler);
//            logger.info("mts测试6");
            Ticket ticket = new TicketBuilderHelper(mtsSdk.getBuilderFactory()).getTicket(matchId, marketType, sprotId, playId, marketId, marketValue, odds, money);
//            logger.info("mts测试7");
//            logger.info("{}请求ticket:{} ", "", JSONObject.toJSONString(ticket, SerializerFeature.DisableCircularReferenceDetect));
            long amount = mtsSdk.getClientApi().getMaxStake(ticket) / 10000;
            logger.info("{}请求getMaxStake返回:{} ", "", amount);
        } catch (Exception e) {
            logger.info("mts测试异常:" + e);
            e.printStackTrace();
        }

        //下单
//        try {
//            logger.info("mts测试5");
//            TicketResponseHandler ticketResponseHandler = new TicketResponseHandler(ticketCancelSender, ticketAckSender, mtsSdk.getBuilderFactory());
//            TicketSender ticketSender = mtsSdk.getTicketSender(ticketResponseHandler);
//            logger.info("mts测试6");
//            Ticket ticket = new TicketBuilderHelper(mtsSdk.getBuilderFactory()).getTicket(matchId, marketType, sprotId, playId, marketId, marketValue, odds, money);
//            logger.info("mts测试7");
//            ticketSender.send(ticket);
//            logger.info("mts测试发送完成:" + JSONObject.toJSONString(ticket));
//        } catch (Exception e) {
//            logger.info("mts测试异常:" + e);
//            e.printStackTrace();
//        }
    }


}
