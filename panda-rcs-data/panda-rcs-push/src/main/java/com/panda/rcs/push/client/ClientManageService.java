package com.panda.rcs.push.client;

import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientRequestVo;
import com.panda.rcs.push.entity.vo.OrderBetResponseVO;
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
     * 消息发送
     * @param subscriptionEnums 发送类型
     * @param matchId 赛事Id
     * @param message 发送数据
     */
    void sendMessage(SubscriptionEnums subscriptionEnums, String matchId, Object message);

    /**
     * 消息发送
     * @param subscriptionEnums 发送类型
     * @param matchId 赛事Id
     * @param playIds  玩法集合
     * @param message 发送数据
     */
    void sendMessage(SubscriptionEnums subscriptionEnums, String matchId, List<String> playIds, Object message);

    /**
     * 及时注单推送
     * @param subscriptionEnums 发送类型
     * @param orderBetList 及时注单对象
     * @param enMessage 英文数据
     * @param zsMessage 中文数据
     */
    void sendMessage(SubscriptionEnums subscriptionEnums, List<OrderBetResponseVO> orderBetList, Object enMessage, Object zsMessage);

    /**
     * 消息推送  - 非足球货量推送
     * @param subscriptionEnums 发送类型
     * @param matchId 赛事Id
     * @param matchType 赛事类型
     * @param sportId 运动类型
     * @param playTimeStage 玩法集
     * @param message 推送数据
     */
    void sendMessage(SubscriptionEnums subscriptionEnums, String matchId, Integer matchType, Integer sportId, Integer playTimeStage, Object message);

    /**
     * 右侧消息推送
     * @param subscriptionEnums 发送类型
     * @param userIds 用户Id集合
     * @param zsMessage 中文推送数据
     * @param enMessage 英文推送数据
     */
    void sendMessage(SubscriptionEnums subscriptionEnums, List<String> userIds, Integer sportId, Integer matchType, Object zsMessage, Object enMessage);

    /**
     * 右侧消息推送
     * @param subscriptionEnums 发送类型
     * @param zsMessage 中文推送数据
     * @param enMessage 英文推送数据
     */
    void sendMessage(SubscriptionEnums subscriptionEnums, Integer sportId, Integer matchType, Object zsMessage, Object enMessage);

    /**
     * 事件流推送
     * @param subscriptionEnums 发送类型
     * @param dataSourceCode 事件源类型
     * @param matchId 赛事Id
     * @param sendMessage 推送数据
     */
    void sendMessage(SubscriptionEnums subscriptionEnums, String dataSourceCode, String matchId, Object sendMessage);
    /**
     * 定时清理死信连接
     */
    void removeClient();

    /**
     * 客户端移除
     * @param channel
     */
    void removeClient(Channel channel);

}
