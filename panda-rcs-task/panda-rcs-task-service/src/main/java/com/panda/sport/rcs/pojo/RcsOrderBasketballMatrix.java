package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单-篮球矩阵-关系表
 * </p>
 *
 * @author lithan
 * @since 2021-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsOrderBasketballMatrix implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商户id
     */
    private Long tenantId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 运动种类
     */
    private Integer sportId;

    /**
     * 联赛id
     */
    private Long tournamentId;

    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * 赛事类型:1赛前,2滚球
     */
    private Integer matchType;

    /**
     * 玩法id
     */
    private Integer playId;

    /**
     * 分差中值/总分始值
     */
    private Integer initMarket;

    /**
     * 矩阵数据
     */
    private String recVal;

    /**
     * 投注时间
     */
    private Long betTime;

    /**
     * 创建时间
     */
    private Long createTime;


}
