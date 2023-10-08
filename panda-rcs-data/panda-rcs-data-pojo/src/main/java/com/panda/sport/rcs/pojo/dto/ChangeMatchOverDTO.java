package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class ChangeMatchOverDTO extends RcsBaseEntity<ChangeMatchOverDTO> {

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 体育种类
     */
    private Long sportId;

    /**
     * 比赛是否结束. 0: 未结束;  1: 结束. （比赛彻底结束, 双方不再有加时赛, 点球大战, 且裁判宣布结束）
     */
    private Integer matchOver;

}
