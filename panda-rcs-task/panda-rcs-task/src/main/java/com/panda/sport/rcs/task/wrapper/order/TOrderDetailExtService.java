package com.panda.sport.rcs.task.wrapper.order;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.TOrderDetailExt;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.wrapper
 * @Description :  订单明细扩展
 * @Date: 2020-01-31 11:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface TOrderDetailExtService  extends IService<TOrderDetailExt> {
    /**
     * @Description   修改handleStatus状态
     * @Param [orderStatus, ids]
     * @Author  toney
     * @Date  14:22 2020/4/7
     * @return int
     **/
    int updateHandleStatusByList(@Param("handleStatus") Integer handleStatus, @Param("ids") List<Long> ids);

    List<TOrderDetailExt> selectHandleOrderList();
}
