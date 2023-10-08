package com.panda.sport.rcs.data.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description   联赛玩法margin配置表
 * @Param 
 * @Author  holly
 * @Date  20:02 2020/5/10
 * @return 
 **/
@Mapper
@Component
public interface RcsTournamentTemplatePlayMargainMapper extends BaseMapper<RcsTournamentTemplatePlayMargain> {

    List<RcsTournamentTemplatePlayMargain> selectByPlayId(@Param("matchId") Long matchId, @Param("playId") Integer playId);

}