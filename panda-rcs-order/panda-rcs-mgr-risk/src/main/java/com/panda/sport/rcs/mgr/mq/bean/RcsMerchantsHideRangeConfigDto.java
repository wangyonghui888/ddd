package com.panda.sport.rcs.mgr.mq.bean;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class RcsMerchantsHideRangeConfigDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页展示条数
     */
    private Integer pageSize;

    /**
     * 藏单状态开关  0开 1关
     */
    private Integer status;
    /**
     * 最大藏单金额
     */
    private Long hideMoney;

    /**
     * 运动种类id
     */
    private Integer sportId;

    /**
     * 商户编码
     */
    private String merchantsCode;

    /**
     * 最后编辑者
     */
    private String updateUsername;


    /**
     * id主键集合
     */
    private List<Integer> ids;

    /**
     * 商户id集合
     */
    private List<String> merchantsCodes;


}
