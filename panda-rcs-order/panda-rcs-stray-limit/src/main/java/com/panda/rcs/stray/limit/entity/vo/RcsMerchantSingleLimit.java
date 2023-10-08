package com.panda.rcs.stray.limit.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 高风险单注赛种投注限制
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Data
public class RcsMerchantSingleLimit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 单注赔付限额类型 M串N = M*1000+N
     * 2001 2串1
     * 3001 3串1
     * 3004 3串4
     * 4001 4串1
     * 40011 4串11
     * 5001 5串1
     * 50026 5串26
     * 6001 6串1
     * 60057 6串57
     * 7001 7串1
     * 700120 7串120
     * 8001  8串1
     * 800247 8串247
     * 9001  9串1
     * 900502 9串502
     * 10001 10串1
     * 10001013 10串1013
     */
    @ApiModelProperty("单注赔付限额类型 M串N = M*1000+N")
    private Integer strayType;


    @ApiModelProperty("高风险赔率（X） 区间")
    private String highRiskConfig;

    /**
     * 是否启用 0 、启用 1、未启用
     */
    private Integer status;

    private Date createTime;

    private Date updateTime;

    /**
     * 体育种类id
     * */
    private Integer sportId;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;
}
