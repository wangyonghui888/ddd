package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author wiker
 * @date 2023/7/30 16:59
 * 动态漏单配置表相关数据
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RcsOmitConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    /**
     * 商户ID
     */
    private Long merchantsId;
    /**
     * 商户编码
     */
    private String merchantsCode;
    /**
     * '漏单比例'
     */
    private BigDecimal volumePercentage;
    /**
     * '金额区间始'
     */
    private Integer minMoney;
    /**
     * 金额区间终
     */
    private Integer maxMoney;
    /**
     * 标签开关
     */
    private Integer bqStatus;
    /**
     * 全局开关
     */
    private Integer qjStatus;
    /**
     * 编辑人
     */
    private String remark;
    /**
     * '创建时间'
     */
    private Timestamp crtTime;
    /**
     * 修改时间
     */
    private Timestamp updateTime;
    /**
     * 标签ID
     */
    private String levelId;

    /**
     * 默认配置源1：是 2：否
     */
    private Integer isDefaultSrc;
}


