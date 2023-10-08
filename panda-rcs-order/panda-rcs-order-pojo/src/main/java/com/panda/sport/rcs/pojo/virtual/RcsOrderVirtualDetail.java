package com.panda.sport.rcs.pojo.virtual;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 第三方 虚拟赛事 注单详细信息表
 * </p>
 *
 * @author lithan
 * @since 2020-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsOrderVirtualDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 注单编号
     */
    private String betNo;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 运动种类编号
     */
    private Integer sportId;

    /**
     * 运动种类名称
     */
    private String sportName;

    /**
     * 对阵信息
     */
    private String matchInfo;

    /**
     * 联赛id(第三方playListId)
     */
    private Long tournamentId;

    /**
     * 赛事编号(第三方eventId)
     */
    private Long matchId;

    /**
     * 盘口ID(第三方marketId)
     */
    private String marketId;

    /**
     * 投注项ID(第三方oddId)
     */
    private String playOptionsId;

    /**
     * 投注项名称，直接保存业务系统传递的值
     */
    private String playOptionsName;

    /**
     * 赔率
     */
    private String oddsValue;

    /**
     * 注单金额，指的是下注本金2位小数，投注时x100
     */
    private Long betAmount;

    /**
     * 最高可赢金额
     */
    private Long maxWinAmount;

    /**
     * 串关类型(1:单注(默认) M000N:M串N  )
     */
    private Integer seriesType;

    /**
     * 投注时间(业务传)
     */
    private Long betTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 订单状态( 0 待处理  1：成功  2：拒绝)
     */
    private Integer orderStatus;


}
