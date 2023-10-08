package com.panda.sport.rcs.mgr.operation.vo;

import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.Data;

import java.io.Serializable;

@Data
public class PredictSettleOrderMqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    SettleItem settleItem;
    TOrderDetail orderDetail;
}
