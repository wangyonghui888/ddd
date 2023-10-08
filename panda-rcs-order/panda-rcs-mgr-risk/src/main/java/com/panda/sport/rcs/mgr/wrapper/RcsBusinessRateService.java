package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRate;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTOReqVo;

import java.util.List;
import java.util.Map;

/**
 * @author :  gulang
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  TODO
 * @Date: 2019-11-25 22:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsBusinessRateService extends IService<RcsQuotaBusinessRate> {

    IPage<RcsQuotaBusinessRateDTO> queryListPage(Integer pageNum, Integer pageSize, RcsQuotaBusinessRateDTOReqVo dto);

    int updateBusinessRate(RcsQuotaBusinessRateDTO dto,RcsQuotaBusinessRateDTO beforDto);

    Map getAllRate();

    void saveAllRate(RcsQuotaBusinessRateDTO dto);

    void batchUpdateBusinessRate(RcsQuotaBusinessRateDTO dto);

    void batchUpdateVirtualRate(RcsQuotaBusinessRateDTO dto);

    List<RcsQuotaBusinessRateDTO> queryByBusinessId(RcsQuotaBusinessRateDTO dto);
    List<RcsQuotaBusinessRateDTO> queryByBusinessIds(RcsQuotaBusinessRateDTO dto);

    void initBusinessRate();
    void initRedisBusinessRate();
}
