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
 * @Date: 2020-09-04 11:40
 * @ModificationHistory Who    When    What
 * 用户单场限额
 */
@Data
public class RcsQuotaUserSingleSiteQuota extends RcsBaseEntity<RcsQuotaUserSingleSiteQuota> {
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
     * 用户单场限额基础值
     */
    private Long userSingleSiteQuotaBase;
    /**
     * 早盘用户单场限额比例  0.0001-10
     */
    private BigDecimal earlyUserSingleSiteQuotaProportion;
    /**
     * 早盘用户单场限额
     */
    private BigDecimal earlyUserSingleSiteQuota;
    /**
     * 滚球用户单场限额比例  0.0001-10
     */
    private BigDecimal liveUserSingleSiteQuotaProportion;
    /**
     * 滚球用户单场限额
     */
    private BigDecimal liveUserSingleSiteQuota;
    /**
     * 0 未生效 1生效
     */
    private Integer status;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;


}
