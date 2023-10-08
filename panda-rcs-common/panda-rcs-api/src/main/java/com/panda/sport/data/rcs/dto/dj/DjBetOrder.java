package com.panda.sport.data.rcs.dto.dj;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DjBetOrder
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/21 22:25
 * @Version 1.0
 **/
@Data
public class DjBetOrder implements Serializable {
    private static final long serialVersionUID = 6916943873230755416L;

    /**
     * 投注金额
     */
    private Long amount;

    /**
     * 串关数量 单关就是1，三串1 就是3
     */
    private Integer num;

    /**
     * 注单类型[注单类
     * 型 1-普通注单
     * 2-普通串关注单
     * 3-局内串关注单,
     * 4-复合玩法注单
     */
    private Integer orderType;

    /**
     * 投注项集合
     */
    private List<Selection> selections;
}
