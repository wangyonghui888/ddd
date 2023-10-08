package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 特殊事件切换
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecEventChangeDTO {



    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 特殊事件code
     */
    private String eventCode;
    /**
     * 特殊事件名称
     */
    private String eventName;
    /**
     * 上一次特殊事件code
     */
    private String lastEventCode;
    /**
     * 上一次特殊事件名称
     */
    private String lastEventName;

    /**
     * 当前事件主客 home:主队 away:客队
     */
    private String currHomeAway;

    /**
     * 上一次事件主客 home:主队 away:客队
     */
    private String lastHomeAway;

    /**
     * 操作類別編碼
     */
    private Integer operatePageCode;

    private SpecEventChangeDTO beforeParams;
}
