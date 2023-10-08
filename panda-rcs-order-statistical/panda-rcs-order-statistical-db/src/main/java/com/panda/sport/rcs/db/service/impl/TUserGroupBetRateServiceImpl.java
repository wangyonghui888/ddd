package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.db.entity.TUserGroupBetRate;
import com.panda.sport.rcs.db.mapper.TUserGroupBetRateMapper;
import com.panda.sport.rcs.db.service.ITUserGroupBetRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 玩家组管理 服务类
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@Service
public class TUserGroupBetRateServiceImpl extends ServiceImpl<TUserGroupBetRateMapper, TUserGroupBetRate> implements ITUserGroupBetRateService {

    @Autowired
    private TUserGroupBetRateMapper mapper;

}
