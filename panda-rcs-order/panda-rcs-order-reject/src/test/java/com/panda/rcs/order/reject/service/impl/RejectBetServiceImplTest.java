package com.panda.rcs.order.reject.service.impl;

import com.panda.rcs.order.reject.OrderRejectServer;
import com.panda.rcs.order.reject.service.RejectBetService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.tournament.MatchTemplatePlayMarginDataReqVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


/**
 * @author Beulah
 * @date 2023/5/7 14:52
 * @description todo
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderRejectServer.class)
@Slf4j
public class RejectBetServiceImplTest {


    @Resource
    RejectBetServiceImpl rejectBetService;

    @Test
   public void testQueryMatchTemplatePlayMargin(){

        Request<MatchTemplatePlayMarginDataReqVo> requestParam = new Request<>();
        MatchTemplatePlayMarginDataReqVo reqVo = new MatchTemplatePlayMarginDataReqVo();
        reqVo.setSportId(2);
        reqVo.setMatchId(1234567L);
        reqVo.setMatchType(1);
        reqVo.setPlayId(157);
        requestParam.setData(reqVo);
//        rejectBetService.queryMatchTemplatePlayMargin(requestParam,"1");
    }


}