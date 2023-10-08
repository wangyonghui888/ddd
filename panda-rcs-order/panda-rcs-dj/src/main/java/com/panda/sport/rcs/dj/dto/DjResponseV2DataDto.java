package com.panda.sport.rcs.dj.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DjResponseDto
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/19 20:33
 * @Version 1.0
 **/
@Data
public class DjResponseV2DataDto implements Serializable {


    private static final long serialVersionUID = -8359425443379431184L;
    //最大赔付金额
    private Integer max_prize;
    //当日剩余的派彩金额，当派彩金额不足时，电竞客户端有相应的提示，体育那边没这个要求，可以忽略这个参数
    private Integer win_balance;

    //数据对象
    private List<DjResponseV2ListDto> list;


}
