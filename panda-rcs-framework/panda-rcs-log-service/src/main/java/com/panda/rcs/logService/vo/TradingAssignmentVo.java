package com.panda.rcs.logService.vo;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * @program: xindaima
 * @description:指派数据
 * @author: kimi
 * @create: 2020-11-06 17:21
 **/
@Data
public class TradingAssignmentVo {

    private Long setNo;

    private Long typeId;

    /**
     * 国际化
     */
    private Map setNames;

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 运动种类id。 对应表 sport.id
     */
    private Long sportId;

    /**
     * 0:滚球 1:早盘
     */
    private Integer marketType;

    List<TradingAssignmentSubPlayVo> sysTraderWeightList;

}
