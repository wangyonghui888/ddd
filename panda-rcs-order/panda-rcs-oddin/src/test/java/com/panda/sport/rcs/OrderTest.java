package com.panda.sport.rcs;

import com.alibaba.fastjson.JSON;
import com.panda.sport.data.rcs.vo.oddin.RejectReasonVo;
import com.panda.sport.data.rcs.vo.oddin.StandardMarketVo;
import com.panda.sport.data.rcs.vo.oddin.StandardMatchVo;
import com.panda.sport.data.rcs.vo.oddin.TicketVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderTy;
import com.panda.sport.rcs.oddin.service.RcsOrderService;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.panda.sport.rcs.oddin.common.Constants.STANDAR_MATCH_MARKET_INFO_OF_ODDIN;

/**
 * @author Beulah
 * @date 2023/3/20 17:29
 * @description 单元测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OddinBootstrap.class)
@Slf4j
public class OrderTest {

    @Resource
    RcsOrderService rcsOrderService;
    @Resource
    TicketOrderService ticketOrderService;
    @Resource
    private RedisClient redisClient;

    @Test
    public void tyOrderRallbackTest() {

        TicketVo vo = new TicketVo();
        vo.setSourceId(2);
        vo.setTicket_status("ACCEPTANCE_STATUS_ACCEPTED");
        vo.setId("506285525040078777");
        RejectReasonVo reasonVo = new RejectReasonVo();
        reasonVo.setCode("CODE_UNSPECIFIED");
        reasonVo.setMessage("");
        vo.setReject_reson(reasonVo);
//        TicketVo.ExchangeRate exchangeRate = new TicketVo.ExchangeRate();
//        exchangeRate.setValue(75893L);
//        vo.setExchange_rate(exchangeRate);
//        TicketVo.AutoAcceptedOdds acceptedOdds = new TicketVo.AutoAcceptedOdds();
//        AutoAcceptedOddsVo oddsVo = new AutoAcceptedOddsVo();
//        oddsVo.setUsedOdds(0.0F);
//        oddsVo.setRequestedOdds(0.0F);
//        acceptedOdds.setValue(oddsVo);
//        vo.setAuto_accepted_odds(acceptedOdds);
        rcsOrderService.rejectOrder(vo);
    }

    @Test
    public void updateOrderTest() {
        RcsOddinOrderTy order = new RcsOddinOrderTy();
        order.setStatus("ACCEPTANCE_STATUS_ACCEPTED");
        order.setOrderNo("5057025973710014");
        ticketOrderService.updateOrder(order, 2);
    }

    @Test
    public void setCache() {
        StandardMatchVo standardMatchVo = new StandardMatchVo();
        standardMatchVo.setDataSourceCode("OD");
        standardMatchVo.setSportId(1L);
        standardMatchVo.setStandardMatchInfoId(3487528L);
        standardMatchVo.setStandardTournamentId(1184441L);
        standardMatchVo.setStatus(0);
        List<StandardMarketVo> marketVoList = new ArrayList<>();
        StandardMarketVo marketVo1 = new StandardMarketVo();
        marketVo1.setId(141232133773323038L);
        marketVo1.setStatus(0);
        marketVoList.add(marketVo1);
        StandardMarketVo marketVo2 = new StandardMarketVo();
        marketVo2.setId(145538193090236312L);
        marketVo2.setStatus(1);
        marketVoList.add(marketVo2);
        standardMatchVo.setMarketList(marketVoList);
        String key = String.format(STANDAR_MATCH_MARKET_INFO_OF_ODDIN, standardMatchVo.getStandardMatchInfoId());
        redisClient.setExpiry(key, JSON.toJSONString(standardMatchVo), 2 * 60 * 60 * 1000L);
    }

}
