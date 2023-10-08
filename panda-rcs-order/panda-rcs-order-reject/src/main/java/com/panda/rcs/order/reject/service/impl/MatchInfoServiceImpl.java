package com.panda.rcs.order.reject.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.entity.enums.VarSwitchEnum;
import com.panda.rcs.order.reject.mapper.MatchInfoMapper;
import com.panda.rcs.order.reject.service.CommonSendMsgServer;
import com.panda.rcs.order.reject.service.MatchInfoService;
import com.panda.rcs.order.reject.utils.SendMessageUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchInfoServiceImpl implements MatchInfoService {

    private final SendMessageUtils sendMessage;
    private final CommonSendMsgServer commonSendMsgServerImpl;
    private final MatchInfoMapper matchInfoMapper;


    @Override
    public boolean getVarSwitchStatus(String matchId) {
        //系统级VAR收单开关状态
        String sysVarSwitchKey = RedisKey.RCS_SYS_TOURNAMENT_VAR_SWITCH;
        String sysVarSwitchValue = RcsLocalCacheUtils.getValueInfo(sysVarSwitchKey);
        if (StringUtils.isBlank(sysVarSwitchValue)) {
            String sysVarSwitchValueDb = matchInfoMapper.getVarSwitchStatus(1L, 131L);
            sysVarSwitchValue = "1".equalsIgnoreCase(sysVarSwitchValueDb) ? VarSwitchEnum.Open.getCode() : VarSwitchEnum.Close.getCode();
            commonSendMsgServerImpl.sendMsg(sysVarSwitchKey, sysVarSwitchValue);
        }
        log.info("::{}::获取VAR收单系统开关状态：{}", matchId, sysVarSwitchValue);
        //赛事级VAR收单开关状态
        String matchVarSwitchKey = String.format(RedisKey.RCS_VAR_SWITCH_MATCH, matchId);
        String matchVarSwitchValue = RcsLocalCacheUtils.getValueInfo(matchVarSwitchKey);

        if (StringUtils.isBlank(matchVarSwitchValue)) {
            String matchVarSwitchValueDb = matchInfoMapper.getVarSwitchStatus(3L, Long.valueOf(matchId));
            matchVarSwitchValue = "1".equalsIgnoreCase(matchVarSwitchValueDb) ? VarSwitchEnum.Open.getCode() : VarSwitchEnum.Close.getCode();
            commonSendMsgServerImpl.sendMsg(matchVarSwitchKey, matchVarSwitchValue);
        }
        log.info("::{}::获取VAR收单赛事开关状态：{}", matchId, matchVarSwitchValue);

        return VarSwitchEnum.Open.getCode().equalsIgnoreCase((String.valueOf(sysVarSwitchValue))) && VarSwitchEnum.Open.getCode().equalsIgnoreCase(matchVarSwitchValue);
    }


    @Override
    public boolean getVarSwitchStatus(String matchId, String orderNo) {
        boolean varSwitchStatus = this.getVarSwitchStatus(matchId);
        log.info("::{}::获取VAR收单开关状态：{}", orderNo, varSwitchStatus);
        return varSwitchStatus;
    }


    /**
     * VAR订单发送等待或拒单状态
     */
    @Override
    public void sendVarOrderStatus(String linkId, String matchId, String sportId, Integer varOrderStatus) {
        JSONObject json = new JSONObject()
                .fluentPut("matchId", matchId)
                .fluentPut("orderStatus", varOrderStatus);
        String topic = "RCS_VAR_ORDER_REJECT";
        String tags = matchId + "_" + sportId + "VAR_ORDER_REJECT";
        String keys = matchId + "_" + sportId;
        log.info("::{}::赛事ID:{}::发送VAR订单等待状态消息队列topic={},tags={}", linkId, matchId, topic, tags);
        //避免无法拿到当前事件，延迟一秒，待事件缓存完毕，再处理
        sendMessage.sendMessage(topic, tags, keys, json,null,1);
    }

    /**
     * 修改缓存var收单状态
     */
    @Override
    public void updateVarAccept(String matchId, String varStatus) {
        String varAcceptKey = String.format(RedisKey.RCS_ORDER_VAR_ACCEPT_STATUS, matchId);
        RcsLocalCacheUtils.timedCache.put(varAcceptKey, varStatus, 4 * 60 * 60 * 1000);
        commonSendMsgServerImpl.sendMsg(varAcceptKey, varStatus);
    }


    /**
     * 查询缓存var收单状态
     */
    @Override
    public String getVarAccept(String matchId) {
        String varAcceptKey = String.format(RedisKey.RCS_ORDER_VAR_ACCEPT_STATUS, matchId);
        return RcsLocalCacheUtils.getValueInfo(varAcceptKey);
    }
}
