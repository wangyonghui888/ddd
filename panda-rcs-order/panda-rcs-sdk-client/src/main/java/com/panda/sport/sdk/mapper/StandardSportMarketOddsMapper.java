package com.panda.sport.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.sdk.vo.StandardMatchInfo;
import com.panda.sport.sdk.vo.StandardSportMarketOdds;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StandardSportMarketOddsMapper extends BaseMapper<StandardSportMarketOdds> {
}
