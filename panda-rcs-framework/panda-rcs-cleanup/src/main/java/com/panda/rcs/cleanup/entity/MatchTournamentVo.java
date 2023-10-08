package com.panda.rcs.cleanup.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchTournamentVo implements Serializable {

    private Long matchId;

    private Long templateId;

}
