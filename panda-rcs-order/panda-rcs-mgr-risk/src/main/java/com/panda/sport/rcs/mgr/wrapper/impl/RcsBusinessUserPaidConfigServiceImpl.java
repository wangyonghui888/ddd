package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.panda.sport.rcs.pojo.RcsBusinessUserPaidConfig;
import com.panda.sport.rcs.mapper.RcsBusinessUserPaidConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessUserPaidConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.vo.UserPaidVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户最大赔付设置 服务实现类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Service
public class RcsBusinessUserPaidConfigServiceImpl extends ServiceImpl<RcsBusinessUserPaidConfigMapper, RcsBusinessUserPaidConfig> implements RcsBusinessUserPaidConfigService {

    @Autowired
    RcsBusinessUserPaidConfigMapper rcsBusinessUserPaidConfigMapper;


    @Override
    public List<UserPaidVo> getUserPaidListView(Long businessId) {
        return rcsBusinessUserPaidConfigMapper.getUserPaidListView(businessId);
    }

    @Override
    public void updateRcsBusinessUserPaidConfig(RcsBusinessUserPaidConfig rcsBusinessUserPaidConfig) {
        rcsBusinessUserPaidConfigMapper.updateById(rcsBusinessUserPaidConfig);
    }

}
