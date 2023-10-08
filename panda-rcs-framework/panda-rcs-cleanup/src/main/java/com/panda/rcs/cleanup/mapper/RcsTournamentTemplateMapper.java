package com.panda.rcs.cleanup.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RcsTournamentTemplateMapper {
    /**
     * 根据赛事ID查询模板ID
     * @param matchList 赛事ID
     * @return 模板ID
     */
    List<Long> queryTemplateIdByMatchId(@Param("matchList") List<Long> matchList);

    void deleteTemplateById(@Param("templateIdList") List<Long> templateIdList);
    void deleteMargainByTemplateId(@Param("templateIdList") List<Long> templateIdList);

    void deleteMargainRefByMargainId(@Param("margainId") List<Long> margainIdList);

    void deleteTemplateAcceptConfig(@Param("templateIdList") List<Long> templateIdList);
    void deleteTemplateAcceptEvent(@Param("acceptConfigIdList") List<Long> acceptConfigIdList);
    /**
     * 根据模板ID 查询 margainId
     * @param templateIdList 模板ID
     * @return margainId
     */
    List<Long> queryMargainIdByTemplateId(@Param("templateIdList") List<Long> templateIdList);

    List<Long> queryTemplateAcceptConfigId(@Param("templateIdList") List<Long> templateIdList);

    /**
     * 查询赛事模板ID
     * @return
     */
    List<Long> queryTemplateIdByNotMatchId();
}
