package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.DataTypeEnum;
import com.panda.sport.rcs.mgr.utils.IPUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserDailyQuotaService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsQuotaUserDailyQuota;
import com.panda.sport.rcs.pojo.vo.LimitCacheClearVo;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author :  kimi
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :  TODO
 * @Date: 2020-09-09 13:31
 * @ModificationHistory Who    When    What
 * 用户单日限额
 */
@Slf4j
@RestController
@RequestMapping(value = "/rcsQuotaUserDailyQuota")
public class RcsQuotaUserDailyQuotaController {
    @Autowired
    private RcsQuotaUserDailyQuotaService rcsQuotaUserDailyQuotaService;

    @Autowired
    private ProducerSendMessageUtils sendMessage;
    /**
     * @Description 获取用户单日限额页面
     * @Param []
     * @Author  kimi
     * @Date   2020/9/9
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaUserDailyQuota>>
     **/
    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    public HttpResponse<List<RcsQuotaUserDailyQuota>> getList(){
        try {
            return rcsQuotaUserDailyQuotaService.getList();
        }catch (Exception e){
            log.error("::rcsQuotaUserDailyQuota:: getList ERROR {}",e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }
    
    /**
     * @Description   更新用户单日限额
     * @Param [list]
     * @Author  kimi
     * @Date   2020/9/9
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaUserDailyQuota>>
     **/
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public HttpResponse<List<RcsQuotaUserDailyQuota>> update(@RequestBody RcsQuotaUserDailyQuota rcsQuotaUserDailyQuota, HttpServletRequest request){
        try {
            HttpResponse<List<RcsQuotaUserDailyQuota>> validation = validation(rcsQuotaUserDailyQuota);
            if (validation!=null){
                return validation;
            }
            rcsQuotaUserDailyQuota.setDayCompensation(rcsQuotaUserDailyQuota.getDayCompensationProportion().multiply(new BigDecimal(rcsQuotaUserDailyQuota.getDayCompensationBase())));
            rcsQuotaUserDailyQuota.setCrossDayCompensation(rcsQuotaUserDailyQuota.getCrossDayCompensationProportion().multiply(new BigDecimal(rcsQuotaUserDailyQuota.getDayCompensationBase())));
            rcsQuotaUserDailyQuota.setIp(IPUtil.getRequestIp(request));
            rcsQuotaUserDailyQuotaService.updateQuotaUserDailyQuota(rcsQuotaUserDailyQuota);
            LimitCacheClearVo limitCacheClearVo=new LimitCacheClearVo();
            limitCacheClearVo.setDataType(DataTypeEnum.USER_DAILY_LIMIT.getType());
            limitCacheClearVo.setSportId(rcsQuotaUserDailyQuota.getSportId());
            limitCacheClearVo.setVal2(rcsQuotaUserDailyQuota.getDayCompensation().toPlainString());
            limitCacheClearVo.setVal3(rcsQuotaUserDailyQuota.getCrossDayCompensation().toPlainString());
            sendMessage.sendMessage("RCS_LIMIT_CACHE_CLEAR_TOPIC",limitCacheClearVo);
            return getList();
        }catch (Exception e){
            log.error("::rcsQuotaUserDailyQuota{}:: update ERROR {}",rcsQuotaUserDailyQuota.getId(),e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    public HttpResponse<List<RcsQuotaUserDailyQuota>> validation(@RequestBody RcsQuotaUserDailyQuota rcsQuotaUserDailyQuota){
        rcsQuotaUserDailyQuota.setDayCompensationProportion(rcsQuotaUserDailyQuota.getDayCompensationProportion().divide(Constants.BASE));
        if (Constants.MIN_PROPORTION.compareTo(rcsQuotaUserDailyQuota.getDayCompensationProportion()) > 0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaUserDailyQuota.getDayCompensationProportion()) < 0) {
            log.error("::validation{}:: 用户单日串关赔付总限额比例超出范围，输入范围为是0.0001-10",rcsQuotaUserDailyQuota.getId());
            return HttpResponse.fail("用户单日串关赔付总限额比例超出范围，自定义输入范围0.01-1000");
        }
        rcsQuotaUserDailyQuota.setCrossDayCompensationProportion(rcsQuotaUserDailyQuota.getCrossDayCompensationProportion().divide(Constants.BASE));
        if (Constants.MIN_PROPORTION.compareTo(rcsQuotaUserDailyQuota.getCrossDayCompensationProportion()) > 0 || Constants.DAY_COMPENSATION_PROPORTION.compareTo(rcsQuotaUserDailyQuota.getCrossDayCompensationProportion()) < 0) {
            log.error("::validation{}:: 用户单日串关赔付总限额比例超出范围，输入范围为是0.0001-10",rcsQuotaUserDailyQuota.getId());
            return HttpResponse.fail("用户单日串关赔付总限额比例超出范围，自定义输入范围0.01-100");
        }
        return null;
    }
}
