package com.panda.sport.rcs.mts.sportradar.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsMtsOrderExtMapper;
import com.panda.sport.rcs.mts.sportradar.wrapper.RcsMtsOrderExtService;
import com.panda.sport.rcs.pojo.RcsMtsOrderExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RcsMtsOrderExtServiceImpl extends ServiceImpl<RcsMtsOrderExtMapper, RcsMtsOrderExt> implements RcsMtsOrderExtService {

    @Autowired
    private RcsMtsOrderExtMapper mapper;

    @Override
    @Async
    public void addMtsOrder(RcsMtsOrderExt rcsMtsOrderExt) {
        mapper.insert(rcsMtsOrderExt);
    }
}
