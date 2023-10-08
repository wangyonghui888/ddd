package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description  :  藏单区间配置表
 * @author       :  Pumolo
 * @Date:  2023-04-22
*/
@Data
@TableName(value = "rcs_hide_range_config")
public class RcsHideRangeConfig {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 运动种类id
     */
    private Integer sportId;

    /**
     * 藏单状态开关 0关 1开
     */
    private Integer hideStatus;
    /**
     * 藏单阈值
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal hideAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}