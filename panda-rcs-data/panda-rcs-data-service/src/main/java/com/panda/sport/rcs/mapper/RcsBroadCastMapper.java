package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsBroadCast;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  enzo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  预警消息
 * @Date: 2020-09-16 16:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsBroadCastMapper extends BaseMapper<RcsBroadCast> {
    /**
     * @Description  查询收藏赛事的玩家
     * @Param [matchId]
     * @Author  kimi
     * @Date   2020/10/23
     * @return java.util.List<java.lang.Integer>
     **/
    List<Integer> selectUserIdByCollection(@Param("matchId") Integer matchId);
    
    /**
     * @Description   //TODO
     * @Param [s]
     * @Author  kimi
     * @Date   2020/10/23
     * @return com.panda.sport.rcs.pojo.RcsBroadCast
     **/
    RcsBroadCast selectRcsBroadCast(@Param("s") String s);
    
    /**
     * @Description   //TODO
     * @Param [matchId]
     * @Author  kimi
     * @Date   2020/10/23
     * @return java.lang.Integer
     **/
    Integer selectSportIdByMatchId(@Param("matchId") Integer matchId);
}
