package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsPreOrderDetailExt;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Eamon
 * @since 2023年7月10日15:31:37
 */
@Service
public interface RcsPreOrderDetailExtMapper extends BaseMapper<RcsPreOrderDetailExt> {

    /**
    * 修改提前结算订单状态
     * @param order 提前结算注单实体
    */
    void updateOrderStatus(RcsPreOrderDetailExt order);

}
