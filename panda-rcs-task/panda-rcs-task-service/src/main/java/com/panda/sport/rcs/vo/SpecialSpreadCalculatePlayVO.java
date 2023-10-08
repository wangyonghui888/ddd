package com.panda.sport.rcs.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.panda.sport.rcs.log.annotion.format.LogFormatAnnotion;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 盘口设置表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@LogFormatAnnotion
public class SpecialSpreadCalculatePlayVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 玩法id
     */
    @LogFormatAnnotion(name = "玩法 ID" )
    private Long playId;
    /**
     * 是否特殊抽水1:是 0:否
     */
    @TableField(exist = false)
    private Integer isSpecialPumping;
    /**
     * 特殊抽水赔率区间
     */
    @TableField(exist = false)
    private String specialOddsInterval;
}
