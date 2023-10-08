package com.panda.sport.rcs.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2019-11-19 19:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TournamentNameVo {
    /**
     * @Description //赛事id
     * @return
     **/
    private Long id;
    /**
     * @Description //联赛名字
     * @return
     **/
    private String text;

    /**
     * @Description //战队名字
     * @return
     **/
    private List<TeamVo> teamVoArrayList = new ArrayList<>();
    /**
     * @Description //赛事比分
     * @return
     **/
    private String score;
    /**
     * @Description //比赛进行了多久
     * @return
     **/
    private Integer secondsMatchStart = 0;
    /**
     * @Description //开赛时间
     * @return
     **/
    private Long beginTime;
    /**
     * @Description //联赛id
     * @return
     **/
    private Long standardTournamentId;
    /**
     * 标准赛事编码. 用于管理的赛事id
     */
    private String matchManageId;
    /**
     * 比赛阶段id. 取自基础表 : match_status.id
     */
    private Long period;
}
