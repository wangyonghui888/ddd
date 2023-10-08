package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsTournamentOrderAcceptConfig;
import com.panda.sport.rcs.pojo.RcsTournamentOrderAcceptEventConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RcsTournamentOrderAcceptConfigMapper extends BaseMapper<RcsTournamentOrderAcceptConfig> {
}