package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-09-05
 */
@Mapper
@Component
public interface RcsTournamentTemplateAcceptConfigMapper extends BaseMapper<RcsTournamentTemplateAcceptConfig> {
    RcsTournamentTemplateAcceptConfig selectOrderAcceptConfig(@Param("matchId") Long matchId, @Param("playSetId")Integer playSetId);
}
