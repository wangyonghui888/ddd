package com.panda.rcs.push.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientCache;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.constant.BaseConstant;
import com.panda.rcs.push.entity.enums.LanguageEnums;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientRequestVo;
import com.panda.rcs.push.entity.vo.OrderBetResponseVO;
import com.panda.rcs.push.entity.vo.SingleSubInfoVo;
import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientManageServiceImpl implements ClientManageService {

    /**
     * 客户端信息保存
     *
     * @param channel
     * @param clientRequest
     */
    @Override
    public void putClient(Channel channel, ClientRequestVo clientRequest) {
        if (ClientCache.clientGroupMap.containsKey(channel)) {
            if (clientRequest.getSubscribe() != null && clientRequest.getSubscribe().size() == 1 && clientRequest.getSubscribe().containsKey(SubscriptionEnums.PLAY_CONFIGURATION.getKey())) {
                ClientRequestVo currClientRequest = ClientCache.clientGroupMap.get(channel);
                clientRequest.getSubscribe().putAll(currClientRequest.getSubscribe());
            }
        }

        if (clientRequest.getNeedCommands() != null && clientRequest.getNeedCommands().length > 0 && clientRequest.getCurrentMatchIds() != null && clientRequest.getCurrentMatchIds().length > 0) {
            if (clientRequest.getSubscribe() == null) {
                Map<Integer, List<SingleSubInfoVo>> subscribe = new HashMap<>();
                for (Integer subCmd : clientRequest.getNeedCommands()) {
                    List<SingleSubInfoVo> singleSubInfoVos = new ArrayList<>();
                    SingleSubInfoVo singleSubInfoVo = new SingleSubInfoVo();
                    singleSubInfoVo.setMatchId(Long.parseLong(Integer.toString(clientRequest.getCurrentMatchIds()[0])));
                    singleSubInfoVo.setMarketCategoryIds(clientRequest.getMarketCategoryIds());
                    singleSubInfoVos.add(singleSubInfoVo);
                    subscribe.put(SubscriptionEnums.getSubscriptionEnums(subCmd).getKey(), singleSubInfoVos);
                }

                clientRequest.setSubscribe(subscribe);
            }
        }

        ClientCache.clientGroupMap.put(channel, clientRequest);
        log.error("::Socket客户端订阅成功，当前连接客户端总数->{}，连接信息->{}", ClientCache.clientGroupMap.size(), channel);
    }

    /**
     * 赛事消息发送
     *
     * @param subscriptionEnums 发送类型
     * @param matchId           赛事Id
     * @param message           发送数据
     */
    @Override
    public void sendMessage(SubscriptionEnums subscriptionEnums, String matchId, Object message) {
        if (ClientCache.clientGroupMap.size() > 0) {
            Map<Channel, ClientRequestVo> validClientMap = ClientCache.clientGroupMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(subscriptionEnums.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (validClientMap.size() > 0) {
                DefaultChannelGroup pushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                for (ConcurrentHashMap.Entry<Channel, ClientRequestVo> entry : validClientMap.entrySet()) {
                    ClientRequestVo clientRequest = entry.getValue();
                    if (null != clientRequest.getSubscribe() && clientRequest.getSubscribe().size() > 0) {
                        List<SingleSubInfoVo> singleSubInfoLists = clientRequest.getSubscribe().get(subscriptionEnums.getKey());
                        if (null != singleSubInfoLists && singleSubInfoLists.size() > 0 && StringUtils.isNotBlank(matchId)) {
                            List<SingleSubInfoVo> _singleSubInfoLists = singleSubInfoLists.stream().filter(m -> m.getMatchId() != null && matchId.equals(Long.toString(m.getMatchId()))).collect(Collectors.toList());
                            if (_singleSubInfoLists.size() > 0) {
                                pushClientGroup.add(entry.getKey());
                            }
                        }
                    } else {
                        if (null != clientRequest.getCurrentMatchIds() && clientRequest.getCurrentMatchIds().length > 0) {
                            if (Arrays.asList(clientRequest.getCurrentMatchIds()).contains(Integer.parseInt(matchId))) {
                                pushClientGroup.add(entry.getKey());
                            }
                        }
                    }
                }

                if (!pushClientGroup.isEmpty()) {
                    commSendMessage(pushClientGroup, message);
                }
            }
        }
    }

    /**
     * 赛事 + 玩法 消息发送
     *
     * @param subscriptionEnums 发送类型
     * @param matchId           赛事Id
     * @param playIds           玩法集合
     * @param message           发送数据
     */
    @Override
    public void sendMessage(SubscriptionEnums subscriptionEnums, String matchId, List<String> playIds, Object message) {
        if (ClientCache.clientGroupMap.size() > 0) {
            Map<Channel, ClientRequestVo> validClientMap = ClientCache.clientGroupMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(subscriptionEnums.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (validClientMap.size() > 0) {
                DefaultChannelGroup pushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                for (ConcurrentHashMap.Entry<Channel, ClientRequestVo> entry : validClientMap.entrySet()) {
                    try {
                        ClientRequestVo clientRequest = entry.getValue();
                        if (null != clientRequest.getSubscribe() && clientRequest.getSubscribe().size() > 0) {
                            List<SingleSubInfoVo> singleSubInfoLists = clientRequest.getSubscribe().get(subscriptionEnums.getKey());
                            if (CollectionUtils.isNotEmpty(singleSubInfoLists)) {
                                List<SingleSubInfoVo> _singleSubInfoLists = singleSubInfoLists.stream().filter(m -> matchId.equals(Long.toString(m.getMatchId()))).collect(Collectors.toList());
                                if (_singleSubInfoLists.size() > 0) {
                                    if (_singleSubInfoLists.get(0).getMarketCategoryIds() != null && _singleSubInfoLists.get(0).getMarketCategoryIds().length > 0) {
                                        List<Integer> subPlayIds = Arrays.asList(_singleSubInfoLists.get(0).getMarketCategoryIds());
                                        List<String> _subPlayIds = new ArrayList<>();
                                        if (subPlayIds.size() > 0) {
                                            subPlayIds.forEach(sp -> {
                                                if (null != sp) {
                                                    _subPlayIds.add(Integer.toString(sp));
                                                }
                                            });
                                        }

                                        List<String> containsPlayIds = _subPlayIds.stream().filter(playIds::contains).collect(Collectors.toList());
                                        if (containsPlayIds.size() > 0) {
                                            pushClientGroup.add(entry.getKey());
                                        }
                                    } else {
                                        pushClientGroup.add(entry.getKey());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("::异常信息-推送指令-{}->{}", subscriptionEnums.getKey(), e);
                    }
                }
                if (!pushClientGroup.isEmpty()) {
                    commSendMessage(pushClientGroup, message);
                }else{
                    log.info("::{}::赛事ws推送-推送指令未订阅-{}",matchId, subscriptionEnums.getKey());
                }
            }else{
                log.info("::{}::赛事ws推送-推送指令未订阅-{}",matchId, subscriptionEnums.getKey());
            }
        }else{
            log.info("::{}::赛事ws推送-推送指令未订阅-{}",matchId, subscriptionEnums.getKey());
        }
    }

    /**
     * 及时注单推送
     *
     * @param subscriptionEnums 发送类型
     * @param orderBetList      及时注单对象
     * @param enMessage         英文数据
     * @param zsMessage         中文数据
     */
    @Override
    public void sendMessage(SubscriptionEnums subscriptionEnums, List<OrderBetResponseVO> orderBetList, Object enMessage, Object zsMessage) {
        if (ClientCache.clientGroupMap.size() > 0) {
            Map<Channel, ClientRequestVo> validClientMap = ClientCache.clientGroupMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(subscriptionEnums.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (validClientMap.size() > 0) {
                List<String> matchIds = new ArrayList<>();
                List<Integer> tournamentIds = new ArrayList<>();
                List<Integer> playIds = new ArrayList<>();
                List<Integer> playSetIds = new ArrayList<>();
                orderBetList.forEach(o -> {
                    matchIds.add(o.getMatchId());
                    if(o.getTournamentId() != null && o.getTournamentId() != 0){
                        tournamentIds.add(o.getTournamentId());
                    }
                    playIds.add(o.getPlayId());
                    playSetIds.add(o.getPlaySetId() != null ? o.getPlaySetId().intValue() : 0);
                });

                //英文订阅客户端组管理
                DefaultChannelGroup enPushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                //中文订阅客户端组管理
                DefaultChannelGroup zsPushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

                List<Channel> enChannelIds = new ArrayList<>();
                List<Channel> zsChannelIds = new ArrayList<>();

                for (ConcurrentHashMap.Entry<Channel, ClientRequestVo> entry : validClientMap.entrySet()) {
                    try {
                        if (null == entry.getValue().getSubscribe()) {
                            continue;
                        }

                        List<SingleSubInfoVo> singleSubInfoLists = entry.getValue().getSubscribe().get(subscriptionEnums.getKey());
                        if (CollectionUtils.isNotEmpty(singleSubInfoLists)) {
                            SingleSubInfoVo singleSubInfo = singleSubInfoLists.get(0);

                            //额度过滤
                            if (null != singleSubInfo.getBetAmount() && Integer.parseInt(orderBetList.get(0).getBetAmount()) < singleSubInfo.getBetAmount()) {
                            //if (null != singleSubInfo.getBetAmount() && Integer.parseInt(orderBetList.get(0).getProductAmountTotal()) < singleSubInfo.getBetAmount()) {
                                continue;
                            }

                            //球种过滤
                            if (!BaseConstant.NULL_STRING.equals(singleSubInfo.getSportId()) && null != singleSubInfo.getSportId()) {
                                List<String> sports = Arrays.asList(singleSubInfo.getSportId().split(BaseConstant.COMMA));
                                boolean hasNotSport = true;
                                for (OrderBetResponseVO vo : orderBetList) {
                                    if (sports.contains(vo.getSportId().toString())) {
                                        hasNotSport = false;
                                        break;
                                    }
                                }
                                if (hasNotSport)
                                    continue;
                            }

                            //滚球早盘类型过滤
                            if (null != singleSubInfo.getMatchType() && !singleSubInfo.getMatchType().equals(orderBetList.get(0).getMatchType())) {
                                continue;
                            }

                            //订单类型 单/串关过滤
                            if (null != singleSubInfo.getPassType()) {
                                Integer i;
                                //单、串解析
                                i = orderBetList.get(0).getSeriesType() == 1 ? 1 : 2;
                                if (!singleSubInfo.getPassType().equals(i)) {
                                    continue;
                                }
                            }

                            //用户标签过滤
                            if (0 != orderBetList.get(0).getUserTagLevel() && singleSubInfo.getUserLevels().length > 0) {
                                boolean hasNotUserTagLevel = true;
                                List<Integer> list = Arrays.asList(singleSubInfo.getUserLevels());
                                for (OrderBetResponseVO orderBetResponseVO : orderBetList) {
                                    if (list.contains(orderBetResponseVO.getUserTagLevel())) {
                                        hasNotUserTagLevel = false;
                                        break;
                                    }
                                }
                                if (hasNotUserTagLevel) {
                                    continue;
                                }
                            }
                            Integer deviceType = 0;
                            //设备类型过滤
                            if(orderBetList.get(0).getDeviceType() != null){
                                deviceType = (orderBetList.get(0).getDeviceType() == 3 || orderBetList.get(0).getDeviceType() == 4) ? 3 : orderBetList.get(0).getDeviceType();
                            }

                            deviceType = (deviceType == 1 || deviceType == 2 || deviceType == 3) ? deviceType : 4;
                            if(singleSubInfo.getDeviceType() != null && !Arrays.asList(singleSubInfo.getDeviceType()).contains(deviceType)){
                                continue;
                            }

                            //预约投注过滤
                            Integer pendingOrder = orderBetList.get(0).getIsPendingOrder() != null && orderBetList.get(0).getIsPendingOrder() == 1 ? 1 : 0;
                            if(singleSubInfo.getPendingOrderMark() != null && !singleSubInfo.getPendingOrderMark().equals(pendingOrder)){
                                continue;
                            }

                            //玩法过滤
                            if (singleSubInfo.getPlayIds().length > 0 && !Arrays.asList(singleSubInfo.getPlayIds()).containsAll(playIds)) {
                                continue;
                            }

                            //玩法集过滤
                            if (singleSubInfo.getRiskPlayIds() != null && singleSubInfo.getRiskPlayIds().length > 0 && !Arrays.asList(singleSubInfo.getRiskPlayIds()).containsAll(playSetIds)) {
                                continue;
                            }

                            //联赛过滤
                            if (tournamentIds.size() > 0 && singleSubInfo.getTournamentIds().length > 0) {
                                boolean hasNotTournament = true;
                                List<Integer> list = Arrays.asList(singleSubInfo.getTournamentIds());
                                for (Integer tournamentId : tournamentIds) {
                                    if (list.contains(tournamentId)) {
                                        hasNotTournament = false;
                                        break;
                                    }
                                }
                                if (hasNotTournament) {
                                    continue;
                                }
                            }

                            //多赛事维度
                            if (singleSubInfo.getMatchIds().length > 0){
                                boolean hasNotMatchid = true;
                                List<String> list = Arrays.asList(singleSubInfo.getMatchIds());
                                for (String matchId : matchIds) {
                                    if(list.contains(matchId)){
                                        hasNotMatchid = false;
                                        break;
                                    }
                                }

                                if(hasNotMatchid){
                                    continue;
                                }
                            }

                            //单赛事
                            if (null != singleSubInfo.getMatchId() && !matchIds.contains(Long.toString(singleSubInfo.getMatchId()))) {
                                continue;
                            }

                            //国际化过滤
                            if (singleSubInfo.getLanguageType().equals(LanguageEnums.LANGUAGE_ENGLISH.getKey())) {
                                enPushClientGroup.add(entry.getKey());
                                enChannelIds.add(entry.getKey());
                            } else {
                                zsPushClientGroup.add(entry.getKey());
                                zsChannelIds.add(entry.getKey());
                            }
                        }
                    } catch (Exception e) {
                        log.error("::异常信息-推送指令-{}->{}", subscriptionEnums.getKey(), e);
                    }
                }

                if (!enPushClientGroup.isEmpty()) {
                    log.info("::::实时注单-订单编号(中文Push)={},推送客户端->{}", orderBetList.get(0).getOrderNo(), enChannelIds);
                    commSendMessage(enPushClientGroup, enMessage);
                }

                if (!zsPushClientGroup.isEmpty()) {
                    log.info("::::实时注单-订单编号(英文Push)={},推送客户端->{}", orderBetList.get(0).getOrderNo(), zsChannelIds);
                    commSendMessage(zsPushClientGroup, zsMessage);
                }
            }
        }
    }

    @Override
    public void sendMessage(SubscriptionEnums subscriptionEnums, String matchId, Integer matchType, Integer sportId, Integer playTimeStage, Object message) {
        if (ClientCache.clientGroupMap.size() > 0) {
            Map<Channel, ClientRequestVo> validClientMap = ClientCache.clientGroupMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(subscriptionEnums.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (validClientMap.size() > 0) {
                DefaultChannelGroup pushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                for (ConcurrentHashMap.Entry<Channel, ClientRequestVo> entry : validClientMap.entrySet()) {
                    List<SingleSubInfoVo> singleSubInfoLists = entry.getValue().getSubscribe().get(subscriptionEnums.getKey());
                    if (CollectionUtils.isNotEmpty(singleSubInfoLists)) {
                        SingleSubInfoVo singleSubInfoVo = singleSubInfoLists.get(0);
                        if (matchId.equals(Long.toString(singleSubInfoVo.getMatchId()))) {
                            if (null != playTimeStage) {
                                if (Arrays.asList(singleSubInfoVo.getPlayTimeStages()).contains(playTimeStage)) {
                                    pushClientGroup.add(entry.getKey());
                                }
                            } else {
                                pushClientGroup.add(entry.getKey());
                            }
                        }
                    }
                }

                if (!pushClientGroup.isEmpty()) {
                    commSendMessage(pushClientGroup, message);
                }
            }
        }
    }

    @Override
    public void sendMessage(SubscriptionEnums subscriptionEnums, List<String> userIds, Integer sportId, Integer matchType, Object zsMessage, Object enMessage) {
        if (ClientCache.clientGroupMap.size() > 0) {
            Map<Channel, ClientRequestVo> validClientMap = ClientCache.clientGroupMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(subscriptionEnums.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (validClientMap.size() > 0) {
                //英文订阅客户端组管理
                DefaultChannelGroup enPushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                //中文订阅客户端组管理
                DefaultChannelGroup zsPushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

                for (ConcurrentHashMap.Entry<Channel, ClientRequestVo> entry : validClientMap.entrySet()) {
                    List<SingleSubInfoVo> singleSubInfoLists = entry.getValue().getSubscribe().get(subscriptionEnums.getKey());
                    if (CollectionUtils.isNotEmpty(singleSubInfoLists)) {
                        SingleSubInfoVo singleSubInfoVo = singleSubInfoLists.get(0);
                        if (!sportId.equals(Integer.parseInt(singleSubInfoVo.getSportId()))) {
                            continue;
                        }

                        if (userIds.contains(singleSubInfoVo.getUserId())) {
                            //国际化过滤
                            if (singleSubInfoVo.getLanguageType().equals(LanguageEnums.LANGUAGE_ENGLISH.getKey())) {
                                enPushClientGroup.add(entry.getKey());
                            } else {
                                zsPushClientGroup.add(entry.getKey());
                            }
                        }
                    }
                }

                if (!enPushClientGroup.isEmpty()) {
                    commSendMessage(enPushClientGroup, enMessage);
                }

                if (!zsPushClientGroup.isEmpty()) {
                    commSendMessage(zsPushClientGroup, zsMessage);
                }
            }
        }
    }

    @Override
    public void sendMessage(SubscriptionEnums subscriptionEnums, Integer sportId, Integer matchType, Object zsMessage, Object enMessage) {
        if (ClientCache.clientGroupMap.size() > 0) {
            Map<Channel, ClientRequestVo> validClientMap = ClientCache.clientGroupMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(subscriptionEnums.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (validClientMap.size() > 0) {

                //中文订阅客户端组管理
                DefaultChannelGroup zsPushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

                for (ConcurrentHashMap.Entry<Channel, ClientRequestVo> entry : validClientMap.entrySet()) {
                    List<SingleSubInfoVo> singleSubInfoLists = entry.getValue().getSubscribe().get(subscriptionEnums.getKey());
                    if (CollectionUtils.isNotEmpty(singleSubInfoLists)) {
                        SingleSubInfoVo singleSubInfoVo = singleSubInfoLists.get(0);
                        if (!sportId.equals(Integer.parseInt(singleSubInfoVo.getSportId()))) {
                            continue;
                        }
                        zsPushClientGroup.add(entry.getKey());
                    }
                }

                if (!zsPushClientGroup.isEmpty()) {
                    commSendMessage(zsPushClientGroup, zsMessage);
                }
            }
        }
    }

    @Override
    public void sendMessage(SubscriptionEnums subscriptionEnums, String dataSourceCode, String matchId, Object sendMessage) {
        if (ClientCache.clientGroupMap.size() > 0) {
            Map<Channel, ClientRequestVo> validClientMap = ClientCache.clientGroupMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(subscriptionEnums.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (validClientMap.size() > 0) {
                DefaultChannelGroup pushClientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                for (ConcurrentHashMap.Entry<Channel, ClientRequestVo> entry : validClientMap.entrySet()) {
                    ClientRequestVo clientRequest = entry.getValue();
                    if (null != clientRequest.getSubscribe() && clientRequest.getSubscribe().size() > 0) {
                        List<SingleSubInfoVo> singleSubInfoLists = clientRequest.getSubscribe().get(subscriptionEnums.getKey());
                        if (null != singleSubInfoLists && singleSubInfoLists.size() > 0) {
                            SingleSubInfoVo subInfoVo = singleSubInfoLists.get(0);
                            if (matchId.equals(Long.toString(subInfoVo.getMatchId())) && dataSourceCode.equals(subInfoVo.getDataSourceCode())) {
                                pushClientGroup.add(entry.getKey());
                            }
                        }
                    }
                }

                if (!pushClientGroup.isEmpty()) {
                    commSendMessage(pushClientGroup, sendMessage);
                }
            }
        }
    }

    /**
     * 公共发送方法
     *
     * @param channelGroup
     * @param message
     */
    private static void commSendMessage(DefaultChannelGroup channelGroup, Object message) {
        if (null == channelGroup || channelGroup.isEmpty()) {
            return;
        }

        DefaultChannelGroup pushChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelGroup.forEach(ch -> {
            if(null != ch && ch.isActive()){
                pushChannelGroup.add(ch);
            }
        });

        if(pushChannelGroup.isEmpty()){
            return;
        }

        pushChannelGroup.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(message))).addListener(cf -> {
            if (!cf.isSuccess()) {
                log.error("推送数据失败(PushError) ==> 错误信息:", cf.cause());
            }
        });
    }

    /**
     * 定时清理死信连接
     */
    @Override
    public void removeClient() {
        if (ClientCache.clientGroupMap.size() > 0) {
            ClientCache.clientGroupMap.forEach((k, v) -> {

            });
        }
    }

    /**
     * 客户端移除
     *
     * @param channel
     */
    @Override
    public void removeClient(Channel channel) {
        if (ClientCache.clientGroupMap.containsKey(channel)) {
            ClientCache.clientGroupMap.remove(channel);
        }
    }

}
