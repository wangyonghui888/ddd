package com.panda.sport.rcs.gts.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * gts 获取token 传参vo
 * @author z9-lithan
 * @date 2023-01-06 15:21:20
 */
@Data
public class GtsAuthorizationReqVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 固定 client_credentials
     */
    private String grantType;
    /**
     *  平台id
     */
    private String clientId;
    /**
     *  平台秘钥
     */
    private String clientSecret;


}
