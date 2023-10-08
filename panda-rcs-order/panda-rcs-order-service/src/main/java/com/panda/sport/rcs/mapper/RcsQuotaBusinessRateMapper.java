package com.panda.sport.rcs.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRate;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTOReqVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  gulang
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  TODO
 * @Date: 2019-11-25 22:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsQuotaBusinessRateMapper extends BaseMapper<RcsQuotaBusinessRate> {

    IPage<RcsQuotaBusinessRateDTO> queryListPage(IPage<RcsQuotaBusinessRateDTO> page , @Param("dto") RcsQuotaBusinessRateDTOReqVo dto);

//    IPage<RcsQuotaBusinessRateDTO> queryRateListPage(IPage<RcsQuotaBusinessRateDTO> page , @Param("dto") RcsQuotaBusinessRateDTOReqVo dto);

    List<RcsQuotaBusinessRateDTO> queryByBusinessId(@Param("dto") RcsQuotaBusinessRateDTO dto);
//    List<RcsQuotaBusinessRateDTO> queryRateByBusinessId(@Param("dto") RcsQuotaBusinessRateDTO dto);

    List<RcsQuotaBusinessRateDTO> queryByBusinessIds(@Param("dto") RcsQuotaBusinessRateDTO dto);

    List<RcsQuotaBusinessRateDTO> queryAllRate();

    int updateBusinessRate(@Param("dto") RcsQuotaBusinessRateDTO dto);

    int batchUpdateBusinessRate(@Param("dto") RcsQuotaBusinessRateDTO dto);

    int batchUpdateVirtualRate(@Param("dto") RcsQuotaBusinessRateDTO dto);

    int insertOrUpdateBusinessRate(RcsQuotaBusinessRateDTO dto);

    int selectMerchantsCount();
    /***/
    List<RcsQuotaBusinessRateDTO> queryNoSetRate();

    List<RcsQuotaBusinessRateDTO> selectMerchantsList(@Param("dto") RcsQuotaBusinessRateDTO dto);
    List<RcsQuotaBusinessRateDTO> selectMerchantsByPage(@Param("offset") int offset,@Param("pageSize")int pageSize);
}