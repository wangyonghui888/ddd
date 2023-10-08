package com.panda.sport.rcs.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 玩法集表
 */
@Data
public class TempleteDateSource implements Serializable {

    /**
     * 运动种类id。 对应表 sport.id
     */
    private  Long sportId;
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 玩法集ID
     */
    private Long categorySetId;

    /**
     * 对应data_source.code
     */
    private String dataSourceCode;

    /**
     * 事件编码. 对应 match_event_type.event_code
     */
    private String eventCode;
}
