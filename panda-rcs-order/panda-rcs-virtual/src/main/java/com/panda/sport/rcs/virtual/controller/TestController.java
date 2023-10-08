package com.panda.sport.rcs.virtual.controller;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.virtual.BetAmountLimitReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetItemReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetReqVo;
import com.panda.sport.rcs.virtual.service.VirtualApiServiceImpl;
import com.panda.sport.rcs.virtual.service.VirtualServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 回调接口
 *
 * @author : lithan
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    VirtualApiServiceImpl virtualApiService;

    @Autowired
    VirtualServiceImpl virtualService;

    /**
     * 确认出售
     *
     * @return
     */
    @RequestMapping("/bet")
    public String walletSell() {

        Long stake = 100000L;
        BetReqVo reqVo = new BetReqVo();
        reqVo.setTenantId(2L);
        reqVo.setOrderNo(System.currentTimeMillis() + "");
        reqVo.setTotalStake(stake);
        reqVo.setUserId(2021020213L);
        reqVo.setIp("122.56.95.62");
        reqVo.setSeriesType(1);
        reqVo.setDeviceType(1);
        reqVo.setCurrencyCode("RMB");
        reqVo.setBetTime(System.currentTimeMillis());
        List<BetItemReqVo> list = new ArrayList<>();

        BetItemReqVo item = new BetItemReqVo();
        item.setBetNo(System.currentTimeMillis() + "");
        item.setPlayListId(Long.valueOf("24002"));
        item.setEventId(Long.valueOf(414775));
        item.setMarketId("win");
        item.setOddId("win_1");
        item.setMatchInfo("测试");
        item.setSportId(1);
        item.setStake(stake);
        item.setOddValue(new BigDecimal(644000).divide(new BigDecimal("100000.0")).toString());
        list.add(item);


        reqVo.setOrderItemList(list);

        Request<BetReqVo> request = new Request<>();
        request.setData(reqVo);

//        String str = "{\"betTime\":1611925944001,\"currencyCode\":\"1\",\"deviceType\":2,\"ip\":\"43.243.94.205\",\"ipArea\":\"中国,香港,\",\"orderItemList\":[{\"betNo\":\"32265104302080\",\"eventId\":820283,\"marketId\":\"Handicap_Home_0_5\",\"matchInfo\":\"MCI v LIV\",\"maxWinAmount\":930,\"oddId\":\"Home_Minus_0_5\",\"oddValue\":\"1.93\",\"playListId\":14002,\"playOptionsName\":\"MCI -0.5\",\"sportId\":1001,\"sportName\":\"虚拟足球\",\"stake\":1000}],\"orderNo\":\"122394687160143\",\"seriesType\":1,\"tenantId\":2,\"totalStake\":1000,\"userId\":263935889683132416,\"vipLevel\":0}";
//        BetReqVo strVo = JSONObject.parseObject(str, BetReqVo.class);
//        strVo.setOrderNo(UUID.randomUUID().toString());
//        strVo.getOrderItemList().get(0).setBetNo(UUID.randomUUID().toString());
//        request.setData(strVo);

        request.setGlobalId(UUID.randomUUID().toString());
        Response res = virtualApiService.bet(request);
        System.out.println(JSONObject.toJSONString(res));
        return "稳";
    }

    //WebConfigurer 这个类会影响
    @RequestMapping(value = "/betPost")
    @ResponseBody
    public Response betPost(@RequestBody Request<BetReqVo> request){
        Response res = virtualApiService.bet(request);
        System.out.println(JSONObject.toJSONString(res));
        return res;
    }


    @RequestMapping("/limit")
    public Response getBetAmountLimit(){
        Request<BetAmountLimitReqVo> request = new Request<>();
        BetAmountLimitReqVo reqVo = new BetAmountLimitReqVo();
        reqVo.setTenantId(10086L);
        reqVo.setUserId(202102060005L);
        reqVo.setSeriesType(1);
        request.setData(reqVo);
        Response res = virtualApiService.getBetAmountLimit(request);
        System.out.println(JSONObject.toJSONString(res));
        return res;
    }

}
