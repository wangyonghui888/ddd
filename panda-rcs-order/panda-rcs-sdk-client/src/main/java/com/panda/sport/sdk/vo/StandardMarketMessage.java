package com.panda.sport.sdk.vo;

import com.panda.sport.rcs.enums.MarketStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author black
 * @ClassName: StandardMarketMessage
 * @Description: TODO
 * @date 2020年8月11日 上午10:10:00
 */
@Data
public class StandardMarketMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准盘口id
     * 非空
     */
    private Long id;
    /**
     * 盘口位置id
     */
    private String placeNumId;

    /**
     * 盘口差
     */
    private Double marketHeadGap;

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
     * 操盘方式：0自动操盘，1手动操盘
     */
    private Integer tradeType;

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
     * 盘口级别，数字越小优先级越高
     */
    private Integer placeNum;
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
     * 三方盘口源状态,给风控操盘使用，融合侧不做修改
     */
    private Integer thirdMarketSourceStatus;
    /**
     * 旧三方盘口源状态,给风控操盘使用，融合侧不做修改
     */
    private Integer oldThirdMarketSourceStatus;

    /**
     * 通过以上三种状态加上操盘赛事状态得出的最终状态
     * 盘口状态0-5. 0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘, 3:settled 已结算, 4:cancelled 已取消, 5:handedOver  盘口的中间状态，该状态的盘口后续不会有赔率过来 11:锁盘状态
     */
    private Integer status;

    /**
     * 操盘后台设置的位置状态，融合侧不做修改，可为空（操盘后台没有设置位置状态为空）
     */
    private Integer placeNumStatus;
    /**
     * pa状态，赔率合法性校验，最大最小值校验设置的状态，可为空(没有经过赔率合法性校验为空)
     */
    private Integer paStatus;

    private String paStatusReason;
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
     * 盘口来源 0：数据商 1：融合构建
     */
    private Integer marketSource;


    /**
     * 盘口投注项
     */
    private List<StandardMarketOddsMessage> marketOddsList;

    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

    private String sendData;

    /**
     * 子玩法id
     */
    private Long childMarketCategoryId;
    /**
     * 收盘状态
     */
    private Integer endEdStatus;

    public String getPaStatusReason() {
        if (paStatus != null && paStatus == MarketStatusEnum.SEAL.getState()) {
            return paStatusReason;
        }
        return "";
    }
}
