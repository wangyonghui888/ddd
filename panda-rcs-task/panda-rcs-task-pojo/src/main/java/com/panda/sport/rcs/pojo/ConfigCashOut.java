package com.panda.sport.rcs.pojo;

import com.panda.merge.dto.ConfigCashOutTradeItemDTO;
import lombok.Data;

@Data
public class ConfigCashOut extends ConfigCashOutTradeItemDTO {
    /**
     * 数据商是否支持提前结算
     * ＆关系，现存盘口只要一个盘口满足
     */
    private Integer cashOutStatus;

    private Integer pendingOrderStatus;
}
