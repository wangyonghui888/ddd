package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.TUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  用户
 * @Date: 2020-06-03 21:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Mapper
public interface TUserMapper extends BaseMapper<TUser> {

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    TUser selectByUserId(@Param("userId") Long userId);

    int insertOrUpdate(TUser user);
}
