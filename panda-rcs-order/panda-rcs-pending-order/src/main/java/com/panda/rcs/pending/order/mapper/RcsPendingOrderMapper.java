package com.panda.rcs.pending.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.pending.order.pojo.RcsPendingOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RcsPendingOrderMapper extends BaseMapper<RcsPendingOrder> {

    List<RcsPendingOrder> selectPendingOrderList(@Param("matchId") Long matchId, @Param("limit") Integer limit);
}
