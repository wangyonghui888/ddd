package com.panda.sport.rcs.third.betlisten.gts;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.gts.GtsBetAssessmentResVo;
import com.panda.sport.rcs.third.enums.OrderStatusEnum;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.realtime.ConnectionState;
import io.ably.lib.realtime.ConnectionStateListener;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.ClientOptions;
import io.ably.lib.types.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.panda.sport.rcs.third.common.Constants.*;

@Slf4j
@Component
public class AblyBetListen {

    private static final String ABLY_KEY = "xNEKjA.z_qG0g:Iz_yzkoSchvz4qG-0uKzcdXbivB86fMsL_eacK5b__A";
    private static final String ENVIRONMENT = "geniussports";
    private static final String[] fallbackHosts =
            {"geniussports-a-fallback.ably-realtime.com",
                    "geniussports-b-fallback.ably-realtime.com",
                    "geniussports-c-fallback.ably-realtime.com",
                    "geniussports-d-fallback.ably-realtime.com",
                    "geniussports-e-fallback.ably-realtime.com"};

    @Resource
    RedisClient redisClient;
    @Resource
    private IOrderHandlerService orderHandlerService;

    @Bean
    public void AblyListGtsBet() {
        ClientOptions options = new ClientOptions();
        options.key = ABLY_KEY;
        options.environment = ENVIRONMENT;
        options.fallbackHosts = fallbackHosts;
        AblyRealtime ably = null;
        try {
            ably = new AblyRealtime(options);
        } catch (AblyException e) {
            log.error("====创建AblyRealtime失败=====", e);
        }

        ably.connection.on(ConnectionState.connected, new ConnectionStateListener() {
            @Override
            public void onConnectionStateChanged(ConnectionStateChange state) {
                log.info("New state is " + state.current.name());
                switch (state.current) {
                    case connected: {
                        // Successful connection
                        log.info("成功链接到gts的Ably!");
                        break;
                    }
                    case failed: {
                        // Failed connection
                        break;
                    }
                }
            }
        });

        /**
         * 监听channel 信息
         */
        List<Channel> channelList = new ArrayList<>();
        channelList.add(ably.channels.get("onyxcrown:0"));
        channelList.add(ably.channels.get("onyxcrown:1"));
        channelList.add(ably.channels.get("onyxcrown:2"));
        channelList.add(ably.channels.get("onyxcrown:3"));
        channelList.add(ably.channels.get("onyxcrown:4"));
        for (Channel ch : channelList) {
            try {
                ch.subscribe(new Channel.MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        log.info("Ably::接收到gts ably 及时消息:{}", JSONObject.toJSONString(message.data));
                        if (Objects.isNull(message.data)) {
                            return;
                        }
                        GtsBetAssessmentResVo ablyMessage = JSONObject.parseObject((String) message.data, GtsBetAssessmentResVo.class);
                        String orderNo = ablyMessage.getBetId();
                        String isNotAllowedBetKey = String.format(GTS_IS_NOT_ALLOWED_ORDER, orderNo);
                        String orderExt = redisClient.get(isNotAllowedBetKey);
                        log.info("Ably::获取注单时返回isBetAllowed为false时缓存的订单信息:{}",JSONObject.toJSONString(orderExt));
                        ThirdOrderExt ext = JSONObject.parseObject(orderExt, ThirdOrderExt.class);
                        if (Objects.isNull(ext)) {
                            log.info("Ably::获取注单时返回isBetAllowed为false时缓存的订单信息为空,不需要继续执行下面代码");
                            return;
                        }
                        String third = ext.getThird();
                        log.info("Ably::{}::投注Ably推送消息拒单-数据商{}拒单", orderNo, third);
                        int infoStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
                        ext.setOrderStatus(OrderStatusEnum.REJECTED.getCode());
                        int mtsIsCache = orderHandlerService.getMtsIsCache(third, 3);
                        orderHandlerService.updateOrder(ext, infoStatus, third + "通过Ably拒单:" + ablyMessage.getRejectReason().getReasonMessage(), mtsIsCache);
                        ExtendBean detail = ext.getList().get(0);
                        String optionId = detail.getSelectId();
                        String oddFinally = detail.getOdds();
                        int acceptOdds = ext.getAcceptOdds();
                        String thirdOrderCache = String.format(THIRD_ORDER_CACHE, third, optionId, oddFinally, acceptOdds);
                        redisClient.delete(thirdOrderCache);
                        log.info("Ably::{}::Ably返回拒单消息-{}订单接拒状态删除缓存完成,key={}", orderNo, third, thirdOrderCache);
                        String albyRejectOrder = String.format(GTS_IS_NOT_ALLOWED_ORDER_REJECT, orderNo);
                        redisClient.setExpiry(albyRejectOrder, "1", 30L);
                        log.info("Ably::{}::Ably返回拒单消息进行拒单完成后-{}订单状态记录成一句单,key={}", orderNo, third, thirdOrderCache);
                    }
                });
            } catch (AblyException e) {
                log.info("Ably::====监听gts ably 消息异常====", e);
            }
        }
    }
}
