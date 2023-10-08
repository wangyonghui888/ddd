package com.panda.sport.rcs.db.service;

import com.panda.sport.rcs.db.entity.UserSpecialStatis;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户行为详情-投注偏好/财务特征-日统计表 服务类
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-21
 */
public interface IUserSpecialStatisService extends IService<UserSpecialStatis> {
    /**
     * 添加或者删除
     * @param userSpecialStatis
     * @return
     */
    int insertOrUpdate(UserSpecialStatis userSpecialStatis);
}
