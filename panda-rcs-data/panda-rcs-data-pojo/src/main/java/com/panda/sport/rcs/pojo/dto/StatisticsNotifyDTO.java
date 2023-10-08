package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetailSource;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.message
 * @Date: 2019-10-07 17:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StatisticsNotifyDTO extends RcsBaseEntity<StatisticsNotifyDTO> {
    /**
     * 赛事id
     */
    private Long standardMatchId;

    /**
     * 从哪个通首来
     * -1 表示只推送时间
     * 1 比分
     */
    private Integer channel;
    
    private Long secondsFromStart;
    /**
     * 玩法总数量
     */
    private Integer categoryCount;

    private Integer period;

    List<MatchStatisticsInfoDetail> list;

}
