package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsUserConfigNew;
import com.panda.sport.rcs.pojo.TUser;
import com.panda.sport.rcs.pojo.TUserLabel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  用户
 * @Date: 2020-06-03 21:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface TUserMapper extends BaseMapper<TUser> {

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    TUser selectByUserId(@Param("userId") Long userId);

    /**
     * 获取用户商户ID
     * @param userId
     * @return
     */
    Map<String,String> selectBusinessIdByUserId(@Param("userId") Long userId);


    int insertOrUpdate(TUser user);

    Long selectUserMerchantsIdById(@Param("userId") Long userId);

    int updateUserTagId(@Param("userId")long userId,@Param("tagId") int tagId);


    int userLabelSaveList(@Param("list") List<TUserLabel> list);

    int userConfigNewSaveList(@Param("list") List<RcsUserConfigNew> list);



}
