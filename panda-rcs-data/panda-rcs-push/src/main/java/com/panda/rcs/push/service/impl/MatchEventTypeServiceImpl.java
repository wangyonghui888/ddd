package com.panda.rcs.push.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.push.service.MatchEventTypeService;
import com.panda.sport.rcs.mapper.MatchEventTypeMapper;
import com.panda.sport.rcs.pojo.MatchEventType;
import org.springframework.stereotype.Service;

@Service
public class MatchEventTypeServiceImpl extends ServiceImpl<MatchEventTypeMapper, MatchEventType> implements MatchEventTypeService {
}
