package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.vo.SportMarketOddsQueryVo;
import com.panda.sport.rcs.vo.odds.RcsStandardMarketDTO;
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
    /**
     * @Description   //根据盘口查赔率和投注项
     * @Param [marketId]
     * @Author  Sean
     * @Date  15:23 2020/6/28
     * @return java.util.List<com.panda.sport.rcs.vo.SportMarketOddsVo>
     **/
    List<StandardSportMarketOdds> queryMarketOddsByMarket(@Param("marketId") Long marketId);
    /**
     * @Description   //查询盘口和赔率信息
     * @Param [config]
     * @Author  sean
     * @Date   2021/6/13
     * @return java.util.List<com.panda.sport.rcs.vo.odds.RcsStandardMarketDTO>
     **/
    StandardMarketDTO selectMarketOddsByMarketIds(@Param("config")RcsMatchMarketConfig config);
}
