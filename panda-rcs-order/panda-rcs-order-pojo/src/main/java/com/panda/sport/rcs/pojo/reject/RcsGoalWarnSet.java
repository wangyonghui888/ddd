package com.panda.sport.rcs.pojo.reject;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
public class RcsGoalWarnSet extends RcsBaseEntity<RcsGoalWarnSet> {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Date createTime;

    private Date updateTime;

    private Long standardTournamentId;

    private Long standardMatchId;

    private Long standardTeamId;

    private Integer betUserNum;

    private Integer maxAmount;

    private Integer beforeGoalSeconds;

    private String userId;

    private String userName;
}
