package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2020-09-04 11:19
 * @ModificationHistory Who    When    What
 * 商户单场限额
 */
@Data
public class RcsQuotaMerchantSingleFieldLimit extends RcsBaseEntity<RcsQuotaMerchantSingleFieldLimit> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 体育种类
     */
    private Integer sportId;
    /**
     * 联赛等级
     */
    private Integer templateLevel;
    /**
     * 赔付限额基础值
     */
    private Long compensationLimitBase;
    /**
     * 早盘赔付限额比例  0.0001-10
     */
    private BigDecimal earlyMorningPaymentLimitRatio;
    /**
     * 早盘赔付限额
     */
    private Long earlyMorningPaymentLimit;
    /**
     * 滚球赔付限额比例  0.0001-10
     */
    private BigDecimal liveBallPayoutLimitRatio;
    /**
     * 滚球赔付限额
     */
    private Long liveBallPayoutLimit;
    /**
     * 0 未生效  1生效
     */
    private Integer status;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;
}
