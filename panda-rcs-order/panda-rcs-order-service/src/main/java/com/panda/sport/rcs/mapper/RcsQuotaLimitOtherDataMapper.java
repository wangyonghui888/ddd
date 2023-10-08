package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-09-06 16:36
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsQuotaLimitOtherDataMapper extends BaseMapper<RcsQuotaLimitOtherData> {
    /**
     * @Description   //TODO
     * @Param [sportId]
     * @Author  kimi
     * @Date   2020/10/17
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData>
     **/
    List<RcsQuotaLimitOtherData> selectBySportId(@Param("sportId") Integer sportId);
}
