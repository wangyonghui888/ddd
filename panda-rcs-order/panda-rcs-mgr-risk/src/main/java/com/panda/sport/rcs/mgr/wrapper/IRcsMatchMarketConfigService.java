package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 赛事设置表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-24
 */
public interface IRcsMatchMarketConfigService extends IService<RcsMatchMarketConfig> {
    /**
     * 查询操盘限额
     *
     * @param config
     * @return
     */
    RcsMatchMarketConfig queryMaxBetAmount(RcsMatchMarketConfig config);
//
//    /**
//     * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
//     * @Description //获取平衡值
//     * @Param [matchId, marketCategoryId, marketId]
//     * @Author kimi
//     * @Date 2019/11/9
//     **/
//    List<RcsMatchMarketConfig> getRcsMatchMarketConfigList(Long matchId, Long marketId);

//    /**
//     * @return void
//     * @Description //更新数据
//     * @Param [rcsMatchMarketConfig]
//     * @Author kimi
//     * @Date 2019/11/9
//     **/
//    void update(RcsMatchMarketConfig rcsMatchMarketConfig);
    /**
     * @Description   //获取所有位置水差
     * @Param [config]
     * @Author  sean
     * @Date   2021/1/24
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsMatchMarketConfig>
     **/
    List<RcsMatchMarketConfig> getRcsMatchMarketConfigs(RcsMatchMarketConfig config);
    /**
     * @Description   //重新计算水差
     * @Param [config, list]
     * @Author  sean
     * @Date   2021/1/24
     * @return java.util.List<com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig>
     **/
    List<MatchMarketPlaceConfig> getPlaceConfigs(BigDecimal waterValue, List<RcsMatchMarketConfig> list);
    /**
     * @Description   //修订位置水差
     * @Param [placeConfigs, list]
     * @Author  sean
     * @Date   2021/1/24
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsMatchMarketConfig>
     **/
    List<RcsMatchMarketConfig> limitPlaceWater(RcsMatchMarketConfig config,BigDecimal waterValue);
}
