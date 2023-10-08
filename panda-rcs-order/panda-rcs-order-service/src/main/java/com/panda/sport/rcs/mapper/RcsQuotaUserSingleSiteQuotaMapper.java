package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleSiteQuota;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-09-06 11:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Component
public interface RcsQuotaUserSingleSiteQuotaMapper extends BaseMapper<RcsQuotaUserSingleSiteQuota> {
    List<RcsQuotaUserSingleSiteQuota> selectRcsQuotaUserSingleSiteQuota(@Param("sportId") Integer sportId);
}
