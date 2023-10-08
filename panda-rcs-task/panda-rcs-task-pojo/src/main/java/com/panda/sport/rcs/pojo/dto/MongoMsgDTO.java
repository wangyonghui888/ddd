package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

@Data
public class MongoMsgDTO {


    private Long matchWarnTimeTimeStamp;

    private String matchWarnTimeTimeDate;

    private Long matchId;

    private Long matchManageId;

    private String matchInfo;

    private Integer matchStatus;

    private String matchStatusStr;

    private Long matchBeginTimeTimeStamp;

    private String matchBeginTimeDate;

    private Long matchEndTimeTimeStamp;

    private String matchEndTimeDate;

    private String matchUnsettledOrder;

    private Integer matchUnsettledOrder2;

    private Integer matchSettleStatus;

    private String matchSettleStatusStr;

    private Long sportId;

    private String sportStr;

    private Object lang;
}