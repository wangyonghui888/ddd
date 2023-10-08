package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 延迟信息类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelayCacheInfoDTO extends RcsBaseEntity<DelayCacheInfoDTO> {

    /**
     *  链路id
     **/
    private String LinkId;
    /**
     *  链路id
     **/
    private String topic;
    /**
     *  saveTime
     **/
    private Long saveTime;
    /**
     *  是否已经发送
     **/
    private Boolean isSend;

}
