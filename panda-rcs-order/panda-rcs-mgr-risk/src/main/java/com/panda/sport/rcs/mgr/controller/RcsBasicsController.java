package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSON;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.mgr.mq.impl.trigger.TriggerChangeImpl;
import com.panda.sport.rcs.mgr.wrapper.IStandardSportTypeService;
import com.panda.sport.rcs.mgr.wrapper.RcsCodeService;
import com.panda.sport.rcs.mgr.wrapper.SystemItemDictService;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.pojo.StandardSportType;
import com.panda.sport.rcs.pojo.SystemItemDict;
import com.panda.sport.rcs.vo.BussinessVo;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description //b端基础接口
 * @Param
 * @Author kimi
 * @Date 2019/10/16
 * @return
 **/

@RestController
@RequestMapping(value = "/get")
@Slf4j
public class RcsBasicsController {
    @Autowired
    private RcsCodeService rcsCodeService;
    @Autowired
    private IStandardSportTypeService standardSportTypeService;
    @Autowired
    private SystemItemDictService systemItemDictService;

    /**
     * @Description //返回商户列表，暂时放在了数据字典里面
     * @Param []
     * @Author kimi
     **/
    @RequestMapping(value = "/business")
    public HttpResponse<List<BussinessVo>> getBusinessList() {
        List<RcsCode> rcsCodeList;
        List<BussinessVo> list = new ArrayList<>();
        try {
            rcsCodeList = rcsCodeService.getBusinessList();
        } catch (Exception e) {
            log.error("::business:: ERROR{}",e.getMessage(), e);
            return HttpResponse.fail("风控服务器出问题，请通知检查");
        }
        if (rcsCodeList != null && rcsCodeList.size() > 0) {
            list = BeanCopyUtils.copyPropertiesOfList(rcsCodeList, BussinessVo.class);
        }
        return HttpResponse.success(list);
    }

    /**
     * @return
     * @Description //返回运动种类列表
     * @Param []
     * @Author kimi
     * @Date 2019/10/12
     **/

    @RequestMapping(value = "/sportType")
    public HttpResponse<List<StandardSportType>> getSportTypeList() {
        try {
            List<StandardSportType> standardSportTypeList = standardSportTypeService.getStandardSportTypeList();
            return HttpResponse.success(standardSportTypeList);
        } catch (Exception e) {

            log.error("::sportType:: ERROR {}",e.getMessage(), e);
            return HttpResponse.fail("风控服务器出问题，请通知检查");
        }
    }

    /**
     * @return com.panda.sport.rcs.vo.HttpResponse<java.lang.String>
     * @Description 获取玩法时段id 和值
     * @Param []
     * @Author kimi
     * @Date 2019/10/16
     **/

    @RequestMapping(value = "/getPlayingTimes")
    public HttpResponse<String> getPlayingTimes() {
        try {
            List<SystemItemDict> playingTimes = systemItemDictService.getPlayingTimes();
            return HttpResponse.success(JSON.toJSON(playingTimes));
        } catch (Exception e) {
            log.error("::getPlayingTimes:: ERROR {}",e.getMessage(), e);
            return HttpResponse.fail("风控服务器出问题，请通知检查");
        }
    }

    /*@RequestMapping(value = "/test")
    public String test(BaseRcsOrderVo baseRcsOrderVo) {
        Date date = new Date(1567416860155L);
        //baseRcsOrderVo.setStartDate(date);
        date = new Date(1577450405489L);
        //baseRcsOrderVo.setEndDate(date);
        long s = orderStatisticBetTimeMapper.queryUserCount(baseRcsOrderVo);
        return "Sring" + s;
    }*/

    @Autowired
    TriggerChangeImpl change;

    @RequestMapping(value = "/change",method = RequestMethod.POST)
    public HttpResponse<String> change(@RequestBody OrderBean orderBean) {
        try {
            change.orderHandle(orderBean);
            return HttpResponse.success(null);
        } catch (Exception e) {
            log.error("::{}:: change ERROR{},{}",orderBean.getOrderNo(),e.getMessage(), e);
            return HttpResponse.fail("风控服务器出问题，请通知检查");
        }
    }
    
}
