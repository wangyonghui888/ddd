package com.panda.sport.rcs.entity;

import lombok.Data;

import java.util.List;

/**
 * 红猫赛事结果
 * @author vere
 * @date 2023-06-06
 * @version 1.0.0
 *
 */
@Data
public class RedCatMatchResult {
    private List<RedCatMatches> data;
}
