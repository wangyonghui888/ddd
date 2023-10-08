package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsOddsConvertMapping;
import org.springframework.stereotype.Repository;

import java.util.List;

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
