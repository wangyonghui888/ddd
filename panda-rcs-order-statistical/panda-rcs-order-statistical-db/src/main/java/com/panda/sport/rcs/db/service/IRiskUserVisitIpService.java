package com.panda.sport.rcs.db.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.vo.api.response.IpListResVo;
import com.panda.sport.rcs.common.vo.api.response.UserListByIpResVo;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户行为详情-访问特征-用户登录ip记录表 服务类
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-24
 */
public interface IRiskUserVisitIpService extends IService<RiskUserVisitIp> {
    /**
     * @Description   查询按userId和登录 时间进行查询
     * @Param [userId, loginDate]
     * @Author toney
     * @Date  15:56 2021/1/9
     * @return java.util.List<com.panda.sport.rcs.db.entity.RiskUserVisitIp>
     **/
    List<RiskUserVisitIp> queryByUserIdAndLoginDate(Long userId,Long loginDate);

    /**
     * @Description   根据IP查询所有关联用户
     * @Param [ip]
     * @Author kir
     * @Date  15:58 2021/2/2
     * @return List<UserListByIpResVo>
     **/
    IPage<UserListByIpResVo> queryUserListByIp(Page<UserListByIpResVo> page, @Param("ip") String ip);

    /**
     * @Description   根据IP查询所有关联用户
     * @Param [startTime,endTime]
     * @Author kir
     * @Date  15:58 2021/2/2
     * @return List<RiskUserVisitIp>
     **/
    List<RiskUserVisitIp> queryListByLoginTime(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
