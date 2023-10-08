package com.panda.rcs.logService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.logService.vo.RcsOperateLogVO;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.log.format.RcsOperateSimpleLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface RcsOperateLogMapper extends BaseMapper<RcsOperateLog> {

    List<RcsOperateLog> selectByParam(RcsOperateLogVO param);

    Integer selectCountByParam(RcsOperateLogVO param);

    /**
     * 查詢簡易Log
     * @param param
     * @return
     */
    List<RcsOperateSimpleLog> selectSimpleLog(RcsOperateLogVO param);

    Integer selectSimpleLogCount(RcsOperateLogVO param);

    /**
     * 批量新增操作日志
     * @param list
     * @return
     */
    Integer bathInserts(List<RcsOperateLog> list);

}
