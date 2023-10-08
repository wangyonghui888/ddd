package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;

/**
 * gts Bet Receive api  GtsBetgeniusContent 对象
 * @author z9-lithan
 * @date 2023-01-07 16:31:22
 */
@Data
public class GtsBetGeniusContentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  运动id
     */
    private String sportId;

    /**
     *  运动名称
     */
    private String sportName;

    /**
     *  联赛id
     */
    private String competitionId;

    /**
     *  联赛名称
     */
    private String competitionName;

    /**
     *  赛事id
     */
    private String fixtureId;

    /**
     *  赛事名称
     */
    private String fixtureName;

    /**
     *  赛事开始时间
     */
    private String fixtureStartTimeUTC;

    /**
     *  盘口id
     */
    private String marketId;

    /**
     *  盘口名称
     */
    private String marketName;

    /**
     *  投注项id
     */
    private String selectionId;

    /**
     *  投注项名称
     */
    private String selectionName;

}

