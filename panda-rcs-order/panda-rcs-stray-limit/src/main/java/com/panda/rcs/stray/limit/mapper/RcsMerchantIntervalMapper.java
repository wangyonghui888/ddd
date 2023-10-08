package com.panda.rcs.stray.limit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantInterval;
import org.springframework.stereotype.Repository;

/**
 * 高风险单注区间最高赔付金额 mapper
 */
@Repository
public interface RcsMerchantIntervalMapper extends BaseMapper<RcsMerchantInterval> {
}
