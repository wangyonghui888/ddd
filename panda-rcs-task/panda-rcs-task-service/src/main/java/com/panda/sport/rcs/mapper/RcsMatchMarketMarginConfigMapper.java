package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.panda.sport.rcs.pojo.RcsMatchMarketMarginConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 赛事设置表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-24
 */
@Component
public interface RcsMatchMarketMarginConfigMapper extends BaseMapper<RcsMatchMarketMarginConfig> {
    /**
     * @Description   //更新margin
     * @Param [model]
     * @Author  Sean
     * @Date  12:44 2020/11/3
     * @return void
     **/
    void insertOrUpdateMarketMarginConfig(@Param("config") RcsMatchMarketMarginConfig model);

    void updateZero(@Param("matchId")Long matchId);
}
