package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsDangerFpMapper;
import com.panda.sport.rcs.pojo.danger.RcsDangerFp;
import com.panda.sport.rcs.task.wrapper.RiskFpService;
import org.springframework.stereotype.Service;

@Service
public class RiskFpServiceImpl extends ServiceImpl<RcsDangerFpMapper, RcsDangerFp> implements RiskFpService {
}
