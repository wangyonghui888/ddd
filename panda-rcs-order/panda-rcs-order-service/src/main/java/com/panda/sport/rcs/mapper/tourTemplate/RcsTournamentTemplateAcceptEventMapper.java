package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptEvent;
import feign.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description   联赛模板事件从表
 * @Param 
 * @Author  toney
 * @Date  20:02 2020/5/10
 * @return 
 **/
@Repository
public interface RcsTournamentTemplateAcceptEventMapper extends BaseMapper<RcsTournamentTemplateAcceptEvent> {
    int deleteByPrimaryKey(Integer id);

    int insertOrUpdateBatch(@Param("list") List<RcsTournamentTemplateAcceptEvent> list);

    int insertSelective(RcsTournamentTemplateAcceptEvent record);

    RcsTournamentTemplateAcceptEvent selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RcsTournamentTemplateAcceptEvent record);

    int updateByPrimaryKey(RcsTournamentTemplateAcceptEvent record);

    /**
     * 按联赛 id进行搜索，取联赛配置
     * @param tournamentId 联赛id
     * @param sportId 赛种id
     * @return
     */
    List<RcsTournamentTemplateAcceptEvent> queryByTournamentId(@Param("tournamentId") Long tournamentId,@Param("sportId")Integer sportId);

    /**
     * 按联赛级别进行搜索,取模板
     * @param tournamentLevel
     * @return
     */
    List<RcsTournamentTemplateAcceptEvent> queryByTournamentLevel(@Param("tournamentLevel") Integer tournamentLevel,@Param("sportId")Integer sportId);

    List<RcsTournamentTemplateAcceptEvent> selectOrderAcceptEvent(@org.apache.ibatis.annotations.Param("matchId") Long matchId, @org.apache.ibatis.annotations.Param("playSetId") Integer playSetId);

}