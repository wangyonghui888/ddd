package com.panda.sport.rcs.db.service.impl;

import com.panda.sport.rcs.db.entity.UserSpecialStatis;
import com.panda.sport.rcs.db.mapper.UserSpecialStatisMapper;
import com.panda.sport.rcs.db.service.IUserSpecialStatisService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class UserSpecialStatisServiceImpl extends ServiceImpl<UserSpecialStatisMapper, UserSpecialStatis> implements IUserSpecialStatisService {
    @Autowired
    private UserSpecialStatisMapper userSpecialStatisMapper;
    /**
     * 添加或者更新
     * @param userSpecialStatis
     * @return
     */
    @Override
    public int insertOrUpdate(UserSpecialStatis userSpecialStatis){
        return userSpecialStatisMapper.insertOrUpdate(userSpecialStatis);
    }
}
