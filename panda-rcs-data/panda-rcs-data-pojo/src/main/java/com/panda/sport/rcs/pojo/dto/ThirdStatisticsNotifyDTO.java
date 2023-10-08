package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetailSource;
import lombok.Data;

import java.util.List;

/**
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.message
 * @Date: 2019-10-07 17:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ThirdStatisticsNotifyDTO extends RcsBaseEntity<ThirdStatisticsNotifyDTO> {
    /**
     * 赛事id
     */
    private Long standardMatchId;

    private Long secondsFromStart;

    private Integer period;

    private String dataSourceCode;
    
    private List<MatchStatisticsInfoDetailSource> matchStatisticsInfoDetailList;

}
