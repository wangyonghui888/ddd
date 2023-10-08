package com.panda.rcs.order.reject.mapper;

import com.panda.rcs.order.reject.entity.OddsChangeInfo;
import com.panda.rcs.order.reject.entity.RcsTournamentTemplateAcceptConfigDto;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.rcs.order.reject.entity.RcsTournamentTemplateAcceptConfigRps;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-05-2022/5/4 14:19
 */
@Repository
public interface MatchInfoMapper {


    OddsChangeInfo queryOddsChangeInfo(@Param("matchId") Long matchId, @Param("matchType") Integer matchType, @Param("playId") Integer playId);

    String queryMtsTemplateConfig(@Param("matchId") Long matchId, @Param("matchType") Integer matchType);

    String queryDataSourceCode(@Param("matchId") Long matchId, @Param("categorySetId") Long categorySetId);

    String queryPreSettleDataSourceCode(@Param("matchId") Long matchId, @Param("categorySetId") Long categorySetId);

    RcsTournamentTemplateAcceptConfigDto queryWaitSecondsInfo(@Param("obj") OrderItem orderItem);

    RcsTournamentTemplateAcceptConfigDto querySubWaitSecondsInfo(@Param("obj") OrderItem orderItem);

    RcsTournamentTemplateAcceptConfigRps querWaitTimeInfo(@Param("matchId") Long matchId, @Param("categorySetId") Integer categorySetId);

    RcsTournamentTemplateAcceptConfigRps querSettleWaitTimeInfo(@Param("matchId") Long matchId, @Param("categorySetId") Integer categorySetId);

    String getVarSwitchStatus(@Param("typeId") Long typeId, @Param("typeValue") Long typeValue);
}
