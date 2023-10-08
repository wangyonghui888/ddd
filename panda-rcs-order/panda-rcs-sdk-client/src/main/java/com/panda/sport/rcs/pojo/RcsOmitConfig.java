package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author wiker
 * @date 2023/8/19 22:53
 *
 **/
@Data
public class RcsOmitConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    /**
     * 商户ID
     */
    private BigInteger merchantsId;
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
    private BigInteger minMoney;
    /**
     * 金额区间终
     */
    private BigInteger maxMoney;
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
    private Date crtTime;
    /**
     * 修改时间
     */
    private Timestamp updateTime;
    /**
     * 标签ID
     */
    @TableField(value = "level_id")
    private String levelId;
}
