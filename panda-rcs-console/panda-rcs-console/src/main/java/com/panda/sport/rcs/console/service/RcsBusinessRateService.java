package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.console.pojo.RcsQuotaBusinessRateExcelVO;
import com.panda.sport.rcs.console.response.PageDataResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author admin
 */
public interface RcsBusinessRateService {

    PageDataResult listPage(Integer pageNum, Integer pageSize, RcsQuotaBusinessRateDTO dto);

    void updateBusinessRate(RcsQuotaBusinessRateDTO dto);

    Map<String, String> getAllRate();

    void saveAllRate(RcsQuotaBusinessRateDTO dto);

    void batchUpdateBusinessRate(RcsQuotaBusinessRateDTO dto);

    void batchUpdateVirtualRate(RcsQuotaBusinessRateDTO dto);


    void initBusinessRate();

    void initRedisBusinessRate();

    PageDataResult listPageDj(Integer pageNum, Integer pageSize, RcsQuotaBusinessRateDTO dto);

    int updateBusinessRateDj(RcsQuotaBusinessRateDTO dto);

    Map<String, String> getAllRateDj();

    void saveAllRateDj(RcsQuotaBusinessRateDTO dto);

    void batchUpdateBusinessRateDj(RcsQuotaBusinessRateDTO dto);

    void batchAddBusinessRateDj(RcsQuotaBusinessRateDTO dto);

    void batchAddOrUpdateBusinessRateDj(List<RcsQuotaBusinessRateExcelVO> collect, CountDownLatch countDownLatch);

    void batchUpdateVirtualRateDj(RcsQuotaBusinessRateDTO dto);


    void initBusinessRateDj();

    void initRedisBusinessRateDj();
}
