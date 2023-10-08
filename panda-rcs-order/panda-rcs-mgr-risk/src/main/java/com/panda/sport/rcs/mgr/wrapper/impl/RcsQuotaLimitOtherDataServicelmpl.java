package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.mapper.RcsQuotaLimitOtherDataMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.BusinessLimitLogTypeEnum;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaBusinessLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaLimitOtherDataService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaMerchantSingleFieldLimitService;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData;
import com.panda.sport.rcs.pojo.vo.RcsQuotaCrossBorderLimitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-06 16:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsQuotaLimitOtherDataServicelmpl extends ServiceImpl<RcsQuotaLimitOtherDataMapper, RcsQuotaLimitOtherData> implements RcsQuotaLimitOtherDataService {

    @Autowired
    private RcsQuotaMerchantSingleFieldLimitService rcsQuotaMerchantSingleFieldLimitService;

    @Override
    @Transactional
    public void updateQuotaLimitOtherData(List<RcsQuotaLimitOtherData> newList){
        //记录修改日志
        addQuotaLimitOtherDataLog(newList);
        //批量更新数据
        updateBatchById(newList);

    }

    private void addQuotaLimitOtherDataLog(List<RcsQuotaLimitOtherData> newList){
        //操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(6);
        for (RcsQuotaLimitOtherData newData : newList) {
            RcsQuotaLimitOtherData oldData = getById(newData.getId());
            if (oldData != null) {
                if (newData.getBaseValue().compareTo(oldData.getBaseValue()) != 0) {
                    String paramName = "单注最高投注额";
                    if (newData.getType() > 3 && newData.getType() <= 12){
                        paramName = "各投注项计入单场/玩法限额的比例-";
                        long sportId = newData.getSportId();
                        String sportName;
                        if (sportId == -1){
                            sportName = "其他";
                        }else{
                            sportName = SportIdEnum.getNameById(sportId);
                        }
                        paramName = paramName + sportName;
                    }else if (newData.getType() > 12){
                        paramName = "计入串关已用额度的比例";
                    }
                    if (newData.getType() > 3){
                        String seriesName = getSeriesName(newData.getType());
                        paramName = paramName + "-" + seriesName;
                    }
                    String afterVal = newData.getBaseValue().multiply(Constants.BASE).longValue()+"";
                    String beforeVal = oldData.getBaseValue().multiply(Constants.BASE).longValue()+"";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
            }
        }
    }

    private String getSeriesName(int type){
        String seriesName;
        switch (type){
            case 4:
                seriesName = "2串1";
                break;
            case 5:
                seriesName = "3串1";
                break;
            case 6:
                seriesName = "4串1";
                break;
            case 7:
                seriesName = "5串1";
                break;
            case 8:
                seriesName = "6串1";
                break;
            case 9:
                seriesName = "7串1";
                break;
            case 10:
                seriesName = "8串1";
                break;
            case 11:
                seriesName = "9串1";
                break;
            case 12:
                seriesName = "10串1";
                break;
            case 103:
                seriesName = "3串N";
                break;
            case 104:
                seriesName = "4串N";
                break;
            case 105:
                seriesName = "5串N";
                break;
            case 106:
                seriesName = "6串N";
                break;
            case 107:
                seriesName = "7串N";
                break;
            case 108:
                seriesName = "8串N";
                break;
            case 109:
                seriesName = "9串N";
                break;
            case 110:
                seriesName = "10串N";
                break;
            default:
                seriesName = "";
        }
        return seriesName;
    }
}
