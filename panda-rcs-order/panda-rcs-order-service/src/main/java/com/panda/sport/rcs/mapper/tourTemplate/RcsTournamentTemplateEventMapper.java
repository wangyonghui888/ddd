package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateEvent;
import feign.Param;

import java.util.List;

/**
 * @Description   联赛模板事件主表
 * @Param 
 * @Author  toney
 * @Date  20:01 2020/5/10
 * @return 
 **/
public interface RcsTournamentTemplateEventMapper extends BaseMapper<RcsTournamentTemplateEvent> {
    int deleteByPrimaryKey(Integer id);

    int insertOrUpdateBatch(@Param("list") List<RcsTournamentTemplateEvent> list);

    int insertSelective(RcsTournamentTemplateEvent record);

    RcsTournamentTemplateEvent selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RcsTournamentTemplateEvent record);

    int updateByPrimaryKey(RcsTournamentTemplateEvent record);
}