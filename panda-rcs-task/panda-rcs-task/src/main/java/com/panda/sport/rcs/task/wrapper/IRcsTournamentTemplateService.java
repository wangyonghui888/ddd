package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.dto.TournamentTemplateDto;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;

import java.util.List;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.trade.wrapper.tourTemplate
 * @Description :  联赛模板
 * @Date: 2020-05-10 20:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IRcsTournamentTemplateService extends IService<RcsTournamentTemplate>{
    /**
     * 按联赛 id进行搜索，取联赛配置
     * @param queryByTournamentId
     * @return
     */
    List<TournamentTemplateDto> queryByTournamentId(Long queryByTournamentId, Integer sportId,Integer matchType);

    /**
     * 按联赛级别进行搜索,取模板
     * @param tournamentLevel,sportId,matchType
     * @return
     */
    List<TournamentTemplateDto> queryByTournamentLevel(Integer tournamentLevel,Integer sportId,Integer matchType);

    /**
     * 查询配置
     * @param matchInfo
     * @return
     */
    List<TournamentTemplateDto> query(StandardMatchInfo matchInfo,Integer matchType);

    /**
     * 模板margin移除到历史表
     */
    void syncTemplateToHistory(List<RcsTournamentTemplatePlayMargain> playMarginList);

    /**
     * 模板滚球接拒单事件移除到历史表
     */
    void syncTemplateAcceptEventToHistory(List<RcsTournamentTemplateAcceptConfig> playMarginList);
}
