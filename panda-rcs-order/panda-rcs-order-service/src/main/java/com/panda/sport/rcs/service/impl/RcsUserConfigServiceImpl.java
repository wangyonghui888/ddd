package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsUserConfigMapper;
import com.panda.sport.rcs.pojo.RcsUserConfig;
import com.panda.sport.rcs.service.IRcsUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RcsUserConfigServiceImpl extends ServiceImpl<RcsUserConfigMapper, RcsUserConfig> implements IRcsUserConfigService {
    @Autowired
    private RcsUserConfigMapper mapper;

}

