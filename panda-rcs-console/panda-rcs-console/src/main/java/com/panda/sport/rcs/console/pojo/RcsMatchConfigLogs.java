package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2019-11-08 20:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Data
@Table(name = "rcs_match_config")
public class RcsMatchConfigLogs {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 赛事ID
     */
    @Column(name ="match_id")
    private Long matchId;

    /**
     * 赛事状态
     */
    @Column(name ="operate_match_status")
    private Integer operateMatchStatus;

    /**
     * 修改时间
     */
    @Column(name ="modify_time")
    private Date modifyTime;

    /**
     * 修改人
     */
    @Column(name ="modify_user")
    private String modifyUser;

    /**
     * 操盘类型  0是自动  1是手动
     */
    @Column(name ="trade_type")
    private Integer tradeType;

    /**
     * 自动调价参数
     */
    @Column(name = "price_adjustment_parameters")
    private BigDecimal priceAdjustmentParameters;
}