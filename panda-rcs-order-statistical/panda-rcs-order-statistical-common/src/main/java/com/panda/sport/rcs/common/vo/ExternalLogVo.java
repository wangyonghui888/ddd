package com.panda.sport.rcs.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改外部备注vo
 *
 * @Date: 2022-3-27 16:32:08
 */
@Data
public class ExternalLogVo implements Serializable {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 备注
     */
    private String remark;


    /**
     * 操作人名称
     */
    private String changeManner;
    /**
     * 操作人id
     */
    private String changeMannerId;



}
