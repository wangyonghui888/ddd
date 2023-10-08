package com.panda.sport.rcs.pojo.vo.predict.pending;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.panda.sport.rcs.pojo.vo.ActualVolumeVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 预测货量表
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rcs_predict_pending_bet_statis")
public class RcsPredictPendingBetStatis implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运动种类
     */
    private Integer sportId;

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
     * 盘口id
     */
    private Long marketId;

    /**
     * 投注项
     */
    private String oddsItem;

    /**
     * 基准分 投注时比分
     */
    private String betScore;

    /**
     * 货量
     */
    private BigDecimal betAmount;

    /**
     * 投注笔数
     */
    private Long betNum;

    /**
     * 赔率和
     */
    private BigDecimal oddsSum;

    /**
     * 完整让球(盘口值)
     */
    private String marketValueComplete;

    /**
     * 当前让球(盘口值)
     */
    private String marketValueCurrent;

    /**
     * 投注项名称
     */
    private String playOptions;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 货量-纯赔付额
     */
    private BigDecimal betAmountPay;

    /**
     * 货量-混合型（注单亚赔大于1的，货量为注单额*亚赔；注单亚赔小于或等于1的，货量为注单额）
     */
    private BigDecimal betAmountComplex;

    /**
     * 子玩法ID
     */
    private String subPlayId;

    @TableField(exist = false)
    private List<ActualVolumeVO> actualVolumeVOList;

}
