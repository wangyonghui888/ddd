package com.panda.sport.rcs.predict.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.sport.rcs.predict.vo
 * @Description :  TODO
 * @Date: 2022-03-20 15:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class PredictDto implements Serializable {

    private Integer playId;

    /**
     * 赛事编号
     */
    private Long matchId;
    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    private Integer matchType;

    /**
     * 运动种类编号
     */
    private Integer sportId;
    /**
     * 子玩法标识
     */
    private String subPlayId;

    /**
     * 盘口值
     */
    private String dataTypeValue;


    private Integer seriesType;
}
