package com.panda.sport.rcs.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.db.entity.UserProfileSecondTags;

/**
 * <p>
 * 用户行为详情-投注偏好/财务特征-日统计表 服务类
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-21
 */
public interface IUserProfileSecondTagsService extends IService<UserProfileSecondTags> {
    /**
     * 添加或者删除
     *
     * @param userProfileSecondTags
     * @return
     */
    int insert(UserProfileSecondTags userProfileSecondTags);
}
