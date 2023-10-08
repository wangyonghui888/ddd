package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class RcsTournamentTemplateAcceptConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 模板id
     */
    private Long templateId;

    /**
     * 玩法集id
     */
    private Integer categorySetId;
    /**赛事种类*/
    @TableField(exist = false)
    private Integer sportId;

    /**赛事id*/
    @TableField(exist = false)
    private Long matchId;
    /**
     * SR  BC  BG
     */
    private String dataSource;

    /**
     * 最大等待时间
     */
    private Integer maxWaitTime;
    /**
     * 最小等待时间
     */
    private Integer minWaitTime;
    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 事件名称
     */
    private String eventName;

    public Integer getEventTypeNumber(){
        if (MatchEventConfigEnum.EVENT_SAFETY.getCode().equalsIgnoreCase(this.eventType)){
            return MatchEventConfigEnum.EVENT_SAFETY.getType();
        }
        if (MatchEventConfigEnum.EVENT_DANGER.getCode().equalsIgnoreCase(this.eventType)){
            return MatchEventConfigEnum.EVENT_DANGER.getType();
        }
        if (MatchEventConfigEnum.EVENT_CLOSING.getCode().equalsIgnoreCase(this.eventType)){
            return MatchEventConfigEnum.EVENT_CLOSING.getType();
        }
        return MatchEventConfigEnum.EVENT_REJECT.getType();
    }

}
