package com.panda.sport.rcs;


import cn.hutool.json.JSONUtil;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.data.rcs.dto.oddin.TicketsAfterDto;
import com.panda.sport.data.rcs.dto.oddin.entity.*;
import com.panda.sport.rcs.enums.oddin.AcceptOddsChangeEnum;
import com.panda.sport.rcs.enums.oddin.BetStakeTypeEnum;
import com.panda.sport.rcs.enums.oddin.TicketChannelEnum;
import com.panda.sport.rcs.oddin.entity.ots.Enums;
import com.panda.sport.rcs.oddin.entity.ots.TicketCancel;
import com.panda.sport.rcs.oddin.entity.ots.TicketMaxStake;
import com.panda.sport.rcs.oddin.grpc.FutureGrpcService;
import com.panda.sport.rcs.oddin.grpc.PullSingleGrpcService;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = OddinBootstrap.class)
@Slf4j
public class TicketTest {
    String selectionId = "od:match:342660/24/4?map=1&threshold=28.5";
    @Resource
    private TicketOrderService ticketOrderService;
    @Resource
    private FutureGrpcService futureGrpcService;
    @Resource
    private PullSingleGrpcService pullSingleGrpcService;

    @Test
    public void saveOrderTest() {
        Request<TicketDto> request = new Request<>();
        request.setData(getTicketDto());
        ticketOrderService.saveOrder(request);
//        ticketOuterGrpc.ticket(getTicketDto());
    }

    @Test
    public void cancelOrderTest() {
        Request<CancelOrderDto> request = new Request<>();
        CancelOrderDto dto = getCancelOrderDto();
        request.setData(dto);
        log.info("入参：{}", JSONUtil.toJsonStr(dto));
        TicketCancel.TicketCancelResponse response = futureGrpcService.cancelOrder(dto);
//        Response response = ticketOrderService.cancelOrder(request);
        log.info(response.toString());
    }

    @Test
    public void TicketMaxTest() throws Exception {
        TicketMaxStake.TicketMaxStakeResponse response = futureGrpcService.queryMaxBetMoneyBySelect(getTicketDto());
        log.info(response.toString());
    }

    @Test
    public void TicketResult() {
        pullSingleGrpcService.pullSingle(ticketResultDto());
    }

    private TicketResultDto ticketResultDto() {
        TicketResultDto ticketResultDto = new TicketResultDto();
        TicketsAfterDto afterDto = new TicketsAfterDto();
       /* afterDto.setAfter(com.google.protobuf.Timestamp.getDefaultInstance());
        afterDto.setRequest_id("232323232");*/
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId("");
        ticketResultDto.setId(ticketDto.getId());
        ticketResultDto.setTicketsAfterDto(afterDto);
        return ticketResultDto;
    }

    /*private TicketDto getId(){
        TicketDto ticketDto=new TicketDto();
        *//*ticketDto.setId("121212121");*//*
        ticketDto.setId("");
        return  ticketDto;
    }*/


    private TicketDto getTicketDto() {
        //限额投注实体
        TicketDto ticketDto = new TicketDto();
        //请求限额的UID
        ticketDto.setId("11453228000");

        //请求限额,投注的UTC时间
        java.sql.Timestamp d = new Timestamp(System.currentTimeMillis());
        ticketDto.setTimestamp(d);
        //目前电竞只支持单关传1
        ticketDto.setTotalCombinations(1);
        //支持语言
        ticketDto.setCurrency("CNY");
        //自动接受赔率的变化
        ticketDto.setAccept_odds_change(AcceptOddsChangeEnum.ACCEPT_ODDS_CHANGE_ANY);
        //来源渠道
        ticketDto.setChannel(TicketChannelEnum.TICKET_CHANNEL_INTERNET);
        //用户信息
        TicketCustomer customer = new TicketCustomer();
        //用户ID
        customer.setId("232323");
        //支持的语言
        customer.setLanguage("zh");

        //注单列表
        Bet bet = new Bet();
        List<Bet> betList = new ArrayList<>();
        //投注实体
        BetStake betStake = new BetStake();
        //投注金额
        betStake.setValue(200);
        //股权类型
        betStake.setType(BetStakeTypeEnum.BET_STAKE_TYPE_SUM);

        Integer[] systems = new Integer[]{1, 66, 102, 111, 108, 100};
        bet.setSystems(systems);
        bet.setSelections(selectionId);
        bet.setStake(betStake);
        betList.add(bet);
        /*ticketDto.setBets(betList);*/

        TicketSelection selection = new TicketSelection();
        selection.setId(selectionId);
        selection.setForeign(false);
        selection.setOdds("1.002");
        Map<String, TicketSelection> selectionsMap = new HashMap<>();
        selectionsMap.put(selection.getId(), selection);

        ticketDto.setSelections(selectionsMap);
        //赋值用户信息
        ticketDto.setCustomer(customer);
        //赋值投注信息
        ticketDto.setBets(betList);
        ticketDto.setLocation_id(Long.valueOf("12345"));

        return ticketDto;
    }

    public CancelOrderDto getCancelOrderDto() {
        CancelOrderDto cancelOrderDto = new CancelOrderDto();
        cancelOrderDto.setSourceId(1);
        cancelOrderDto.setId("1145322468");
        cancelOrderDto.setCancelReason(Enums.CancelReason.CANCEL_REASON_WRONG_TICKET.getNumber());
        cancelOrderDto.setCancelPercent(1);
        java.sql.Timestamp d = new Timestamp(System.currentTimeMillis());
        cancelOrderDto.setTimestamp(d);
        cancelOrderDto.setCancelReasonDetail(Enums.CancelReason.CANCEL_REASON_WRONG_TICKET.name());
        CancelBetInfo cancelBetInfo = new CancelBetInfo();
        cancelBetInfo.setCancelPercent(1);
        cancelBetInfo.setId("112233");
        cancelOrderDto.setCancelBetInfo(cancelBetInfo);
        return cancelOrderDto;
    }
}
