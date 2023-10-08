package com.panda.sport.rcs.db.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.common.vo.api.request.UserGroupListReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserListReqVo;
import com.panda.sport.rcs.common.vo.api.response.ListByGroupAndUserNumResVo;
import com.panda.sport.rcs.common.vo.api.response.ListByUserByGroupIdResVo;
import com.panda.sport.rcs.common.vo.api.response.ListByUserResVo;
import com.panda.sport.rcs.db.entity.UserProfileGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 玩家组管理 服务类
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
public interface IUserProfileGroupService extends IService<UserProfileGroup> {

    /**
     * @Description 根据玩家组ID，名称或者操作人查询玩家组列表及用户数
     * @Param UserProfileGroup userProfileGroup
     * @Author Kir
     * @Date  2021-01-29
     * @return List<ListByGroupAndUserNumVo>
     **/
    IPage<ListByGroupAndUserNumResVo> queryUserGroups(Page<ListByGroupAndUserNumResVo> page, UserGroupListReqVo vo);

    /**
     * @Description 玩家组管理-用户列表
     * @Param Page,UserListReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByUserResVo>
     **/
    IPage<ListByUserResVo> queryUsers(Page<ListByUserResVo> page, UserListReqVo userListReqVo);

    /**
     * @Description 玩家组管理-用户列表
     * @Param Page, UserListReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByUserResVo>
     **/
    List<ListByUserResVo> queryGroupInfo(List<String> userList);

    /**
     * @Description 玩家组管理-用户列表
     * @Param Page, UserListReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByUserResVo>
     **/
    List<ListByUserResVo> queryUserLevelInfo(List<String> userList);

    /**
     * @Description 根据玩家组ID查询用户列表
     * @Param Page, groupId
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByUserByGroupIdResVo>
     **/
    IPage<ListByUserByGroupIdResVo> queryUsersByGroupId(Page<ListByUserByGroupIdResVo> page, String groupId);
}
