package com.panda.sport.rcs.console.dao;


import com.panda.sport.rcs.console.dto.AdminUserDTO;
import com.panda.sport.rcs.console.pojo.BaseAdminUser;
import com.panda.sport.rcs.console.dto.UserSearchDTO;
import tk.mapper.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseAdminUserMapper extends MyMapper<BaseAdminUser> {

    List<AdminUserDTO> getUserList(UserSearchDTO userSearchDTO);

    BaseAdminUser getUserByUserName(@Param("sysUserName")String sysUserName,@Param("id") Integer id);

    int updateUserStatus(@Param("id") Integer id,@Param("status") Integer status);

    int updateUser(BaseAdminUser user);

    BaseAdminUser findByUserName(@Param("userName") String userName);

    int updatePwd(@Param("userName") String userName,@Param("password") String password);

}