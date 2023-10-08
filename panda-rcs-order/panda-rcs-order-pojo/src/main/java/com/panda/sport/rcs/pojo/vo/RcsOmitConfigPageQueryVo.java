package com.panda.sport.rcs.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class RcsOmitConfigPageQueryVo extends PageQuery{

    /**
     * 商户id
     */
    private Long merchantsId;

    /**
     * 商户编码
     */
    private String merchantsCode;


    //    /**
    //     * 备注开始时间
    //     */
    //    private String startCreateTime;
    //    /**
    //     * 备注结束时间
    //     */
    //    private String endCreateTime;
}
