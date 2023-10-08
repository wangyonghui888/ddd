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
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :  TODO
 * @Date: 2020-09-09 13:19
 * @ModificationHistory Who    When    What
 * 用户单日限额
 */
@Data
public class RcsQuotaUserDailyQuota extends RcsBaseEntity<RcsQuotaUserDailyQuota> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 体育种类   -1总值
     */
    private Integer sportId;
    /**
     * 单日赔付基础值
     */
    private Long dayCompensationBase;
    /**
     * 单日赔付比例
     */
    private BigDecimal dayCompensationProportion;
    /**
     * 单日赔付
     */
    private BigDecimal dayCompensation;
    /**
     * 串关单日赔付比例
     */
    private BigDecimal crossDayCompensationProportion;
    /**
     * 串关单日赔付
     */
    private BigDecimal crossDayCompensation;
    /**
     * 0 未升效  1生效
     */
    private Integer status;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;
}
