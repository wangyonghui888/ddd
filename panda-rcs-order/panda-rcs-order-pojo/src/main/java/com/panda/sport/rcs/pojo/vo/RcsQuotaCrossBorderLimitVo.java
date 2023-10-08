package com.panda.sport.rcs.pojo.vo;

import com.panda.sport.rcs.pojo.RcsQuotaCrossBorderLimit;
import com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.vo
 * @Description :  TODO
 * @Date: 2020-09-12 14:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsQuotaCrossBorderLimitVo {
    private List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList;
    private List<RcsQuotaLimitOtherData> rcsQuotaLimitOtherDataList;
    private HashMap<Integer,List<RcsQuotaCrossBorderLimit>> integerListHashMap;
    private Long quotaBase;

    public void setIntegerListHashMap(List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList1) {
        integerListHashMap=new HashMap<>();
        for (RcsQuotaCrossBorderLimit rcsQuotaCrossBorderLimit:rcsQuotaCrossBorderLimitList1){
            if (integerListHashMap.containsKey(rcsQuotaCrossBorderLimit.getTournamentLevel())){
                List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList3 = integerListHashMap.get(rcsQuotaCrossBorderLimit.getTournamentLevel());
                rcsQuotaCrossBorderLimitList3.add(rcsQuotaCrossBorderLimit);
            }else {
                List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList2=new ArrayList<>();
                rcsQuotaCrossBorderLimitList2.add(rcsQuotaCrossBorderLimit);
                integerListHashMap.put(rcsQuotaCrossBorderLimit.getTournamentLevel(),rcsQuotaCrossBorderLimitList2);
            }
        }
    }

    /**
     * 操作人IP
     */
    private String ip;
}
