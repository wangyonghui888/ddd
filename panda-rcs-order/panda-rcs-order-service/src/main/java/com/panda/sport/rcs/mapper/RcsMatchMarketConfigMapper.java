package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketProbabilityConfig;
import com.panda.sport.rcs.pojo.RcsMatchPlayConfig;

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
     * 查询限额
     * @param config
     * @return
     */
    List<RcsMatchMarketConfig> queryMaxBetAmount(RcsMatchMarketConfig config);
	/**
	 * @Description   //更新自动水差
	 * @Param [item]
	 * @Author  Sean
	 * @Date  17:54 2020/7/16
	 * @return int
	 **/
	int insertOrUpdateMarketMarginConfig(@Param("item") ThreewayOverLoadTriggerItem item);
    /**
     * @Description   //根据赛事id和玩法id查询所有有效的盘口
     * @Param [config]
     * @Author  Sean
     * @Date  11:04 2020/10/6
     * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
     **/
    List<StandardMarketDTO> selectMarketOddsByMarketIds(@Param("config") RcsMatchMarketConfig config);
	/**
	 * @Description   //查询盘口配置
	 * @Param [orderItem]
	 * @Author  Sean
	 * @Date  17:59 2020/10/17
	 * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
	 **/
    RcsMatchMarketConfig queryMarketConfigByIndex(@Param("item") OrderItem orderItem);

	RcsMatchMarketConfig queryMarketConfig(@Param("item") OrderItem orderItem);
    
    
	void insertOrUpdatePlayMarginConfig(@Param("item") RcsMatchPlayConfig config);
	/**
	 * @Description   //获取盘口配置
	 * @Param [overLoadTriggerItem]
	 * @Author  sean
	 * @Date   2021/1/22
	 * @return void
	 **/
	RcsMatchMarketConfig getMarketConfig(@Param("item")ThreewayOverLoadTriggerItem overLoadTriggerItem);

	/**
	 * @Description   //修复位置水差
	 * @Param [list]
	 * @Author  sean
	 * @Date   2021/1/24
	 * @return void
	 **/
	Integer updateMatchMarketWaters(@Param("list")List<RcsMatchMarketConfig> list);
	/**
	 * @Description   //查询多项盘配置
	 * @Param [config]
	 * @Author  sean
	 * @Date   2021/5/14
	 * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
	 **/
    RcsMatchMarketConfig queryMostOddsTypeMarketConfig(@Param("config")RcsMatchMarketConfig config);
	/**
	 * @Description   //获取投注项概率差
	 * @Param [config]
	 * @Author  sean
	 * @Date   2021/5/15
	 * @return java.util.List<com.panda.sport.rcs.pojo.RcsMatchMarketProbabilityConfig>
	 **/
    List<RcsMatchMarketProbabilityConfig> getOddsTypeProbabilitys(@Param("config")RcsMatchMarketConfig config);
	/**
	 * @Description   //更新概率差
	 * @Param [ps]
	 * @Author  sean
	 * @Date   2021/5/15
	 * @return void
	 **/
	int insertOrUpdateMarketProbabilityConfig(@Param("list")List<RcsMatchMarketProbabilityConfig> ps);
	/**
	 * @Description   //根据盘口和投注项查询配置
	 * @Param [config]
	 * @Author  sean
	 * @Date   2021/6/19
	 * @return com.panda.sport.rcs.pojo.RcsMatchMarketProbabilityConfig
	 **/
	RcsMatchMarketProbabilityConfig getOddsTypeProbability(@Param("config")RcsMatchMarketConfig config);
	/**
	 * @Description   //查询子玩法跳赔参数
	 * @Param [config]
	 * @Author  sean
	 * @Date   2021/7/9
	 * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
	 **/
	RcsMatchMarketConfig queryMatchMarketConfigSub(@Param("config")RcsMatchMarketConfig config);
	/**
	 * @Description   //插入配置
	 * @Param [config]
	 * @Author  sean
	 * @Date   2021/8/5
	 * @return void
	 **/
    void initMarketConfig(@Param("config")RcsMatchMarketConfig config);
}
