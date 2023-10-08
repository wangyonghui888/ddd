package com.panda.sport.data.rcs.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 投注单详细信息表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InitOrderBean implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 运动种类编号
     */
    private Integer sportId;

    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    private Integer matchType;

    /**
     * 商户id
     */
    private Long tenantId;
}
