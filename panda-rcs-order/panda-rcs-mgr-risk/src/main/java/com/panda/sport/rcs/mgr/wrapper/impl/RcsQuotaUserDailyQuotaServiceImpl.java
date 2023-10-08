package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsQuotaUserDailyQuotaMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.BusinessLimitLogTypeEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaUserDailyQuotaEnum;
import com.panda.sport.rcs.mgr.wrapper.IStandardSportTypeService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaMerchantSingleFieldLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserDailyQuotaService;
import com.panda.sport.rcs.pojo.RcsQuotaUserDailyQuota;
import com.panda.sport.rcs.vo.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-09 13:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsQuotaUserDailyQuotaServiceImpl extends ServiceImpl<RcsQuotaUserDailyQuotaMapper, RcsQuotaUserDailyQuota> implements RcsQuotaUserDailyQuotaService {
    @Autowired
    private IStandardSportTypeService standardSportTypeService;
    @Autowired
    private RcsQuotaUserDailyQuotaMapper rcsQuotaUserDailyQuotaMapper;
    @Autowired
    private RcsQuotaMerchantSingleFieldLimitService rcsQuotaMerchantSingleFieldLimitService;

    @Override
    @Transactional
    public HttpResponse<List<RcsQuotaUserDailyQuota>> getList() {
        Map<String, Object> columnMap=new HashMap<>();
        columnMap.put("status",1);
        List<RcsQuotaUserDailyQuota> rcsQuotaUserDailyQuotas = rcsQuotaUserDailyQuotaMapper.selectByMap(columnMap);
        if (CollectionUtils.isEmpty(rcsQuotaUserDailyQuotas)) {
            rcsQuotaUserDailyQuotas=initRcsQuotaUserDailyQuota();
            if (CollectionUtils.isEmpty(rcsQuotaUserDailyQuotas)) {
                log.error("RcsQuotaUserDailyQuota数据初始化失败");
                return HttpResponse.error(-1, "数据初始化失败");
            }
        }
        return HttpResponse.success(rcsQuotaUserDailyQuotas);
    }
    /**
     * @Description 初始化用户单日限额
     * @Param []
     * @Author  kimi
     * @Date   2020/9/9
     * @return void
     **/
    public   List<RcsQuotaUserDailyQuota> initRcsQuotaUserDailyQuota(){
        List<RcsQuotaUserDailyQuota> rcsQuotaUserDailyQuotaList=new ArrayList<>();
        for (RcsQuotaUserDailyQuotaEnum rcsQuotaUserDailyQuotaEnum: RcsQuotaUserDailyQuotaEnum.values()){
            RcsQuotaUserDailyQuota rcsQuotaUserDailyQuota=new RcsQuotaUserDailyQuota();
            rcsQuotaUserDailyQuota.setSportId(rcsQuotaUserDailyQuotaEnum.getSportId());
            rcsQuotaUserDailyQuota.setDayCompensationBase(Constants.QUOTA_BASE);
            rcsQuotaUserDailyQuota.setDayCompensationProportion(new BigDecimal(rcsQuotaUserDailyQuotaEnum.getDayCompensationProportion()));
            rcsQuotaUserDailyQuota.setDayCompensation(rcsQuotaUserDailyQuota.getDayCompensationProportion().multiply(new BigDecimal(Constants.QUOTA_BASE)));
            rcsQuotaUserDailyQuota.setCrossDayCompensationProportion(new BigDecimal(rcsQuotaUserDailyQuotaEnum.getCrossDayCompensationProportion()));
            rcsQuotaUserDailyQuota.setCrossDayCompensation(rcsQuotaUserDailyQuota.getCrossDayCompensationProportion().multiply(new BigDecimal(Constants.QUOTA_BASE)));
            rcsQuotaUserDailyQuota.setStatus(1);
            rcsQuotaUserDailyQuotaList.add(rcsQuotaUserDailyQuota);
        }
        saveBatch(rcsQuotaUserDailyQuotaList);
        return rcsQuotaUserDailyQuotaList;
    }


    @Override
    @Transactional
    public void updateQuotaUserDailyQuota(RcsQuotaUserDailyQuota rcsQuotaUserDailyQuota){
        //记录参数修改日志
        addQuotaUserDailyLog(rcsQuotaUserDailyQuota);
        //更新数据
        updateById(rcsQuotaUserDailyQuota);
    }

    private void addQuotaUserDailyLog(RcsQuotaUserDailyQuota newData){
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(3);
        RcsQuotaUserDailyQuota oldData = getById(newData.getId());
        String sportName = "";
        if (newData.getSportId() != 0){
            if (newData.getSportId() == -1){
                sportName = "其他";
            }else{
                sportName = SportIdEnum.getNameById(newData.getSportId().longValue());
            }
        }
        if (oldData != null) {
            if (newData.getDayCompensationBase().longValue() != oldData.getDayCompensationBase()){
                String paramName;
                if (newData.getSportId() == 0){
                    paramName = "单日单关赔付总限额-默认值";
                }else {
                    paramName = "单日单关赔付限额-"+ sportName+"-默认值";
                }
                String afterVal = newData.getDayCompensationBase()+"";
                String beforeVal = oldData.getDayCompensationBase()+"";
                //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
            }
            if (newData.getDayCompensationProportion().compareTo(oldData.getDayCompensationProportion()) != 0) {
                String paramName;
                if (newData.getSportId() == 0){
                    paramName = "单日单关赔付总限额";
                }else {
                    paramName = "单日单关赔付限额-"+ sportName;
                }
                String afterVal = newData.getDayCompensation().longValue()+"";
                String beforeVal = oldData.getDayCompensation().longValue()+"";
                //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
            }
            if (newData.getCrossDayCompensationProportion().compareTo(oldData.getCrossDayCompensationProportion()) != 0) {
                String paramName;
                if (newData.getSportId() == 0){
                    paramName = "单日串关赔付总限额";
                }else {
                    paramName = "单日串关赔付限额-"+ sportName;
                }
                String afterVal = newData.getCrossDayCompensation().longValue()+"";
                String beforeVal = oldData.getCrossDayCompensation().longValue()+"";
                //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
            }

        }
    }
}
