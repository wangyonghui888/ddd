package com.panda.rcs.stray.limit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantInterval;

import java.util.List;

public interface RcsMerchantIntervalService extends IService<RcsMerchantInterval> {

    RcsMerchantInterval queryBySportAndStrayType(Integer sportId, Integer strayType);


    List<RcsMerchantInterval> queryAll(Integer sportId);

    void updateData(List<RcsMerchantInterval> rcsMerchantIntervalList);
}
