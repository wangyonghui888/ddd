package com.panda.rcs.order.utils;

import com.panda.rcs.order.entity.vo.ClientResponseVo;

public class ClientResponseUtils {

    /**
     * 创建推送数据
     * @param command
     * @param responseText
     * @param ack
     * @param globalId
     * @param msgId
     * @param requestData
     * @return
     */
    public static ClientResponseVo createResponseContext(int command, Object responseText, int ack, String globalId, String msgId, Object requestData){
        ClientResponseVo responseVo = new ClientResponseVo();
        responseVo.setCommand(command);
        responseVo.setResponseData(responseText);
        responseVo.setAck(ack);
        responseVo.setGlobalId(globalId);
        responseVo.setMsgId(msgId);
        responseVo.setRequestData(requestData);
        responseVo.setTime(System.currentTimeMillis());
        return responseVo;
    }

}
