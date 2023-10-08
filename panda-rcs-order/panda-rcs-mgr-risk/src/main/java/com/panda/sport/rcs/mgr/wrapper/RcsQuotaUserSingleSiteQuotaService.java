package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleSiteQuota;
import com.panda.sport.rcs.pojo.vo.RcsQuotaUserSingleSiteQuotaVo;
import com.panda.sport.rcs.vo.HttpResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2020-09-06 11:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsQuotaUserSingleSiteQuotaService extends IService<RcsQuotaUserSingleSiteQuota> {
    /**
     * @Description   //TODO
     * @Param [sportId]
     * @Author  kimi
     * @Date   2020/9/26
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaUserSingleSiteQuota>>
     **/
    HttpResponse<List<RcsQuotaUserSingleSiteQuota>> getList(Integer sportId);

    @Transactional
    int singleSiteQuotaUpdate(RcsQuotaUserSingleSiteQuotaVo rcsQuotaUserSingleSiteQuotaVo);
}
