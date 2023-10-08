package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2020-09-06 16:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsQuotaLimitOtherDataService extends IService<RcsQuotaLimitOtherData> {
    @Transactional
    void updateQuotaLimitOtherData(List<RcsQuotaLimitOtherData> newList);
}
