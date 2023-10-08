package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsDangerUserMapper;
import com.panda.sport.rcs.pojo.danger.RcsDangerUser;
import com.panda.sport.rcs.task.wrapper.RiskUserGroupService;
import org.springframework.stereotype.Service;

@Service
public class RiskUserGroupServiceImpl extends ServiceImpl<RcsDangerUserMapper, RcsDangerUser> implements RiskUserGroupService {
}
