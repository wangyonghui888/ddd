package com.panda.sport.rcs.service.impl;

import com.panda.sport.rcs.common.vo.rule.DangerousR4Vo;
import com.panda.sport.rcs.common.vo.rule.OrderDetailVo;
import com.panda.sport.rcs.customdb.mapper.DangerousRuleExtMapper;
import com.panda.sport.rcs.service.IDangerousRuleDataService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 危险投注 查询数据专用Service
 *
 * @author :  lithan
 * @date: 2020-07-03 11:24:59
 */
@Service
public class DangerousRuleDataServiceImpl implements IDangerousRuleDataService {

    @Autowired
    DangerousRuleExtMapper dangerousRuleExtMapper;

    /**
     * 检查投注时间之后的@second秒内 是否有指定的事件
     *
     * @param beginTime  时间范围 事件开始时间
     * @param endTime    时间范围 事件结束时间
     * @param matchId    标准赛事id
     * @param eventCodes 事件编码 用逗号隔开
     * @return
     */
    @Override
    public Long getEventNum(Long beginTime, Long endTime, Long matchId, String eventCodes) {
        Long num = dangerousRuleExtMapper.getEventNum(beginTime, endTime, matchId, eventCodes);
        return num;
    }

    /**
     * orderNo 订单号
     *
     * @param orderNo
     * @return
     */
    @Override
    public Integer getOrderRiskStatus(String orderNo) {
        return dangerousRuleExtMapper.getOrderRiskStatus(orderNo);
    }

    /**
     * 篮球打洞判断
     *
     * @param matchId
     * @return
     */
    @Override
    public List<DangerousR4Vo> getBasketball(Long matchId,Integer playId, Long userId) {
        return dangerousRuleExtMapper.getBasketball(matchId,playId,userId);
    }

    /**
     * 获取用户相同投注项的注单
     *
     * @param matchId
     * @param playId
     * @param playOptionsId
     * @return
     */
    @Override
    public List<OrderDetailVo> getOrderByPlayOptions(Long matchId, Integer playId, Long playOptionsId, Long userId) {
        return dangerousRuleExtMapper.getOrderByPlayOptions(matchId, playId, playOptionsId, userId);
    }

    /**
     * 获取用户相同赛事的注单
     *
     * @param matchId
     * @param userId
     * @return
     */
    @Override
    public List<OrderDetailVo> getOrderByMatchId(Long matchId, Long userId) {
        return dangerousRuleExtMapper.getOrderByMatchId(matchId, userId);
    }


}
