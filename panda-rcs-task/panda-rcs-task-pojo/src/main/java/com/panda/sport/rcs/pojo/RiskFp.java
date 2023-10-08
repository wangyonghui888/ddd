package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RiskFp {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 指纹id
     */
    private String fingerprintId;
    /**
     * 设备类型
     */
    private String device;
    /**
     * 最后下注时间
     */
    private String maxBetTime;
    /**
     * 投注金额
     */
    private String betAmount;
    /**
     * 盈利金额
     */
    private String netAmount;
    /**
     * 盈利率
     */
    private String netAmountRate;
    /**
     * 胜率
     */
    private String winAmountRate;
    /**
     * 危险等级
     */
    private String riskLevel;
    /**
     * 关联用户数量
     */
    private Integer userCount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Long createTime;

}
