package com.panda.sport.rcs.mapper.champion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsChampionRiskConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 多语言 Mapper 接口
 * @Description :  冠军玩法操盘及限额管理
 * </p>
 *
 * @author Kir
 * @since 2021-06-08
 */
public interface RcsChampionRiskConfigMapper extends BaseMapper<RcsChampionRiskConfig> {
    /**
     * @Description   //查询限额配置和跳赔参数
     * @Param [config]
     * @Author  sean
     * @Date   2021/6/13
     * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
     **/
    RcsMatchMarketConfig queryChampionRiskConfig(@Param("config")RcsMatchMarketConfig config);
}
