package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;

/**
 * gts 获取tokenvo
 * @author z9-lithan
 * @date 2023-01-06 15:21:20
 */
@Data
public class GtsAuthorizationVo implements Serializable {

    private static final long serialVersionUID = 1L;
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

    /**
     *  两种token  1 Bet Assessment  API  2 Bet Feed Receiver API
     *  com.panda.sport.rcs.third.enums.TokenTypeEnum
     */
    private int type;
    /**
     *  数据商类型(com.panda.sport.rcs.third.enums.ThirdTokenTypeEnum 枚举类)
     */
    private int third;

    /**
     * 刷新的时间点
     */
    private long refreshTime;


}
