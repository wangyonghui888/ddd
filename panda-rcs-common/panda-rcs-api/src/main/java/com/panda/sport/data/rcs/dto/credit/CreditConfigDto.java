package com.panda.sport.data.rcs.dto.credit;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用模式配置
 * @Author : Paca
 * @Date : 2021-04-27 15:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class CreditConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * merchantId = null && creditId = null    查询模板配置
     * merchantId != null && creditId = null   查询商户配置
     * merchantId != null && creditId != null  查询信用代理配置
     */
    /**
     * 商户ID，为空查询模板配置
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;

    /**
     * 信用代理ID，为空查询模板配置
     */
    private String creditId;

    /**
     * 信用代理名称
     */
    private String creditName;

    /**
     * 单注限额，只有用户维度才有单注限额，代理维度没有
     */
    private List<CreditSinglePlayBetConfigDto> singlePlayBetConfigList;

    /**
     * 玩法限额
     */
    private List<CreditSinglePlayConfigDto> singlePlayConfigList;

    /**
     * 单场限额，只有代理维度才有单场限额，用户维度没有
     */
    private List<CreditSingleMatchConfigDto> singleMatchConfigList;

    /**
     * 串关限额
     */
    private List<CreditSeriesConfigDto> seriesConfigList;
}
