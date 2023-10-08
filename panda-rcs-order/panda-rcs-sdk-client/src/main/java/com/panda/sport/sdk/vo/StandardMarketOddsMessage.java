package com.panda.sport.sdk.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author black
 * @ClassName: StandardMarketOddsMessage
 * @Description: TODO
 * @date 2020年8月11日 上午10:10:52
 * @see com.panda.merge.dto.message.StandardMarketOddsMessage
 */
@Data
public class StandardMarketOddsMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准投注项ID
     */
    private Long id;

    /**
     * 盘口ID  standard_sport_market.id
     */
    private Long marketId;

    /**
     * 当前投注项是否被激活.1激活; 0未激活(锁盘)
     */
    private Integer active;

    /**
     * 投注项结算结果文本
     */
    private String settlementResultText;

    /**
     * 投注项结算结果文本
     */
    private String settlementResult;

    /**
     * 赛果已确认: Confirmed, 盘中事件确认: LiveScouted, 未知: Unknown
     */
    private String betSettlementCertainty;

    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 附加字段1
     */
    private String addition1;

    /**
     * 附加字段2
     */
    private String addition2;

    /**
     * 附加字段3
     */
    private String addition3;

    /**
     * 附加字段4
     */
    private String addition4;

    /**
     * 附加字段5
     */
    private String addition5;

    /**
     * 投注项名称. 
     */
    private String name;

    /**
     * 投注项名称中包含的表达式的值
     * @since v1.3 将在未来移除该字段
     */
    @Deprecated
    private String nameExpressionValue;

    /**
     * 投注项赔率. 单位: 0.00001
     * @since v1.2丢弃, 将在后续版本删除
     */
    @Deprecated
    private Integer oddsValue;
    
    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer paOddsValue;

    /**
     * 标准投注项模板id   standard_sport_odds_fields_templet.id
     */
    private Long oddsFieldsTemplateId;
    /**
     * 三方源投注项模板id
     */
    private String thirdTemplateSourceId;
    /**
     * 投注项原始赔率. 单位: 0.00001
     * @since v1.2丢弃, 将在后续版本删除
     */
    @Deprecated
    private Integer originalOddsValue;

    /**
     * 投注给哪一方: T1主队, T2客队, 
     */
    private String targetSide;

    /**
     * 用于排序, 大于1, 越小越靠前
     */
    private Integer orderOdds;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 该字段用于做风控时, 需要替换成风控服务商提供的投注项id.  如果数据源发生切换, 当前字段需要更新. 
     */
    private String thirdOddsFieldSourceId;

    private String remark;

    private Long modifyTime;

    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

    /**
     * 水差值
     */
    private Double marketDiffValue;
    /**
     * magin值
     */
    private BigDecimal margin;

    /**
     * 概率差
     */
    private Double probability;

    /**
     * 概率赔率
     */
    private Integer probabilityOdds;
    /**
     * margin概率赔率
     */
    private Integer marginProbabilityOdds;

    /**
     * 描点 ：0(否),1(是)
     */
    private Integer anchor;

    /**
     * 开启/关闭投注项手动模式，0-关闭，1-开启
     */
    private Integer status = 0;
}
