package com.panda.sport.rcs.pojo.dj;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName RcsDjBet
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/22 14:30
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value="rcs_dj_order_detail")
public class RcsDjBet implements Serializable {
    private static final long serialVersionUID = 7073465062631993641L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

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
     * 运动种类id
     */
    private Long sportId;

    /**
     * 运动名称
     */
    private String sportName;

    /**
     * 对阵信息
     */
    private String matchInfo;

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 盘口id
     */
    private String marketId;

    /**
     * 投注项id
     */
    private String playOptionsId;

    /**
     * 投注项名称
     */
    private String playOptionsName;


    /**
     * 赔率
     */
    private String oddsValue;

    /**
     * 串关类型
     */
    private Integer seriesType;

    /**
     * 投注时间
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
     * 订单状态
     */
    private Integer orderStatus;

}
