package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit;
import com.panda.sport.rcs.pojo.vo.RcsQuotaMerchantSingleFieldLimitVo;
import com.panda.sport.rcs.vo.HttpResponse;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2020-09-04 17:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsQuotaMerchantSingleFieldLimitService extends IService<RcsQuotaMerchantSingleFieldLimit> {
    /**
     * @Description   //TODO
     * @Param [sportId]
     * @Author  kimi
     * @Date   2020/9/26
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit>>
     **/
    HttpResponse<List<RcsQuotaMerchantSingleFieldLimit>> getList(Integer sportId );

    /**
     * 初始化商户单场限额
     * @param sportId
     * @return
     */
    List<RcsQuotaMerchantSingleFieldLimit> initRcsQuotaMerchantSingleFieldLimit(Integer sportId);

    List<RcsQuotaMerchantSingleFieldLimit> fieldLimitUpdate(RcsQuotaMerchantSingleFieldLimitVo rcsQuotaMerchantSingleFieldLimitVo);

    void insertBusinessLimitLog(String paramName, String operateType, String beforeVal, String afterVal);

    void insertBusinessLimitLogIP(String paramName, String operateType, String beforeVal, String afterVal,String ip);
}
