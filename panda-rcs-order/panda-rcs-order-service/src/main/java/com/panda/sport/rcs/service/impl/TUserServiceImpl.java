package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.pojo.TUser;
import com.panda.sport.rcs.service.ITUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * TUser
 * </p>
 *
 * @author magic
 * @since 2022-10-19
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {

    @Override
    @Transactional
    public int updateUserTagId(long userId, int tagId) {
        return baseMapper.updateUserTagId(userId, tagId);
    }
}
