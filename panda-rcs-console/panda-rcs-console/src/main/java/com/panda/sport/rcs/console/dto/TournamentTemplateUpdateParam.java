package com.panda.sport.rcs.console.dto;

import groovy.transform.EqualsAndHashCode;
import lombok.Data;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TournamentTemplateUpdateParam extends RcsTournamentTemplate {
    /**
     * 传给融合的操盘模式  PA or MTS
     */
    private String riskManagerCode;
    /**
     * @Description 玩法margain配置
     * @Param
     * @Author toney
     * @Date 19:48 2020/5/12
     * @return
     **/
    private List<TournamentTemplatePlayMargainParam> playMargainList;

    /**
     * 操作類別編碼
     */
    private Integer operatePageCode;

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     *  赛事管理id
     */
    private String matchManageId;
}
