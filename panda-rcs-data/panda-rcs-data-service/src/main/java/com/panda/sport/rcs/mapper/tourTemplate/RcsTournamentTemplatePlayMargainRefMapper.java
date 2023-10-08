package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargainRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description   联赛模板margiain引用表
 * @Param 
 * @Author  toney
 * @Date  20:02 2020/5/10
 * @return 
 **/
public interface RcsTournamentTemplatePlayMargainRefMapper extends BaseMapper<RcsTournamentTemplatePlayMargainRef> {
    int deleteByPrimaryKey(Integer id);


    int insertSelective(RcsTournamentTemplatePlayMargainRef record);

    RcsTournamentTemplatePlayMargainRef selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RcsTournamentTemplatePlayMargainRef record);

    int updateByPrimaryKey(RcsTournamentTemplatePlayMargainRef record);


   int insertOrUpdateBatch(@Param("list")List<RcsTournamentTemplatePlayMargainRef> margainRefList);
}