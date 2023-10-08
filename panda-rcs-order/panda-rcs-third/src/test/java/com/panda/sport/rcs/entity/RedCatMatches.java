package com.panda.sport.rcs.entity;

import lombok.Data;

import java.util.List;

/**
 * 红猫赛事
 */
@Data
public class RedCatMatches {
    private Integer matchId;
    private List<RedCatMarks> markets;
}
