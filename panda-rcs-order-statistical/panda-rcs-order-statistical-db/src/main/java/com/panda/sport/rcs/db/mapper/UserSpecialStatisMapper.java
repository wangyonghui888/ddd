package com.panda.sport.rcs.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.db.entity.UserSpecialStatis;

/**
 * <p>
 * 用户行为详情-投注偏好/财务特征-日统计表 Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-21
 */
public interface UserSpecialStatisMapper extends BaseMapper<UserSpecialStatis> {
    /**
     * 添加或者删除
     * @param userSpecialStatis
     * @return
     */
    int insertOrUpdate(UserSpecialStatis userSpecialStatis);
}
