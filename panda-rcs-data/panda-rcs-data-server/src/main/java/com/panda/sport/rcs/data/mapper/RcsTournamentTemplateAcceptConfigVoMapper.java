package com.panda.sport.rcs.data.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfigVo;
import feign.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-09-05
 */
public interface RcsTournamentTemplateAcceptConfigVoMapper extends BaseMapper<RcsTournamentTemplateAcceptConfigVo> {

    List<RcsTournamentTemplateAcceptConfigVo> selectAcceptConfig(@Param("vo") RcsTournamentTemplateAcceptConfigVo template);
}
