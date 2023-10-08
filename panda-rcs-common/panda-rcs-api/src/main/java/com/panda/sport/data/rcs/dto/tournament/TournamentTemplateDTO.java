package com.panda.sport.data.rcs.dto.tournament;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author carver
 */
@Data
public class TournamentTemplateDTO implements Serializable {
    /**
     * 赛种
     */
    private Integer sportId;
    /**
     * 联赛id
     */
    private Long tournamentId;
    /**
     * 1：早盘；0：滚球
     */
    private Integer matchType;
    /**
     * 是否使用当前联赛等级模板(1:是 0:否)
     */
    private Integer isCurrentTemp;
    /**
     * 模板id
     */
    private Long templateId;
    /**
     * 标准赛事id
     */
    private Long standardMatchId;
    /**
     * 联赛等级
     */
    private Integer tournamentLevel;
    /**
     * 操盘平台
     */
    private String riskManagerCode;
    
    /**
     * 联赛id
     */
    private List<Long> tournamentIds;
    
    /**
     * 数据源编码
     */
    private String dataSourceCode;
}
