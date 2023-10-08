package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.db.entity.SSport;

import java.util.List;

/**
 * <p>
 * 标准球类表. 【数据来自融合表：standard_sport_type】 Mapper 接口
 * </p>
 *
 * @author dorich
 * @since 2020-07-17
 */
public interface SSportMapper {

    List<SSport> query();

}
