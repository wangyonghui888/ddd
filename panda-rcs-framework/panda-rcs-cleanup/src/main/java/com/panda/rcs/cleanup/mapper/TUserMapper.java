package com.panda.rcs.cleanup.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TUserMapper {

    /**
     * 查询需要删除的用户Id
     * @param merchantCode
     * @param userName
     * @return
     */
    List<Long> getUserByMerchantCodeAndTimeAndUserName(@Param("merchantCode") String merchantCode, @Param("userName") String userName);
    /**
     * 用户表清理
     * @param userIds
     * @return
     */
    int deleteUserByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 用户标签表清理
     * @param userIds
     * @return
     */
    int deleteUserLabelByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 用户配置表清理
     * @param userIds
     * @return
     */
    int deleteUserConfigByUserIds(@Param("userIds") List<Long> userIds);

}
