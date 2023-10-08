package com.panda.rcs.push.entity.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName StandardMarketOddsMessageVO
 * @Description: TODO
 * @Author Vector
 * @Date 2019/12/14
 * @see com.panda.merge.dto.message.StandardMarketOddsMessage
 **/
@Getter
@Setter
public class LiveStandardMarketOddsMessageVO implements Serializable {
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
     * 投注项原始赔率. 单位: 0.00001
     * @since v1.2丢弃, 将在后续版本删除
     */
    @Deprecated
    private Integer originalOddsValue;

    /**
     * 用于排序, 大于1, 越小越靠前
     */
    private Integer orderOdds;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;


    private Long modifyTime;

    /**
     * 赔率显示值
     */
    private String fieldOddsValue;

    /**
     * 降级后的欧赔数据
     */
    private String nextLevelOddsValue;

    private BigDecimal margin;
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
     * 开启/关闭投注项手动模式，0-关闭，1-开启
     */
    private Integer status = 0;

    /**
     * margin概率赔率
     */
    private Integer marginProbabilityOdds;

    /**
     * 体育名称编码
     */
    private Long nameCode;

    /**
     * 三方数据投注项状态  0未激活（锁）  1激活  2投注项封
     */
    private Integer thirdSourceActive;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
