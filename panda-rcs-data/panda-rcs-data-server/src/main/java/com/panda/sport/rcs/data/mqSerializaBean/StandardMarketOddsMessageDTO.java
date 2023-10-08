package com.panda.sport.rcs.data.mqSerializaBean;

import com.panda.merge.dto.I18nItemDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @Description  :  标准盘口投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:41:31
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketOddsMessageDTO implements Serializable{
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
     * 当前投注项是否被激活.1关闭; 0开启
     */
    private Integer status;

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
     * 注：此字段变更成的上游的pa_odds_value  入库到数据库odds_value
     */
    @Deprecated
    private Integer oddsValue;

    /**
     * 注：此字段是变更成的上游的 oddsValue 入库到数据库 previous_odds_value 字段
     */
    private Integer previousOddsValue;

    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer paOddsValue;

    /**
     * 标准投注项模板id   standard_sport_odds_fields_templet.id
     */
    private Long oddsFieldsTemplateId;

    /**
     * 标准投注项模板id   standard_sport_odds_fields_templet.id
     */
    @Deprecated
    private Long oddsFieldsTempletId;
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
     * 水差值
     */
    private Double marketDiffValue;


    /**
     * 概率差
     */
    private Double probability;


    /**
     * 概率赔率
     */
    private Integer probabilityOdds;



    /**
     * 描点 ：0(否),1(是)
     */
    private Integer anchor;

    /**
     * margin概率赔率
     */
    private Integer marginProbabilityOdds;

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
     * 名称编码. 用于多语言。交易项可能有也可能没有该字段。需要的时候填入
     */
    private List<I18nItemDTO> i18nNames;

    private String i18nNamesStr;
    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

    private BigDecimal margin;

    private Long createTime;

    /**
     * 清理概率差标识
     */
    private Integer clearProbability;

    /**
     * 体育名称编码
     */
    private Long nameCode;

    /**
     * 三方数据投注项状态  0未激活（锁）  1激活  2投注项封
     */
    private Integer thirdSourceActive;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardMarketOddsMessageDTO that = (StandardMarketOddsMessageDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
