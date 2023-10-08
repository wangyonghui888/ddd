package com.panda.sport.rcs.oddin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author :wiker
 * @Date: 2023-21 13:25
 * 下单渠道目前只有互联网pc和4手机端H5
 **/
@Getter
@AllArgsConstructor
public enum TicketChannel {
    TICKET_CHANNEL_UNSPECIFIED(0, "未指定"),
    TICKET_CHANNEL_INTERNET(1, "互联网"),
    TICKET_CHANNEL_RETAIL(2, "零售"),
    TICKET_CHANNEL_TERMINAL(3, "终端"),
    TICKET_CHANNEL_MOBILE(4, "移动的"),
    TICKET_CHANNEL_PHONE(5, "电话"),
    TICKET_CHANNEL_SMS(6, "短信"),
    TICKET_CHANNEL_CALL_CENTRE(7, "呼叫中心"),
    TICKET_CHANNEL_TV_APP(8, "电视APP"),
    TICKET_CHANNEL_AGENT(9, "代理人");


    private Integer code;
    private String message;
}
