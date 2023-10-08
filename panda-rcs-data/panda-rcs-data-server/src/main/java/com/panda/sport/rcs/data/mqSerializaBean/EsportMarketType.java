package com.panda.sport.rcs.data.mqSerializaBean;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * esport_tournament
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EsportMarketType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 玩法id
     */
    private Long id;

    /**
     * 玩法中文名称
     */
    @JSONField(name = "cn_name")
    private String marektCnName;

    /**
     * 玩法英文名称
     */
    @JSONField(name = "en_name")
    private String marektEnName;

    /**
     * 所属游戏ID,对应体育体种
     */
    @JSONField(name = "game_id")
    private Long gameId;

    /**
     * 玩法类型，全局-1 单场-2 篮球上半场-3 冠军-4)
     */
    private Integer category;

    /**
     * 选项类型 1-输赢 2-让分 3-大小 4趣味 5-波胆 <br />6-胜负平 7-单双 8-是否 9-复合 <br />10-猜冠军（单项结算）11-猜冠军（多项结算）
     */
    @JSONField(name = "option_type")
    private Integer oddsType;

    /**
     * 投注项数量
     */
    @JSONField(name = "option_total")
    private Integer fieldsNum;

    /**
     * 选项名称
     */
    @JSONField(name = "option_name")
    private String optionName;

    /**
     * 选项英文名称
     */
    @JSONField(name = "option_en_name")
    private String optionEnName;

    /**
     * 选项英文名称
     */
    @JSONField(name = "game_short_name")
    private String gameShortName;

    /**
     * 赔付占比
     */
    @JSONField(name = "bonus_proportion")
    private Integer bonusProportion;

    /**
     * 玩法标签
     */
    private Long tag;

    /**
     * 玩法标签
     */
    @JSONField(name = "tag_code")
    private Integer tagCode;

    /**
     * 联赛等级
     */
    @JSONField(name = "league_level")
    private String leagueLevel;

    /**
     * 返还率递增 范围限定在负15%至正15%
     */
    @JSONField(name = "lnc_return_rate")
    private Float lncReturnRate;

    /**
     * 玩法模组 1-赛前 2-BP 3-滚球中
     */
    @JSONField(name = "module")
    private Integer marketType;

    /**
     * 是否串关 是-1 否-0
     */
    @JSONField(name = "is_pass_off")
    private Integer isPassOff;

    /**
     * 状态 1-正常 0-关闭
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer status;

    /**
     * 状态 1-正常 0-关闭
     */
    private Integer visible;

    private Long createTime;

    private Long modifyTime;

    /**
     * 国际化name_code
     */
    private Long nameCode;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer standardMarketId;

    /**
     * 客户PC端模板展示
     */
    private Integer templatePcClient;

    /**
     * 客户H5端展示
     */
    private Integer templateH5Client;

    //排序值
    private Integer orderNo;

    // 是否属于多盘口玩法.0no;1yes.默认no
    private Integer multiMarket;

    /**
     * 玩法名称编码. 用于多语言.
     */
    private Long descNameCode;

    /**
     * (玩法)是否展开,默认1; 0:不展开  1:展开
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer isCollapse = 1;


    /**
     * 玩法投注项类型可能值
     */
    private String oddsTypeVal;


}
