package com.panda.sport.rcs.customdb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.panda.sport.rcs.db.mapper.RiskUserVisitIpMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户行为详情-访问特征-用户登录ip记录表 Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-24
 */
public interface RiskUserVisitIpExtMapper {
    /**
     * 根据订单获取用户ip
     *
     * @return
     */
    public List<RiskUserVisitIp> getUserOrderIp(@Param("beginTime") long beginTime, @Param("endTime") long endTime);
}
