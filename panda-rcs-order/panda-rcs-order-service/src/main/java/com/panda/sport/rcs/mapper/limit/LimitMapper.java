package com.panda.sport.rcs.mapper.limit;

import com.panda.sport.rcs.pojo.limit.RcsUserSpecialBetLimitConfigVo;
import com.panda.sport.rcs.pojo.vo.LimitRcsQuotaUserSingleNoteVo;
import com.panda.sport.rcs.pojo.vo.UserReferenceLimitResVo;
import com.panda.sport.rcs.pojo.vo.UserReferenceLimitVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 限額相關查詢
 * @author :  lithan
 * @Date: 2020-11-13 10:19:31
 * --------  ---------  --------------------------
 */
@Repository
public interface LimitMapper{
    List<LimitRcsQuotaUserSingleNoteVo> getRcsTournamentTemplatePlayMargainRefList(@Param("sportId") Integer sportId, @Param("matchId") Long matchId);

    List <UserReferenceLimitVo> referenceLimit();

    UserReferenceLimitVo referenceUserSingleLimit();

    UserReferenceLimitVo referenceUserMatchLimit();

    UserReferenceLimitResVo referenceCross();

    List<RcsUserSpecialBetLimitConfigVo> queryRcsUserSpecialBetLimitConfig(RcsUserSpecialBetLimitConfigVo config);

    String queryPlayInfoById(@Param("sportId") Integer sportId, @Param("playId") Integer playId);

    Integer getUserConfigNewByUserId(@Param("userId") long id);
}
