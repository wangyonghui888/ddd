package com.panda.sport.data.rcs.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ThreewayOverLoadTriggerItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 玩法ID
     */
    private Integer playId;

    /**
     * 赛事编号
     */
    private Long matchId;
    
    /**
     * 位置
     */
    private Integer placeNum;

    /**
     * 盘口id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long marketId;

    /**
     * 投注类型ID(对应上游的投注项ID),传给风控的
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long playOptionsId;

    /**
     * 是否使用数据源0：手动；1：使用数据源
     */
    private Integer dataSource;

    /**
     * 调价策略 asc 代表当前投注项升水 desc 降水
     */
    private FixDirectionEnum fixDirectionEnum;
    /**
     * 操盘类型 EU 欧盘 MY 马来盘
     */
    private String marketType;
    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    private Integer matchType;

    /**
     * 0 否 1 是 自动封盘 2 关盘
     */
    private String autoBetStop;
    /**
     * 操盘类型 EU 欧盘 MY 马来盘
     */
    private String maxOdds;

    /**
     * 0 否 1 是 自动封盘
     */
    private String minOdds;

    /**
     * 当前投注项对应side，主/大用home表示 平tie
     */
    private String oddsType;

    /**
     * 主一级赔率变化率
     */
    private BigDecimal homeLevelFirstOddsRate;

    /**
     * 所有投注项
     */
    private List<Long> optionList ;
    /**
     * margin值
     */
    private BigDecimal margin;
    /**
     * 主胜margin
     */
    private BigDecimal homeMargin;
    /**
     * 客胜margin
     */
    private BigDecimal awayMargin;
    /**
     * 平局margin
     */
    private BigDecimal tieMargin;

    public static enum FixDirectionEnum {
        ASC,DESC;
    }
    /**
     * @Description   主队水差
     * @Param 
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return 
     **/
    private  Double homeAutoChangeRate;
    /**
     * @Description   客队水差
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    private  Double awayAutoChangeRate;
    /**
     * @Description   和局水差
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    private  Double tieAutoChangeRate;
    /**
     * @Description   自动水差开关 0 关闭 1 开启
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    private  Integer switchAutoChangeRate;
    /**
     * @Description   联赛id
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    private  Integer tournamentId;
    /**
     * @Description   玩法阶段
     * @Param 
     * @Author  Sean
     * @Date  16:34 2020/1/16
     * @return 
     **/
    private  Integer playPhaseType;
    /**
     * 玩法阶段对应玩法种类
     */
    private Integer rollType;
    /**
     * 玩法阶段对应玩法种类
     */
    private Long diffOdds;
    /**
     * 玩法阶段对应玩法种类
     */
    private String nameExpressionValue;
    
    private String msg = "match_info(match_manage_id):play_option_name已触发早盘跳水封盘，请及时检查开启。";
 
    public ThreewayOverLoadTriggerItem replaceMsg(String key,String value) {
       	this.msg = msg.replaceAll(key, value);
       	return this;
    }
}
