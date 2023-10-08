package com.panda.sport.rcs.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * 玩家组管理 Mapper 接口
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
public interface UserProfileGroupMapper extends BaseMapper<UserProfileGroup> {

    /**
     * @Description 根据玩家组ID，名称或者操作人查询玩家组列表及用户数
     * @Param Page, UserProfileGroup
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByGroupAndUserNumResVo>
     **/
    IPage<ListByGroupAndUserNumResVo> queryUserGroups(Page<ListByGroupAndUserNumResVo> page, @Param("vo") UserGroupListReqVo vo);

    /**
     * @Description 玩家组管理-用户列表
     * @Param Page, UserListReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByUserResVo>
     **/
    IPage<ListByUserResVo> queryUsers(Page<ListByUserResVo> page, @Param("userListReqVo")UserListReqVo userListReqVo);

    /**
     * @Description 玩家组管理-用户列表
     * @Param Page, UserListReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByUserResVo>
     **/
    List<ListByUserResVo> queryGroupInfo(@Param("userList")List<String> userList);

    /**
     * @Description 玩家组管理-用户列表
     * @Param Page, UserListReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByUserResVo>
     **/
    List<ListByUserResVo> queryUserLevelInfo(@Param("userList")List<String> userList);

    /**
     * @Description 根据玩家组ID查询用户列表
     * @Param Page, groupId
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<ListByUserByGroupIdResVo>
     **/
    IPage<ListByUserByGroupIdResVo> queryUsersByGroupId(Page<ListByUserByGroupIdResVo> page, @Param("groupId") String groupId);
}
