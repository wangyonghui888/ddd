package com.panda.sport.rcs.mgr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.mapper.RcsQuotaCrossBorderLimitMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.DataTypeEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaLimitOtherTypeEnum;
import com.panda.sport.rcs.mgr.utils.IPUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaCrossBorderLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaLimitOtherDataService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsQuotaCrossBorderLimit;
import com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData;
import com.panda.sport.rcs.pojo.vo.LimitCacheClearVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaCrossBorderLimitVo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  kimi
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.controller
 * @Description : 单注串关限额
 * @Date: 2020-09-12 14:53
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "/rcsQuotaCrossBorderLimit")
public class RcsQuotaCrossBorderLimitController {
    @Autowired
    private RcsQuotaCrossBorderLimitMapper rcsQuotaCrossBorderLimitMapper;
    @Autowired
    private RcsQuotaCrossBorderLimitService rcsQuotaCrossBorderLimitService;
    @Autowired
    private RcsQuotaLimitOtherDataService rcsQuotaLimitOtherDataService;
    @Autowired
    private ProducerSendMessageUtils sendMessage;
    /**
     * @Description   单注串关限额页面数据
     * @Param [sportId]
     * @Author  kimi
     * @Date   2020/9/12
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.vo.RcsQuotaCrossBorderLimitVo>
     **/
    @RequestMapping(value = "/getList",method = RequestMethod.GET)
    public HttpResponse<RcsQuotaCrossBorderLimitVo> getList(Integer sportId){
        try {
            if (sportId==null){
                log.warn("::getList:: 体育种类不能为空");
                return HttpResponse.error(-1, "体育种类不能为空");
            }
            return rcsQuotaCrossBorderLimitService.getList(sportId);
        }catch (Exception e){
            log.error("::{}::QuotaCrossBorderLimit 查询错误{}、{}",sportId,e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }
    
    /**
     * @Description   //更新数据
     * @Param [rcsQuotaCrossBorderLimitList]
     * @Author  kimi
     * @Date   2020/9/12
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.RcsQuotaCrossBorderLimit>
     **/
    @RequestMapping(value = "/updateRcsQuotaCrossBorderLimit",method = RequestMethod.POST)
    public HttpResponse<RcsQuotaCrossBorderLimitVo> updateRcsQuotaCrossBorderLimit(@RequestBody RcsQuotaCrossBorderLimitVo rcsQuotaCrossBorderLimitVo, HttpServletRequest request){
        try {
            HttpResponse<RcsQuotaCrossBorderLimitVo> validation = validation(rcsQuotaCrossBorderLimitVo);
            if (validation!=null){
                return validation;
            }
            List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList = rcsQuotaCrossBorderLimitVo.getRcsQuotaCrossBorderLimitList();
            Integer sportId = rcsQuotaCrossBorderLimitList.get(0).getSportId();
            HashMap<Integer, RcsQuotaCrossBorderLimit> hashMap = new HashMap<>();
            for (RcsQuotaCrossBorderLimit rcsQuotaCrossBorderLimit : rcsQuotaCrossBorderLimitList) {
                hashMap.put(rcsQuotaCrossBorderLimit.getId(), rcsQuotaCrossBorderLimit);
            }
            Long quotaBase = rcsQuotaCrossBorderLimitVo.getQuotaBase();
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("sport_id", sportId);
            List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitDataList = rcsQuotaCrossBorderLimitMapper.selectByMap(columnMap);
            for (RcsQuotaCrossBorderLimit rcsQuotaCrossBorderLimitData : rcsQuotaCrossBorderLimitDataList) {
                if (quotaBase != null) {
                    rcsQuotaCrossBorderLimitData.setQuotaBase(quotaBase);
                }
                RcsQuotaCrossBorderLimit rcsQuotaCrossBorderLimit = hashMap.get(rcsQuotaCrossBorderLimitData.getId());
                if (rcsQuotaCrossBorderLimit != null) {
                    rcsQuotaCrossBorderLimitData.setQuotaProportion(rcsQuotaCrossBorderLimit.getQuotaProportion());
                }
                rcsQuotaCrossBorderLimitData.setQuota(rcsQuotaCrossBorderLimitData.getQuotaProportion().multiply(new BigDecimal(rcsQuotaCrossBorderLimitData.getQuotaBase())));
                //添加IP
                rcsQuotaCrossBorderLimitData.setIp(IPUtil.getRequestIp(request));
            }
            rcsQuotaCrossBorderLimitService.updateQuotaCrossBorderLimit(rcsQuotaCrossBorderLimitDataList);
            rcsQuotaCrossBorderLimitVo.setRcsQuotaCrossBorderLimitList(null);
            rcsQuotaCrossBorderLimitVo.setIntegerListHashMap(rcsQuotaCrossBorderLimitDataList);
            LimitCacheClearVo limitCacheClearVo=new LimitCacheClearVo();
            limitCacheClearVo.setDataType(DataTypeEnum.SERIES_PAYMENT_LIMIT.getType());
            limitCacheClearVo.setSportId(sportId);
            sendMessage.sendMessage("RCS_LIMIT_CACHE_CLEAR_TOPIC",limitCacheClearVo);
            return HttpResponse.success(rcsQuotaCrossBorderLimitVo);
        }catch (Exception e){
            log.error("::updateRcsQuotaCrossBorderLimit:: ERROR {}",e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }
    
    /**
     * @Description 验证参数
     * @Param [rcsQuotaCrossBorderLimitVo]
     * @Author  kimi
     * @Date   2020/10/3
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.vo.RcsQuotaCrossBorderLimitVo>
     **/
    public HttpResponse<RcsQuotaCrossBorderLimitVo> validation(RcsQuotaCrossBorderLimitVo rcsQuotaCrossBorderLimitVo){
        List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList = rcsQuotaCrossBorderLimitVo.getRcsQuotaCrossBorderLimitList();
        if (CollectionUtils.isEmpty(rcsQuotaCrossBorderLimitList)){
            log.warn("::validation::修改的数据不能为空");
            return HttpResponse.error(-1, "修改的数据不能为空");
        }
        Integer sportId = rcsQuotaCrossBorderLimitList.get(0).getSportId();
        if (sportId == null) {
            log.warn("::validation::体育种类不能为空");
            return HttpResponse.error(-1, "体育种类不能为空");
        }
        for (RcsQuotaCrossBorderLimit rcsQuotaCrossBorderLimit : rcsQuotaCrossBorderLimitList) {
            rcsQuotaCrossBorderLimit.setQuotaProportion(rcsQuotaCrossBorderLimit.getQuotaProportion().divide(Constants.BASE));
            if (Constants.MIN_PROPORTION.compareTo(rcsQuotaCrossBorderLimit.getQuotaProportion()) > 0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaCrossBorderLimit.getQuotaProportion()) < 0) {
                log.error("::validation::商户单场限额比例超出范围范围是0.0001-10");
                return HttpResponse.fail("商户单场限额比例超出范围,自定义输入范围0.01-1000");
            }
        }
        return  null;
    }
    
    /**
     * @Description   更新页面其他数据
     * @Param [rcsQuotaLimitOtherData]
     * @Author  kimi
     * @Date   2020/9/12
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData>
     **/
    @RequestMapping(value = "/updateRcsQuotaLimitOtherData",method = RequestMethod.POST)
    public HttpResponse<RcsQuotaLimitOtherData> updateRcsQuotaLimitOtherData(@RequestBody RcsQuotaCrossBorderLimitVo rcsQuotaCrossBorderLimitVo, HttpServletRequest request){
        try {
            HttpResponse<RcsQuotaLimitOtherData> rcsQuotaLimitOtherDataHttpResponse = validationRcsQuotaLimitOtherData(rcsQuotaCrossBorderLimitVo);
            if (rcsQuotaLimitOtherDataHttpResponse!=null){
                return rcsQuotaLimitOtherDataHttpResponse;
            }
            List<RcsQuotaLimitOtherData> rcsQuotaLimitOtherDataList = rcsQuotaCrossBorderLimitVo.getRcsQuotaLimitOtherDataList();
            String ip= IPUtil.getRequestIp(request);
            for(RcsQuotaLimitOtherData ipdata: rcsQuotaLimitOtherDataList){
                ipdata.setIp(ip);
            }
            //修改数据并且记录日志
            rcsQuotaLimitOtherDataService.updateQuotaLimitOtherData(rcsQuotaLimitOtherDataList);
            List<Integer> list = new ArrayList<>();
            QueryWrapper<RcsQuotaLimitOtherData> wrapper = new QueryWrapper<>();
            for (RcsQuotaLimitOtherTypeEnum rcsQuotaLimitOtherTypeEnum : RcsQuotaLimitOtherTypeEnum.values()) {
                list.add(rcsQuotaLimitOtherTypeEnum.getType());
            }
            wrapper.lambda().in(RcsQuotaLimitOtherData::getType, list).eq(RcsQuotaLimitOtherData::getSportId,rcsQuotaLimitOtherDataList.get(0).getSportId());
            LimitCacheClearVo limitCacheClearVo=new LimitCacheClearVo();
            limitCacheClearVo.setDataType(DataTypeEnum.SERIES_RATIO.getType());
            limitCacheClearVo.setSportId(getSportIdByOther(rcsQuotaLimitOtherDataList));
            sendMessage.sendMessage("RCS_LIMIT_CACHE_CLEAR_TOPIC",limitCacheClearVo);
            LimitCacheClearVo limitCacheClearVo1=new LimitCacheClearVo();
            limitCacheClearVo1.setDataType(DataTypeEnum.BET_AMOUNT_LIMIT.getType());
            limitCacheClearVo1.setSportId(rcsQuotaLimitOtherDataList.get(0).getSportId());
            sendMessage.sendMessage("RCS_LIMIT_CACHE_CLEAR_TOPIC",limitCacheClearVo1);
            for (RcsQuotaLimitOtherData rcsQuotaLimitOtherData : rcsQuotaLimitOtherDataList) {
                if (rcsQuotaLimitOtherData.getType()>100){
                    LimitCacheClearVo limitCacheClearVo2=new LimitCacheClearVo();
                    limitCacheClearVo2.setDataType(DataTypeEnum.SERIES_CONNECTION_RATIO.getType());
                    sendMessage.sendMessage("RCS_LIMIT_CACHE_CLEAR_TOPIC",limitCacheClearVo2);
                    break;
                }
            }
            return HttpResponse.success(rcsQuotaLimitOtherDataService.list(wrapper));
        }catch (Exception e){
            log.error("::updateRcsQuotaLimitOtherData:: ERROR {}",e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    private Integer getSportIdByOther(List<RcsQuotaLimitOtherData> list) {
        for (RcsQuotaLimitOtherData otherData : list) {
            Integer type = otherData.getType();
            Integer sportId = otherData.getSportId();
            if (type != null && type >= 4 && type <= 12 && sportId != null) {
                return sportId;
            }
        }
        return 0;
    }

    /**
     * @Description 验证参数
     * @Param [rcsQuotaCrossBorderLimitVo]
     * @Author  kimi
     * @Date   2020/10/3
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData>
     **/
    private HttpResponse<RcsQuotaLimitOtherData> validationRcsQuotaLimitOtherData(RcsQuotaCrossBorderLimitVo rcsQuotaCrossBorderLimitVo){
        List<RcsQuotaLimitOtherData> rcsQuotaLimitOtherDataList = rcsQuotaCrossBorderLimitVo.getRcsQuotaLimitOtherDataList();
        if (CollectionUtils.isEmpty(rcsQuotaLimitOtherDataList)){
            log.warn("::validationRcsQuotaLimitOtherData::修改的数据不能为空");
            return HttpResponse.error(-1, "修改的数据不能为空");
        }
        if (rcsQuotaLimitOtherDataList.get(0).getType()!=2 && rcsQuotaLimitOtherDataList.get(0).getType()!=3) {
            if (rcsQuotaLimitOtherDataList.get(0).getSportId() == null) {
                log.warn("::validationRcsQuotaLimitOtherData::体育种类不能为空");
                return HttpResponse.error(-1, "体育种类不能为空");
            }
        }
        for (RcsQuotaLimitOtherData rcsQuotaLimitOtherData:rcsQuotaLimitOtherDataList){
            if (rcsQuotaLimitOtherData.getType()>=3) {
                rcsQuotaLimitOtherData.setBaseValue(rcsQuotaLimitOtherData.getBaseValue().divide(Constants.BASE));
                if (Constants.MIN_PROPORTION.compareTo(rcsQuotaLimitOtherData.getBaseValue()) > 0 || Constants.MAX_PROPORTION.compareTo(rcsQuotaLimitOtherData.getBaseValue()) < 0) {
                    log.warn("::validationRcsQuotaLimitOtherData::商户单场限额比例超出范围范围是0.0001-10");
                    return HttpResponse.fail("商户单场限额比例超出范围,自定义输入范围0.01-1000");
                }
            }
        }
        return null;
    }
}
