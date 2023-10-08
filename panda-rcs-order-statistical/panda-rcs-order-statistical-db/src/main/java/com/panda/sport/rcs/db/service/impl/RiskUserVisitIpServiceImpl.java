package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.vo.api.response.IpListResVo;
import com.panda.sport.rcs.common.vo.api.response.UserListByIpResVo;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.panda.sport.rcs.db.mapper.RiskUserVisitIpMapper;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户行为详情-访问特征-用户登录ip记录表 服务实现类
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-24
 */
@Service
public class RiskUserVisitIpServiceImpl extends ServiceImpl<RiskUserVisitIpMapper, RiskUserVisitIp> implements IRiskUserVisitIpService {
    @Autowired
    private RiskUserVisitIpMapper riskUserVisitIpMapper;
    /**
     * @Description   查询按userId和登录 时间进行查询
     * @Param [userId, loginDate]
     * @Author toney
     * @Date  15:56 2021/1/9
     * @return java.util.List<com.panda.sport.rcs.db.entity.RiskUserVisitIp>
     **/
    @Override
    public List<RiskUserVisitIp> queryByUserIdAndLoginDate(Long userId,Long loginDate){
        return riskUserVisitIpMapper.queryByUserIdAndLoginDate(userId,loginDate);
    }

    /**
     * @Description   根据IP查询所有关联用户
     * @Param [ip]
     * @Author kir
     * @Date  15:58 2021/2/2
     * @return List<UserListByIpResVo>
     **/
    @Override
    public IPage<UserListByIpResVo> queryUserListByIp(Page<UserListByIpResVo> page, String ip) {
        return riskUserVisitIpMapper.queryUserListByIp(page, ip);
    }

    /**
     * @Description   根据IP查询所有关联用户
     * @Param [startTime,endTime]
     * @Author kir
     * @Date  15:58 2021/2/2
     * @return List<RiskUserVisitIp>
     **/
    @Override
    public List<RiskUserVisitIp> queryListByLoginTime(Long startTime, Long endTime) {
        return riskUserVisitIpMapper.queryListByLoginTime(startTime, endTime);
    }

}
