package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.mapper.RcsQuotaUserSingleNoteMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.DataTypeEnum;
import com.panda.sport.rcs.mgr.utils.IPUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserSingleNoteService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  kimi
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description :
 * @Date: 2020-09-12 9:55
 * @ModificationHistory Who    When    What
 * 用户单注单关限额
 */
@Slf4j
@RestController
@RequestMapping(value = "/rcsQuotaUserSingleNote")
public class RcsQuotaUserSingleNoteController {
    @Autowired
    private RcsQuotaUserSingleNoteService rcsQuotaUserSingleNoteService;

    /**
     * @Description  获取用户单注单关限额
     * @Param [rcsQuotaUserSingleNote]
     * @Author  kimi
     * @Date   2020/9/12
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote>>
     **/
    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    public HttpResponse<List<RcsQuotaUserSingleNote>> getList(RcsQuotaUserSingleNote  rcsQuotaUserSingleNote){
        try {
            Integer sportId = rcsQuotaUserSingleNote.getSportId();
            if (null ==sportId){
                return HttpResponse.error(-1, "体育种类不能为空");
            }
            if (11 == sportId || 2 == sportId){
                return HttpResponse.error(-1, "体育种类不能为篮球或者足球");
            }
            if (null == rcsQuotaUserSingleNote.getBetState()){
                return HttpResponse.error(-1, "投注阶段不能为空");
            }
            return rcsQuotaUserSingleNoteService.getList(rcsQuotaUserSingleNote);
        }catch (Exception e){
            log.error("::rcsQuotaUserSingleNote{}:: getList ERROR {}",rcsQuotaUserSingleNote.getId(),e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }
    /**
     * @Description  更新数据
     * @Param [rcsQuotaUserSingleNote]
     * @Author  kimi
     * @Date   2020/9/12
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote>>
     **/
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public HttpResponse<List<RcsQuotaUserSingleNote>> update(@RequestBody RcsQuotaUserSingleNote rcsQuotaUserSingleNote, HttpServletRequest request){
        try {
            HttpResponse<List<RcsQuotaUserSingleNote>> validation = validation(rcsQuotaUserSingleNote);
            if (validation!=null){
                return validation;
            }
            rcsQuotaUserSingleNote.setIp(IPUtil.getRequestIp(request));
            return HttpResponse.success(rcsQuotaUserSingleNoteService.singleNoteUpdate(rcsQuotaUserSingleNote));
        }catch (Exception e){
            log.error("::rcsQuotaUserSingleNote{}:: update ERROR {}",rcsQuotaUserSingleNote.getId(),e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * @Description  验证参数
     * @Param [rcsQuotaUserSingleNote]
     * @Author  kimi
     * @Date   2020/10/3
     * @return com.panda.sport.rcs.vo.HttpResponse<java.util.List<com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote>>
     **/

    public HttpResponse<List<RcsQuotaUserSingleNote>> validation(@RequestBody RcsQuotaUserSingleNote rcsQuotaUserSingleNote) {
        if (rcsQuotaUserSingleNote.getSportId() == null) {
            log.warn("::validation{}:: 体育种类不能为空",rcsQuotaUserSingleNote.getId());
            return HttpResponse.error(-1, "体育种类不能为空");
        }
        if (rcsQuotaUserSingleNote.getSportId() == 1 || rcsQuotaUserSingleNote.getSportId() == 2) {
            log.warn("::validation{}:: 体育种类不能为篮球或者足球",rcsQuotaUserSingleNote.getId());
            return HttpResponse.error(-1, "体育种类不能为篮球或者足球");
        }
        if (rcsQuotaUserSingleNote.getBetState() == null) {
            log.warn("::validation{}:: 投注阶段不能为空",rcsQuotaUserSingleNote.getId());
            return HttpResponse.error(-1, "投注阶段不能为空");
        }
        rcsQuotaUserSingleNote.setSingleBetLimitRatio(rcsQuotaUserSingleNote.getSingleBetLimitRatio().divide(Constants.BASE));
        if (Constants.MIN_PROPORTION.compareTo(rcsQuotaUserSingleNote.getSingleBetLimitRatio()) > 0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaUserSingleNote.getSingleBetLimitRatio()) < 0) {
            log.warn("::validation{}:: 商户单场限额比例超出范围范围是0.0001-10",rcsQuotaUserSingleNote.getId());
            return HttpResponse.fail("商户单场限额比例超出范围,自定义输入范围0.01-1000");
        }
        rcsQuotaUserSingleNote.setCumulativeCompensationPlayingRatio(rcsQuotaUserSingleNote.getCumulativeCompensationPlayingRatio().divide(Constants.BASE));
        if (Constants.MIN_PROPORTION.compareTo(rcsQuotaUserSingleNote.getCumulativeCompensationPlayingRatio()) > 0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaUserSingleNote.getCumulativeCompensationPlayingRatio()) < 0) {
            log.warn("::validation{}:: 商户单场限额比例超出范围范围是0.0001-10",rcsQuotaUserSingleNote.getId());
            return HttpResponse.fail("商户单场限额比例超出范围,自定义输入范围0.01-1000");
        }
        return null;
    }
    }
