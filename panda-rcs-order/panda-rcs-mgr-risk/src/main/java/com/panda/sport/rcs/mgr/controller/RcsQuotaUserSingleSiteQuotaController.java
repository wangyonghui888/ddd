package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.mapper.RcsQuotaUserSingleSiteQuotaMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.DataTypeEnum;
import com.panda.sport.rcs.mgr.utils.IPUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserSingleSiteQuotaService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleSiteQuota;
import com.panda.sport.rcs.pojo.vo.LimitCacheClearVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaUserSingleSiteQuotaVo;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  kimi
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :  TODO
 * @Date: 2020-09-06 11:05
 * @ModificationHistory Who    When    What
 * 用户单场限额
 */
@Slf4j
@RestController
@RequestMapping(value = "/rcsQuotaUserSingleSiteQuota")
public class RcsQuotaUserSingleSiteQuotaController {
    @Autowired
    private RcsQuotaUserSingleSiteQuotaMapper rcsQuotaUserSingleSiteQuotaMapper;
    @Autowired
    private RcsQuotaUserSingleSiteQuotaService rcsQuotaUserSingleSiteQuotaService;
    /**
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.RcsQuotaUserSingleSiteQuota>
     * @Description 获取用户单场限额数据
     * @Param [sportId]
     * @Author kimi
     * @Date 2020/9/6
     **/
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public HttpResponse<List<RcsQuotaUserSingleSiteQuota>> getList(Integer sportId) {
        try {
            //1：验证参数
            if (sportId == null) {
                log.warn("::{}::rcsQuotaUserSingleSiteQuota::体育种类不能为空",sportId);
                return HttpResponse.error(-1, "体育种类不能为空");
            }

            if (sportId == 1 || sportId == 2) {
                log.warn("::{}::rcsQuotaUserSingleSiteQuota::体育种类不能为篮球或者足球",sportId);
                return HttpResponse.error(-1, "体育种类不能为篮球或者足球");
            }
            return rcsQuotaUserSingleSiteQuotaService.getList(sportId);
        } catch (Exception e) {
            log.error("::rcsQuotaUserSingleSiteQuota:: getList ERROR {}",e.getMessage(), e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }


    /**
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.vo.RcsQuotaUserSingleSiteQuotaVo>
     * @Description 更新用户单场限额数据
     * @Param []
     * @Author kimi
     * @Date 2020/9/6
     **/
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public HttpResponse<List<RcsQuotaUserSingleSiteQuota>> update(@RequestBody RcsQuotaUserSingleSiteQuotaVo rcsQuotaUserSingleSiteQuotaVo, HttpServletRequest request) {
        try {
            HttpResponse<List<RcsQuotaUserSingleSiteQuota>> validation = validation(rcsQuotaUserSingleSiteQuotaVo);
            if (validation!=null){
                return validation;
            }
            rcsQuotaUserSingleSiteQuotaVo.setIp(IPUtil.getRequestIp(request));
            Integer sportId = rcsQuotaUserSingleSiteQuotaService.singleSiteQuotaUpdate(rcsQuotaUserSingleSiteQuotaVo);
            return getList(sportId);
        } catch (Exception e) {
            log.error("::rcsQuotaUserSingleSiteQuota:: update ERROR {}",e.getMessage(), e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    private HttpResponse<List<RcsQuotaUserSingleSiteQuota>> validation(@RequestBody RcsQuotaUserSingleSiteQuotaVo rcsQuotaUserSingleSiteQuotaVo) {
        List<RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaList = rcsQuotaUserSingleSiteQuotaVo.getRcsQuotaUserSingleSiteQuotaList();
        if (CollectionUtils.isEmpty(rcsQuotaUserSingleSiteQuotaList)){
            log.warn("::validation::修改的数据为空");
            return HttpResponse.fail("修改的数据为空");
        }
        for (RcsQuotaUserSingleSiteQuota rcsQuotaUserSingleSiteQuota : rcsQuotaUserSingleSiteQuotaList) {
            if (rcsQuotaUserSingleSiteQuota.getEarlyUserSingleSiteQuotaProportion()!=null) {
                rcsQuotaUserSingleSiteQuota.setEarlyUserSingleSiteQuotaProportion(rcsQuotaUserSingleSiteQuota.getEarlyUserSingleSiteQuotaProportion().divide(Constants.BASE));
                if (Constants.MIN_PROPORTION.compareTo(rcsQuotaUserSingleSiteQuota.getEarlyUserSingleSiteQuotaProportion()) > 0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaUserSingleSiteQuota.getEarlyUserSingleSiteQuotaProportion()) < 0) {
                    log.warn("::validation::商户单场限额比例超出范围范围是0.01-100");
                    return HttpResponse.fail("商户单场限额比例超出范围,自定义输入范围0.01-1000");
                }
            }
            if (rcsQuotaUserSingleSiteQuota.getLiveUserSingleSiteQuotaProportion()!=null) {
                rcsQuotaUserSingleSiteQuota.setLiveUserSingleSiteQuotaProportion(rcsQuotaUserSingleSiteQuota.getLiveUserSingleSiteQuotaProportion().divide(Constants.BASE));
                if (Constants.MIN_PROPORTION.compareTo(rcsQuotaUserSingleSiteQuota.getLiveUserSingleSiteQuotaProportion()) > 0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaUserSingleSiteQuota.getLiveUserSingleSiteQuotaProportion()) < 0) {
                    log.warn("::validation::商户单场限额比例超出范围范围是0.01-100");
                    return HttpResponse.fail("商户单场限额比例超出范围,自定义输入范围0.01-1000");
                }
            }
        }
        return null;
    }
}
