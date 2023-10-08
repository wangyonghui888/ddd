package com.panda.sport.rcs.pojo.tourTemplate;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>
 *
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-10-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
public class RcsTournamentTemplatePlayMargain implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal margin;

    /**
     * 模板id
     */
    private Integer templateId;

    /**
     * 玩法id
     */
    private Integer playId;

    /**
     * 0：滚球 1：赛前
     */
    private Integer matchType;

    /**
     * MY：马来盘  EU：欧洲盘  Other：其他
     */
    private String marketType;

    /**
     * 最大盘口数
     */
    private Integer marketCount;

    /**
     * 玩法集id
     */
    private Integer categorySetId;

    /**
     * 水差
     */
    private BigDecimal awayAutoChangeRate;

    /**
     * 是否开售 1：是  0：否
     */
    private Integer isSell;

    /**
     * 足球自动关盘时间设置：6、上半场期间 41、加时赛上半场 7、下半场期间 42、加时赛下半场
     篮球自动关盘时间设置：13、第1节 14、第2节 15、第3节 16、第4节 40、加时
     */
    private Integer autoCloseMarket;

    /**
     * 比赛进程时间
     */
    private Integer matchProgressTime;

    /**
     * 补时时间
     */
    private Integer injuryTime;

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
    private BigDecimal marketNearDiff = new BigDecimal("0");

    /**
     * 盘口差
     */
    private BigDecimal marketDiff = new BigDecimal("0");

    /**
     * 相邻盘口赔率差值
     */
    private BigDecimal marketNearOddsDiff;

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


    @TableField(exist = false)
    public List<RcsTournamentTemplatePlayMargainRef> playMargainRefList;

    public List<RcsTournamentTemplatePlayMargainRef> getPlayMargainRefList() {
        return playMargainRefList;
    }


}
