package com.panda.sport.rcs.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.mongo.I18nBean;
import com.panda.sport.rcs.pojo.StandardSportTeam;

import java.util.Map;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 标准球队信息
 * @Author : Paca
 * @Date : 2020-11-22 10:19
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface StandardSportTeamService extends IService<StandardSportTeam> {
    /**
     * 查询球队信息
     *
     * @param matchId
     * @return
     */
    Map<String, I18nBean> selectTeamsByMatchId(Long matchId);
}
