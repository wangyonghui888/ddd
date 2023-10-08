package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import lombok.Data;

import java.util.List;
@Data
public class ClearDTO extends RcsBaseEntity<ClearDTO> {

    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 1.来自融合的自动清理标识  2.手动清理标识   3，task比分清理标识  4.赛前切滚球清识标识  5.数据源切换清理标识
     */
    private Integer clearType;
    /**
     * 赛事类型,0:普通赛事、1冠军赛事
     */
    private Integer type;
    /**
     * 清理类型
     */
    private List<ClearSubDTO> list;
    /**
     * 体育种类
     */
    private Long sportId;
    /**
     * 比赛开始时间. 比赛开始时间 UTC时间
     */
    private Long beginTime;




}
