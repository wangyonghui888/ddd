package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.MatchMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * merchants_single_percentage  表初始清理，清理掉不在标准赛事表中的数据
 */
@Slf4j
@Component
@JobHandler(value = "initClearMerchantsSinglePercentageJob")
public class InitClearMerchantsSinglePercentageJob extends IJobHandler {

    @Autowired
    private MatchMapper matchMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        int rows = 0;
        do{
            rows = matchMapper.deleteMerchantsSinglePercentageInit();
            log.info("::merchants_single_percentage表数据清理::，本次清理数据->{}", rows);
            Thread.sleep(200);
        } while(rows != 0);
        return ReturnT.SUCCESS;
    }

}
