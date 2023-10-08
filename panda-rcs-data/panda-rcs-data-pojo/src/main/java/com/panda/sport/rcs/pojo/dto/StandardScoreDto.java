package com.panda.sport.rcs.pojo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Data
public class StandardScoreDto {
    /**
     * 赛事id
     */
    private Long standardMatchId;
    /**
     * 阶段
     */
    private Integer periodId;
    /**
     * 局
     */
    private Integer secondNum;
    /**
     *赛种
     */
    private Long sportId;
    /**
     *数据源
     */
    private String dataSourceCode;
    /**
     *原数据比分
     */
    private Map<String,Map<String,Object>> scores;
    /**
     *事件源类型
     */
    private Integer eventSourceType;
    /**
     *时间
     */
    private Long scoreTime;
    /**
     *统计比分
     */
    private Map<String,Object> allScores;


}
