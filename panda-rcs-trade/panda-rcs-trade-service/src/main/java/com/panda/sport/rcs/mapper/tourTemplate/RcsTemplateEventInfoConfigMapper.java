package com.panda.sport.rcs.mapper.tourTemplate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTemplateEventInfoConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RcsTemplateEventInfoConfigMapper extends BaseMapper<RcsTemplateEventInfoConfig> {

    void insertBatchEventConfig(@Param("list") List<RcsTemplateEventInfoConfig> list);
}
