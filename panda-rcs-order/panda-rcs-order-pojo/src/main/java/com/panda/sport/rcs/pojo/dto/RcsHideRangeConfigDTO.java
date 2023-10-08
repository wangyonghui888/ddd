package com.panda.sport.rcs.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 藏单金额区间配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RcsHideRangeConfigDTO {


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
    private BigDecimal hideAmount;

}
