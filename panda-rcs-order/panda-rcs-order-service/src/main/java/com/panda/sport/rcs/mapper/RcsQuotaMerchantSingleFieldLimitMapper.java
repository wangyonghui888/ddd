package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-09-04 17:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsQuotaMerchantSingleFieldLimitMapper extends BaseMapper<RcsQuotaMerchantSingleFieldLimit> {
    List<RcsQuotaMerchantSingleFieldLimit> selectRcsQuotaMerchantSingleFieldLimit(@Param("sportId") Integer sportId);
}
