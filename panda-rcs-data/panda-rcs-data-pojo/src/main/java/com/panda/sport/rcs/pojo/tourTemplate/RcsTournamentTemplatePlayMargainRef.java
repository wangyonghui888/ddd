package com.panda.sport.rcs.pojo.tourTemplate;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  联赛模板玩法与margain之间引用
 * @Date: 2020-05-12 16:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsTournamentTemplatePlayMargainRef implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * margain表id
     */
    private Long margainId;
    /**
     * 类型
     */
    private Integer timeType;
    /**
     * 值
     */
    private Long timeVal;
    /**
     * 时间分钟长度
     */
    private Long minuteTimeVal;
    /**
     * margain值
     */
    private String margain;
    /**
     * 创建时间
     */

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
