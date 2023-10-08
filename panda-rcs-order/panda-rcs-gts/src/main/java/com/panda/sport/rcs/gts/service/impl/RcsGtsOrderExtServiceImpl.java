package com.panda.sport.rcs.gts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.gts.service.RcsGtsOrderExtService;
import com.panda.sport.rcs.mapper.RcsGtsOrderExtMapper;
import com.panda.sport.rcs.pojo.RcsGtsOrderExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RcsGtsOrderExtServiceImpl extends ServiceImpl<RcsGtsOrderExtMapper, RcsGtsOrderExt> implements RcsGtsOrderExtService {

    @Autowired
    private RcsGtsOrderExtMapper mapper;

    @Override
    @Async
    public void addGtsOrder(RcsGtsOrderExt ext) {
        mapper.insert(ext);
    }
}
