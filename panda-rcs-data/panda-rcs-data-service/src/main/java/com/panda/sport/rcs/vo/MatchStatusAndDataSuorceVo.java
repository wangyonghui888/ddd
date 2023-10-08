package com.panda.sport.rcs.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2020-02-19 13:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@Accessors(chain = true)
public class MatchStatusAndDataSuorceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 赛事Id
     */
    private Long matchId;

    /**
     * 0-自动，1-手动，3-自动+手动
     */
    private Integer matchDataSource;

    /**
     * 等级，1-赛事，2-玩法，3-盘口位置，4-玩法集
     *
     * @see com.panda.sport.rcs.enums.TraderLevelEnum
     */
    private Integer level;

    /**
     * Id，根据level取值，赛事ID、玩法ID、盘口位置、玩法集ID
     */
    private String id;

    /**
     * 玩法集下所有玩法ID，level=4-玩法集 时传值
     */
    private List<Long> categoryIdList;

    /**
     * 操盘类型  0是自动 1是手动
     *
     * @see com.panda.sport.rcs.enums.TradeTypeEnum
     */
    private Integer dataSource;

    /**
     * 操盘类型 开关封锁
     *
     * @see com.panda.sport.rcs.enums.MarketStatusEnum
     */
    private Integer status;

    /**
     * 操盘平台 MTS ，PA
     */
    private String riskManagerCode;

}
