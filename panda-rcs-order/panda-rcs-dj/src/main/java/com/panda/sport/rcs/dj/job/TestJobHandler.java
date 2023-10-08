package com.panda.sport.rcs.dj.job;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.dj.DJAmountLimitResVo;
import com.panda.sport.data.rcs.dto.dj.DJLimitAmoutRequest;
import com.panda.sport.data.rcs.dto.dj.Selection;
import com.panda.sport.rcs.dj.dto.DjResponseV2DataDto;
import com.panda.sport.rcs.dj.service.DjServiceImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName TestJobHandler
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/21 10:58
 * @Version 1.0
 **/
@Slf4j
@JobHandler(value = "testJobHandler")
@Component
public class TestJobHandler extends IJobHandler {


    @Autowired
    private DjServiceImpl djService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
//        DJLimitAmoutRequest reqVo = new DJLimitAmoutRequest();
//        reqVo.setUserId(42213662126L);
//        reqVo.setUsername("156689tt");
//        Selection selection = new Selection();
//        selection.setMatchId(32342751911461277L);
//        selection.setOddsId(32343194517756942L);
//        reqVo.setMerchant(31433517168705439L);
//        reqVo.setSeriesType(1);
//        reqVo.setTester("0");
//        selection.setMarketId(32343194517523628L);
//        List<Selection> selectionList = Lists.newArrayList(selection);
//        reqVo.setSelectionList(selectionList);
//        DJAmountLimitResVo limitResVo = djService.getBetAmountLimit(reqVo);
//        XxlJobLogger.log("返回对象={}", JSONObject.toJSONString(limitResVo));
//        return SUCCESS;

        String data = "{\"merchant\":2,\"selectionList\":[{\"marketId\":65006878521527683,\"matchId\":65006686648517063,\"matchInfo\":\"RED.A v \u2060Atletec\",\"odds\":2.0,\"oddsId\":65006878522783359,\"playOptionsName\":\"Over>2.5\",\"sportId\":100,\"sportName\":\"英雄聯盟\"}],\"seriesType\":1,\"tester\":\"0\",\"userId\":504177492140700006,\"username\":\"111111_test666\"}";
        DJLimitAmoutRequest djLimitDto = JSONObject.parseObject(data, DJLimitAmoutRequest.class);
        Request<DJLimitAmoutRequest> request = new Request<>();
        request.setGlobalId("2504177492140700006");
        request.setData(djLimitDto);
        //DJAmountLimitResVo djAmountLimitResVo = djService.getBetAmountLimit(request.getData());
        //XxlJobLogger.log("限额V2返回对象={}", JSONObject.toJSONString(djAmountLimitResVo));
        return SUCCESS;
    }
}
