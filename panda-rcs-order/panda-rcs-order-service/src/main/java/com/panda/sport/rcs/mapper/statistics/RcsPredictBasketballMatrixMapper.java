package com.panda.sport.rcs.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.statistics.RcsPredictBasketballMatrix;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 篮球矩阵表 Mapper 接口
 * </p>
 *
 * @author lithan
 * @since 2021-01-09
 */
public interface RcsPredictBasketballMatrixMapper extends BaseMapper<RcsPredictBasketballMatrix> {
    void saveOrUpdate(@Param("list") List<RcsPredictBasketballMatrix> list);
}
