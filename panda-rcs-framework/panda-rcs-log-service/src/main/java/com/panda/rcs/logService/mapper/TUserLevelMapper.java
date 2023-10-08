package com.panda.rcs.logService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.logService.vo.TUserLevel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TUserLevelMapper extends BaseMapper<TUserLevel> {
}
