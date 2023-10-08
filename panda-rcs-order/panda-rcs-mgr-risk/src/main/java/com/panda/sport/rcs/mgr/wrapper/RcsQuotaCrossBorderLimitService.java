package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsQuotaCrossBorderLimit;
import com.panda.sport.rcs.pojo.vo.RcsQuotaCrossBorderLimitVo;
import com.panda.sport.rcs.vo.HttpResponse;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2020-09-12 16:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsQuotaCrossBorderLimitService extends IService<RcsQuotaCrossBorderLimit> {
    /**
     * @Description   //TODO
     * @Param [sportId]
     * @Author  kimi
     * @Date   2020/9/26
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.vo.RcsQuotaCrossBorderLimitVo>
     **/
    HttpResponse<RcsQuotaCrossBorderLimitVo> getList(Integer sportId);

    /**
     *
     * @param sportId
     * @return
     */
    List<RcsQuotaCrossBorderLimit> initRcsQuotaCrossBorderLimit(Integer sportId);

    void updateQuotaCrossBorderLimit(List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitDataList);
}
