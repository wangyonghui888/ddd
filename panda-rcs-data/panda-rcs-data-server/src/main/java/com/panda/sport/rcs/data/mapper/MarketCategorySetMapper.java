package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMarketCategorySet;
import com.panda.sport.rcs.pojo.bo.FindMarketCategoryListAndNamesBO;
import com.panda.sport.rcs.pojo.bo.GetPerformanceSetPlaysBO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MarketCategorySetMapper extends BaseMapper<RcsMarketCategorySet> {

    RcsMarketCategorySet queryPlaySetCode();

    List<GetPerformanceSetPlaysBO> getPerformanceSetPlays(@Param("sportId") Long sportId);

    /**
     * 玩法集下的玩法列表，用于玩法集列表下的二级目录,并得到国际化
     * @param idList
     * @return
     */
    List<FindMarketCategoryListAndNamesBO> findMarketCategoryListAndNames(@Param("sportId") Long sportId,@Param("list") List<Long> idList);
}
