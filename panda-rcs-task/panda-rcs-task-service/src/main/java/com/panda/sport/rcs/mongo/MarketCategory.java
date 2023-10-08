package com.panda.sport.rcs.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "rcs_market_category")
public class MarketCategory {

    /**
     * @Description //标准玩法id
     **/
    @Field(value = "id")
    private Long id;

    /**
     * 体育种类
     */
    private Long sportId;

    /**
     * 赛事Id
     */
    private String matchId;

    /**
     * 比赛开始时间
     */
    private String matchStartTime;

    /**
     * 玩法自动手动
     */
    private Integer tradeType;
    /**
     * 玩法状态
     */
    private Integer status;
    /**
     * 占位符玩法总状态，默认开
     */
    private Integer mainPlayStatus;

    private String type;
    /**
     * 玩法名称
     */
    private Map<String, String> names;
    /**
     * 玩法模板ID
     */
    private Integer templateId;
    /**
     * 排序
     */
    private Integer orderNo;

    /**
     * 设置盘口数量
     */
    private Integer marketCount;

    /**
     * 盘口类型
     */
    private String marketType;
    /**
     * 赔率（水差）变动幅度
     */
    private BigDecimal oddsAdjustRange;
    /**
     * 盘口调整幅度
     */
    private BigDecimal marketAdjustRange;

    /**
     * margain值
     */
    private String margain;

    private String updateTime;
    /**
     * 水差是否关联，0-不关联，1-关联
     */
    private Integer relevanceType;
    /**
     * 赔率报警 true:报警   false:解除
     */
    private boolean warningSign;
    /**
     * 数据源
     */
    private String dataSource;

    /**
     * 数据站点
     */
    private String internalDataSourceCode;



    /**
     * 盘口来源 0：数据商 1：融合构建
     */
    private Integer marketSource;

    /**
     * 玩法级别提前结算开关
     */
    private Integer categoryPreStatus;

    /**
     * 数据商是否支持提前结算
     * ＆关系，现存盘口只要一个盘口满足
     */
    private Integer cashOutStatus;
    /**
     * 盘口集合
     */
    private List<MatchMarketVo> matchMarketVoList;
    /**
     * 玩法期望值
     */
    private List<PredictForecastVo> forecast;

}
