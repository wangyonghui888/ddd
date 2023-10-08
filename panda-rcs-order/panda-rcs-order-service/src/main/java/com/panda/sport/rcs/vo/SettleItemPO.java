package com.panda.sport.rcs.vo;

import com.panda.sport.data.rcs.dto.SettleItem;
import lombok.Data;

import java.util.List;

/**
 * @program: 结算扩展类
 * @description:
 * @author: skyKong
 * @create:
 **/
@Data
public class SettleItemPO extends SettleItem {
    /**
     * 订单限额类型
     * */
    private Integer limitType;
}
