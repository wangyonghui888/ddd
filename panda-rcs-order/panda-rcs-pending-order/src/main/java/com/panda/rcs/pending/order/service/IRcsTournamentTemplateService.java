package com.panda.rcs.pending.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.pending.order.param.TournamentTemplateParam;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.rcs.pending.order.pojo.TournamentTemplateVo;

import java.util.List;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-05-2022/5/2 18:39
 */
public interface IRcsTournamentTemplateService extends IService<RcsTournamentTemplate> {


    /**
     * 获取模板和玩法参数数据
     *
     * @param tournamentTemplateParam
     * @return
     */
    TournamentTemplateVo queryPendingOrder(TournamentTemplateParam tournamentTemplateParam);

    /**
     * 获取预约投注速率,如果失败赋值默认值
     */
    Integer getOrderRateLimit(Long matchId,Integer matchType);


    /**
     * 获取模板和玩法参数数据
     *
     * @param tournamentTemplateParams
     * @return
     */
    RcsTournamentTemplate getTournamentTemplate(TournamentTemplateParam tournamentTemplateParams);

    List<RcsTournamentTemplate> getTournamentTemplateList();

}
