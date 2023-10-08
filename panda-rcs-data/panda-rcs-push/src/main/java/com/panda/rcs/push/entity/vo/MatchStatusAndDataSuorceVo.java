package com.panda.rcs.push.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class MatchStatusAndDataSuorceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String linkId;

    /**
     * 121-出涨预警标志
     */
    private Integer function;

    /**
     * 赛种
     */
    private Long sportId;

    /**
     * 赛事Id
     */
    private Long matchId;

    /**
     * 0-自动，1-手动，3-自动+手动
     */
    private List<Integer> matchDataSource;

    /**
     * 等级，1-赛事，2-玩法，3-盘口位置，4-玩法集
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
     * @see com.panda.sport.rcs.enums.TradeEnum
     */
    private Integer dataSource;

    /**
     * 操盘类型 开关封锁
     *
     * @see com.panda.sport.rcs.enums.TradeStatusEnum
     */
    private Integer status;

    /**
     * 操盘平台 MTS ，PA
     */
    private String riskManagerCode;

    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 子玩法ID
     */
    private Long subPlayId;

    /**
     * 玩法集编码
     */
    private String playSetCode;

    /**
     * 水差是否关联，0-不关联，1-关联
     */
    private Integer relevanceType;

    /**
     * 占位符玩法总开关封锁
     * key=playId,value=playStatus
     */
    private Map<String, Integer> mainPlayStatusMap;

    /**
     * 出涨预警标志
     * key=matchId/playId_subPlayId
     * value=1/0
     */
    private Map<String, String> chuZhangWarnSignMap;
}
