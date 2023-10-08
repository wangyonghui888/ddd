package com.test.api;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.virtual.third.client.ApiException;
import com.panda.sport.rcs.virtual.third.client.api.EntityApi;
import com.panda.sport.rcs.virtual.third.client.api.EventBlockApi;
import com.panda.sport.rcs.virtual.third.client.api.TicketApi;
import com.panda.sport.rcs.virtual.third.client.api.WalletApi;
import com.panda.sport.rcs.virtual.third.client.model.*;
import org.junit.Test;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicketAllTest {
	
    private final EntityApi entityApi = new EntityApi();
    
    private final WalletApi walletApi = new WalletApi();
    
    private final EventBlockApi eventApi = new EventBlockApi();
    
    private final TicketApi ticketApi = new TicketApi();
	
    @Test
    public void createUser() throws ApiException {
    	ApiTest.setApiDomain();
    	
    	//挂载的用户id（类似代理）
        Integer entityParentId = 1548;
        String entityName = "lithan998";
        String extId = entityName;
        try {
        	//获取entityId :4500
            Entity response = entityApi.entityAdd(entityParentId, entityName, "ENABLED", extId, "", true, Arrays.asList("External"));
            System.out.println(response);
            
            //获取calculationId，下注使用：2202
            Integer entityId = response.getId();
            Integer calculationId = entityApi.getCalculationIdByEntityId(entityId, extId);
            System.err.println(calculationId);
            
            //设置币种
            List<Integer> entitiesId = Arrays.asList(new Integer[]{entityId});
            LocalizationContext contexts = new LocalizationContext().defaultCurrency("RMB");
            CONtext coNtext = new CONtext(contexts);
            entityApi.setContext(entitiesId, coNtext);
            
            //查看是否创建钱包
            List<Wallet> walletList = walletApi.walletFindAllByEntityId(entityId, 1, 0, "DESC", null);
            System.out.println(walletList);
            
            //没有就创建钱包
            if(walletList == null ) {
                Wallet wallet = walletApi.walletCreate(entityId, "RMB", null, null, null, null, null, null, null, null);
                System.out.println(wallet);
            }
            
            //获取最大最小值
//            CalculationContext context = entityApi.getCalculationContextById(calculationId);
//            System.out.println(JSONObject.toJSON(context.getTicketContext().getCurrencySetting()));
//            List<TicketCurrencySetting> ticketList = context.getTicketContext().getCurrencySetting().stream().filter(bean -> "CNY".equals(bean.getKey())).collect(Collectors.toList());
//            System.err.println(JSONObject.toJSONString(ticketList.get(0).getLimits()));
            
            
            //获取赛事
            SellTicketExternal ticket = new SellTicketExternal(calculationId, extId, entityParentId);
            TicketDetail ticketDetail = new TicketDetail();
            //设置赛事 和投注
            List<TicketEvent> eventList = new ArrayList<>();

            TicketEvent event = new TicketEvent();
            event.setPlaylistId(24002);
            event.setEventId(Integer.valueOf(360141).longValue());
            TicketBet ticketBet = new TicketBet("win", "win_1", "6.58", 10D);
            event.addBetsItem(ticketBet);
            eventList.add(event);

//            TicketEvent event2 = new TicketEvent();
//            event2.setPlaylistId(14002);
//            event2.setEventId(717811L);
//            TicketBet ticketBet2 = new TicketBet("Double_Chance", "Draw_Away", "1.31", 10D);
//            event2.addBetsItem(ticketBet2);
//            eventList.add(event2);


            ticketDetail.setEvents(eventList);



//            设置SystemBet
//            List<SystemBet> systemBetList = new ArrayList<>();
//            SystemBet systemBet = new SystemBet();
//            systemBet.setSystemCount(eventList.size());
//            systemBet.setGrouping(2);
//            systemBet.setStake(100D);
//
////            { "maxBonus": 0 , "minBonus": 0,  "minWinning":0, "maxWinning":0, "limitMaxPayout": 10000000 }
//            WinningData winningData = new WinningData();
//            winningData.setLimitMaxPayout(1000000D);
//            winningData.setMaxBonus(0D);
//            winningData.setMaxWinning(100 * 2.42 * 1.31);
//            winningData.setMinBonus(0D);
//            winningData.setMinWinning(100 * 2.42 * 1.31);
//            systemBet.setWinningData(winningData);
//
//            systemBetList.add(systemBet);
//            ticketDetail.setSystemBets(systemBetList);

            ticket.setDetails(ticketDetail);
            System.err.println(JSONObject.toJSONString(ticket));
//            System.err.println(ticket);
            //下单
            TicketTransaction ticketRes = ticketApi.ticketCreate(ticket);
            System.err.println(JSONObject.toJSONString(ticket));

        }catch (ApiException e){
            System.err.println(e.getResponseBody());
        }catch (Exception e){
            System.err.println(e);
        }
    }


    private void test1(){
        //串关 一串多
        //格式：
        /*{
            "timeSend":"{{event_time}}",
            "calculationId":2160,
            "entityExtId":"1234",
            "parentId":1548,
            "details":{
                "events":[
                    {
                        "eventId":23217,
                        "playlistId":11101,
                        "bets":[
                            {
                                "oddId":"Home",
                                "oddValue":"1.25",
                                "stake":1
                            }
                        ]
                    }
                ]
            }
        }*/
        SellTicketExternal ticket = new SellTicketExternal();
        ticket.setCalculationId(2202);
        ticket.setParentId(1548);
        ticket.setEntityExtId("test04");
        ticket.setTimeSend(OffsetDateTime.now());

        TicketDetail ticketDetail = new TicketDetail();
        ticket.setDetails(ticketDetail);
        List<TicketEvent> eventList = new ArrayList<>();
        ticketDetail.setEvents(eventList);
        List<SystemBet> systemBetList = new ArrayList<>();
        ticketDetail.setSystemBets(systemBetList);

        TicketEvent ticketEvent = new TicketEvent();
        eventList.add(ticketEvent);
        ticketEvent.setEventId(	23217L);
        ticketEvent.setPlaylistId(11101);
        List<TicketBet> betList = new ArrayList<>();
        ticketEvent.setBets(betList);
        TicketBet ticketBet = new TicketBet();
        betList.add(ticketBet);
        ticketBet.setMarketId("Match result");
        ticketBet.setOddId("Home");
        ticketBet.setOddValue("1.84");
        ticketBet.setStake(1D);
        TicketBet ticketBet2 = new TicketBet();
        betList.add(ticketBet2);
        ticketBet2.setMarketId("Double_Result");
        ticketBet2.setOddId("HomeAway");
        ticketBet2.setOddValue("86.3");
        ticketBet2.setStake(3D);
    }

    private void test2(){
//        串关 多串多
//        格式：
//        {
//            "timeSend":"{{event_time}}",
//            "calculationId":2160,
//            "entityExtId":"1234",
//            "parentId":1548,
//            "details":{
//                "events":[
//                    {
//                        "eventId":23217,
//                        "playlistId":11101,
//                        "bets":[
//                            {
//                                "oddId":"Home",
//                                "oddValue":"1.25",
//                                "stake":1
//                            }，
//                              {
//                                "oddId":"Home",
//                                "oddValue":"1.25",
//                                "stake":1
//                            }
//                        ]
//                    },{
//                        "eventId":23218,
//                        "playlistId":11101,
//
//        "bets":[
//                            {
//                                "oddId":"Home",
//                                "oddValue":"1.71",
//                                "stake":1.5
//                            }
//                        ]
//                    }
//                ]
//            }
//        }
        SellTicketExternal ticket = new SellTicketExternal();
        ticket.setCalculationId(2202);
        ticket.setParentId(1548);
        ticket.setEntityExtId("test04");
        ticket.setTimeSend(OffsetDateTime.now());

        TicketDetail ticketDetail = new TicketDetail();
        ticket.setDetails(ticketDetail);
        List<TicketEvent> eventList = new ArrayList<>();
        ticketDetail.setEvents(eventList);

        List<SystemBet> systemBetList = new ArrayList<>();
        ticketDetail.setSystemBets(systemBetList);
        SystemBet systemBet = new SystemBet();
        TicketEvent ticketEvent = new TicketEvent();
        eventList.add(ticketEvent);
        ticketEvent.setEventId(23328L);
        ticketEvent.setPlaylistId(11101);

        List<TicketBet> betList = new ArrayList<>();
        ticketEvent.setBets(betList);

        TicketBet ticketBet = new TicketBet();
        betList.add(ticketBet);
        ticketBet.setMarketId("Match_Result");
        ticketBet.setOddId("Away");
        ticketBet.setOddValue("5.12");
        ticketBet.setStake(5D);

        TicketBet ticketBet1 = new TicketBet();
        betList.add(ticketBet1);
        ticketBet1.setMarketId("Over_Under_2_5");
        ticketBet1.setOddId("under_2_5");
        ticketBet1.setOddValue("1.67");
        ticketBet1.setStake(1.5);

        TicketEvent ticketEvent2 = new TicketEvent();
        eventList.add(ticketEvent2);
        ticketEvent2.setEventId(23327L);
        ticketEvent2.setPlaylistId(11101);
        List<TicketBet> betList3 = new ArrayList<>();
        ticketEvent2.setBets(betList3);
        TicketBet ticketBet2 = new TicketBet();
        betList3.add(ticketBet2);
        ticketBet2.setMarketId("Double_Chance");
        ticketBet2.setOddId("Draw_Away");
        ticketBet2.setOddValue("1.30");
        ticketBet2.setStake(10D);
    }
}
