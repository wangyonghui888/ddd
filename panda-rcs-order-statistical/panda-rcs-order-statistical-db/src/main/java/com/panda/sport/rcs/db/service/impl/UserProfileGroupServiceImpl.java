package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.common.vo.api.request.UserGroupListReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserListReqVo;
import com.panda.sport.rcs.common.vo.api.response.ListByGroupAndUserNumResVo;
import com.panda.sport.rcs.common.vo.api.response.ListByUserByGroupIdResVo;
import com.panda.sport.rcs.common.vo.api.response.ListByUserResVo;
import com.panda.sport.rcs.db.entity.UserProfileGroup;
import com.panda.sport.rcs.db.mapper.UserProfileGroupMapper;
import com.panda.sport.rcs.db.service.IUserProfileGroupService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 玩家组管理 服务类
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@Service
public class UserProfileGroupServiceImpl extends ServiceImpl<UserProfileGroupMapper, UserProfileGroup> implements IUserProfileGroupService {

    @Autowired
    private UserProfileGroupMapper mapper;

    @Override
    public IPage<ListByGroupAndUserNumResVo> queryUserGroups(Page<ListByGroupAndUserNumResVo> page, UserGroupListReqVo vo) {
        return mapper.queryUserGroups(page, vo);
    }

    @Override
    public IPage<ListByUserResVo> queryUsers(Page<ListByUserResVo> page, UserListReqVo userListReqVo){
        return mapper.queryUsers(page, userListReqVo);
    }

    @Override
    public List<ListByUserResVo> queryGroupInfo(List<String> userList) {
        return mapper.queryGroupInfo(userList);
    }

    @Override
    public List<ListByUserResVo> queryUserLevelInfo(List<String> userList) {
        return mapper.queryUserLevelInfo(userList);
    }

    @Override
    public IPage<ListByUserByGroupIdResVo> queryUsersByGroupId(Page<ListByUserByGroupIdResVo> page, String groupId) {
        return mapper.queryUsersByGroupId(page, groupId);
    }

}
