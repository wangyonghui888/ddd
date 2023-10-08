package com.panda.rcs.push.mapper;

import com.panda.rcs.push.entity.vo.PlayInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayInfoMapper {

    /**
     * @Description   根据玩法id查询所在玩法集
     * @return com.panda.sport.rcs.pojo.RcsMarketCategorySet
     **/
    PlayInfoVo findMarketCategoryListByPlayId(@Param("id") Integer id, @Param("sportId") Long sportId);

}
