package com.test.api.virtual;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.virtual.BetAmountLimitReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetItemReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetReqVo;
import com.panda.sport.rcs.VirtualBootstrap;
import com.panda.sport.rcs.virtual.service.VirtualApiServiceImpl;
import com.panda.sport.rcs.virtual.service.VirtualServiceImpl;
import com.panda.sport.rcs.virtual.third.client.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = VirtualBootstrap.class)
public class VirtualApiTest {

    @Autowired
    VirtualApiServiceImpl virtualApiService;

    @Autowired
    VirtualServiceImpl virtualService;

    @Test
    public void bet() throws ApiException {
        BetReqVo reqVo = new BetReqVo();
        reqVo.setTenantId(2L);
        reqVo.setOrderNo(System.currentTimeMillis() + "");
        reqVo.setTotalStake(1000L);
        reqVo.setUserId(10086L);
        reqVo.setIp("122.56.95.62");
        reqVo.setSeriesType(1);
        reqVo.setDeviceType(1);
        reqVo.setCurrencyCode("RMB");
        reqVo.setBetTime(System.currentTimeMillis());
        List<BetItemReqVo> list = new ArrayList<>();

        BetItemReqVo item = new BetItemReqVo();
        item.setBetNo(System.currentTimeMillis() + "");
        item.setPlayListId(Long.valueOf("24005"));
        item.setEventId(Long.valueOf(369489));
        item.setMarketId("over_under");
        item.setOddId("under");
        item.setSportId(1);
        item.setStake(1000L);
        item.setOddValue(new BigDecimal(180000).divide(new BigDecimal("100000.0")).toString());
        list.add(item);

//        BetItemReqVo item2 = new BetItemReqVo();
//        item2.setBetNo(System.currentTimeMillis() + "2");
//        item2.setPlayListId(Long.valueOf("24007"));
//        item2.setEventId(Integer.valueOf(224874).longValue());
//        item2.setMarketId("over_under");
//        item2.setOddId("over");
//        item2.setOddValue(new BigDecimal(297000).divide(new BigDecimal("100000.0")).toString());
//        item2.setSportId(1);
//        item2.setStake(1000L);
//        list.add(item2);

//        BetItemReqVo item3 = new BetItemReqVo();
//        item3.setBetNo(System.currentTimeMillis() + "3");
//        item3.setPlayListId(Long.valueOf("29000"));
//        item3.setEventId(Integer.valueOf(698464).longValue());
//        item3.setMarketId("Match_Result");
//        item3.setOddId("Home");
//        item3.setOddValue(new BigDecimal(416000).divide(new BigDecimal("100000.0")).toString());
//        item3.setSportId(1);
//        item3.setStake(1000L);
//        list.add(item3);

//        BetItemReqVo item4 = new BetItemReqVo();
//        item4.setBetNo(System.currentTimeMillis() + "4");
//        item4.setPlayListId(Long.valueOf("29000"));
//        item4.setEventId(Integer.valueOf(698714).longValue());
//        item4.setMarketId("Match_Result");
//        item4.setOddId("Home");
//        item4.setOddValue(new BigDecimal(416000).divide(new BigDecimal("100000.0")).toString());
//        item4.setSportId(1);
//        item4.setStake(1000L);
//        list.add(item4);

        reqVo.setOrderItemList(list);

        Request<BetReqVo> request = new Request<>();
        request.setData(reqVo);
        request.setGlobalId(UUID.randomUUID().toString());
        Response res =virtualApiService.bet(request);
        System.out.println(JSONObject.toJSONString(res));
    }

    @Test
    public void getBetAmountLimit(){
        Request<BetAmountLimitReqVo> request = new Request<>();
        BetAmountLimitReqVo reqVo = new BetAmountLimitReqVo();
        reqVo.setTenantId(1L);
        reqVo.setUserId(10086L);
        reqVo.setSeriesType(3004);
        request.setData(reqVo);
        Response res = virtualApiService.getBetAmountLimit(request);
        System.out.println(JSONObject.toJSONString(res));
    }

    @Test
    public void ticketCancel(){
        List<Long> ticketIds = new ArrayList<>();
        ticketIds.add(32413L);
        Map<String, Object> flag = virtualService.ticketCancel(ticketIds);
        System.out.println(JSONObject.toJSONString(flag));
    }




}
