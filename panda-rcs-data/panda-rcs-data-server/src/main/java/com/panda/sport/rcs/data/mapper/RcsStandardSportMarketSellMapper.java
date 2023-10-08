package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName RcsStandardSportMarketSellMapper
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/12/30 
**/
@Mapper
public interface RcsStandardSportMarketSellMapper extends BaseMapper<RcsStandardSportMarketSell> {
    int updateBatch(List<RcsStandardSportMarketSell> list);

    int batchInsert(@Param("list") List<RcsStandardSportMarketSell> list);

    int insertOrUpdate(RcsStandardSportMarketSell record);

    int insertOrUpdateSelective(RcsStandardSportMarketSell record);

    int updateBatchSelective(List<RcsStandardSportMarketSell> list);

	void updateNosetBtTraderInfo(RcsStandardSportMarketSell data);

}