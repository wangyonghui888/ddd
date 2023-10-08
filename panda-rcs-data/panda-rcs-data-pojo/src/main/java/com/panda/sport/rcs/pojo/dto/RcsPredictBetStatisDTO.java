package com.panda.sport.rcs.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.pojo.vo.ActualVolumeVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 预测货量表
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-18
 */
@Data
public class RcsPredictBetStatisDTO extends RcsBaseEntity<RcsPredictBetStatisDTO> {

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
     * 子玩法ID
     */
    private String subPlayId;

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
     * 完整让球(盘口值)
     */
    private String marketValueComplete;

    /**
     * 当前让球(盘口值)
     */
    private String marketValueCurrent;

    /**
     * 投注笔数
     */
    private Long betNum;

    /**
     * 赔率和
     */
    private BigDecimal oddsSum;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 投注项名称
     */
    private String playOptions;

    private List<ActualVolumeVO> actualVolumeVOList;
}
