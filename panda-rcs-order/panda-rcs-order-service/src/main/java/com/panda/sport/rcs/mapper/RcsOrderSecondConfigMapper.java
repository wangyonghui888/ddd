package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsOrderSecondConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @author :  carver
 * @Project Name :  panda-rcs-trade
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-11-17 20:27
 */
@Repository
public interface RcsOrderSecondConfigMapper extends BaseMapper<RcsOrderSecondConfig> {

    Integer selectOrderSecondConfig(@Param("matchId")Long matchId, @Param("playSet")String playSet, @Param("time")Long time, @Param("userLevel")Integer userLevel, @Param("betAmount")Long betAmount);
}
