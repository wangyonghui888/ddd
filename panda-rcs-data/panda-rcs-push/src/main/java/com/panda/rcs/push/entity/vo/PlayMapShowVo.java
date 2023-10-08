package com.panda.rcs.push.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayMapShowVo implements Serializable {

    private int sportId;

    private String categorySetId;

    private int clientShow;

    private int liveOdds;

    private String matchId;

}
