package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description:
 * @date 2019/9/26 17:59
 * @ModificationHistory Who    When    What
 */
@Data
public class StandardMatchTeamRelationDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 比赛中的作用.主客队或者其他.home:主场队;away:客场队
     */
    private String matchPosition;

    /**
     * 备注
     */
    private String remark;
}
