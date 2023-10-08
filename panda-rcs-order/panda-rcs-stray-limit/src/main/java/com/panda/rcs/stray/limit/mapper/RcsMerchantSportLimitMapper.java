package com.panda.rcs.stray.limit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSportLimit;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 单日串关赛种赔付限额及派彩限额 Mapper 接口
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Repository
public interface RcsMerchantSportLimitMapper extends BaseMapper<RcsMerchantSportLimit> {

}
