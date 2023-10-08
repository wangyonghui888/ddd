package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMerchantsHideRangeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 *
 * @author bobi
 */
@Service
public interface RcsMerchantsHideRangeConfigMapper extends BaseMapper<RcsMerchantsHideRangeConfig> {
}