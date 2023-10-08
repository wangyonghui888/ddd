package com.panda.sport.rcs.task.wrapper.statistics.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.statistics.RcsProfitMarketMapper;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.task.wrapper.statistics.RcsProfitMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.statistics.impl
 * @Description :  盘口级别
 * @Date: 2019-12-11 15:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsProfitMarketServiceImpl extends ServiceImpl<RcsProfitMarketMapper, RcsProfitMarket> implements RcsProfitMarketService {
 @Autowired
 private RcsProfitMarketMapper profitMarketMapper;
    /**
     * @Description   更新
     * @Param [rcsProfitMarket]
     * @Author  toney
     * @Date  17:34 2020/2/6
     * @return java.lang.Integer
     **/
    @Override
    public Integer update(RcsProfitMarket rcsProfitMarket){
        return profitMarketMapper.update(rcsProfitMarket);
    }


    /**
     * 添加或者新增
     * @param rcsProfitMarket
     * @return
     */
    @Override
    public Integer insertOrSave(RcsProfitMarket rcsProfitMarket){
        return profitMarketMapper.insertOrUpdate(rcsProfitMarket);
    }


}
