package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsQuotaUserDailyQuota;
import com.panda.sport.rcs.vo.HttpResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2020-09-09 13:36
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsQuotaUserDailyQuotaService extends IService<RcsQuotaUserDailyQuota> {
    /**
     * @Description   //TODO
     * @Param []
     * @Author  kimi
     * @Date   2020/9/26
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaUserDailyQuota>>
     **/
    HttpResponse<List<RcsQuotaUserDailyQuota>> getList();

    /**
     *  初始化数据
     * @return
     */
    List<RcsQuotaUserDailyQuota> initRcsQuotaUserDailyQuota();

    @Transactional
    void updateQuotaUserDailyQuota(RcsQuotaUserDailyQuota rcsQuotaUserDailyQuota);
}
