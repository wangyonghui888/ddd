package com.panda.sport.rcs.gts.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author :  lithan
 * @Description :  gts参数
 * @Date: 2023-01-06 16:52:52
 */
@Component
@ConfigurationProperties(prefix = "gts")
@Data
public class GtsInitConfig {

    /**
     * 投注验证接口apikey
     */
    private String betAssessmentApiKey;

    /**
     * 投注验证接口url
     */
    private String betAssessmentUrl;

    /**
     * 投注验证接口 gts参数 平台客户id
     */
    private String betAssessClientId;
    /**
     * 投注验证接口 gts参数平台秘钥
     */
    private String betAssessClientCecret;

    /**
     * 投注确认接口apikey
     */
    private String betReceiverApiKey;
     /**
     * 投注确认接口  url
     */
    private String betReceiverApiUrl;
    /**
     * 投注确认接口gts参数 平台客户id
     */
    private String betReceiverClientId;
    /**
     * 投注确认接口gts参数平台秘钥
     */
    private String betReceiverClientCecret;

    /**
     * 投注确认接口 gts参数 固定client_credentials
     */
    private String grantType;

    /**
     * 投注确认接口 gts参数 固定client_credentials
     */
    private String tokenUrl;
}
