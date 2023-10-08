package com.panda.rcs.stray.limit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSingleLimit;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 高风险单注赛种投注限制 Mapper 接口
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Repository
public interface RcsMerchantSingleLimitMapper extends BaseMapper<RcsMerchantSingleLimit> {

}
