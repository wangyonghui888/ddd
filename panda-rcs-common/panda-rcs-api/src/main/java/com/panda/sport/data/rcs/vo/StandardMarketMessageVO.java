package com.panda.sport.data.rcs.vo;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.data.rcs.dto.I18nItemDTO;
import com.panda.sport.rcs.utils.LongToStringSerializer;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName StandardMarketMessageVO
 * @Description: TODO
 * @Author Vector
 * @Date 2019/12/14
 **/
@Getter
@Setter
public class StandardMarketMessageVO   implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long standardMatchInfoId;

    /**
     * 标准盘口id
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 标准玩法id   standard_sport_market_category.id
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long marketCategoryId;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;

    /**
     * 该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5
     */
    private String oddsValue;

    /**
     * 盘口名称,V1.2统一命名规则.
     */
    private String oddsName;

    /**
     * 排序类型
     */
    private String orderType;

    /**
     * 盘口级别，数字越小优先级越高
     */
    private Integer oddsMetric;
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
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 盘口状态0-5. 0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘, 3:settled 已结算, 4:cancelled 已取消, 5:handedOver  盘口的中间状态，该状态的盘口后续不会有赔率过来 11:锁盘状态
     */
    private Integer status;

    /**
     * 玩法所属时段，
     * 对应字典parent_type_id=7
     */
    private String scopeId;

    /**
     * 该字段用于做风控时，需要替换成风控服务商提供的盘口id。 如果数据源发生切换，当前字段需要更新。
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private String thirdMarketSourceId;

    private String remark;

    private Long modifyTime;

    /**
     * 盘口名称编码. 用于多语言
     */
    private List<I18nItemDTO> i18nNames;

    /**
     * 盘口投注项
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private List<StandardMarketOddsMessageVO> marketOddsList;

    /**
     * 标记0：非全空  1：全空
     * @return
     */
    private Integer paOddsValueIsZero;

    /**
     * 设置值
     * @return
     */
    public void setPaOddsValueIsZero(){
        if(marketOddsList== null || marketOddsList.size() == 0){
            paOddsValueIsZero = 0;
            return;
        }

        for(StandardMarketOddsMessageVO vo:marketOddsList){
            if(vo.getPaOddsValue() != 0){
                paOddsValueIsZero = 0;
                return;
            }
        }
        paOddsValueIsZero = 1;
    }

    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}


}
