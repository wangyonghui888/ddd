package com.panda.rcs.cleanup.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettleMapper {

    /**
     * 删除7天前的结算数据
     * @param expiredTime 过期时间戳
     * @return
     */
    int deleteSettle(@Param("expiredTime") Long expiredTime);

    /**
     * 删除7天前的结算详细数据
     * @param expiredTime 过期时间戳
     * @return
     */
    int deleteSettleDetail(@Param("expiredTime") Long expiredTime);

    /**
     * 删除结算表
     * @param orderNos
     * @return
     */
    int deleteSettleByOrderNo(@Param("orderNos") List<String> orderNos);

    /**
     * 删除结算详细表
     * @param orderNos
     * @return
     */
    int deleteSettleDetailByOrderNo(@Param("orderNos") List<String> orderNos);

}
