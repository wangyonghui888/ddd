package com.panda.sport.data.rcs.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.data.rcs.dto.I18nItemDTO;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName StandardMarketOddsMessageVO
 * @Description: TODO
 * @Author Vector
 * @Date 2019/12/14
 **/

@Getter
@Setter
public class StandardMarketOddsMessageVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准投注项ID
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 盘口ID  standard_sport_market.id
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long oddsFieldsTemplateId;
    /**
     * 三方源投注项模板id
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private String thirdOddsFieldSourceId;

    private String remark;

    private Long modifyTime;
    /**
     * 名称编码. 用于多语言。交易项可能有也可能没有该字段。需要的时候填入
     */
    private List<I18nItemDTO> i18nNames;

    /**
     * 赔率显示值
     */
    private String fieldOddsValue;

    /**
     * 降级后的欧赔数据
     */
    private String nextLevelOddsValue;

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
