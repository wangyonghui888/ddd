package com.panda.sport.rcs.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitLogReqVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RcsQuotaBusinessLimitLogMapper extends BaseMapper<RcsQuotaBusinessLimitLog>  {

    RcsQuotaBusinessLimitLog selectByPrimaryKey(Long id);

    IPage<RcsQuotaBusinessLimitLog> queryByPage(IPage<RcsQuotaBusinessLimitLog> page, @Param("reqVo") RcsQuotaBusinessLimitLogReqVo reqVo);

    String getSportNameByIds(@Param("ids") String ids);

    int bathInserts(List<RcsQuotaBusinessLimitLog> list);
}