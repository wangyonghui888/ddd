package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.dto.MatchHomeAwayDTO;
import com.panda.sport.rcs.console.dto.StandardMatchInfoDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;
import java.util.Map;


@Repository
public interface StandardMatchInfoDTOMapper extends MyMapper<StandardMatchInfoDTO> {
    /**
     * @return com.panda.sport.rcs.console.dto.StandardMatchInfo
     * @Description 查询赛事基本信息、开售信息、盘口信息
     * @Param [matchInfo]
     * @Author Sean
     * @Date 14:33 2020/3/11
     **/
    StandardMatchInfoDTO queryMatchAndMarketInfo(@Param("matchInfo") StandardMatchInfoDTO matchInfo);

    /**
     * @return java.util.List<java.lang.String>
     * @Description 查询对阵双方信息
     * @Param [matchInfo]
     * @Author Sean
     * @Date 17:47 2020/3/11
     **/
    List<MatchHomeAwayDTO> queryMatchTeams(@Param("matchId") String matchId);

    /**
     * @return void
     * @Description //TODO
     * @Param [params]
     * @Author kimi
     * @Date 2020/7/26
     **/
    void setAddNewBusinessData(@Param("params") Map<String, Object> params);
}