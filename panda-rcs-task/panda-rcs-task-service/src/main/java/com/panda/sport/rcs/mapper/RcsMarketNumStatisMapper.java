package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMarketNumStatis;

/**
 * <p>
 * 盘口位置统计表 Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-10-03
 */
public interface RcsMarketNumStatisMapper extends BaseMapper<RcsMarketNumStatis> {

    /**
     *    添加记录或者更新记录
     **/
    int insertOrUpdate(RcsMarketNumStatis rcsMarketNumStatis);
}
