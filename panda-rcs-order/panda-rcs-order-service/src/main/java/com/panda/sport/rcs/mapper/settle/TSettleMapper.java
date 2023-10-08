package com.panda.sport.rcs.mapper.settle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.pojo.report.CalcSettleItem;
import com.panda.sport.rcs.pojo.report.MinDates;
import com.panda.sport.rcs.pojo.settle.TSettle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 结算表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-07
 */
@Mapper
public interface TSettleMapper extends BaseMapper<TSettle> {

    Long getCountCustomizedOrder(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    List<CalcSettleItem> getCustomizedOrderList( @Param("beginTime") Long beginTime, @Param("endTime") Long endTime,@Param("start") Integer start,@Param("limit") Integer limit);

    MinDates getMinDates(@Param("startTime") Long startTime);

    void updateTSettleToOrderStatic(@Param("list") CalcSettleItem list);

    IPage<Long> selectIdByDate(Page<Long> param,@Param("startDate") Long startDate,@Param("endDate") Long endDate);

    void updateSettleStatusByIds(@Param("list") List<Long> list);



    List<TSettle>  selectByMatchId(Long matchId);



    int insertOrUpdate(TSettle settle);

    /**
     * @Author toney
     * @Date 2020/12/10 上午 10:34
     * @Description 更新状态
     * @param orderNo
     * @param operateStatus
     * @param operateTime
     * @Return int
     * @Exception
     */
    int updateOperateStatus(@Param("orderNo") String orderNo,@Param("operateStatus")Integer operateStatus,@Param("operateTime")Long operateTime);
}
