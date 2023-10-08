package com.panda.sport.rcs.mgr.service.orderhide.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author skyKong
 * @since 2022-9-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwitchStatusVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 赛种
     */
    private String sportIds;


    /**
     * 开关状态；
     * */
    private Integer status;

}
