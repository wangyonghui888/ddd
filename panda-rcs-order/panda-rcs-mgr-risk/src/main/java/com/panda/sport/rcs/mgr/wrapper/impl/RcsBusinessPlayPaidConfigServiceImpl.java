package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsBusinessPlayPaidConfigMapper;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessPlayPaidConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author :  kimi
 * @Description :  玩法维度操作接口
 * @Date: 2019-10-03 21:14
 */
@Service
public class RcsBusinessPlayPaidConfigServiceImpl extends ServiceImpl<RcsBusinessPlayPaidConfigMapper, RcsBusinessPlayPaidConfig> implements RcsBusinessPlayPaidConfigService {
    @Autowired
    private RcsBusinessPlayPaidConfigMapper rcsBusinessPlayPaidConfigMapper;

    @Override
    @Transactional
    public List<RcsBusinessPlayPaidConfig> getRcsBusinessPlayPaidConfigList(Map<String, Object> columnMap) {
        List<RcsBusinessPlayPaidConfig> rcsBusinessPlayPaidConfigList = rcsBusinessPlayPaidConfigMapper.selectByMap(columnMap);
        return rcsBusinessPlayPaidConfigList;
    }

    @Override
    public void updateRcsBusinessPlayPaidConfig(RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig) {
        rcsBusinessPlayPaidConfigMapper.updateById(rcsBusinessPlayPaidConfig);
    }

    @Override
    public boolean insertRcsBusinessPlayPaidConfigList(List<RcsBusinessPlayPaidConfig> rcsBusinessPlayPaidConfigList) {
        return saveBatch(rcsBusinessPlayPaidConfigList);
    }
}
