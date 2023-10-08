package com.panda.sport.rcs.repository;

import com.panda.sport.rcs.pojo.TOrderDetailExt;
import com.panda.sport.rcs.pojo.TOrderDetailExtDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

public interface TOrderDetailExtRepository {

    TOrderDetailExtDO queryUnHandleOrderExt();

    TOrderDetailExtDO queryLatestOrderExt();

    List<TOrderDetailExtDO> selectWaitedOrderList(Map<String, Object> params);

    Integer updateOrderDetailExtStatusByOrderNo(TOrderDetailExtDO tOrderDetailExt);

    List<TOrderDetailExtDO> getOrderDetailExtByIds(List<String> ids);

    Integer updateIgnorePauseOrder(TOrderDetailExtDO tOrderDetailExt);

}
