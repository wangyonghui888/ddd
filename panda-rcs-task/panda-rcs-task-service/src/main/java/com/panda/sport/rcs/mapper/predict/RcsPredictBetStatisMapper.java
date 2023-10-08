package com.panda.sport.rcs.mapper.predict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsPredictBetStatis;

/**
 * <p>
 * 预测货量表 Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-18
 */
public interface RcsPredictBetStatisMapper extends BaseMapper<RcsPredictBetStatis> {


    /**
     * 保存或者更新
     * @param entity
     */
    void saveOrUpdate(RcsPredictBetStatis entity);

}
