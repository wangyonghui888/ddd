package com.panda.sport.rcs.pojo.tourTemplate;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * rcs_tournament_template_ref
 * @author 
 */
@Data
public class RcsTournamentTemplateRef implements Serializable {
    private Long id;

    /**
     * 联赛Id
     */
    private Long tournamentId;

    /**
     * 联赛模板Id
     */
    private Long tournamentTemplateId;

    /**
     * 创建时间
     */
    private Date crateTime;

    /**
     * 更改时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}