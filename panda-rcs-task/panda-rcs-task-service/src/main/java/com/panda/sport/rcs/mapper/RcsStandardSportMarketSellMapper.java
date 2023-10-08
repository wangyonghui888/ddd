package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;
import com.panda.sport.rcs.vo.StandardMarketSellQueryVo;
import com.panda.sport.rcs.vo.StandardMarketSellVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName RcsStandardSportMarketSellMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/12/30
 **/
public interface RcsStandardSportMarketSellMapper extends BaseMapper<RcsStandardSportMarketSell> {
    int updateBatch(List<RcsStandardSportMarketSell> list);

    int batchInsert(@Param("list") List<RcsStandardSportMarketSell> list);

    int insertOrUpdate(RcsStandardSportMarketSell record);

    int updateBatchSelective(List<RcsStandardSportMarketSell> list);

    IPage<StandardMarketSellVo> selectRcsStandardSportMarketSell(IPage<StandardMarketSellVo> page, @Param("standardMarketSellQueryVo") StandardMarketSellQueryVo standardMarketSellQueryVo);

    RcsStandardSportMarketSell queryMarketSell(@Param("record") RcsStandardSportMarketSell record);

    List<Map> getMatchNumberByType(@Param("beginTimeMillis") long beginTimeMillis, @Param("endTimeMillis") long endTimeMillis);
}