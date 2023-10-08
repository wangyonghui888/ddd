package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.db.entity.RiskOrderTagIp;
import com.panda.sport.rcs.db.mapper.RiskOrderTagIpMapper;
import com.panda.sport.rcs.db.service.IRiskOrderTagIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 投注IP管理-用户IP及标签统计表 服务实现类
 * </p>
 *
 * @author author
 * @since 2021-02-02
 */
@Service
public class RiskOrderTagIpServiceImpl extends ServiceImpl<RiskOrderTagIpMapper, RiskOrderTagIp> implements IRiskOrderTagIpService {

    @Autowired
    private RiskOrderTagIpMapper mapper;

    @Override
    public List<RiskOrderTagIp> queryListByUserId(Long[] ids, Integer count) {
        return mapper.queryListByUserId(ids, count);
    }
}
