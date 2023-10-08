package com.panda.rcs.order.reject.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.service.CommonSendMsgServer;
import com.panda.rcs.order.reject.utils.SendMessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.order.reject.service.impl
 * @Description :  公共发送消息服务类
 * @Date: 2023-01-07 15:15
 * --------  ---------  --------------------------
 */
@Service
@RequiredArgsConstructor
public class CommonSendMsgServerImpl implements CommonSendMsgServer {
    private final SendMessageUtils sendMessage;

    @Override
    public void sendMsg(String key, Object value) {
        JSONObject json = new JSONObject();
        json.put(RedisKey.KEY, key);
        json.put(RedisKey.VALUE, value);
        sendMessage.sendMessage(RedisKey.RCS_ORDER_REJECT_CACHE_UPDATE, "", key, json);
    }
}
