package com.panda.sport.rcs.task.mq.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class MatchCategoryUpdateBean implements Serializable {

    private Long sportId;

    private Long matchId;
    /**
     * 玩法集ID
     */
    private Long cateCollId;

    private List<Long> categoryIds;

    public MatchCategoryUpdateBean(Long sportId, Long matchId) {
        this.sportId = sportId;
        this.matchId = matchId;
    }
}
