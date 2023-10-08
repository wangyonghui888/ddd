package com.panda.sport.rcs.dj.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DjResponseDto
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/19 20:33
 * @Version 1.0
 **/
@Data
public class DjResponseV2ListDto implements Serializable {


    private static final long serialVersionUID = -8359425443379431184L;
    //赛事ID，对应投注时的match_id(赛事串关参与底下的说明文字)
    private String match_id;
    //最小投注金额
    private Integer min_bet;
    //最大投注金额
    private Integer max_bet;


}
