package com.panda.sport.rcs.third.entity.redCat;

import lombok.Data;

/**
 * auth token 存储类
 * @author vere
 * @date 2023-05-24
 * @version 1.0.0
 */
@Data
public class RedCatAuthorizationVo {

    /**
     * Token
     */
    private String accessToken;
    /**
     *  过期时间
     */
    private Long expiresIn;
    /**
     *  固定 Bearer
     */
    private String tokenype;
}
