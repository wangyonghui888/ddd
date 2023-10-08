package com.panda.rcs.logService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.logService.vo.StandardSportMarketOdds;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;



/**
 * <p>
 * 赛事盘口交易项表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Repository
@Mapper
public interface StandardSportMarketOddsMapper extends BaseMapper<StandardSportMarketOdds> {

  StandardSportMarketOdds queryByOddsTypeAndDateAndMarketId(@Param("oddsType")String oddsType, @Param("marketId")Long marketId);


}
