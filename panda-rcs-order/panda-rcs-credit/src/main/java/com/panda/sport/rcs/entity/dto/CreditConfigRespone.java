package com.panda.sport.rcs.entity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.data.rcs.dto.credit.CreditSeriesConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSingleMatchConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSinglePlayBetConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditSinglePlayConfigDto;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreditConfigRespone implements Serializable {
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;
    private String creditId;
    /**
     * 单注限额，只有用户维度才有单注限额，代理维度没有
     */
    private List<CreditSinglePlayBetConfigDto> singlePlayBetConfigList;
    private List<CreditSinglePlayConfigDto> singlePlayConfigList;
    private List<CreditSingleMatchConfigDto> singleMatchConfigList;
    private List<CreditSeriesConfigDto> seriesConfigList;
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
}
