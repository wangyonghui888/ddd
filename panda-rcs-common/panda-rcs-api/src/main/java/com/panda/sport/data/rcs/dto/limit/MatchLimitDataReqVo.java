package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.dto.limit
 * @Description : 赛事限额请求入参
 * @Author : Paca
 * @Date : 2020-09-25 11:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchLimitDataReqVo implements Serializable {

    private static final long serialVersionUID = 4421884046177425425L;

    /**
     * 赛种
     */
    private Integer sportId;

    /**
     * 联赛等级
     */
    private Integer tournamentLevel;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 限额数据类型
     *
     * @see com.panda.sport.rcs.enums.limit.LimitDataTypeEnum
     */
    private List<Integer> dataTypeList;

    /**
     * 玩法信息
     */
    private Integer playId;
}
