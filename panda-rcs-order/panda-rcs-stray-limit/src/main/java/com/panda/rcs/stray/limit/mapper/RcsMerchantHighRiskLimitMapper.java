package com.panda.rcs.stray.limit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantHighRiskLimit;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 高风险单注赔付限额 Mapper 接口
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Repository
public interface RcsMerchantHighRiskLimitMapper extends BaseMapper<RcsMerchantHighRiskLimit> {

}
