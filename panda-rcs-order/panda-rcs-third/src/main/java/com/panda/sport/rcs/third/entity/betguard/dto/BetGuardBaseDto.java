package com.panda.sport.rcs.third.entity.betguard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/4/15 19:31
 * @description todo
 */

public class BetGuardBaseDto implements Serializable {

    //令牌
    @ApiModelProperty(value = "令牌")
    @JsonProperty("AuthToken")
    private String AuthToken;
    //时间戳
    @ApiModelProperty(value = "时间戳")
    @JsonProperty("TS")
    private long TS;
    //签名
    @ApiModelProperty(value = "签名")
    @JsonProperty("Hash")
    private String Hash;


    public String getAuthToken() {
        return AuthToken;
    }

    public long getTS() {
        return TS;
    }

    public String getHash() {
        return Hash;
    }

    public void setAuthToken(String AuthToken) {
        this.AuthToken = AuthToken;
    }

    public void setTS(long TS) {
        this.TS = TS;
    }

    public void setHash(String Hash) {
        this.Hash = Hash;
    }
}
