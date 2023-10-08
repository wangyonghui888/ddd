package com.panda.sport.rcs.console.dao;


import com.panda.sport.rcs.console.dto.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.console.pojo.RcsQuotaBusinessRate;
import com.panda.sport.rcs.console.pojo.RcsQuotaBusinessRateExcelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;

/**
 * @author admin
 */
@Repository
public interface RcsQuotaBusinessRateMapper extends MyMapper<RcsQuotaBusinessRate> {

    List<RcsQuotaBusinessRateDTO> listPage(RcsQuotaBusinessRateDTO dto);
    List<String> getIdList();

    int updateBusinessRate(RcsQuotaBusinessRateDTO dto);

    int batchUpdateBusinessRate(RcsQuotaBusinessRateDTO dto);

    int batchUpdateVirtualRate(RcsQuotaBusinessRateDTO dto);

    int insertOrUpdateBusinessRate(RcsQuotaBusinessRateDTO dto);

    int selectMerchantsCount();
    /***/
    List<RcsQuotaBusinessRateDTO> queryNoSetRate();

    List<RcsQuotaBusinessRateDTO> selectMerchantsList( RcsQuotaBusinessRateDTO dto);
    List<RcsQuotaBusinessRateDTO> selectMerchantsByPage(@Param("offset") int offset,@Param("pageSize")int pageSize);
    List<RcsQuotaBusinessRateDTO> listPageDj(RcsQuotaBusinessRateDTO dto);

    int updateBusinessRateDj(RcsQuotaBusinessRateDTO dto);

    int batchUpdateBusinessRateDj(RcsQuotaBusinessRateDTO dto);
    int batchAddBusinessRateDj(RcsQuotaBusinessRateDTO dto);

    int batchUpdateVirtualRateDj(@Param("dto") RcsQuotaBusinessRateDTO dto);

    int insertOrUpdateBusinessRateDj(RcsQuotaBusinessRateDTO dto);
    int batchAddOrUpdateBusinessRateDj(List<RcsQuotaBusinessRateExcelVO> list);

    int selectMerchantsCountDj();
    /***/
    List<RcsQuotaBusinessRateDTO> queryNoSetRateDj();

    List<RcsQuotaBusinessRateDTO> selectMerchantsListDj(@Param("dto") RcsQuotaBusinessRateDTO dto);
    List<RcsQuotaBusinessRateDTO> selectMerchantsByPageDj(@Param("offset") int offset,@Param("pageSize")int pageSize);
}