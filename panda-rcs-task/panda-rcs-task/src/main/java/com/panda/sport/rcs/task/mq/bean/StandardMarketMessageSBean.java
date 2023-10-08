package com.panda.sport.rcs.task.mq.bean;

import java.io.Serializable;
import java.util.List;

import com.panda.merge.dto.I18nItemDTO;

import lombok.Data;

/**
 * 
 * @Description  : 标准盘口与投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:01:27
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketMessageSBean implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 标准盘口id
     * 非空
     */
    private Long id;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    private Long standardMatchInfoId;

    /**
     * 非空
     * 标准玩法id   standard_sport_market_category.id
     */
    private Long marketCategoryId;

    /**
     * 非空
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
    private String thirdMarketSourceId;

    private String remark;

    private Long modifyTime;
    
    /**
     * 盘口名称编码. 用于多语言
     */
    private List<I18nItemDTO> i18nNames;

    private String i18nNamesStr;

	/**
	 * 盘口投注项
	 */
	private List<StandardMarketOddsMessageSBean> marketOddsList;

    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

    private Long createTime;

}
