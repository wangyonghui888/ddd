package com.panda.sport.data.rcs.dto.credit;

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
 * @Description : 信用网串关限额
 * @Author : Paca
 * @Date : 2021-04-29 19:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@TableName("rcs_credit_series_limit")
public class RcsCreditSeriesLimit implements Serializable {

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
     * 串关类型，2-2串1，3-3串N，4-4串N，5-5串N，6-6串N，7-7串N，8-8串N，9-9串N，10-10串N
     */
    @TableField(value = "series_type")
    private Integer seriesType;

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
