package com.panda.sport.data.rcs.dto.trade;

import java.io.Serializable;

import lombok.Data;

/**
 * @Description : 用户id
 * @Author : jordan
 * @Date : 2022-04-22 13:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class UserIdDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

}