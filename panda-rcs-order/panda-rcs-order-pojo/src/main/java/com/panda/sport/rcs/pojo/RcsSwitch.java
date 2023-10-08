package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author wiker
 * @date 2023/7/30 16:59
 * 动态漏单配置表相关数据
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RcsSwitch implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 开关编码, 如LOUDAN
     */
    private String switchCode;
    /**
     * 编辑备注
     */
    private String remark;
    /**
     * '创建时间'
     */
    private Date crtTime;
    /**
     * 修改时间
     */
    private Timestamp updateTime;

    /**
     * 状态 1：开 2：关
     */
    private Integer switchStatus;

}


