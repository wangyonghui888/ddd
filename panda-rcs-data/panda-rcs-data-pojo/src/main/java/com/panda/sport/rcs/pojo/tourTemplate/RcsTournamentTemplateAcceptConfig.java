package com.panda.sport.rcs.pojo.tourTemplate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-09-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsTournamentTemplateAcceptConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 模板id
     */
    private Integer templateId;

    /**
     * 玩法集id
     */
    private Integer categorySetId;

    /**
     * SR  BC  BG
     */
    private String dataSource;

//    /**
//     * T常规
//     */
//    private Integer normal;


    /**
     * T延时
     */
    private Integer minWaitTime;

    /**
     * 最大延时
     */
    private Integer maxWaitTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
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
    /**
     * 等待时间
     */
//    @TableField(exist = false)
//    private Integer waitTime;

    /**
     * 赛事种类
     */
    @TableField(exist = false)
    private Integer sportId;

    /**
     * 赛事id
     */
    @TableField(exist = false)
    private Long matchId;
//    /**事件类型级别*/
//    @TableField(exist = false)
//    private Integer eventTypeNumber;

    public Integer getEventTypeNumber() {

        if (MatchEventConfigEnum.EVENT_SAFETY.getCode().equalsIgnoreCase(this.eventType)) {
            return MatchEventConfigEnum.EVENT_SAFETY.getType();
        }
        if (MatchEventConfigEnum.EVENT_DANGER.getCode().equalsIgnoreCase(this.eventType)) {
            return MatchEventConfigEnum.EVENT_DANGER.getType();
        }
        if (MatchEventConfigEnum.EVENT_CLOSING.getCode().equalsIgnoreCase(this.eventType)) {
            return MatchEventConfigEnum.EVENT_CLOSING.getType();
        }
        return MatchEventConfigEnum.EVENT_REJECT.getType();
    }

    /**
     * @return java.lang.String
     * @Description //获取格式化时间
     * @Param []
     * @Author sean
     * @Date 2020/11/13
     **/
    public String getUpdateTimeStr() {
        return DateUtils.transferLongToDateStrings(System.currentTimeMillis());
    }
}
