package com.panda.rcs.order.client;

import com.panda.rcs.order.entity.enums.SubscriptionEnums;
import com.panda.rcs.order.entity.vo.ClientRequestVo;
import com.panda.rcs.order.entity.vo.OrderBetResponseVO;
import io.netty.channel.Channel;

import java.util.List;

public interface ClientManageService {

    /**
     * 客户端信息保存
     * @param channel 连接客户端
     * @param clientRequest 客户端订阅信息
     */
    void putClient(Channel channel, ClientRequestVo clientRequest);

    /**
     * 及时注单推送
     * @param subscriptionEnums 发送类型
     * @param orderBetList 及时注单对象
     * @param enMessage 英文数据
     * @param zsMessage 中文数据
     */
    void sendMessage(SubscriptionEnums subscriptionEnums, List<OrderBetResponseVO> orderBetList, Object enMessage, Object zsMessage);

    /**
     * 客户端移除
     * @param channel
     */
    void removeClient(Channel channel);
}
