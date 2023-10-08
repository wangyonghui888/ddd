package com.panda.sport.rcs.mongo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author :  Administrator
 * @Project Name :  panda-rcs-task-group
 * @Package Name :  com.panda.sport.rcs.mongo
 * @Description :  TODO
 * @Date: 2020-07-19 9:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchMarketOddsVo {


    @Field(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 多语言
     */
    private Map<String, String> names;
    private Long nameCode;
    /**
     * 投注项名称中包含的表达式的值
     */
    private String nameExpressionValue;

    /**
     * 投注给哪一方：T1主队，T2客队
     */
    private String targetSide;

    /**
     * 赔率显示值
     */
    private String fieldOddsValue;

    /**
     * 赔率原始值
     */
    private Integer fieldOddsOriginValue;

    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 实货量
     */
    private BigDecimal betAmount;

    /**
     * 盈利期望值
     */
    private BigDecimal profitValue;

    /**
     * 注单数
     */
    private BigDecimal betNum;

    /**
     * 水差值
     */
    private Double marketDiffValue;

    /**
     * 降级后的欧赔数据
     */
    private String nextLevelOddsValue;

    /**
     * 当前投注项是否被激活.1激活; 0未激活(锁盘)
     */
    private Integer active;
    /**
     * 排序
     */
    private Integer orderOdds;
    /**
     * magin值
     */
    private BigDecimal margin;

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

    private Integer sortNo = 0;

    private Integer groupId = 0;

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

    private String name;
}
