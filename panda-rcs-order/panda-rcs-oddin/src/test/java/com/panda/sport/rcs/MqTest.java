package com.panda.sport.rcs;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.sport.data.rcs.dto.oddin.BaseDto;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.oddin.entity.common.DataRealTimeMessageBean;
import com.panda.sport.rcs.oddin.util.DjMqUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static com.panda.sport.rcs.oddin.common.Constants.*;

/**
 * @author Beulah
 * @date 2023/3/20 17:29
 * @description 单元测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OddinBootstrap.class)
@Slf4j
public class MqTest {

    @Resource
    DjMqUtils djMqUtils;
    @Resource
    private ProducerSendMessageUtils messageUtils;
    @Test
    public void djMqSendMessageTest() {
        BaseDto dto = new BaseDto();
        dto.setSourceId(1);
        boolean flag = djMqUtils.sendMessage(RCS_RISK_ODDIN_TICKET_RESULT_TO_DJ, JSONObject.toJSONString(dto));
    }

    @Test
    public void tyMqSendMessageTest() {
       /* BaseDto dto = new BaseDto();
        dto.setSourceId(2);*/
        DataRealTimeMessageBean dataRealTimeMessageBean=new DataRealTimeMessageBean();
        StandardMatchMarketMessage standardMatchMarketMessage=new StandardMatchMarketMessage();
        StandardMatchMarketMessage selections=new StandardMatchMarketMessage();
        List<StandardMarketMessage> marketMessageList=new ArrayList<>();
        StandardMarketMessage standardMarketMessage=new StandardMarketMessage();
        standardMarketMessage.setExtraInfo("od:match:111111/1/1?threshold=4.5");
        standardMarketMessage.setId(99989L);
        standardMarketMessage.setMarketCategoryId(330L);
        standardMarketMessage.setMarketType(0);
        marketMessageList.add(standardMarketMessage);
        selections.setStandardMatchInfoId(34252525L);
        selections.setDataSourceCode("OD");
        selections.setMarketList(marketMessageList);
        dataRealTimeMessageBean.setData(selections);


        /*String selections="od:match:356247/1/1?threshold=4.5";
        *//*messageUtils.sendMessage(RCS_RISK_ODDIN_TICKET_RESULT_TO_DJ, JSONObject.toJSONString(dto));*/
        messageUtils.sendMessage(RCS_RISK_ODDIN_TICKET_RESULT_TO_Selecionds, dataRealTimeMessageBean);
    }
}
