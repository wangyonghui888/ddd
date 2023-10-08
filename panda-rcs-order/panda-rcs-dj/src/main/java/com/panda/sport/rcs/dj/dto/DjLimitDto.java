package com.panda.sport.rcs.dj.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName DjLimitDto
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/21 17:49
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DjLimitDto implements Serializable {
    private static final long serialVersionUID = 4780027070964464612L;

    /**
     * 最低投注
     */
    private String min_bet;

    /**
     * 最高投注
     */
    private String max_bet;

    /**
     * 最高赔付
     */
    private String max_prize;
}
