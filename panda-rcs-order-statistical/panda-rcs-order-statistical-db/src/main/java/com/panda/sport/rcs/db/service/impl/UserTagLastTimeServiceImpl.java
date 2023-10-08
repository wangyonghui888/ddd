package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.db.entity.UserTagLastTime;
import com.panda.sport.rcs.db.mapper.UserTagLastTimeMapper;
import com.panda.sport.rcs.db.service.IUserTagLastTimeService;
import org.springframework.stereotype.Service;

@Service
public class UserTagLastTimeServiceImpl extends ServiceImpl<UserTagLastTimeMapper, UserTagLastTime> implements IUserTagLastTimeService {

}
