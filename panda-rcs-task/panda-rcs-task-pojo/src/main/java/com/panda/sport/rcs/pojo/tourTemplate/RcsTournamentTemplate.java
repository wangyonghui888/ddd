package com.panda.sport.rcs.pojo.tourTemplate;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.pojo.RcsOperationLog;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  联赛模板
 * @Date: 2020-05-12 16:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsTournamentTemplate implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * sportId
     */
    private Integer sportId;

    /**
     * 1：级别  2：联赛id   3：默认
     */
    private Integer type;

    /**
     * 默认使用-1,
     */
    private Long typeVal;

    /**
     * 1：早盘；0：滚球
     */
    private Integer matchType;

    /**
     * 赔率源
     */
    private String dataSourceCode;

    /**
     * 比分源1:SR  2:UOF
     */
    private Integer scoreSource;

    /**
     * 赛事模板专用（模板复制来源名称）
     */
    private String templateName;

    /**
     * 订单接矩事件源
     */
    @TableField(exist = false)
    private String orderAcceptEventCode;

    /**
     * 事件延迟时间id
     */
    @TableField(exist = false)
    private Integer templateEventId;

    /**
     * 接拒单模板id
     */
    @TableField(exist = false)
    private Integer templateAcceptEventId;

    /**
     * 赛前盘口数
     */
    @TableField(exist = false)
    private Integer preMarketCount;

    /**
     * 滚球盘口数
     */
    @TableField(exist = false)
    private Integer liveMarketCount;

    /**
     * 最小延迟时间  单位秒
     */
    @TableField(exist = false)
    private Integer acceptMinTime;

    /**
     * 最大延迟时间，单位秒
     */
    @TableField(exist = false)
    private Integer acceptMaxTime;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 赛事提前结算开关 0:关 1:开
     */
    private Integer matchPreStatus;


}
