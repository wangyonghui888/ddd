package com.panda.sport.rcs.task.wrapper.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.vo.OrderSummaryVo;

import java.util.List;

/**
 * <p>
 * 投注单详细信息表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
public interface ITOrderDetailService extends IService<TOrderDetail> {
    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrderDetail>
     * @Description 按ID升序取topX条数据
     * @Param [id, matchId, limit]
     * @Author toney
     * @Date 11:47 2020/1/27
     **/
    List<TOrderDetail> getTopById(Long id, Long matchId, Integer limit);

    /**
     * 查询平衡值明细
     *
     * @param matchId
     * @param marketCategoryId
     * @return
     */
    List<OrderDetailStatReportVo> getMarketStatByMatchIdAndPlayId(Long matchId, Long marketCategoryId, Integer matchType);

    /**
     * @return java.util.List<java.lang.Long>
     * @Description //TODO
     * @Param [sportId, beginTime, matchType]
     * @Author kimi
     * @Date 2020/7/7
     **/
    List<OrderSummaryVo> getTOrderDetailUpBetTime(Long sportId, Integer matchType, List<Long> oddsId);

    /**
     * @return java.util.List<java.lang.Long>
     * @Description //TODO
     * @Param []
     * @Author kimi
     * @Date 2020/7/15
     **/
    List<Long> getOddsId(Long sportId, Long beginTime, Long endTime);
}
