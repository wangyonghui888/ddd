package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.vo.SportMarketOddsQueryVo;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 赛事盘口交易项表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Repository
public interface StandardSportMarketOddsMapper extends BaseMapper<StandardSportMarketOdds> {

    List<SportMarketOddsQueryVo> selectSportMarketOddsList(SportMarketOddsQueryVo condition,List<Long> marketCategoryIds);

	int updateMarketOddsInfo(StandardSportMarketOdds standardSportMarketOdds);

    int batchSaveOrUpdate(@Param("list") List<StandardSportMarketOdds> listStandardSportMarketOdds);
}
