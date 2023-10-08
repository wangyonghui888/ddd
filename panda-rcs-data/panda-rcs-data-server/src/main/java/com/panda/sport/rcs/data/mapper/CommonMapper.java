package com.panda.sport.rcs.data.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;


/**
 * @author V
 */
@Mapper
public interface CommonMapper  {

    @Select(" SELECT score_source from rcs_tournament_template WHERE type = 3  and match_type = 0 and type_val = #{id}")
    String getMatchScoreSource(@Param("id") Long standardMatchInfoId);

    @Insert(" INSERT INTO `rcs_category_odd_templet` (`category`, `odd_type`) VALUES ( #{bean.category},#{bean.oddType});")
    int insertRcsCategoryOddTemplet(@Param("bean") Map bean);

}
