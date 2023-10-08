package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 标准冠军赛事表
 * </p>
 *
 * @author lithan auto
 * @since 2020-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsStandardOutrightMatchInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛种id
     */
    private Long sportId;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 标准联赛id
     */
    private Long standardTournamentId;

    /**
     * 数据源
     */
    private String dataSourceCode;

    /**
     * 下次封盘时间
     */
    private Long nextClosingTime;

    /**
     * 赛事开关封锁 -1 未开 0 :开、2:关、1:封、11
     */
    private Integer matchMarketStatus;

    /**
     * 冠军赛事管理id
     */
    private String standardOutrightManagerId;

    /**
     * 三方冠军赛事id
     */
    private Long thirdOutrightMatchId;

    /**
     * 三方冠军赛事源id
     */
    private String thirdOutrightMatchSourceId;

    /**
     * 标准冠军赛事开始时间
     */
    private Long standrdOutrightMatchBegionTime;

    /**
     * 标准冠军赛事结束时间
     */
    private Long standrdOutrightMatchEndTime;

    /**
     * 冠军赛事开售状态 Sold 开售 Unsold 未售
     */
    private String sellStatus;

    /**
     * 是否自动开售新盘口 Yes  是 No 否
     */
    private String autoSellStatus;

    /**
     * 赛季id
     */
    private String seasonId;

    /**
     * 标准冠军赛事赛季名称
     */
    private String standardOutrightYear;

    /**
     * 是否订阅  0 未订阅  1已订阅
     */
    private Integer booked;

    /**
     * 备注
     */
    private String remark;

    /**
     * 联赛名称编码. 联赛名称编码. 用于多语言
     */
    private Long nameCode;

    /**
     * 新增时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long modifyTime;

    private Date updateTime;


}
