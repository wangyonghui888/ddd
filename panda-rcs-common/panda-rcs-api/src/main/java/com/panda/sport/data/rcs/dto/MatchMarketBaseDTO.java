package com.panda.sport.data.rcs.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 实时盘口赔率基础类
 */
@Data
public class MatchMarketBaseDTO implements Serializable {
    private Map<String, String> names;
    private Long nameCode;
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
}
