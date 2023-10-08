package com.panda.rcs.order.reject.service;


/**
 * @author :  koala
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.order.reject.service.impl
 * @Description :  公共发送消息服务类
 * @Date: 2023-01-07 15:15
 * --------  ---------  --------------------------
 */
public interface CommonSendMsgServer {

    void sendMsg(String key,Object value);
}
