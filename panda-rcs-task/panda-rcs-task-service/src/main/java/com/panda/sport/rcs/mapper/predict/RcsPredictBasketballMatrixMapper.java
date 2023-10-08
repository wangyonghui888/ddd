package com.panda.sport.rcs.mapper.predict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsPredictBasketballMatrix;
import com.panda.sport.rcs.pojo.RcsPredictBetStatis;

/**
 * <p>
 * 篮球矩阵表 Mapper 接口
 * </p>
 *
 * @author lithan
 * @since 2021-01-09
 */
public interface RcsPredictBasketballMatrixMapper extends BaseMapper<RcsPredictBasketballMatrix> {

    void saveOrUpdate(RcsPredictBasketballMatrix entity);
}
