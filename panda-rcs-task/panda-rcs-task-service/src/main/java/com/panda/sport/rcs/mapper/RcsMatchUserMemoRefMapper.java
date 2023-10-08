package com.panda.sport.rcs.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchUserMemoRef;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RcsMatchUserMemoRefMapper extends BaseMapper<RcsMatchUserMemoRef> {

    @Select(" SELECT userInfo.id trader_id, memo.id memo_id, memo.standard_match_id standard_match_id FROM " +
            "rcs_sys_user userInfo,  rcs_match_trade_memo memo " +
            "LEFT JOIN rcs_standard_sport_market_sell sellInfo ON memo.standard_match_id = sellInfo.match_info_id " +
            "WHERE userInfo.logic_delete = '0' AND userInfo.enabled = '1' AND (sellInfo.pre_match_sell_status = 'Sold' OR sellInfo.live_match_sell_status = 'Sold') AND memo.id IS NOT NULL AND userInfo.id != memo.trader_id  ")
    List<RcsMatchUserMemoRef> getNeedRemindMemos();

    @Select("<script> SELECT memo_id, standard_match_id, trader_id FROM rcs_match_user_memo_ref " +
            " WHERE  read_status = '1' AND " +
            "standard_match_id IN <foreach item='item' index='index' collection='unreadMatchIds' open='(' separator=',' close=')'>#{item}</foreach> GROUP BY memo_id,trader_id </script> ")
    List<RcsMatchUserMemoRef> getReadMemoRefInfo(@Param("unreadMatchIds") List<Long> unreadMatchIds);
}