package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.dto.*;
import com.panda.sport.rcs.console.pojo.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.console.pojo.TournamentTemplateExcelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

@Component
public interface RcsTournamentTemplateMapper extends BaseMapper<RcsTournamentTemplatePlayMargain> {
    List<RcsTournamentTemplatePlayMargain> selectTournamentSpecialOddsIntervalInitData(@Param("playId") Long playId);
    int batchUpdateSpecialOddsInterval(@Param("list") List<RcsTournamentTemplatePlayMargain> list);

    List<RcsTournamentTemplate> selectListByInfo(@Param("vo") RcsTournamentTemplate vo);
    TournamentTemplatePlayMargainParam selectPlayMarginByInfo(@Param("vo") TournamentTemplatePlayMargainParam vo);

    RcsTournamentTemplatePlayMargainRef selectPlayMarginRefByInfo(@Param("margainId") Long margainId, @Param("timeVal") Long timeVal);
    int batchInsertMarginPlay(TournamentTemplatePlayMargainParam tournamentTemplatePlayMargainParam);

    int batchInsertMarginRefPlay(RcsTournamentTemplatePlayMargainRef rcsTournamentTemplatePlayMargainRef);

    int batchInsertTemplate(RcsTournamentTemplate rcsTournamentTemplate);

    /**
     * @Description: 根据联赛等级获取玩法模板数据
     **/
    List<RcsTournamentPlayMarginTemplate> queryPlayTemplateInitData(RcsTournamentPlayMarginTemplate param);

    /**
     * 批量插入滚球结算审核事件
     * @param list
     * @return
     */
    int batchInsertEvent(@Param("list") List<RcsTournamentTemplateEvent> list);

    List<RcsTournamentEventTemplate> selectEventListBySportId(@Param("sportId") Integer sportId);

    int batchImportTemplate(List<TournamentTemplateExcelVO> list);
}
