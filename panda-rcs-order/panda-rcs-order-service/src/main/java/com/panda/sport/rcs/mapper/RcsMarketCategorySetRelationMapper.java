package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.PreOrderDetailRequest;
import com.panda.sport.data.rcs.vo.MatchEventInfo;
import com.panda.sport.rcs.pojo.RcsMarketCategorySetRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2019-09-11 15:11
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface RcsMarketCategorySetRelationMapper  extends BaseMapper<RcsMarketCategorySetRelation> {
    /**
     * @Description   //根据玩法查询风控玩法集
     * @Param [orderItem]
     * @Author  Sean
     * @Date  19:36 2020/9/5
     * @return java.lang.Integer
     **/
    String queryCategorySetByPlayId(@Param("item") OrderItem orderItem);

    String queryCategorySetByPlayId(@Param("item") PreOrderDetailRequest orderItem);

    MatchEventInfo queryMatchEventInfo(@Param("matchId") Long matchId);

    String queryMatchPeriodInfo(@Param("matchId") Long matchId);
}
