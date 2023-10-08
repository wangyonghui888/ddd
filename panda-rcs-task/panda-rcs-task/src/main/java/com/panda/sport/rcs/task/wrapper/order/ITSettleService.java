package com.panda.sport.rcs.task.wrapper.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.TSettle;
import com.panda.sport.rcs.pojo.report.CalcSettleItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 结算表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-12-24
 */
public interface ITSettleService extends IService<TSettle> {
    public List<CalcSettleItem> getCustomizedOrderList( Long beginTime,  Long endTime,Integer start,Integer limit);

    public Long getCountCustomizedOrder( Long beginTime,Long endTime);

    List<TSettle>  selectByMatchId(Long matchId);
}
