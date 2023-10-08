package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 提前结算单个玩法开关配置表
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-01-11
 */
@Data
@TableName(value = "rcs_category_pre_settlement_config")
@Builder
public class RcsCategoryPreSettlementConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 玩法ID
     */
    private Integer playId;

    /**
     * 球种ID
     */
    private Integer sportId;

    /**
     * 提前结算早盘开关
     */
    private Boolean morningTradingStatus;

    /**
     * 提前结算滚球开关
     */
    private Boolean rollingBallStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除
     */
    private String deleted;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update_time
     */
    private Date updateTime;


}
