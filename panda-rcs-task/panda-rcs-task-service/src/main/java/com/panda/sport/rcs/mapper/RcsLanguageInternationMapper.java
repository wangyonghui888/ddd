package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.vo.ConditionVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface RcsLanguageInternationMapper extends BaseMapper<RcsLanguageInternation> {
    /**
     * 获取玩法列表
     * @return
     */
    List<ConditionVo> getMarketCategoryList();




    /**
     * @Description   //根据赛事id查询对阵球队名称
     * @Param [matchId]
     * @Author  Sean
     * @Date  19:11 2020/7/31
     * @return java.util.List<com.panda.sport.rcs.pojo.LanguageInternation>
     **/
    List<Map<String,String>> queryTeamNameByMatchId(@Param("matchId") Long matchId);
    /**
     * @Description   //根据赛事id查询联赛名称
     * @Param [matchId]
     * @Author  Sean
     * @Date  19:12 2020/7/31
     * @return java.util.List<com.panda.sport.rcs.pojo.LanguageInternation>
     **/
    List<Map<String,String>> queryTournamentNameByMatchId(@Param("matchId") Long matchId);
}