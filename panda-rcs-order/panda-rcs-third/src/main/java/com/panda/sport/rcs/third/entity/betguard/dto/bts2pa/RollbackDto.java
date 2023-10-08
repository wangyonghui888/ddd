package com.panda.sport.rcs.third.entity.betguard.dto.bts2pa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.panda.sport.rcs.third.entity.betguard.dto.BetGuardBaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/4/15 20:38
 * @description todo
 */

@Data
public class RollbackDto extends BetGuardBaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "交易唯一ID")
    @JsonProperty("TransactionId")
    private Long TransactionId;
    @JsonProperty("BetId")
    private Long BetId;

}
