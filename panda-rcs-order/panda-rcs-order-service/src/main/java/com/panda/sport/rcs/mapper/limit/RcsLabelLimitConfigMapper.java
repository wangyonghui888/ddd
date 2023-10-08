package com.panda.sport.rcs.mapper.limit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsLabelLimitConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-02-04 12:20
 **/
@Component
public interface RcsLabelLimitConfigMapper extends BaseMapper<RcsLabelLimitConfig> {

    /**
     *
     * @param tagLevel
     * @return
     */
    List<RcsLabelLimitConfig> userLevelDelay(@Param("tagLevel") int tagLevel);
}
