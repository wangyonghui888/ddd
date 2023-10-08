package com.panda.sport.rcs.pojo.tourTemplate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  联赛模板margain值
 * @Date: 2020-05-12 16:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsTournamentTemplatePlayMargainHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 模板id
     */
    private Long templateId;
    /**
     * 玩法id
     */
    private Long playId;
    /**
     * 1：早盘；2：滚球
     */
    private Integer matchType;
    /**
     * 1:马来盘  2：欧洲盘
     */
    private String marketType;
    /**
     * 是否开售
     */
    private Integer isSell;

    /**
     * 自動開盤設置
     * 籃球 > 1:上半場，2:下半場，13:第1節，14:第2節，15:第3節，16:第4節，40:加時
     */
    private Integer autoOpenMarket;

    /**
     * 自動開盤時間 (單位: 秒)
     */
    private Integer autoOpenTime;
    /**
     * 自动关盘时间设置1、上半场期间期间2、加时上半场3、下半场结束4、下半场期间
     */

    private Integer autoCloseMarket;

    /**
     * 最大盘口数
     */
    private Integer marketCount;

    /**比赛进程时间
     *
     */
    private Long matchProgressTime;
    /**
     * 补时时间
     */
    private Long injuryTime;

    /**
     * 盘口出涨预警
     */
    private Integer marketWarn;
    /**
     * 支持串关，1:是 0:否
     */
    private Integer isSeries;
    /**
     * 副盘限额比列
     */
    private String viceMarketRatio;
    /**
     * 相邻盘口差值
     */
    private BigDecimal marketNearDiff;
    /**
     * 相邻盘口赔率差值
     */
    private BigDecimal marketNearOddsDiff;
    /**
     * 赔率（水差）变动幅度
     */
    private BigDecimal oddsAdjustRange;
    /**
     * 盘口调整幅度
     */
    private BigDecimal marketAdjustRange;

    /**
     * 生效margin
     */
    private Long validMarginId;
    /**
     * 预约投注开关 0:关 1:开
     */
    private Integer pendingOrderStatus;
    /**
     * 创建时间
     */
    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;
}
