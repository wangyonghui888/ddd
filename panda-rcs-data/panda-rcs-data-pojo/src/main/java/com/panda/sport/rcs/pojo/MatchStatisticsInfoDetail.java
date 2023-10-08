package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.panda.sport.rcs.pojo.vo.WsScoreVO;
import lombok.Data;

@Data
public class MatchStatisticsInfoDetail {
    private Long id;

    /**
    * 标准赛事id
    */
    private Long standardMatchId;

    //不再使用
//    private Long matchStatisticsInfoId;

    private String code;

    private Integer firstNum;

    private Integer secondNum;

    private Integer t1;

    private Integer t2;

    private Long createTime;

    private Long modifyTime;

    private Integer period;

    @TableField(exist = false)
    private WsScoreVO scoreVO;

    @TableField(exist = false)
    private Integer times;

}