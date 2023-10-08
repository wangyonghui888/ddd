package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.config.BuildMarketPlaceConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 赛事设置表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-24
 */
@Component
public interface RcsMatchMarketConfigMapper extends BaseMapper<RcsMatchMarketConfig> {
    /**
     * 查询限额
     * @param config
     * @return
     */
    RcsMatchMarketConfig queryMaxBetAmount(RcsMatchMarketConfig config);

    /**
     * @return void
     * @Description //修改赛事配置改变盘口盘配置
     * @Param [matchId, dataSource, marketStatus]
     * @Author kimi
     * @Date 2019/12/9
     **/
    void updateBatch(@Param("matchId") Long matchId, @Param("dataSource") Integer dataSource, @Param("marketStatus") Integer marketStatus);

    /**
     * @Description   //根据盘口Id将其更新为自动或者手动
     * @Param [standardSportMarketList, dataSource]
     * @Author kimi
     * @Date 2020/1/16
     * @return void
     **/
    void updateDataSource(@Param("standardSportMarketList") List<StandardSportMarket> standardSportMarketList, @Param("dataSource") Integer dataSource);

    /**
     * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
     * @Description //根据盘口id查询数据
     * @Param [marketId]
     * @Author kimi
     * @Date 2020/1/25
     **/
    RcsMatchMarketConfig selectRcsMatchMarketConfigByMarketId(@Param("marketId") Long marketId);

    /**
     * @return void
     * @Description //TODO
     * @Param [standardSportMarketList, rcsPlayConfig]
     * @Author kimi
     * @Date 2020/2/18
     **/
    void insertAndUpdate(@Param("standardSportMarketList") List<StandardSportMarket> standardSportMarketList, @Param("dataSource") Integer dataSource);

    /**
     * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
     * @Description 查询最大调赔值和最大margin比例
     * @Param [marketId]
     * @Author Sean
     * @Date 21:33 2020/2/18
     **/
    RcsMatchMarketConfig selectOddsChangeAndMaxRate(@Param("marketId") Long marketId);
    /**
     * @Description   更新累计赔率变化
     * @Param [marketId, oddsRateTotal]
     * @Author  Sean
     * @Date  11:15 2020/2/19
     * @return void
     **/
    int updateMarginChange(@Param("marketId") Long marketId,@Param("oddsRateTotal") BigDecimal oddsRateTotal);

    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsMatchMarketConfig>
     * @Description //TODO
     * @Param [matchId, playId1, playId2, marketId, relevanceType]
     * @Author kimi
     * @Date 2020/2/20
     **/
    List<RcsMatchMarketConfig> selectRcsMatchMarketConfigByRelevanceType(@Param("matchId") Long matchId, @Param("playId1") Long playId1, @Param("playId2") Long playId2, @Param(
            "marketId") Long marketId);


    void updateRcsMatchMarketConfigToOddsValue(@Param("marketId") Long marketId, @Param("home_market_value") String home_market_value, @Param("away_market_value") String away_market_value);

    /**
     * @return void
     * @Description 更改赔率累计变化值
     * @Param [matchId, playId, marketId]
     * @Author kimi
     * @Date 2020/3/12
     **/
    void updateRcsMatchMarketConfigToOddsChange(@Param("matchId") Long matchId, @Param("playId") Long playId);

	List<Map<String, Object>> queryAllMarketConfigList(Map<String, Object> map);

	void updateAllMarketToZeroByMatchId(Map<String, Object> map);

    /**
     * 获取构建盘口位置配置
     *
     * @param matchId
     * @param playId
     * @return
     */
    List<BuildMarketPlaceConfig> getBuildMarketPlaceConfig(@Param("matchId") Long matchId, @Param("playId") Long playId);
}
