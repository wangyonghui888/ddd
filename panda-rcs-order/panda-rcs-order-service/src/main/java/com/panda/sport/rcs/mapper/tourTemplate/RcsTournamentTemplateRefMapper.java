package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateRef;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RcsTournamentTemplateRefMapper extends BaseMapper<RcsTournamentTemplateRef> {
    int deleteByPrimaryKey(Long id);

    int insert(RcsTournamentTemplateRef record);

    int insertSelective(RcsTournamentTemplateRef record);

    RcsTournamentTemplateRef selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RcsTournamentTemplateRef record);

    int updateByPrimaryKey(RcsTournamentTemplateRef record);

    Integer queryDefaultTime(@Param("matchId") Long matchId);
}