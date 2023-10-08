package com.panda.sport.rcs.entity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.data.rcs.dto.credit.CreditConfigDto;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;

@Data
public class CreditConfigHttpQueryDto extends CreditConfigDto {

    /**
     * 用户Id
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 是否初始化用户特殊限额配置  hasInitSpecialFlag
     */
    Boolean hasInitSpecialFlag = Boolean.FALSE;

    /**
     * 操作人IP
     */
    private String ip;
}
