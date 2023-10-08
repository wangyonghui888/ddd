package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsOrderSummary;
import com.panda.sport.rcs.vo.OrderSummaryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-07-07 17:33
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsOrderSummaryMapper extends BaseMapper<RcsOrderSummary> {
    /**
     * @return void
     * @Description
     * @Param [orderSummaryVo]
     * @Author kimi
     * @Date 2020/7/7
     **/
    void insertOrUpdateOddsValueMax(@Param("orderSummaryVos") Collection<OrderSummaryVo> orderSummaryVos);


    void updateOrInsertOrOddsValueMax(@Param("rcsOrderSummaries") List<RcsOrderSummary> rcsOrderSummaries, @Param("sportId") Long sportId,
                                      @Param("matchId") Long matchId, @Param("playId") Long playId, @Param("marketId") Long marketId);
}
