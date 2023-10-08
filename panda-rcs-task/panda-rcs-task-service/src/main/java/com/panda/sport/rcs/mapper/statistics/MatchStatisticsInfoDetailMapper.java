package com.panda.sport.rcs.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper.statistics
 * @Description :  TODO
 * @Date: 2020-03-02 21:45
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface MatchStatisticsInfoDetailMapper extends BaseMapper<MatchStatisticsInfoDetail> {

    @Select("SELECT * from match_statistics_info_detail WHERE `code` ='red_card_score' AND first_num=0 and standard_match_id= #{matchId} ORDER BY update_time DESC LIMIT 1")
    MatchStatisticsInfoDetail selectRedScore(@Param("matchId") Long matchId);
}
