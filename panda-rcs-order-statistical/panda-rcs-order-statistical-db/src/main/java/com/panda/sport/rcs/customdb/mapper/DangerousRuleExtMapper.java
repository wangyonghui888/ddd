package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.common.vo.rule.DangerousR4Vo;
import com.panda.sport.rcs.common.vo.rule.OrderDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 统计危险投注规则相关数据 Mapper 接口
 * </p>
 *
 * @author
 * @since 2020-07-10 09:19:03
 */

public interface DangerousRuleExtMapper {

    /**
     * @param beginTime  时间范围 事件开始时间
     * @param endTime    时间范围 事件结束时间
     * @param matchId    标准赛事id
     * @param eventCodes 事件编码 用逗号隔开
     * @return
     */
    Long getEventNum(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("matchId") Long matchId, @Param("eventCodes") String eventCodes);

    /**
     * @param orderNo 订单号
     * @return
     */
    Integer getOrderRiskStatus(@Param("orderNo") String orderNo);

    /**
     * 篮球打洞
     *
     * @param matchId 标准赛事id
     * @return
     */
    List<DangerousR4Vo> getBasketball(@Param("matchId") Long matchId, @Param("playId")Integer playId,@Param("userId")Long userId );



    /**
     * 获取相同投注项的注单
     *
     * @param matchId
     * @param playId
     * @param playOptionsId
     * @return
     */
    List<OrderDetailVo> getOrderByPlayOptions(@Param("matchId") Long matchId, @Param("playId") Integer playId, @Param("playOptionsId") Long playOptionsId, @Param("userId") Long userId);

    /**
     * 获取相同投注项的注单
     *
     * @param matchId
     * @return
     */
    List<OrderDetailVo> getOrderByMatchId(@Param("matchId") Long matchId, @Param("userId") Long userId);

    /**
     * 获取时间段内的注单
     * @param beginTime
     * @param endTime
     * @return
     */
    List<OrderDetailVo> getOrderByBetTime(@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);


}
