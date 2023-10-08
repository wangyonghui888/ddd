package com.panda.sport.rcs.pojo.credit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网玩法单注限额
 * @Author : Paca
 * @Date : 2021-07-16 23:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@TableName("rcs_credit_single_play_bet_limit")
public class RcsCreditSinglePlayBetLimit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0-模板配置，其它-商户ID
     */
    @TableField(value = "merchant_id")
    private Long merchantId;

    /**
     * 0-模板配置，其它-信用代理ID
     */
    @TableField(value = "credit_id")
    private String creditId;

    /**
     * 用户Id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 赛种，1-足球，2-篮球，5-网球，-1-其它赛种
     */
    @TableField(value = "sport_id")
    private Integer sportId;

    /**
     * 玩法分类
     */
    @TableField(value = "play_classify")
    private Integer playClassify;

    /**
     * 投注阶段，pre-早盘，live-滚球
     */
    @TableField(value = "bet_stage")
    private String betStage;

    /**
     * 联赛等级，1-一级联赛，2-二级联赛，3-三级联赛，-1-其它联赛
     */
    @TableField(value = "tournament_level")
    private Integer tournamentLevel;

    /**
     * 限额值，单位元
     */
    @TableField(value = "value")
    private BigDecimal value;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}
