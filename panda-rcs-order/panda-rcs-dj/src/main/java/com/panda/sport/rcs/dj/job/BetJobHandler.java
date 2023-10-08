package com.panda.sport.rcs.dj.job;

import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.dj.DJBetReqVo;
import com.panda.sport.data.rcs.dto.dj.DjBetOrder;
import com.panda.sport.data.rcs.dto.dj.Selection;
import com.panda.sport.rcs.dj.service.DjServiceImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName BetJobHandler
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/23 16:53
 * @Version 1.0
 **/
@Component
@JobHandler("BetJobHandler")
public class BetJobHandler extends IJobHandler {

    @Autowired
    private DjServiceImpl djService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        DJBetReqVo djBetReqVo = new DJBetReqVo();
        djBetReqVo.setAccountName("156689tt");
        djBetReqVo.setBetNum(1);
        djBetReqVo.setBetTime(1632376915000L);
        djBetReqVo.setMerchant("31433517168705439");
        djBetReqVo.setDevice(1);
        djBetReqVo.setIp("192.168.4.100");
        djBetReqVo.setOddUpdateType(1);
        djBetReqVo.setVipLevel(0);
        djBetReqVo.setOrderNo("684313132154");
        djBetReqVo.setUserId(42213662126L);
        djBetReqVo.setSeriesType(2001);
        List<DjBetOrder> orderList = new ArrayList<>();
        DjBetOrder order = new DjBetOrder();
        order.setAmount(100L);
        order.setNum(2);
        order.setOrderType(2);
        Selection se = new Selection();
        se.setMatchId(32342751911461277L);
        se.setMarketId(32343194517523628L);
        se.setOddsId(32343194517756942L);
        se.setBetNo("1351565484351");
        se.setPlayOptionsName("小");
        se.setSportId(1);
        se.setSportName("Dota");
        se.setMatchInfo("WS VS PR");
        se.setOdds(1.809);
        Selection selection1 = new Selection();
        selection1.setMatchId(32330818857250592L);
        selection1.setMarketId(32330827757855912L);
        selection1.setOddsId(32330827758506652L);
        selection1.setOdds(1.828);
        selection1.setBetNo("3543543123321");
        selection1.setPlayOptionsName("主队");
        selection1.setSportId(1);
        selection1.setSportName("Dota");
        selection1.setMatchInfo("DT VS DW");
        List<Selection> selections = Lists.newArrayList(se,selection1);
        order.setSelections(selections);
        orderList.add(order);
        djBetReqVo.setOrderList(orderList);
        djService.djBet(djBetReqVo);

        return ReturnT.SUCCESS;
    }
}
