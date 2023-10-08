package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.enums.SeriesTypeEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsQuotaCrossBorderLimitMapper;
import com.panda.sport.rcs.mapper.RcsQuotaLimitOtherDataMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.BusinessLimitLogTypeEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaCrossBorderLimitEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaLimitOtherTypeEnum;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaCrossBorderLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaLimitOtherDataService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaMerchantSingleFieldLimitService;
import com.panda.sport.rcs.pojo.RcsQuotaCrossBorderLimit;
import com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData;
import com.panda.sport.rcs.pojo.vo.RcsQuotaCrossBorderLimitVo;
import com.panda.sport.rcs.vo.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-12 16:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsQuotaCrossBorderLimitServiceImpl extends ServiceImpl<RcsQuotaCrossBorderLimitMapper, RcsQuotaCrossBorderLimit> implements RcsQuotaCrossBorderLimitService {
    @Autowired
    private RcsQuotaLimitOtherDataMapper rcsQuotaLimitOtherDataMapper;
    @Autowired
    private RcsQuotaCrossBorderLimitMapper rcsQuotaCrossBorderLimitMapper;
    @Autowired
    private RcsQuotaCrossBorderLimitService rcsQuotaCrossBorderLimitService;
    @Autowired
    private RcsQuotaLimitOtherDataService rcsQuotaLimitOtherDataService;
    @Autowired
    private RcsQuotaMerchantSingleFieldLimitService rcsQuotaMerchantSingleFieldLimitService;
    @Override
    @Transactional
    public HttpResponse<RcsQuotaCrossBorderLimitVo> getList(Integer sportId) {
        RcsQuotaCrossBorderLimitVo rcsQuotaCrossBorderLimitVo = new RcsQuotaCrossBorderLimitVo();
        List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList = rcsQuotaCrossBorderLimitMapper.selectRcsQuotaCrossBorderLimit(sportId);
        if (CollectionUtils.isEmpty(rcsQuotaCrossBorderLimitList)) {
            rcsQuotaCrossBorderLimitList=initRcsQuotaCrossBorderLimit(sportId);
            if (CollectionUtils.isEmpty(rcsQuotaCrossBorderLimitList)) {
                log.error("RcsQuotaCrossBorderLimit数据初始化失败");
                return HttpResponse.error(-1, "据初始化失败");
            }
        }
        rcsQuotaCrossBorderLimitVo.setIntegerListHashMap(rcsQuotaCrossBorderLimitList);
        List<RcsQuotaLimitOtherData> rcsQuotaLimitOtherDataList = rcsQuotaLimitOtherDataMapper.selectBySportId(sportId);
        if (CollectionUtils.isEmpty(rcsQuotaLimitOtherDataList) || rcsQuotaLimitOtherDataList.size()<3) {
            rcsQuotaLimitOtherDataList=initRcsQuotaLimitOtherData(sportId,rcsQuotaLimitOtherDataList);
            if (CollectionUtils.isEmpty(rcsQuotaLimitOtherDataList) || rcsQuotaCrossBorderLimitList.size()<3) {
                log.error("RcsQuotaLimitOtherData数据初始化失败");
                return HttpResponse.error(-1, "据初始化失败");
            }
        }
        rcsQuotaCrossBorderLimitVo.setRcsQuotaLimitOtherDataList(rcsQuotaLimitOtherDataList);
        for (RcsQuotaLimitOtherData rcsQuotaLimitOtherData:rcsQuotaLimitOtherDataList){
            if (rcsQuotaLimitOtherData.getType()==2){
                rcsQuotaLimitOtherData.setBaseValue(rcsQuotaLimitOtherData.getBaseValue().divide(Constants.BASE));
            }
        }
        return HttpResponse.success(rcsQuotaCrossBorderLimitVo);
    }

    /**
     * @Description   //初始化数据
     * @Param []
     * @Author  kimi
     * @Date   2020/9/12
     * @return void
     **/
    private  List<RcsQuotaLimitOtherData> initRcsQuotaLimitOtherData(Integer sportId,List<RcsQuotaLimitOtherData> rcsQuotaLimitOtherDataList1){
        int size=0;
        if (!CollectionUtils.isEmpty(rcsQuotaLimitOtherDataList1)){
            size=rcsQuotaLimitOtherDataList1.size();
        }
        List<RcsQuotaLimitOtherData> rcsQuotaLimitOtherDataList=new ArrayList<>();
        for (RcsQuotaLimitOtherTypeEnum rcsQuotaLimitOtherTypeEnum:RcsQuotaLimitOtherTypeEnum.values()){
            if (sportId.equals(rcsQuotaLimitOtherTypeEnum.getSportId())){
                RcsQuotaLimitOtherData rcsQuotaLimitOtherData=new RcsQuotaLimitOtherData();
                rcsQuotaLimitOtherData.setType(rcsQuotaLimitOtherTypeEnum.getType());
                rcsQuotaLimitOtherData.setBaseValue(new BigDecimal(rcsQuotaLimitOtherTypeEnum.getBase()));
                rcsQuotaLimitOtherData.setStatus(1);
                rcsQuotaLimitOtherData.setSportId(sportId);
                rcsQuotaLimitOtherDataList.add(rcsQuotaLimitOtherData);
                continue;
            }
            if (size==0 && rcsQuotaLimitOtherTypeEnum.getSportId()==null){
                RcsQuotaLimitOtherData rcsQuotaLimitOtherData=new RcsQuotaLimitOtherData();
                rcsQuotaLimitOtherData.setType(rcsQuotaLimitOtherTypeEnum.getType());
                rcsQuotaLimitOtherData.setBaseValue(new BigDecimal(rcsQuotaLimitOtherTypeEnum.getBase()));
                rcsQuotaLimitOtherData.setStatus(1);
                rcsQuotaLimitOtherDataList.add(rcsQuotaLimitOtherData);
            }
        }
        rcsQuotaLimitOtherDataService.saveBatch(rcsQuotaLimitOtherDataList);
        return rcsQuotaLimitOtherDataList;
    }

    /**
     * @Description   //TODO
     * @Param [sportId]
     * @Author  kimi
     * @Date   2020/9/12
     * @return void
     **/
    public List<RcsQuotaCrossBorderLimit> initRcsQuotaCrossBorderLimit(Integer sportId){
        List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList=new ArrayList<>();
        for (RcsQuotaCrossBorderLimitEnum rcsQuotaCrossBorderLimitEnum:RcsQuotaCrossBorderLimitEnum.values()) {
            int sportId1=sportId;
            if (sportId!=1 && sportId!=2){
                sportId1=5;
            }
            if (sportId1==rcsQuotaCrossBorderLimitEnum.getSportId().intValue()) {
                RcsQuotaCrossBorderLimit rcsQuotaCrossBorderLimit = new RcsQuotaCrossBorderLimit();
                rcsQuotaCrossBorderLimit.setSportId(sportId);
                rcsQuotaCrossBorderLimit.setTournamentLevel(rcsQuotaCrossBorderLimitEnum.getTournamentLevel());
                rcsQuotaCrossBorderLimit.setSeriesConnectionType(rcsQuotaCrossBorderLimitEnum.getSeriesConnectionType());
                rcsQuotaCrossBorderLimit.setQuotaBase(Constants.QUOTA_BASE);
                rcsQuotaCrossBorderLimit.setQuotaProportion(new BigDecimal(rcsQuotaCrossBorderLimitEnum.getQuotaProportion()));
                rcsQuotaCrossBorderLimit.setQuota(rcsQuotaCrossBorderLimit.getQuotaProportion().multiply(new BigDecimal(Constants.QUOTA_BASE)));
                rcsQuotaCrossBorderLimit.setStatus(1);
                rcsQuotaCrossBorderLimitList.add(rcsQuotaCrossBorderLimit);
            }
        }
        rcsQuotaCrossBorderLimitService.saveBatch(rcsQuotaCrossBorderLimitList);
        return rcsQuotaCrossBorderLimitList;
    }

    @Override
    @Transactional
    public void updateQuotaCrossBorderLimit(List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitDataList){
        //记录修改参数日志
        addQuotaCrossBorderLimitLog(rcsQuotaCrossBorderLimitDataList);
        //修改数据
        rcsQuotaCrossBorderLimitService.updateBatchById(rcsQuotaCrossBorderLimitDataList);
    }

    public void addQuotaCrossBorderLimitLog(List<RcsQuotaCrossBorderLimit> newList){
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(6);
        long sportId = newList.get(0).getSportId();
        String paramNamePrefix = "单注赔付限额-";
        String sportName;
        if (sportId == -1){
            sportName = "其他";
        }else{
            sportName = SportIdEnum.getNameById(sportId);
        }
        paramNamePrefix = paramNamePrefix + sportName + "-";
        for (int i = 0; i < newList.size(); i++){
            RcsQuotaCrossBorderLimit newData = newList.get(i);
            RcsQuotaCrossBorderLimit oldData = rcsQuotaCrossBorderLimitService.getById(newData.getId());
            String paramName = paramNamePrefix;
            if (oldData != null){
                String seriesName = SeriesTypeEnum.getValue(oldData.getSeriesConnectionType() + 1);
                paramName = paramName + seriesName + "-";
                String levelName = "无";
                if (oldData.getTournamentLevel() > 0){
                    levelName = oldData.getTournamentLevel() + "级";
                }
                paramName = paramName + levelName;
                //默认值，记录一次日志
                if (i == 0 && newData.getQuotaBase().longValue() != oldData.getQuotaBase().longValue()){
                    String pName = "单注赔付限额-" + sportName + "-默认值";
                    String afterVal = newData.getQuota().longValue()+"";
                    String beforeVal = oldData.getQuota().longValue()+"";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(pName,operateType,beforeVal,afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(pName,operateType,beforeVal,afterVal,newData.getIp());
                }
                //限额比例
                if (newData.getQuotaProportion().compareTo(oldData.getQuotaProportion()) != 0){
                    String afterVal = newData.getQuota().longValue()+"";
                    String beforeVal = oldData.getQuota().longValue()+"";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName,operateType,beforeVal,afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName,operateType,beforeVal,afterVal,newData.getIp());
                }
            }
        }
    }
}
