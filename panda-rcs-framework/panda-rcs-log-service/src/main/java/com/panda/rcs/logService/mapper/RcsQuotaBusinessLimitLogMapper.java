package com.panda.rcs.logService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.rcs.logService.dto.RcsBusinessLogReqVo;
import com.panda.rcs.logService.vo.RcsQuotaBusinessLimitLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RcsBusinessLogMapper
 * */
@Mapper
public interface RcsQuotaBusinessLimitLogMapper extends BaseMapper<RcsQuotaBusinessLimitLog> {

    List<RcsQuotaBusinessLimitLog> queryByPage(@Param("reqVo") RcsBusinessLogReqVo reqVo);

    List<RcsQuotaBusinessLimitLog> queryByExport(@Param("reqVo") RcsBusinessLogReqVo reqVo);
    Integer selectCountByParam(@Param("reqVo") RcsBusinessLogReqVo reqVo);
    int bathInserts(List<RcsQuotaBusinessLimitLog> list);

}
