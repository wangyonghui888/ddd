package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.config.BuildMarketPlaceConfig;
import com.panda.sport.rcs.pojo.config.BuildMarketPlayConfig;
import com.panda.sport.rcs.pojo.config.MarketBuildConfig;
import com.panda.sport.rcs.pojo.config.MarketBuildPlayConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * 查询盘口构建位置配置
     *
     * @param matchId
     * @param playId
     * @return
     * @author Paca
     */
    List<MarketBuildConfig> listMarketBuildConfig(@Param("matchId") Long matchId, @Param("playId") Long playId);

    /**
     * 查询盘口构建玩法配置
     *
     * @param matchId
     * @param playId
     * @return
     */
    MarketBuildPlayConfig queryMarketBuildPlayConfig(@Param("matchId") Long matchId, @Param("playId") Integer playId);

    /**
     * 获取构建盘口位置配置
     *
     * @param matchId
     * @param playId
     * @return
     */
    List<BuildMarketPlaceConfig> getBuildMarketPlaceConfig(@Param("matchId") Long matchId, @Param("playId") Long playId);

    /**
     * 获取盘口差
     *
     * @param matchId
     * @param playId
     * @return
     */
    BuildMarketPlayConfig getMarketHeadGap(@Param("matchId") Long matchId, @Param("playId") Long playId);

    /**
     * 获取构建盘口玩法配置
     *
     * @param matchId
     * @param playId
     * @return
     */
    BuildMarketPlayConfig getBuildMarketPlayConfig(@Param("matchId") Long matchId, @Param("playId") Long playId);

    /**
     * 获取篮球分时配置
     *
     * @param matchId
     * @param playId
     * @return
     */
    List<BuildMarketPlayConfig> queryBasketballBuildMarketConfig(@Param("matchId") Long matchId, @Param("playId") Long playId);
}
