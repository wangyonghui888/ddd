package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.db.entity.UserProfileSecondTags;
import com.panda.sport.rcs.db.mapper.UserProfileSecondTagsMapper;
import com.panda.sport.rcs.db.service.IUserProfileSecondTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户行为详情-投注偏好/财务特征-日统计表 服务实现类
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-21
 */
@Service
public class UserProfileSecondTagsServiceImpl extends ServiceImpl<UserProfileSecondTagsMapper, UserProfileSecondTags> implements IUserProfileSecondTagsService {
    @Autowired
    private UserProfileSecondTagsMapper userProfileSecondTagsMapper;

    /**
     * 添加或者更新
     *
     * @param userProfileSecondTags
     * @return
     */
    @Override
    public int insert(UserProfileSecondTags userProfileSecondTags) {
        return userProfileSecondTagsMapper.insert(userProfileSecondTags);
    }
}
