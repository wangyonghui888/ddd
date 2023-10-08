package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsQuotaCrossBorderLimit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-09-12 14:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsQuotaCrossBorderLimitMapper extends BaseMapper<RcsQuotaCrossBorderLimit> {
    List<RcsQuotaCrossBorderLimit> selectRcsQuotaCrossBorderLimit(@Param("sportId") Integer sportId);
}
