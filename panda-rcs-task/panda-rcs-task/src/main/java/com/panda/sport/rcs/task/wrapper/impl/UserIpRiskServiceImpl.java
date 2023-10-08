package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsDangerIpMapper;
import com.panda.sport.rcs.pojo.danger.RcsDangerIp;
import com.panda.sport.rcs.task.wrapper.UserIpRiskService;
import org.springframework.stereotype.Service;

@Service
public class UserIpRiskServiceImpl extends ServiceImpl<RcsDangerIpMapper, RcsDangerIp> implements UserIpRiskService {
}
