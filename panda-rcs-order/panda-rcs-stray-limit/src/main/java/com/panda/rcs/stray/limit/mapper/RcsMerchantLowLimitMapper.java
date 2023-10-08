package com.panda.rcs.stray.limit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLowLimit;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 单日额度用完最低可投注金额配置 Mapper 接口
 * </p>
 *
 * @author joey
 * @since 2022-04-02
 */
@Repository
public interface RcsMerchantLowLimitMapper extends BaseMapper<RcsMerchantLowLimit> {

}
