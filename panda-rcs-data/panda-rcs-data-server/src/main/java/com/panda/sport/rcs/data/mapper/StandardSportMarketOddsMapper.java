package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketOddsMessageDTO;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName StandardSportMarketOddsMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/7
 **/
@Mapper
public interface StandardSportMarketOddsMapper extends BaseMapper<StandardSportMarketOdds> {

    int batchSaveOrUpdate(@Param("list") List<StandardMarketOddsMessageDTO> listStandardSportMarketOdds);

}