package com.panda.sport.rcs.mapper;

import com.panda.sport.rcs.pojo.RcsOddsConvertMapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * 赔率转换映射表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Repository
public interface RcsOddsConvertMappingMapper extends BaseMapper<RcsOddsConvertMapping> {

	List<RcsOddsConvertMapping> queryOddsMappingList();

}
