package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsCurrencyRate;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RcsCurrencyRateService
 * @Description: TODO
 * @Author Vector
 * @Date 2019/12/20
 **/
public interface RcsCurrencyRateService extends IService<RcsCurrencyRate> {


    int updateBatch(List<RcsCurrencyRate> list);

    int batchInsert(List<RcsCurrencyRate> list);

    int insertOrUpdate(RcsCurrencyRate record);

    int insertOrUpdateSelective(RcsCurrencyRate record);

    int batchSaveOrUpdate(ArrayList<RcsCurrencyRate> rcsCurrencyRates);
}
