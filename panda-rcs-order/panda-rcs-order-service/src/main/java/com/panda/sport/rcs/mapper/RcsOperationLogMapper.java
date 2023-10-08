package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsOperationLog;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  日志
 * @Date: 2020-05-10 21:52
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsOperationLogMapper extends BaseMapper<RcsOperationLog> {
    int deleteByPrimaryKey(Integer id);


    int insertSelective(RcsOperationLog record);

    RcsOperationLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RcsOperationLog record);

    int updateByPrimaryKey(RcsOperationLog record);
}
