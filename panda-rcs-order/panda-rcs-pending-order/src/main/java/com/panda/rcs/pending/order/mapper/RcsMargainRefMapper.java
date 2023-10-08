package com.panda.rcs.pending.order.mapper;

import com.panda.rcs.pending.order.param.TournamentTemplateParam;
import com.panda.rcs.pending.order.pojo.RcsMargainRefVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-05-2022/5/4 14:19
 */
@Repository
public interface RcsMargainRefMapper {


    RcsMargainRefVo currentMargainRef(TournamentTemplateParam param);
}
