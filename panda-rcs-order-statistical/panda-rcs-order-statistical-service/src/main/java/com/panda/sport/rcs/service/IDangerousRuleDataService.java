package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.vo.rule.DangerousR4Vo;
import com.panda.sport.rcs.common.vo.rule.OrderDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 危险投注  查询数据专用Service
 *
 * @author :  lithan
 * @date: 2020-07-03 11:24:13
 */
public interface IDangerousRuleDataService {

    /**
     * 检查投注时间之后的@second秒内 是否有指定的事件
     *
     * @param beginTime  时间范围 事件开始时间
     * @param endTime    时间范围 事件结束时间
     * @param matchId    标准赛事id
     * @param eventCodes 事件编码 用逗号隔开
     */
    public Long getEventNum(Long beginTime, Long endTime, Long matchId, String eventCodes);

    /**
     * 获取注单状态
     *
     * @param orderNo 订单号
     * @return
     */
    public Integer getOrderRiskStatus(@Param("orderNo") String orderNo);

    /**
     * 篮球打洞判断
     *
     * @param matchId
     * @return
     */

    List<DangerousR4Vo> getBasketball(Long matchId, Integer playId, Long userId);

    /**
     * 获取用户相同投注项的注单
     *
     * @param matchId
     * @param playId
     * @param playOptionsId
     * @return
     */
    List<OrderDetailVo> getOrderByPlayOptions(Long matchId, Integer playId, Long playOptionsId, Long userId);

    /**
     * 获取用户相同赛事的注单
     *
     * @param matchId
     * @return
     */
    List<OrderDetailVo> getOrderByMatchId(Long matchId, Long userId);


}
