package com.panda.sport.data.rcs.dto.dj;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DJLimitAmoutRequest
 * @Description TODO
 * @Author zerone
 * @Date 2021/9/16 17:06
 * @Version 1.0
 **/
@Data
public class DJLimitAmoutRequest implements Serializable {

    //用户名
    private String username ;

    //用户id
    private Long userId;

    //用户类型 0:正常账号,1:测试账号
    private String tester;

    //商户id
    private Long  merchant;

    //投注项集合
    private List<Selection> selectionList;
    /**
     * 串关类型(单关传1)
     */
    private Integer seriesType;
}
