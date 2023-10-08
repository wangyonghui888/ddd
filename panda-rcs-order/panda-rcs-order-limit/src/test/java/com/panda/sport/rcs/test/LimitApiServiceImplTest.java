package com.panda.sport.rcs.test;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.rcs.LimitBootstrap;
import com.panda.sport.rcs.limit.controller.SingleLimitController;
import com.panda.sport.rcs.limit.vo.AvailableLimitQueryReqVo;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 限额查询接口测试类
 *
 * @Author Magic
 * @Date 14:20 2023/01/11
 **/
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = LimitBootstrap.class)
public class LimitApiServiceImplTest {

    @Autowired
    LimitApiService limitApiService;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Test
    public void getTagPercentage() {
        Request<Long> request = new Request<>();
        request.setData(4L);
        Response response = limitApiService.getTagPercentage(request);
        log.info("获取标签限额result:{}", JSONObject.toJSONString(response));

        for (int i = 0; i < 10; i++) {
            JSONObject jsonObject = JSONObject.parseObject("{\"sportId\":1,\"dataType\":4,\"matchId\":"+ 1+"}");
            producerSendMessageUtils.sendMessage("rcs_limit_cache_clear_sdk2", "test", "test", jsonObject);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 10; i++) {
            JSONObject jsonObject = JSONObject.parseObject("{\"sportId\":1,\"dataType\":4,\"matchId\":"+ 1+"}");
            producerSendMessageUtils.sendMessage("rcs_limit_cache_clear_sdk2", "test", "test", jsonObject);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 10; i++) {
            JSONObject jsonObject = JSONObject.parseObject("{\"sportId\":1,\"dataType\":4,\"matchId\":"+ 1+"}");
            producerSendMessageUtils.sendMessage("rcs_limit_cache_clear_sdk2", "test", "test", jsonObject);
        }
    }

    @Autowired
    SingleLimitController singleLimitController ;
    @Test
    public void getTagPercenta22ge() {

         String data = "{\n" +
                 "  \"userId\": \"506667693927900032\",\n" +
                 "  \"merchantCode\": \"111111\",\n" +
                 "  \"matchManageId\": \"4342488771445149698\",\n" +
                 "  \"playId\": null\n" +
                 "}";
        singleLimitController.userAvailableLimitQuery(JSONObject.parseObject(data, AvailableLimitQueryReqVo.class));
    }
}