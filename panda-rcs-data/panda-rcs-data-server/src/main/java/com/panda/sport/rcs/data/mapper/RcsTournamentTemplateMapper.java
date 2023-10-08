package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.dto.TournamentTemplateDto;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptEvent;
import feign.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description   联赛模板配置
 * @Param 
 * @Author  toney
 * @Date  20:02 2020/5/10
 * @return 
 **/
@Repository
public interface RcsTournamentTemplateMapper extends BaseMapper<RcsTournamentTemplate> {
    /**
     * 按联赛 id进行搜索，取联赛配置
     * @param tournamentId
     * @return
     */
    List<TournamentTemplateDto> queryByTournamentId(@Param("tournamentId") Long tournamentId, @Param("sportId") Integer sportId,@Param("matchType") Integer matchType);

    /**
     * 按联赛级别进行搜索,取模板
     * @param tournamentLevel
     * @return
     */
    List<TournamentTemplateDto> queryByTournamentLevel(@Param("tournamentLevel") Integer tournamentLevel, @Param("sportId") Integer sportId,@Param("matchType") Integer matchType);

    com.panda.sport.rcs.pojo.RcsTournamentTemplate selectTemplate(@Param("vo") com.panda.sport.rcs.pojo.RcsTournamentTemplate template);
}