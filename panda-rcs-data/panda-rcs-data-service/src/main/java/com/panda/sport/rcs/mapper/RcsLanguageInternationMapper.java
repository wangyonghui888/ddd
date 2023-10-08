package com.panda.sport.rcs.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;

import java.util.List;
import java.util.Map;

@Component
public interface RcsLanguageInternationMapper extends BaseMapper<RcsLanguageInternation> {

    /**
     * 根据namecode获取多语言信息
     * @param nameCode
     * @return
     */
    String getPlayLanguageByNamecode(@Param("nameCode") String nameCode);



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
    Map<String,String> queryTournamentNameByMatchId(@Param("matchId") Long matchId);
}