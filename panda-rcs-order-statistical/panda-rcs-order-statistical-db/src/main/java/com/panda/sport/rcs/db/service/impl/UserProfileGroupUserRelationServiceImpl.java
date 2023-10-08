package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.db.entity.UserProfileGroupUserRelation;
import com.panda.sport.rcs.db.mapper.UserProfileGroupUserRelationMapper;
import com.panda.sport.rcs.db.service.IUserProfileGroupUserRelationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 玩家组与用户关联表 服务类
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@Service
public class UserProfileGroupUserRelationServiceImpl extends ServiceImpl<UserProfileGroupUserRelationMapper, UserProfileGroupUserRelation> implements IUserProfileGroupUserRelationService {

}
