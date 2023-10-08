package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsCurrencyRateMapper;
import com.panda.sport.rcs.pojo.RcsCurrencyRate;
import com.panda.sport.rcs.task.wrapper.RcsCurrencyRateService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RcsCurrencyRateServiceImpl
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/12/20 
**/


@Service
public class RcsCurrencyRateServiceImpl extends ServiceImpl<RcsCurrencyRateMapper, RcsCurrencyRate> implements RcsCurrencyRateService {

    @Resource
    private RcsCurrencyRateMapper rcsCurrencyRateMapper;

    @Override
    public int updateBatch(List<RcsCurrencyRate> list) {
        return rcsCurrencyRateMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<RcsCurrencyRate> list) {
        return rcsCurrencyRateMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsCurrencyRate record) {
        return rcsCurrencyRateMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsCurrencyRate record) {
        return rcsCurrencyRateMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int batchSaveOrUpdate(ArrayList<RcsCurrencyRate> rcsCurrencyRates) {
        if (CollectionUtils.isEmpty(rcsCurrencyRates)) return 0;
        return rcsCurrencyRateMapper.batchSaveOrUpdate(rcsCurrencyRates);
    }

}
