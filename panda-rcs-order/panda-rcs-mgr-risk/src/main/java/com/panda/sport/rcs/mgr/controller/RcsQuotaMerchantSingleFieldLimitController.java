package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.mapper.RcsQuotaMerchantSingleFieldLimitMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.DataTypeEnum;
import com.panda.sport.rcs.mgr.utils.IPUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaMerchantSingleFieldLimitService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit;
import com.panda.sport.rcs.pojo.vo.LimitCacheClearVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaMerchantSingleFieldLimitVo;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  kimi
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :  TODO
 * @Date: 2020-09-04 17:33
 * @ModificationHistory Who    When    What
 * 商户单场限额
 */
@Slf4j
@RestController
@RequestMapping(value = "/rcsQuotaMerchantSingleFieldLimit")
public class RcsQuotaMerchantSingleFieldLimitController {

    @Autowired
    private RcsQuotaMerchantSingleFieldLimitService rcsQuotaMerchantSingleFieldLimitService;

    /**
     * @Description  获取商户单场限额页面数据
     * @Param [sportId]
     * @Author  kimi
     * @Date   2020/9/6
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit>>
     **/
    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    public HttpResponse<List<RcsQuotaMerchantSingleFieldLimit>> getList(Integer sportId ){
        try {
            //1：验证参数
            if (sportId == null) {
                log.warn("::rcsQuotaMerchantSingleFieldLimit::体育种类不能为空");
                return HttpResponse.error(-1, "体育种类不能为空");
            }
            if (sportId==1 || sportId==2){
                log.warn("::rcsQuotaMerchantSingleFieldLimit::体育种类不能为篮球或者足球");
                return HttpResponse.error(-1, "体育种类不能为篮球或者足球");
            }
            return rcsQuotaMerchantSingleFieldLimitService.getList(sportId);

        }catch (Exception e){
            log.error("::rcsQuotaMerchantSingleFieldLimit{}:: ERROR{}",sportId,e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * @Description   //更新商户单场限额数据
     * @Param [rcsQuotaMerchantSingleFieldLimitVo]
     * @Author  kimi
     * @Date   2020/9/6
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit>>
     **/
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public HttpResponse<List<RcsQuotaMerchantSingleFieldLimit>> update(@RequestBody RcsQuotaMerchantSingleFieldLimitVo rcsQuotaMerchantSingleFieldLimitVo, HttpServletRequest request){
        try {
            rcsQuotaMerchantSingleFieldLimitVo.setIp(IPUtil.getRequestIp(request));
            HttpResponse<List<RcsQuotaMerchantSingleFieldLimit>> validation = validation(rcsQuotaMerchantSingleFieldLimitVo);
            if (validation!=null){
                return validation;
            }
            return HttpResponse.success(rcsQuotaMerchantSingleFieldLimitService.fieldLimitUpdate(rcsQuotaMerchantSingleFieldLimitVo));
        }catch (Exception e){
            log.error("::rcsQuotaMerchantSingleFieldLimit:: update ERROR{}",e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }
    
    /**
     * @Description 验证参数
     * @Param [rcsQuotaMerchantSingleFieldLimitVo]
     * @Author  kimi
     * @Date   2020/10/3
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit>>
     **/
    private HttpResponse<List<RcsQuotaMerchantSingleFieldLimit>> validation(RcsQuotaMerchantSingleFieldLimitVo rcsQuotaMerchantSingleFieldLimitVo){
        Long compensationLimitBase = rcsQuotaMerchantSingleFieldLimitVo.getCompensationLimitBase();
        List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitList = rcsQuotaMerchantSingleFieldLimitVo.getRcsQuotaMerchantSingleFieldLimitList();
        if (rcsQuotaMerchantSingleFieldLimitVo==null ||compensationLimitBase==null || CollectionUtils.isEmpty(rcsQuotaMerchantSingleFieldLimitList) ) {
            log.warn("::rcsQuotaMerchantSingleFieldLimit:: validation 数据不能为空");
            return HttpResponse.error(-1, "数据不能为空");
        }
        Integer sportId = rcsQuotaMerchantSingleFieldLimitList.get(0).getSportId();
        if (sportId==null){
            log.warn("::rcsQuotaMerchantSingleFieldLimit:: validation 体育种类不能为空");
            return HttpResponse.error(-1, "体育种类不能为空");
        }

        for (RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimit:rcsQuotaMerchantSingleFieldLimitList){
            if (rcsQuotaMerchantSingleFieldLimit.getEarlyMorningPaymentLimitRatio()!=null){
                rcsQuotaMerchantSingleFieldLimit.setEarlyMorningPaymentLimitRatio(rcsQuotaMerchantSingleFieldLimit.getEarlyMorningPaymentLimitRatio().divide(Constants.BASE));
                if (Constants.MIN_PROPORTION.compareTo(rcsQuotaMerchantSingleFieldLimit.getEarlyMorningPaymentLimitRatio())>0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaMerchantSingleFieldLimit.getEarlyMorningPaymentLimitRatio())<0){
                    log.warn("::rcsQuotaMerchantSingleFieldLimit:: validation 商户单场限额比例超出范围范围是0.0001-10");
                    return HttpResponse.fail("商户单场限额比例超出范围,自定义输入范围0.01-1000");
                }
            }
            if (rcsQuotaMerchantSingleFieldLimit.getLiveBallPayoutLimitRatio()!=null){
                rcsQuotaMerchantSingleFieldLimit.setLiveBallPayoutLimitRatio(rcsQuotaMerchantSingleFieldLimit.getLiveBallPayoutLimitRatio().divide(Constants.BASE));
                if (Constants.MIN_PROPORTION.compareTo(rcsQuotaMerchantSingleFieldLimit.getLiveBallPayoutLimitRatio())>0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaMerchantSingleFieldLimit.getLiveBallPayoutLimitRatio())<0){
                    log.warn("::rcsQuotaMerchantSingleFieldLimit:: validation 商户单场限额比例超出范围范围是0.0001-10");
                    return HttpResponse.fail("商户单场限额比例超出范围,自定义输入范围0.01-1000");
                }
            }
        }
        return null;
    }
}
