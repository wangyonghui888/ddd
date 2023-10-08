package com.panda.sport.rcs.customdb.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 统计用户投注信息 Mapper 接口
 * </p>
 *
 * @author
 * @since 2020-06-23
 */

public interface TagExtMapper {

    /**
     * 获取前一天有下注的用户
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    List<Long> getUserId(@Param("beginDate") Long beginDate, @Param("endDate") Long endDate);

    /**
     * 获取用户是不是投注特征标签用户
     *
     * @return
     */
    Long getUserIdTagType(@Param("userId") Long userId);

    /**
     * 获取所有投注特征标签的用户
     *
     * @return
     */
    List<Long> getBetTagTypeUser();


    List<Long> getUserByTag(@Param("time") long time);


    Map<String, Object> getUserTag(@Param("userId") long userId);


    List<Map<String, Object>> getSpecialMerchantNewUserUserId(String merchantCodes, int userLevel);


    List<Long> getUserByTime(long timeEnd);


    List<Long> getBasketBallUser();
    List<Long> getUserByBasketTag();

}
